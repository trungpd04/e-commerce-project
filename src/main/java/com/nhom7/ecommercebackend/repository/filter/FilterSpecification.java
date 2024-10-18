package com.nhom7.ecommercebackend.repository.filter;

import com.nhom7.ecommercebackend.model.*;
import jakarta.persistence.criteria.*;
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
        if (!filter.getAttributeValueMap().isEmpty()) {
            Join<Product, SubCategory> productSubCategoryJoin = root.join("subcategory", JoinType.INNER);
            Join<Product, Category> productCategoryJoin = productSubCategoryJoin.join("category", JoinType.INNER);
            for (Map.Entry<String, String> attributeValue : filter.getAttributeValueMap().entrySet()) {
                if(attributeValue.getKey().equals("page")
                        || attributeValue.getKey().equals("size")
                        || attributeValue.getKey().equals("sort_by")
                        || attributeValue.getKey().equals("sort_dir")
                ) {
                    continue;
                }
                if (attributeValue.getKey().equals("search") && !attributeValue.getValue().contains("-")) {
                    String keyword = "%" + attributeValue.getValue().toLowerCase().trim() + "%";
                    Predicate keywordFilter = criteriaBuilder.or(
                            criteriaBuilder.like(root.get("name"), keyword),
                            criteriaBuilder.like(productSubCategoryJoin.get("name"), keyword),
                            criteriaBuilder.like(productCategoryJoin.get("name"), keyword)
                    );
                    predicates.add(keywordFilter);
                }
                if (attributeValue.getKey().equals("subcategory_id") && !attributeValue.getValue().contains("-")) {
                    Predicate subcategoryPredicate = criteriaBuilder.equal(
                            productSubCategoryJoin.get("id"), Long.parseLong(attributeValue.getValue())
                    );
                    predicates.add(subcategoryPredicate);
                }
                if (attributeValue.getKey().equals("category_id") && !attributeValue.getValue().contains("-")) {
                    Predicate subcategoryCategory = criteriaBuilder.equal(
                            productCategoryJoin.get("id"), Long.parseLong(attributeValue.getValue())
                    );
                    predicates.add(subcategoryCategory);
                }
                if(attributeValue.getKey().equals("price") && attributeValue.getValue().contains("-")) {
                    String[] priceRange = attributeValue.getValue().split("-");
                    float minPrice = Float.parseFloat(priceRange[0]);
                    float maxPrice = Float.parseFloat(priceRange[1]);
                    Predicate pricePredicate = criteriaBuilder
                            .or(
                                    criteriaBuilder.equal(root.get("price"), minPrice),
                                    criteriaBuilder.equal(root.get("price"), maxPrice),
                                    criteriaBuilder.between(root.get("price"), minPrice, maxPrice)
                            );
                    predicates.add(pricePredicate);
                }
                if (attributeValue.getKey() != null && !attributeValue.getKey().equals("price")) {
                    if (attributeValue.getValue().contains("-")) {
                        String[] range = attributeValue.getValue().split("-");
                        int min = Integer.parseInt(range[0]);
                        int max = Integer.parseInt(range[1]);
                        Join<Product, ProductAttributeValue> productAttributeValueJoin = root.join("attributeValues", JoinType.LEFT);
                        Join<ProductAttributeValue, ProductAttribute> productAttributeValueProductAttributeJoin = productAttributeValueJoin.join("productAttribute", JoinType.LEFT);

                        String attributeName = attributeValue.getKey();
                        Predicate attributeNamePredicate = criteriaBuilder.equal(
                                productAttributeValueProductAttributeJoin.get("name"), attributeName
                        );
                        Predicate attributeRangePredicate = criteriaBuilder.or(
                                criteriaBuilder.equal(productAttributeValueJoin.get("value").as(Integer.class), min),
                                criteriaBuilder.equal(productAttributeValueJoin.get("value").as(Integer.class), min),
                                criteriaBuilder.between(productAttributeValueJoin.get("value").as(Integer.class), min, max)
                        );
                        Predicate attributeRangePredicateAnd = criteriaBuilder.and(
                                attributeNamePredicate,
                                attributeRangePredicate
                        );
                        predicates.add(attributeRangePredicateAnd);
                    } else {
                        if (!attributeValue.getKey().equals("search")
                                && !attributeValue.getKey().equals("subcategory_id")
                                && !attributeValue.getKey().equals("category_id")
                                && !attributeValue.getValue().contains("-")
                        ) {
                            Join<Product, ProductAttributeValue> productAttributeValueJoin = root.join("attributeValues", JoinType.LEFT);
                            Join<ProductAttributeValue, ProductAttribute> productAttributeValueProductAttributeJoin = productAttributeValueJoin.join("productAttribute", JoinType.LEFT);

                            String attributeName = attributeValue.getKey();
                            Predicate attributeNamePredicate = criteriaBuilder.equal(
                                    productAttributeValueProductAttributeJoin.get("name"), attributeName
                            );
                            Predicate attributeRangePredicate = criteriaBuilder.and(
                                    attributeNamePredicate,
                                    criteriaBuilder.equal(productAttributeValueJoin.get("value"), attributeValue.getValue())
                            );
                            predicates.add(attributeRangePredicate);
                        }
                    }
                }
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}





