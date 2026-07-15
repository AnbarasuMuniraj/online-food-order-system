# REST API Documentation: Online Food Order System

This document outlines the REST APIs exposed by the backend services of the Online Food Order Processing System, primarily hosted by the `order-service` on port `8081` (proxied to port `3000` under `/api/*` in the web application).

---

## 1. base URL
- **Local Dev (Direct)**: `http://localhost:8081`
- **Local Dev (Vite Proxy)**: `http://localhost:3000` (All requests prefixed with `/api`)

---

## 2. API Endpoints

### 2.1 Create Order
Creates a new order, saves it in the database in the `PLACED` state, and initiates the asynchronous Camunda BPMN workflow by publishing an event to ActiveMQ.

* **Endpoint**: `POST /api/orders`
* **Content-Type**: `application/json`
* **Request Body Structure**:

```json
{
  "customerName": "string",
  "item": "string",
  "amount": number
}
```

* **Example Request Payload**:
```json
{
  "customerName": "John Doe",
  "item": "Double Cheeseburger & Fries",
  "amount": 18.99
}
```

* **Response Headers**:
  - `Content-Type: application/json`

* **Successful Response (Status: `201 Created`)**:
```json
{
  "id": 1,
  "customerName": "John Doe",
  "item": "Double Cheeseburger & Fries",
  "amount": 18.99,
  "status": "PLACED",
  "createdAt": "2026-07-11T23:23:48.232Z"
}
```

---

### 2.2 Get All Orders
Retrieves a list of all orders registered in the system database. Used by the dashboard to show the state of active and past orders.

* **Endpoint**: `GET /api/orders`
* **Response Headers**:
  - `Content-Type: application/json`

* **Successful Response (Status: `200 OK`)**:
```json
[
  {
    "id": 1,
    "customerName": "John Doe",
    "item": "Double Cheeseburger & Fries",
    "amount": 18.99,
    "status": "DELIVERED",
    "createdAt": "2026-07-11T23:23:48.232Z"
  },
  {
    "id": 2,
    "customerName": "Jane Smith",
    "item": "Veggie Pizza",
    "amount": 14.50,
    "status": "PAYMENT_COMPLETED",
    "createdAt": "2026-07-11T23:30:15.112Z"
  }
]
```

---

### 2.3 Get Order by ID
Retrieves details and state updates for a specific order.

* **Endpoint**: `GET /api/orders/{id}`
* **Path Parameters**:
  - `id` (Long, Required) - The unique ID of the order.

* **Response Headers**:
  - `Content-Type: application/json`

* **Successful Response (Status: `200 OK`)**:
```json
{
  "id": 2,
  "customerName": "Jane Smith",
  "item": "Veggie Pizza",
  "amount": 14.50,
  "status": "PAYMENT_COMPLETED",
  "createdAt": "2026-07-11T23:30:15.112Z"
}
```

* **Error Response (Status: `500 Internal Server Error` or `404 Not Found`)**:
```json
{
  "message": "Order not found with id: 99"
}
```
