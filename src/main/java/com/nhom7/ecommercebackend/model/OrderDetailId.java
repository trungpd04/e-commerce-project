package com.nhom7.ecommercebackend.model;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;


@Getter
@Setter
public class OrderDetailId {
    private Long product;
    private UUID order;
}
