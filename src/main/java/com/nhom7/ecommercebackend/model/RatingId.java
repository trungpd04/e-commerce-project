package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RatingId implements Serializable {

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

}
