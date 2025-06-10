package cloud.microservices.orders.utils;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

/**
 * Utility class for handling pagination in REST APIs.
 * Provides methods for creating paginated responses with appropriate headers.
 */
public class PaginationUtil {

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_LINK = "Link";
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Validates and normalizes pagination parameters.
     * 
     * @param page The requested page number (0-based)
     * @param size The requested page size
     * @return An array containing the normalized [page, size]
     */
    public static int[] validatePaginationParams(Integer page, Integer size) {
        int validPage = (page != null && page >= 0) ? page : 0;
        int validSize = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        
        return new int[] { validPage, validSize };
    }

    /**
     * Creates a paginated response with appropriate headers.
     * 
     * @param items The list of items for the current page
     * @param totalCount The total count of items across all pages
     * @param page The current page number (0-based)
     * @param size The page size
     * @param uriInfo The URI info for building pagination links
     * @param <T> The type of items in the list
     * @return A Response object with pagination headers
     */
    public static <T> Response createPaginatedResponse(
            List<T> items, 
            long totalCount, 
            int page, 
            int size, 
            UriInfo uriInfo) {
        
        Response.ResponseBuilder responseBuilder = Response.ok(items);
        
        // Add X-Total-Count header
        responseBuilder.header(HEADER_X_TOTAL_COUNT, totalCount);
        
        // Add Link header with prev, next, first, last links
        String baseUrl = uriInfo.getRequestUriBuilder().replaceQueryParam("page", "").replaceQueryParam("size", "").build().toString();
        int lastPage = (int) Math.ceil((double) totalCount / size) - 1;
        
        StringBuilder linkHeader = new StringBuilder();
        
        // First page link
        linkHeader.append(String.format("<%s?page=0&size=%d>; rel=\"first\"", baseUrl, size));
        
        // Previous page link (if not on first page)
        if (page > 0) {
            linkHeader.append(String.format(", <%s?page=%d&size=%d>; rel=\"prev\"", baseUrl, page - 1, size));
        }
        
        // Next page link (if not on last page)
        if (page < lastPage) {
            linkHeader.append(String.format(", <%s?page=%d&size=%d>; rel=\"next\"", baseUrl, page + 1, size));
        }
        
        // Last page link
        linkHeader.append(String.format(", <%s?page=%d&size=%d>; rel=\"last\"", baseUrl, lastPage, size));
        
        responseBuilder.header(HEADER_LINK, linkHeader.toString());
        
        return responseBuilder.build();
    }
}