package org.springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springboot.config.OpenLibraryProperties;
import org.springboot.entity.dto.ExternalBookDTO;
import org.springboot.entity.request.ExternalBookSearchRequest;
import org.springboot.exception.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class OpenLibraryClient {

    private static final String SOURCE_PLATFORM = "openLibrary";
    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final String WORK_URL = "https://openlibrary.org%s.json";
    private static final int TAG_LIMIT = 5;

    private final RestClient restClient;

    public OpenLibraryClient(OpenLibraryProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(8));
        requestFactory.setReadTimeout(Duration.ofSeconds(20));
        if (properties.getProxy().isEnabled()) {
            InetSocketAddress address = new InetSocketAddress(
                    properties.getProxy().getHost(),
                    properties.getProxy().getPort()
            );
            requestFactory.setProxy(new Proxy(Proxy.Type.HTTP, address));
            log.info("Open Library 请求代理已启用: {}:{}", properties.getProxy().getHost(), properties.getProxy().getPort());
        }
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    public List<ExternalBookDTO> search(ExternalBookSearchRequest request, int limit) {
        URI uri = UriComponentsBuilder.fromUriString(SEARCH_URL)
                .queryParam("q", request.getKeyword())
                .queryParam("limit", limit)
                .queryParam("page", request.getPage())
                .queryParamIfPresent("language", java.util.Optional.ofNullable(blankToNull(request.getLanguage())))
                .build()
                .encode()
                .toUri();

        try {
            OpenLibrarySearchResponse response = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.USER_AGENT, "SYLibrary/1.0 (book import; contact: local-admin)")
                    .retrieve()
                    .body(OpenLibrarySearchResponse.class);

            if (response == null || response.docs == null) {
                return List.of();
            }

            return response.docs.stream()
                    .map(this::toExternalBook)
                    .map(this::enrichWork)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (ResourceAccessException e) {
            log.error("Open Library 网络请求失败，url={}", uri, e);
            throw new ServiceException("连接 Open Library 失败，请检查网络或代理后重试", e);
        } catch (Exception e) {
            log.error("Open Library 请求失败，url={}", uri, e);
            throw new ServiceException("获取 Open Library 书籍失败: " + e.getMessage(), e);
        }
    }

    private ExternalBookDTO toExternalBook(OpenLibraryDoc doc) {
        if (isBlank(doc.title)) {
            return null;
        }

        ExternalBookDTO book = new ExternalBookDTO();
        book.setSourcePlatform(SOURCE_PLATFORM);
        book.setSourceId(firstNonBlank(doc.key, first(doc.edition_key)));
        book.setName(limitText(doc.title, 80));
        book.setAuthor(limitText(join(doc.author_name, ", ", 3), 64));
        book.setImageUrl(doc.cover_i == null ? null : "https://covers.openlibrary.org/b/id/" + doc.cover_i + "-L.jpg");
        book.setTags(limitText(join(doc.subject, ", ", TAG_LIMIT), 128));
        book.setIsbn(limitText(first(doc.isbn), 30));
        book.setPublisher(limitText(first(doc.publisher), 255));
        book.setPublishYear(doc.first_publish_year == null ? null : String.valueOf(doc.first_publish_year));

        String description = firstSentence(doc.first_sentence);
        book.setDescription(limitText(description, 1000));
        book.setComment(limitText(buildComment(book), 256));
        book.setExistsLocal(false);
        return book;
    }

    private ExternalBookDTO enrichWork(ExternalBookDTO book) {
        if (book == null || isBlank(book.getSourceId())) {
            return book;
        }
        if (!isBlank(book.getTags()) && !isBlank(book.getDescription())) {
            return book;
        }
        if (!book.getSourceId().startsWith("/works/")) {
            return book;
        }

        URI uri = URI.create(String.format(WORK_URL, book.getSourceId()));
        try {
            OpenLibraryWork work = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.USER_AGENT, "SYLibrary/1.0 (book import; contact: local-admin)")
                    .retrieve()
                    .body(OpenLibraryWork.class);

            if (work == null) {
                return book;
            }
            if (isBlank(book.getTags())) {
                book.setTags(limitText(join(work.subjects, ", ", TAG_LIMIT), 128));
            }
            if (isBlank(book.getDescription())) {
                book.setDescription(limitText(descriptionText(work.description), 1000));
            }
            book.setComment(limitText(buildComment(book), 256));
            return book;
        } catch (Exception e) {
            log.debug("Open Library 详情补全失败，url={}", uri, e);
            return book;
        }
    }

    private String buildComment(ExternalBookDTO book) {
        List<String> parts = new ArrayList<>();
        if (!isBlank(book.getDescription())) {
            parts.add(book.getDescription());
        }
        if (!isBlank(book.getPublishYear())) {
            parts.add("出版年份：" + book.getPublishYear());
        }
        if (!isBlank(book.getPublisher())) {
            parts.add("出版社：" + book.getPublisher());
        }
        return String.join("\n", parts);
    }

    private String firstSentence(Object value) {
        return descriptionText(value);
    }

    private String descriptionText(Object value) {
        if (value instanceof String text) {
            return text;
        }
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .filter(text -> !isBlank(text))
                    .findFirst()
                    .orElse(null);
        }
        if (value instanceof Map<?, ?> map) {
            Object text = map.get("value");
            return text == null ? null : String.valueOf(text);
        }
        return null;
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

    private String first(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .filter(value -> !isBlank(value))
                .findFirst()
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

    private String blankToNull(String value) {
        return isBlank(value) ? null : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class OpenLibrarySearchResponse {
        public List<OpenLibraryDoc> docs;
    }

    private static class OpenLibraryDoc {
        public String key;
        public String title;
        public List<String> edition_key;
        public List<String> author_name;
        public Integer cover_i;
        public List<String> subject;
        public Object first_sentence;
        public List<String> isbn;
        public List<String> publisher;
        public Integer first_publish_year;
    }

    private static class OpenLibraryWork {
        public Object description;
        public List<String> subjects;
    }
}
