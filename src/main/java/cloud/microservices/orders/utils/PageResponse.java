package cloud.microservices.orders.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.io.Serializable;
import java.util.List;

/**
 * A wrapper class for paginated responses.
 * Includes both the data and pagination metadata.
 *
 * @param <T> The type of items in the list
 */
@JsonSerialize
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("pagination")
    private PaginationMetadata pagination;

    /**
     * Default constructor.
     */
    public PageResponse() {
    }

    /**
     * Constructor with content and pagination metadata.
     *
     * @param content    The list of items for the current page
     * @param pagination The pagination metadata
     */
    public PageResponse(List<T> content, PaginationMetadata pagination) {
        this.content = content;
        this.pagination = pagination;
    }

    /**
     * Creates a new PageResponse with the given parameters.
     *
     * @param content    The list of items for the current page
     * @param totalCount The total count of items across all pages
     * @param page       The current page number (0-based)
     * @param size       The page size
     * @param <T>        The type of items in the list
     * @return A new PageResponse instance
     */
    public static <T> PageResponse<T> of(List<T> content, long totalCount, int page, int size) {
        PaginationMetadata metadata = new PaginationMetadata(page, size, totalCount);
        return new PageResponse<>(content, metadata);
    }

    /**
     * Gets the content.
     *
     * @return The list of items for the current page
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content The list of items for the current page
     */
    public void setContent(List<T> content) {
        this.content = content;
    }

    /**
     * Gets the pagination metadata.
     *
     * @return The pagination metadata
     */
    public PaginationMetadata getPagination() {
        return pagination;
    }

    /**
     * Sets the pagination metadata.
     *
     * @param pagination The pagination metadata
     */
    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }

    /**
     * This method is used by Jackson for serialization.
     * It ensures that the object is properly serialized to JSON.
     *
     * @return The content of this PageResponse
     */
    public List<T> getItems() {
        return content;
    }

    /**
     * Inner class for pagination metadata.
     */
    @JsonSerialize
    @JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
    public static class PaginationMetadata implements Serializable {
        private static final long serialVersionUID = 2L;

        @JsonProperty("page")
        private int page;

        @JsonProperty("size")
        private int size;

        @JsonProperty("totalElements")
        private long totalElements;

        @JsonProperty("totalPages")
        private int totalPages;

        @JsonProperty("first")
        private boolean first;

        @JsonProperty("last")
        private boolean last;

        /**
         * Default constructor.
         */
        public PaginationMetadata() {
        }

        /**
         * Constructor with pagination parameters.
         *
         * @param page          The current page number (0-based)
         * @param size          The page size
         * @param totalElements The total count of items across all pages
         */
        public PaginationMetadata(int page, int size, long totalElements) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
            this.first = page == 0;
            this.last = page >= totalPages - 1;
        }

        /**
         * Gets the current page number (0-based).
         *
         * @return The current page number
         */
        public int getPage() {
            return page;
        }

        /**
         * Sets the current page number.
         *
         * @param page The current page number
         */
        public void setPage(int page) {
            this.page = page;
        }

        /**
         * Gets the page size.
         *
         * @return The page size
         */
        public int getSize() {
            return size;
        }

        /**
         * Sets the page size.
         *
         * @param size The page size
         */
        public void setSize(int size) {
            this.size = size;
        }

        /**
         * Gets the total count of items across all pages.
         *
         * @return The total count of items
         */
        public long getTotalElements() {
            return totalElements;
        }

        /**
         * Sets the total count of items.
         *
         * @param totalElements The total count of items
         */
        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        /**
         * Gets the total number of pages.
         *
         * @return The total number of pages
         */
        public int getTotalPages() {
            return totalPages;
        }

        /**
         * Sets the total number of pages.
         *
         * @param totalPages The total number of pages
         */
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        /**
         * Checks if this is the first page.
         *
         * @return true if this is the first page, false otherwise
         */
        public boolean isFirst() {
            return first;
        }

        /**
         * Sets whether this is the first page.
         *
         * @param first true if this is the first page, false otherwise
         */
        public void setFirst(boolean first) {
            this.first = first;
        }

        /**
         * Checks if this is the last page.
         *
         * @return true if this is the last page, false otherwise
         */
        public boolean isLast() {
            return last;
        }

        /**
         * Sets whether this is the last page.
         *
         * @param last true if this is the last page, false otherwise
         */
        public void setLast(boolean last) {
            this.last = last;
        }

        /**
         * This method is used by Jackson for serialization.
         * It ensures that the object is properly serialized to JSON.
         *
         * @return The metadata as a map
         */
        public java.util.Map<String, Object> getMetadata() {
            java.util.Map<String, Object> metadata = new java.util.HashMap<>();
            metadata.put("page", page);
            metadata.put("size", size);
            metadata.put("totalElements", totalElements);
            metadata.put("totalPages", totalPages);
            metadata.put("first", first);
            metadata.put("last", last);
            return metadata;
        }
    }
}