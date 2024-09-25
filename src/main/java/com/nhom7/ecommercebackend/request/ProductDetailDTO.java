package com.nhom7.ecommercebackend.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Primary;

@Data
@Builder
public class ProductDetailDTO {

    private String cpu;

    private int ram;

    @JsonProperty("design_description")
    private String designDescription;

    @JsonProperty("os_name")
    private String osName;

    private String screen;

    @JsonProperty("battery_capacity")
    private int batteryCapacity;

    private String manufacturer;

    @JsonProperty("guarantee_month")
    private int guaranteeMonth;

    private String color;
    private int quantity;
}
