package org.springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springboot.config.GoogleBooksProperties;
import org.springboot.entity.dto.ExternalBookDTO;
import org.springboot.entity.request.ExternalBookSearchRequest;
import org.springboot.exception.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GoogleBooksClient {

    private static final String SOURCE_PLATFORM = "googleBooks";
    private static final String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes";

    private final RestClient restClient;
    private final GoogleBooksProperties properties;

    public GoogleBooksClient(GoogleBooksProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(8));
        requestFactory.setReadTimeout(Duration.ofSeconds(20));
        if (properties.getProxy().isEnabled()) {
            InetSocketAddress address = new InetSocketAddress(
                    properties.getProxy().getHost(),
                    properties.getProxy().getPort()
            );
            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, address));
            log.info("Google Books 请求代理已启用: {}:{}", properties.getProxy().getHost(), properties.getProxy().getPort());
        }
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public List<ExternalBookDTO> search(ExternalBookSearchRequest request, int limit) {
        int startIndex = Math.max(0, (request.getPage() - 1) * limit);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(SEARCH_URL)
                .queryParam("q", request.getKeyword())
                .queryParam("maxResults", Math.min(limit, 40))
                .queryParam("startIndex", startIndex)
                .queryParam("printType", "books");
        if (!isBlank(request.getLanguage())) {
            builder.queryParam("langRestrict", request.getLanguage());
        }
        if (!isBlank(properties.getApiKey())) {
            builder.queryParam("key", properties.getApiKey());
        }

        URI uri = builder.build().encode().toUri();

        try {
            GoogleBooksSearchResponse response = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.USER_AGENT, "SYLibrary/1.0 (book import; contact: local-admin)")
                    .retrieve()
                    .body(GoogleBooksSearchResponse.class);

            if (response == null || response.items == null) {
                return List.of();
            }

            return response.items.stream()
                    .map(this::toExternalBook)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (ResourceAccessException e) {
            log.error("Google Books 网络请求失败，url={}", uri, e);
            throw new ServiceException("连接 Google Books 失败，请检查网络或代理后重试", e);
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Google Books 配额不足，url={}", uri, e);
            throw new ServiceException("Google Books 配额不足，请配置可用 API Key，或切换到 Open Library 搜索", e);
        } catch (Exception e) {
            log.error("Google Books 请求失败，url={}", uri, e);
            throw new ServiceException("获取 Google Books 书籍失败: " + e.getMessage(), e);
        }
    }

    private ExternalBookDTO toExternalBook(GoogleBookItem item) {
        if (item == null || item.volumeInfo == null || isBlank(item.volumeInfo.title)) {
            return null;
        }

        GoogleVolumeInfo info = item.volumeInfo;
        ExternalBookDTO book = new ExternalBookDTO();
        book.setSourcePlatform(SOURCE_PLATFORM);
        book.setSourceId(item.id);
        book.setName(limitText(info.title, 80));
        book.setAuthor(limitText(join(info.authors, ", ", 3), 64));
        book.setImageUrl(limitText(normalizeImageUrl(imageUrl(info.imageLinks)), 256));
        book.setTags(limitText(join(info.categories, ", ", 5), 128));
        book.setDescription(limitText(info.description, 1000));
        book.setIsbn(limitText(isbn(info.industryIdentifiers), 30));
        book.setPublisher(limitText(info.publisher, 255));
        book.setPublishYear(limitText(publishYear(info.publishedDate), 20));
        book.setComment(limitText(buildComment(book), 256));
        book.setExistsLocal(false);
        return book;
    }

    private String buildComment(ExternalBookDTO book) {
        if (!isBlank(book.getDescription())) {
            return book.getDescription();
        }
        if (!isBlank(book.getPublishYear())) {
            return "出版年份：" + book.getPublishYear();
        }
        return null;
    }

    private String isbn(List<IndustryIdentifier> identifiers) {
        if (identifiers == null || identifiers.isEmpty()) {
            return null;
        }
        return identifiers.stream()
                .filter(identifier -> "ISBN_13".equals(identifier.type))
                .map(identifier -> identifier.identifier)
                .filter(value -> !isBlank(value))
                .findFirst()
                .orElseGet(() -> identifiers.stream()
                        .map(identifier -> identifier.identifier)
                        .filter(value -> !isBlank(value))
                        .findFirst()
                        .orElse(null));
    }

    private String imageUrl(ImageLinks imageLinks) {
        if (imageLinks == null) {
            return null;
        }
        return firstNonBlank(imageLinks.extraLarge,
                firstNonBlank(imageLinks.large,
                        firstNonBlank(imageLinks.medium,
                                firstNonBlank(imageLinks.thumbnail, imageLinks.smallThumbnail))));
    }

    private String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null) {
            return null;
        }
        return imageUrl.startsWith("http://") ? "https://" + imageUrl.substring("http://".length()) : imageUrl;
    }

    private String publishYear(String publishedDate) {
        if (isBlank(publishedDate)) {
            return null;
        }
        return publishedDate.length() >= 4 ? publishedDate.substring(0, 4) : publishedDate;
    }

    private String join(List<String> values, String delimiter, int maxItems) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .filter(value -> !isBlank(value))
                .limit(maxItems)
                .reduce((left, right) -> left + delimiter + right)
                .orElse(null);
    }

    private String firstNonBlank(String first, String second) {
        return isBlank(first) ? second : first;
    }

    private String limitText(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class GoogleBooksSearchResponse {
        public List<GoogleBookItem> items;
    }

    private static class GoogleBookItem {
        public String id;
        public GoogleVolumeInfo volumeInfo;
    }

    private static class GoogleVolumeInfo {
        public String title;
        public List<String> authors;
        public String publisher;
        public String publishedDate;
        public String description;
        public List<IndustryIdentifier> industryIdentifiers;
        public List<String> categories;
        public ImageLinks imageLinks;
    }

    private static class IndustryIdentifier {
        public String type;
        public String identifier;
    }

    private static class ImageLinks {
        public String smallThumbnail;
        public String thumbnail;
        public String medium;
        public String large;
        public String extraLarge;
    }
}
