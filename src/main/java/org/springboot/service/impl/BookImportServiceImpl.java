package org.springboot.service.impl;

import org.springboot.entity.Book;
import org.springboot.entity.dto.BookImportResultDTO;
import org.springboot.entity.dto.ExternalBookDTO;
import org.springboot.entity.request.BookImportSelectedRequest;
import org.springboot.entity.request.ExternalBookSearchRequest;
import org.springboot.exception.InvalidRequestException;
import org.springboot.mapper.BookMapper;
import org.springboot.service.BookImportService;
import org.springboot.service.GoogleBooksClient;
import org.springboot.service.OpenLibraryClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookImportServiceImpl implements BookImportService {

    private static final String OPEN_LIBRARY = "openLibrary";
    private static final String GOOGLE_BOOKS = "googleBooks";
    private static final int SEARCH_LIMIT = 100;
    private static final int SELECTED_IMPORT_LIMIT = 100;
    private static final int DIRECT_IMPORT_LIMIT = 500;

    private final OpenLibraryClient openLibraryClient;
    private final GoogleBooksClient googleBooksClient;
    private final BookMapper bookMapper;

    public BookImportServiceImpl(OpenLibraryClient openLibraryClient, GoogleBooksClient googleBooksClient, BookMapper bookMapper) {
        this.openLibraryClient = openLibraryClient;
        this.googleBooksClient = googleBooksClient;
        this.bookMapper = bookMapper;
    }

    @Override
    public List<ExternalBookDTO> searchExternalBooks(ExternalBookSearchRequest request) {
        validateSearchRequest(request);
        int limit = normalizeLimit(request.getLimit(), 20, SEARCH_LIMIT);
        List<ExternalBookDTO> books = searchBySource(request, limit);
        return applyFilters(books, request);
    }

    @Override
    public BookImportResultDTO importSelectedBooks(BookImportSelectedRequest request) {
        if (request == null || request.getBooks() == null || request.getBooks().isEmpty()) {
            throw new InvalidRequestException("请选择要导入的书籍");
        }
        if (!isSupportedSource(request.getSource())) {
            throw new InvalidRequestException("暂只支持 Open Library 和 Google Books 导入");
        }

        int limit = Math.min(request.getBooks().size(), SELECTED_IMPORT_LIMIT);
        BookImportResultDTO result = new BookImportResultDTO();
        result.setSelected(request.getBooks().size());
        result.setRequested(limit);
        result.setFetched(limit);

        importBooks(request.getBooks().subList(0, limit), result);
        return result;
    }

    @Override
    public BookImportResultDTO importOpenLibraryBooks(ExternalBookSearchRequest request) {
        validateSearchRequest(request);
        int limit = normalizeLimit(request.getLimit(), 20, DIRECT_IMPORT_LIMIT);
        request.setLimit(limit);
        request.setSource(OPEN_LIBRARY);
        request.setSkipExisting(false);

        BookImportResultDTO result = new BookImportResultDTO();
        result.setRequested(limit);

        List<ExternalBookDTO> books = openLibraryClient.search(request, limit);
        books = applyFilters(books, request);
        result.setFetched(books.size());

        importBooks(books, result);
        return result;
    }

    private void importBooks(List<ExternalBookDTO> books, BookImportResultDTO result) {
        for (ExternalBookDTO externalBook : books) {
            try {
                if (isInvalidBook(externalBook)) {
                    result.setFailed(result.getFailed() + 1);
                    continue;
                }

                markExistsLocal(externalBook);
                if (Boolean.TRUE.equals(externalBook.getExistsLocal())) {
                    result.setDuplicated(result.getDuplicated() + 1);
                    continue;
                }

                bookMapper.addBook(toBook(externalBook));
                result.setImported(result.getImported() + 1);
            } catch (Exception e) {
                result.setFailed(result.getFailed() + 1);
            }
        }
    }

    private List<ExternalBookDTO> applyFilters(List<ExternalBookDTO> books, ExternalBookSearchRequest request) {
        List<ExternalBookDTO> result = new ArrayList<>();
        for (ExternalBookDTO book : books) {
            markExistsLocal(book);

            if (Boolean.TRUE.equals(request.getOnlyWithCover()) && isBlank(book.getImageUrl())) {
                continue;
            }
            if (Boolean.TRUE.equals(request.getOnlyWithDescription()) && isBlank(book.getDescription())) {
                continue;
            }
            if (Boolean.TRUE.equals(request.getSkipExisting()) && Boolean.TRUE.equals(book.getExistsLocal())) {
                continue;
            }

            result.add(book);
        }
        return result;
    }

    private void markExistsLocal(ExternalBookDTO book) {
        book.setExistsLocal(existsLocal(book));
    }

    private boolean existsLocal(ExternalBookDTO book) {
        if (!isBlank(book.getSourcePlatform()) && !isBlank(book.getSourceId())
                && bookMapper.countBySource(book.getSourcePlatform(), book.getSourceId()) > 0) {
            return true;
        }
        if (!isBlank(book.getIsbn()) && bookMapper.countByIsbn(book.getIsbn()) > 0) {
            return true;
        }
        return !isBlank(book.getName()) && !isBlank(book.getAuthor())
                && bookMapper.countByNameAndAuthor(book.getName(), book.getAuthor()) > 0;
    }

    private Book toBook(ExternalBookDTO externalBook) {
        Book book = new Book();
        book.setName(limitText(externalBook.getName(), 80));
        book.setAuthor(limitText(externalBook.getAuthor(), 64));
        book.setImageUrl(limitText(externalBook.getImageUrl(), 256));
        book.setTags(limitText(externalBook.getTags(), 128));
        book.setComment(limitText(firstNonBlank(externalBook.getComment(), externalBook.getDescription()), 256));
        book.setSourcePlatform(limitText(firstNonBlank(externalBook.getSourcePlatform(), OPEN_LIBRARY), 50));
        book.setSourceId(limitText(externalBook.getSourceId(), 100));
        book.setIsbn(limitText(externalBook.getIsbn(), 30));
        book.setPublisher(limitText(externalBook.getPublisher(), 255));
        book.setPublishYear(limitText(externalBook.getPublishYear(), 20));
        book.setDescription(limitText(externalBook.getDescription(), 1000));
        return book;
    }

    private void validateSearchRequest(ExternalBookSearchRequest request) {
        if (request == null || isBlank(request.getKeyword())) {
            throw new InvalidRequestException("请输入搜索关键词");
        }
        request.setKeyword(request.getKeyword().trim());
        if (OPEN_LIBRARY.equals(request.getSource()) && request.getKeyword().length() < 3) {
            throw new InvalidRequestException("Open Library 搜索关键词至少需要 3 个字符，请输入更具体的词，例如：中国历史、历史小说、Java");
        }
        if (isBlank(request.getSource())) {
            request.setSource(OPEN_LIBRARY);
        }
        if (!isSupportedSource(request.getSource())) {
            throw new InvalidRequestException("暂只支持 Open Library 和 Google Books 导入");
        }
        if (request.getPage() == null || request.getPage() < 1) {
            request.setPage(1);
        }
    }

    private List<ExternalBookDTO> searchBySource(ExternalBookSearchRequest request, int limit) {
        if (GOOGLE_BOOKS.equals(request.getSource())) {
            return googleBooksClient.search(request, limit);
        }
        return openLibraryClient.search(request, limit);
    }

    private boolean isSupportedSource(String source) {
        return OPEN_LIBRARY.equals(source) || GOOGLE_BOOKS.equals(source);
    }

    private int normalizeLimit(Integer input, int defaultValue, int maxValue) {
        if (input == null || input < 1) {
            return defaultValue;
        }
        return Math.min(input, maxValue);
    }

    private boolean isInvalidBook(ExternalBookDTO book) {
        return book == null || isBlank(book.getName()) || isBlank(book.getSourceId());
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
}
