# Orders Microservice

This microservice is responsible for managing orders in the e-commerce system. It provides APIs for creating, updating, retrieving, and deleting orders.

## Features

- Create new orders
- Update existing orders
- Retrieve orders by ID, customer ID, status, date range, or payment method
- Delete orders
- Update order status

## API Endpoints

### Orders

- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create a new order
- `PUT /api/orders/{id}` - Update an existing order
- `DELETE /api/orders/{id}` - Delete an order
- `GET /api/orders/customer/{customerId}` - Find orders by customer ID
- `GET /api/orders/status/{status}` - Find orders by status
- `GET /api/orders/date-range` - Find orders by date range
- `GET /api/orders/payment-method/{paymentMethod}` - Find orders by payment method
- `PATCH /api/orders/{id}/status/{status}` - Update order status

## Running the Microservice

### Prerequisites

- JDK 21 or later
- Maven 3.8.1 or later
- PostgreSQL database

### Configuration

The microservice can be configured using environment variables:

- `DB_USERNAME` - Database username (default: postgres)
- `DB_PASSWORD` - Database password (default: postgres)
- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name (default: orders)
- `HTTP_PORT` - HTTP port (default: 8088)
- `AUDIT_SERVICE_URL` - URL of the Audit service (default: http://localhost:8086)

### Running Locally

```bash
mvn quarkus:dev
```

### Building and Running in Production

```bash
mvn package
java -jar target/quarkus-app/quarkus-run.jar
```

## Integration with Other Microservices

This microservice integrates with:

- **Audit Service**: Logs all operations (create, read, update, delete) for auditing purposes.
- **Catalog Service**: Uses product information from the catalog service when creating orders.

## Data Model

### Order

- `id` - Order ID
- `customerId` - Customer ID
- `orderDate` - Date and time when the order was created
- `totalAmount` - Total amount of the order
- `status` - Order status (CREATED, PAYMENT_PENDING, PAID, SHIPPED, DELIVERED, CANCELLED, RETURNED, REFUNDED)
- `shippingAddress` - Shipping address
- `billingAddress` - Billing address
- `paymentMethod` - Payment method
- `paymentId` - Payment ID
- `items` - List of order items

### Order Item

- `id` - Order item ID
- `order` - Order to which this item belongs
- `productId` - Product ID
- `productName` - Product name
- `price` - Price of the product
- `quantity` - Quantity of the product