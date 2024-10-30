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
public class Rating extends BaseEntity {

    @EmbeddedId
    private RatingId id = new RatingId();

    @Range(min = 1, max = 5, message = "Rating must be in range 1-5 star!")
    private int rate;

    private String comment;

    public Rating id(User user, Product product) {
        this.id.setUser(user);
        this.id.setProduct(product);
        return this;
    }
}
