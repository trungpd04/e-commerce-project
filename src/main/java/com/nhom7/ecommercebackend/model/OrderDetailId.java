package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;


@Getter
@Setter
public class OrderDetailId {
    private Long product;
    private UUID order;
}
