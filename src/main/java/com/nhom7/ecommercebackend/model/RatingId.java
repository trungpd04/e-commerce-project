package com.nhom7.ecommercebackend.model;

import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@IdClass(RatingId.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingId implements Serializable {
    private User user;
    private Product product;
}
