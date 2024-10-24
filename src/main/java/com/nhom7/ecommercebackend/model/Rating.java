package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(RatingId.class)
public class Rating extends BaseEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    @Range(min = 1, max = 5, message = "Rating must be in range 1-5 star!")
    private int rate;

    private String comment;
}
