package com.nhom7.ecommercebackend.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class OrderStatus {
    public static final String PENDING = "pending";
    public static final String PROCESSING = "processing";
    public static final String SHIPPED = "shipped";
    public static final String DELIVERED = "delivered";
    public static final String CANCELLED = "cancelled";
}

