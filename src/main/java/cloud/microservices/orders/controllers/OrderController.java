package cloud.microservices.orders.controllers;

import cloud.microservices.orders.dtos.OrderCreateDTO;
import cloud.microservices.orders.dtos.OrderDTO;
import cloud.microservices.orders.dtos.OrderUpdateDTO;
import cloud.microservices.orders.entities.OrderStatus;
import cloud.microservices.orders.services.OrderService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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
     * Gets all orders.
     *
     * @return all orders
     */
    @GET
    @RolesAllowed({"admin", "client"})
    @Operation(summary = "Get all orders", description = "Returns all orders")
    @APIResponse(responseCode = "200", description = "List of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response getAllOrders() {
        logger.info("Getting all orders");

        try {
            List<OrderDTO> orders = orderService.getAllOrders();
            logger.info("Retrieved {} orders", orders.size());

            return Response.ok()
                    .entity(orders)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
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
    @RolesAllowed({"admin", "client"})
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
    @RolesAllowed({"admin", "client"})
    @Operation(summary = "Create a new order", description = "Creates a new order")
    @APIResponse(responseCode = "201", description = "Order created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "400", description = "Invalid input")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response createOrder(@Valid OrderCreateDTO orderCreateDTO) {
        logger.info("Creating new order for customer: {}", orderCreateDTO.getCustomerId());
        logger.debug("Order details: items count={}, shipping address={}",
                orderCreateDTO.getItems() != null ? orderCreateDTO.getItems().size() : 0,
                orderCreateDTO.getShippingAddress());

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
    @RolesAllowed({"admin", "client"})
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
        logger.debug("Update details: status={}, shipping address={}",
                orderUpdateDTO.getStatus(), orderUpdateDTO.getShippingAddress());

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
    @RolesAllowed("admin")
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
     * Find by customer id response.
     *
     * @param customerId the customer id
     * @return the response
     */
    @GET
    @Path("/customer/{customerId}")
    @RolesAllowed({"admin", "client"})
    @Operation(summary = "Find orders by customer ID", description = "Returns orders for the specified customer")
    @APIResponse(responseCode = "200", description = "List of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response findByCustomerId(@PathParam("customerId") String customerId) {
        logger.info("Finding orders for customer ID: {}", customerId);

        try {
            List<OrderDTO> orders = orderService.findByCustomerId(customerId);
            logger.info("Found {} orders for customer ID: {}", orders.size(), customerId);

            return Response.ok()
                    .entity(orders)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (Exception e) {
            logger.error("Error finding orders for customer ID: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Find by status response.
     *
     * @param status the status
     * @return the response
     */
    @GET
    @Path("/status/{status}")
    @RolesAllowed("admin")
    @Operation(summary = "Find orders by status", description = "Returns orders with the specified status")
    @APIResponse(responseCode = "200", description = "List of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response findByStatus(@PathParam("status") OrderStatus status) {
        logger.info("Finding orders with status: {}", status);

        try {
            List<OrderDTO> orders = orderService.findByStatus(status);
            logger.info("Found {} orders with status: {}", orders.size(), status);

            return Response.ok()
                    .entity(orders)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (Exception e) {
            logger.error("Error finding orders with status: {}", status, e);
            throw e;
        }
    }

    /**
     * Find by date range response.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the response
     */
    @GET
    @Path("/date-range")
    @RolesAllowed("admin")
    @Operation(summary = "Find orders by date range", description = "Returns orders created between the specified dates")
    @APIResponse(responseCode = "200", description = "List of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response findByDateRange(@QueryParam("startDate") LocalDateTime startDate,
                                    @QueryParam("endDate") LocalDateTime endDate) {
        logger.info("Finding orders between dates: {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            logger.warn("Missing required date parameters: startDate={}, endDate={}", startDate, endDate);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Both startDate and endDate parameters are required")
                    .build();
        }

        try {
            List<OrderDTO> orders = orderService.findByOrderDateBetween(startDate, endDate);
            logger.info("Found {} orders between {} and {}", orders.size(), startDate, endDate);

            return Response.ok()
                    .entity(orders)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (Exception e) {
            logger.error("Error finding orders between dates: {} and {}", startDate, endDate, e);
            throw e;
        }
    }

    /**
     * Find by payment method response.
     *
     * @param paymentMethod the payment method
     * @return the response
     */
    @GET
    @Path("/payment-method/{paymentMethod}")
    @RolesAllowed("admin")
    @Operation(summary = "Find orders by payment method", description = "Returns orders with the specified payment method")
    @APIResponse(responseCode = "200", description = "List of orders",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderDTO.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response findByPaymentMethod(@PathParam("paymentMethod") String paymentMethod) {
        logger.info("Finding orders with payment method: {}", paymentMethod);

        try {
            List<OrderDTO> orders = orderService.findByPaymentMethod(paymentMethod);
            logger.info("Found {} orders with payment method: {}", orders.size(), paymentMethod);

            return Response.ok()
                    .entity(orders)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .encoding("UTF-8")
                    .build();
        } catch (Exception e) {
            logger.error("Error finding orders with payment method: {}", paymentMethod, e);
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
    @RolesAllowed({"admin", "client"})
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
