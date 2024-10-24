package com.nhom7.ecommercebackend.exception;

import lombok.Getter;

@Getter
public enum MessageKeys {
    CART_EMPTY("Empty Cart!"),
    CART_ITEM_NOT_FOUND("Cart Item does not exist!"),
    PRODUCT_NOT_FOUND("Product does not exist!"),
    TOKEN_EXPIRED("Token has expired!"),
    INVALID_TOKEN("Invalid token!"),
    USER_NOT_EXIST("User does not exist!"),
    UNAUTHENTICATED("Unauthenticated!"),
    UNAUTHORIZED("You does not have permission to access this resource!"),
    USER_ORDER_NOT_EXIST("Your order does not exist!"),
    ORDER_NOT_FOUND("No order exist!"),
    PASSWORD_EXISTED("Password has been created!"),
    PASSWORD_NOT_MATCH("Password does not match retype password!");
    private final String message;
    MessageKeys(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
