package com.nhom7.ecommercebackend.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductImage;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private float price;
    private String thumbnail;
    private String cpu;

    private int ram;

    @JsonProperty("design_description")
    private String designDescription;

    @JsonProperty("os_name")
    private String osName;

    @JsonProperty("screen_type")
    private String screenType;
    @JsonProperty("screen_refresh_rate")
    private int screenRefreshRate;
    @JsonProperty("screen_size")
    private float screenSize;

    @JsonProperty("battery_capacity")
    private int batteryCapacity;

    private String manufacturer;

    @JsonProperty("guarantee_month")
    private int guaranteeMonth;

    private String color;
    private int quantity;
    @JsonProperty("product_images")
    private List<ProductImage> productImages;

    public static ProductDetailResponse fromProduct(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .cpu(product.getProductDetail().getCpu())
                .ram(product.getProductDetail().getRam())
                .screenSize(product.getProductDetail().getScreenSize())
                .screenType(product.getProductDetail().getScreenType())
                .screenRefreshRate(product.getProductDetail().getScreenRefreshRate())
                .osName(product.getProductDetail().getOsName())
                .designDescription(product.getProductDetail().getDesignDescription())
                .batteryCapacity(product.getProductDetail().getBatteryCapacity())
                .guaranteeMonth(product.getProductDetail().getGuaranteeMonth())
                .color(product.getProductDetail().getColor())
                .quantity(product.getProductDetail().getQuantity())
                .manufacturer(product.getProductDetail().getManufacturer())
                .productImages(product.getProductImages())
                .build();
    }
}
