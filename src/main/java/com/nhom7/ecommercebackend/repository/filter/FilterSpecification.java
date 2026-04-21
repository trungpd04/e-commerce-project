package com.nhom7.ecommercebackend.repository.filter;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.ProductAttribute;
import com.nhom7.ecommercebackend.model.ProductAttributeValue;
import com.nhom7.ecommercebackend.model.Rating;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterSpecification<Product> implements Specification<Product> {
    private final Filter filter;

    public FilterSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();


        Join<Product, Category> productCategoryJoin = null;
        Join<Product, ProductAttributeValue> productAttributeValueJoin = null;
        Join<ProductAttributeValue, ProductAttribute> productAttributeValueProductAttributeJoin = null;


        if (filter.getAttributeValueMap().containsKey("search")) {
            String keyword = "%" + filter.getAttributeValueMap().get("search").toLowerCase().trim() + "%";

            Predicate keywordFilter = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), keyword),
                    criteriaBuilder.like(root.get("description"), keyword),
                    criteriaBuilder.like(productCategoryJoin.get("name"), keyword)
            );
            predicates.add(keywordFilter);
        }

        for (Map.Entry<String, String> attributeValue : filter.getAttributeValueMap().entrySet()) {
            if (attributeValue.getKey().equals("page")
                    || attributeValue.getKey().equals("size")
                    || attributeValue.getKey().equals("sort_by")
                    || attributeValue.getKey().equals("sort_dir")
                    || attributeValue.getKey().equals("search")
                    || attributeValue.getKey().equals("rating")) {
                continue;
            }

            if (attributeValue.getKey().equals("is_hot") && !attributeValue.getValue().contains("-")) {
                boolean isHot = attributeValue.getValue().equalsIgnoreCase("true");
                Predicate hotPredicate = criteriaBuilder.equal(root.get("isHot"), isHot);
                predicates.add(hotPredicate);
            }

            if (attributeValue.getKey().equals("category_id") && !attributeValue.getValue().contains("-")) {

                if (productCategoryJoin == null) {
                    productCategoryJoin = root.join("categories", JoinType.LEFT);
                }

                Predicate categoryPredicate = criteriaBuilder.equal(
                        productCategoryJoin.get("id"), Long.parseLong(attributeValue.getValue())
                );

                Predicate categoryActive = null;

                if(!filter.getAllProduct()) {
                    categoryActive = criteriaBuilder.equal(
                            productCategoryJoin.get("active"), true
                    );
                    predicates.add(criteriaBuilder.and(categoryPredicate, categoryActive));
                } else {
                    predicates.add(categoryPredicate);
                }

            }

            if (attributeValue.getKey().equals("price") && attributeValue.getValue().contains("-")) {
                String[] priceRange = attributeValue.getValue().split("-");
                float minPrice = Float.parseFloat(priceRange[0]);
                float maxPrice = Float.parseFloat(priceRange[1]);
                Predicate pricePredicate = criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
                predicates.add(pricePredicate);
            }

            // For attribute-based filtering, apply conditional join only if needed
            if (!attributeValue.getKey().equals("is_hot")
                    && !attributeValue.getKey().equals("category_id")
                    && !attributeValue.getKey().equals("price")) {

                if (productAttributeValueJoin == null) {
                    productAttributeValueJoin = root.join("attributeValues", JoinType.LEFT);
                    productAttributeValueProductAttributeJoin = productAttributeValueJoin.join("productAttribute", JoinType.LEFT);
                }

                String attributeName = attributeValue.getKey();
                Predicate attributeNamePredicate = criteriaBuilder.equal(
                        productAttributeValueProductAttributeJoin.get("name"), attributeName
                );
                Predicate activeAttribute = criteriaBuilder.equal(
                        productAttributeValueProductAttributeJoin.get("active"), true
                );

                if (attributeValue.getValue().contains("-")) {
                    String[] range = attributeValue.getValue().split("-");
                    int min = Integer.parseInt(range[0]);
                    int max = Integer.parseInt(range[1]);
                    Predicate attributeRangePredicate = criteriaBuilder.between(
                            productAttributeValueJoin.get("value").as(Integer.class), min, max
                    );
                    predicates.add(criteriaBuilder.and(attributeNamePredicate, attributeRangePredicate));
                } else {
                    Predicate attributeValuePredicate = criteriaBuilder.equal(
                            productAttributeValueJoin.get("value"), attributeValue.getValue()
                    );
                    predicates.add(criteriaBuilder.and(attributeNamePredicate, attributeValuePredicate));
                }
                predicates.add(activeAttribute);
            }
        }

        if ("rating".equals(filter.getAttributeValueMap().get("sort_by"))) {

            Subquery<Double> avgRatingSubquery = query.subquery(Double.class);
            Root<Rating> ratingRoot = avgRatingSubquery.from(Rating.class);

            avgRatingSubquery.select(criteriaBuilder.avg(ratingRoot.get("rate")));
            avgRatingSubquery.where(criteriaBuilder.equal(ratingRoot.get("id").get("product").get("id"), root.get("id")));

            String sortDir = filter.getAttributeValueMap().getOrDefault("sort_dir", "asc");
            if ("asc".equalsIgnoreCase(sortDir)) {
                query.orderBy(criteriaBuilder.asc(avgRatingSubquery), criteriaBuilder.asc(root.get("id")));
            } else {
                query.orderBy(criteriaBuilder.desc(avgRatingSubquery),  criteriaBuilder.asc(root.get("id")));
            }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

}
