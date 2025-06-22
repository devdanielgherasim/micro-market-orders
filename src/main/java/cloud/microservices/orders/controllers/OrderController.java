package cloud.microservices.orders.controllers;

import cloud.microservices.orders.dtos.OrderCreateDTO;
import cloud.microservices.orders.dtos.OrderDTO;
import cloud.microservices.orders.dtos.OrderUpdateDTO;
import cloud.microservices.orders.entities.OrderStatus;
import cloud.microservices.orders.services.OrderService;
import cloud.microservices.orders.utils.PaginationUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for Order entity.
 */
@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Order", description = "Order operations")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * The Order service.
     */
    @Inject
    OrderService orderService;

    /**
     * Gets all orders with pagination support.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param uriInfo the URI info for building pagination links
     * @return the paginated orders
     */
    @GET
    @Operation(summary = "Get all orders", description = "Returns all orders with pagination support")
    @APIResponse(responseCode = "200", description = "Paginated list of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response getAllOrders(
            @Parameter(description = "Page number (0-based)") @QueryParam("page") Integer page,
            @Parameter(description = "Page size") @QueryParam("size") Integer size,
            @Context UriInfo uriInfo) {
        logger.info("Getting all orders with pagination - page: {}, size: {}", page, size);

        try {
            int[] pageParams = PaginationUtil.validatePaginationParams(page, size);
            int validPage = pageParams[0];
            int validSize = pageParams[1];

            List<OrderDTO> orders = orderService.getAllOrdersPaginated(validPage, validSize);
            long totalCount = orderService.countAllOrders();

            logger.info("Retrieved {} orders out of {} total", orders.size(), totalCount);

            return PaginationUtil.createPaginatedResponse(orders, totalCount, validPage, validSize, uriInfo);
        } catch (Exception e) {
            logger.error("Error retrieving all orders", e);
            throw e;
        }
    }

    /**
     * Gets order by id.
     *
     * @param id the id
     * @return the order by id
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns an order by its ID")
    @APIResponse(responseCode = "200", description = "The order",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response getOrderById(@PathParam("id") Long id) {
        logger.info("Getting order by ID: {}", id);

        try {
            OrderDTO order = orderService.getOrderById(id);
            logger.info("Retrieved order: {}, customer: {}", id, order.getCustomerId());

            return Response.ok()
                    .entity(order)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (NotFoundException e) {
            logger.warn("Order not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving order with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Create order response.
     *
     * @param orderCreateDTO the order create dto
     * @return the response
     */
    @POST
    @Operation(summary = "Create a new order", description = "Creates a new order")
    @APIResponse(responseCode = "201", description = "Order created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "400", description = "Invalid input")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response createOrder(@Valid OrderCreateDTO orderCreateDTO) {
        logger.info("Creating new order for customer: {}", orderCreateDTO.getCustomerId());
        logger.debug("Order details: items count={}",
                orderCreateDTO.getItems() != null ? orderCreateDTO.getItems().size() : 0);

        try {
            OrderDTO createdOrder = orderService.createOrder(orderCreateDTO);
            logger.info("Order created successfully with ID: {}", createdOrder.getId());

            return Response.created(URI.create("/api/orders/" + createdOrder.getId()))
                    .entity(createdOrder)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (Exception e) {
            logger.error("Error creating order for customer: {}", orderCreateDTO.getCustomerId(), e);
            throw e;
        }
    }

    /**
     * Update order response.
     *
     * @param id             the id
     * @param orderUpdateDTO the order update dto
     * @return the response
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an order", description = "Updates an existing order")
    @APIResponse(responseCode = "200", description = "Order updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "400", description = "Invalid input")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response updateOrder(@PathParam("id") Long id, @Valid OrderUpdateDTO orderUpdateDTO) {
        logger.info("Updating order with ID: {}", id);
        logger.debug("Update details: status={}", orderUpdateDTO.getStatus());

        try {
            OrderDTO updatedOrder = orderService.updateOrder(id, orderUpdateDTO);
            logger.info("Order updated successfully: ID={}, status={}", id, updatedOrder.getStatus());

            return Response.ok()
                    .entity(updatedOrder)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (NotFoundException e) {
            logger.warn("Order not found for update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating order with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Delete order response.
     *
     * @param id the id
     * @return the response
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an order", description = "Deletes an order")
    @APIResponse(responseCode = "204", description = "Order deleted")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response deleteOrder(@PathParam("id") Long id) {
        logger.info("Deleting order with ID: {}", id);

        try {
            orderService.deleteOrder(id);
            logger.info("Order deleted successfully: ID={}", id);

            return Response.noContent().build();
        } catch (NotFoundException e) {
            logger.warn("Order not found for deletion with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting order with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Find orders by customer ID with pagination support.
     *
     * @param customerId the customer ID
     * @param page the page number (0-based)
     * @param size the page size
     * @param uriInfo the URI info for building pagination links
     * @return the paginated orders
     */
    @GET
    @Path("/customer/{customerId}")
    @Operation(summary = "Find orders by customer ID", description = "Returns orders for the specified customer with pagination support")
    @APIResponse(responseCode = "200", description = "Paginated list of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response findByCustomerId(
            @PathParam("customerId") String customerId,
            @Parameter(description = "Page number (0-based)") @QueryParam("page") Integer page,
            @Parameter(description = "Page size") @QueryParam("size") Integer size,
            @Context UriInfo uriInfo) {
        logger.info("Finding orders for customer ID: {} with pagination - page: {}, size: {}", customerId, page, size);

        try {
            int[] pageParams = PaginationUtil.validatePaginationParams(page, size);
            int validPage = pageParams[0];
            int validSize = pageParams[1];

            List<OrderDTO> orders = orderService.findByCustomerIdPaginated(customerId, validPage, validSize);
            long totalCount = orderService.countByCustomerId(customerId);

            logger.info("Found {} orders for customer ID: {} out of {} total", orders.size(), customerId, totalCount);

            return PaginationUtil.createPaginatedResponse(orders, totalCount, validPage, validSize, uriInfo);
        } catch (Exception e) {
            logger.error("Error finding orders for customer ID: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Find orders by status with pagination support.
     *
     * @param status the status
     * @param page the page number (0-based)
     * @param size the page size
     * @param uriInfo the URI info for building pagination links
     * @return the paginated orders
     */
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Find orders by status", description = "Returns orders with the specified status with pagination support")
    @APIResponse(responseCode = "200", description = "Paginated list of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response findByStatus(
            @PathParam("status") OrderStatus status,
            @Parameter(description = "Page number (0-based)") @QueryParam("page") Integer page,
            @Parameter(description = "Page size") @QueryParam("size") Integer size,
            @Context UriInfo uriInfo) {
        logger.info("Finding orders with status: {} with pagination - page: {}, size: {}", status, page, size);

        try {
            int[] pageParams = PaginationUtil.validatePaginationParams(page, size);
            int validPage = pageParams[0];
            int validSize = pageParams[1];

            List<OrderDTO> orders = orderService.findByStatusPaginated(status, validPage, validSize);
            long totalCount = orderService.countByStatus(status);

            logger.info("Found {} orders with status: {} out of {} total", orders.size(), status, totalCount);

            return PaginationUtil.createPaginatedResponse(orders, totalCount, validPage, validSize, uriInfo);
        } catch (Exception e) {
            logger.error("Error finding orders with status: {}", status, e);
            throw e;
        }
    }

    /**
     * Find orders by date range with pagination support.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param page the page number (0-based)
     * @param size the page size
     * @param uriInfo the URI info for building pagination links
     * @return the paginated orders
     */
    @GET
    @Path("/date-range")
    @Operation(summary = "Find orders by date range", description = "Returns orders created between the specified dates with pagination support")
    @APIResponse(responseCode = "200", description = "Paginated list of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response findByDateRange(
            @QueryParam("startDate") LocalDateTime startDate,
            @QueryParam("endDate") LocalDateTime endDate,
            @Parameter(description = "Page number (0-based)") @QueryParam("page") Integer page,
            @Parameter(description = "Page size") @QueryParam("size") Integer size,
            @Context UriInfo uriInfo) {
        logger.info("Finding orders between dates: {} and {} with pagination - page: {}, size: {}", 
                startDate, endDate, page, size);

        if (startDate == null || endDate == null) {
            logger.warn("Missing required date parameters: startDate={}, endDate={}", startDate, endDate);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Both startDate and endDate parameters are required")
                    .build();
        }

        try {
            int[] pageParams = PaginationUtil.validatePaginationParams(page, size);
            int validPage = pageParams[0];
            int validSize = pageParams[1];

            List<OrderDTO> orders = orderService.findByOrderDateBetweenPaginated(startDate, endDate, validPage, validSize);
            long totalCount = orderService.countByOrderDateBetween(startDate, endDate);

            logger.info("Found {} orders between {} and {} out of {} total", 
                    orders.size(), startDate, endDate, totalCount);

            return PaginationUtil.createPaginatedResponse(orders, totalCount, validPage, validSize, uriInfo);
        } catch (Exception e) {
            logger.error("Error finding orders between dates: {} and {}", startDate, endDate, e);
            throw e;
        }
    }

    /**
     * Update order status response.
     *
     * @param id     the id
     * @param status the status
     * @return the response
     */
    @PATCH
    @Path("/{id}/status/{status}")
    @Operation(summary = "Update order status", description = "Updates the status of an order")
    @APIResponse(responseCode = "200", description = "Order status updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response updateOrderStatus(@PathParam("id") Long id, @PathParam("status") OrderStatus status) {
        logger.info("Updating status of order ID: {} to {}", id, status);

        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
            logger.info("Order status updated successfully: ID={}, new status={}", id, status);

            return Response.ok()
                    .entity(updatedOrder)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (NotFoundException e) {
            logger.warn("Order not found for status update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating status of order with ID: {} to {}", id, status, e);
            throw e;
        }
    }
}
