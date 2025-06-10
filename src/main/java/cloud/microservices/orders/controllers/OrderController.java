package cloud.microservices.orders.controllers;

import cloud.microservices.orders.dtos.OrderCreateDTO;
import cloud.microservices.orders.dtos.OrderDTO;
import cloud.microservices.orders.dtos.OrderUpdateDTO;
import cloud.microservices.orders.entities.OrderStatus;
import cloud.microservices.orders.services.OrderService;
import jakarta.annotation.security.PermitAll;
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
        List<OrderDTO> orders = orderService.getAllOrders();
        return Response.ok(orders).build();
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
        OrderDTO order = orderService.getOrderById(id);
        return Response.ok(order).build();
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
        OrderDTO createdOrder = orderService.createOrder(orderCreateDTO);
        return Response.created(URI.create("/api/orders/" + createdOrder.getId()))
                .entity(createdOrder)
                .build();
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
        OrderDTO updatedOrder = orderService.updateOrder(id, orderUpdateDTO);
        return Response.ok(updatedOrder).build();
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
        orderService.deleteOrder(id);
        return Response.noContent().build();
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
        List<OrderDTO> orders = orderService.findByCustomerId(customerId);
        return Response.ok(orders).build();
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
        List<OrderDTO> orders = orderService.findByStatus(status);
        return Response.ok(orders).build();
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
        List<OrderDTO> orders = orderService.findByOrderDateBetween(startDate, endDate);
        return Response.ok(orders).build();
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
        List<OrderDTO> orders = orderService.findByPaymentMethod(paymentMethod);
        return Response.ok(orders).build();
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
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return Response.ok(updatedOrder).build();
    }
}
