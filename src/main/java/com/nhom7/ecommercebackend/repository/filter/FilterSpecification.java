package com.nhom7.ecommercebackend.repository.filter;

import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.ProductDetail;
import com.nhom7.ecommercebackend.model.Rating;
import com.nhom7.ecommercebackend.model.SubCategory;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FilterSpecification<Product> implements Specification<Product> {
    private final Filter filter;
    public FilterSpecification(Filter filter) {
        this.filter = filter;
    }
    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if(filter != null && filter.getSubcategoryId() != null ) {
            Join<Product, SubCategory> productSubCategoryJoin = root.join("subcategory", JoinType.INNER);
            Predicate subcategoryPredicate = criteriaBuilder.equal(
              productSubCategoryJoin.get("id"), filter.getSubcategoryId()
            );
            predicates.add(subcategoryPredicate);
        }
        if(filter != null && filter.getCategoryId() != null) {
            Join<Product, SubCategory> productSubCategoryJoin = root.join("subcategory", JoinType.INNER);
            Join<Product, Category> subCategoryCategoryJoin = productSubCategoryJoin.join("category", JoinType.INNER);
            Predicate subcategoryCategory = criteriaBuilder. equal(
                    subCategoryCategoryJoin.get("id"), filter.getCategoryId()
            );
            predicates.add(subcategoryCategory);
        }
        if(filter != null && filter.getKeyword() != null) {
            Join<Product, SubCategory> productSubCategoryJoin = root.join("subcategory", JoinType.INNER);
            Join<Product, Category> subCategoryCategoryJoin = productSubCategoryJoin.join("category", JoinType.INNER);
            String keyword = "%"+ filter.getKeyword().toLowerCase().trim() +"%";
            Predicate keywordFilter = criteriaBuilder.or(
                   criteriaBuilder.like(root.get("name"), keyword),
                    criteriaBuilder.like(productSubCategoryJoin.get("name"), keyword),
                    criteriaBuilder.like(subCategoryCategoryJoin.get("name"), keyword)
            );
            predicates.add(keywordFilter);
        }
        Join<Product, ProductDetail> productDetailJoin = root.join("productDetail", JoinType.INNER);
        if(filter != null && filter.getRam() != null) {
            String[] ramRange = filter.getRam().split("-");
            int ramMin = Integer.parseInt(ramRange[0]);
            int ramMax = Integer.parseInt(ramRange[1]);
            Predicate ramPredicate = criteriaBuilder.or(
                    criteriaBuilder.equal(productDetailJoin.get("ram"), ramMin),
                    criteriaBuilder.equal(productDetailJoin.get("ram"), ramMax),
                    criteriaBuilder.between(productDetailJoin.get("ram"), ramMin, ramMax)
            );
            predicates.add(criteriaBuilder.and(criteriaBuilder.conjunction(), ramPredicate));
        }
        if(filter != null && filter.getPrice() != null) {
            String[] priceRange = filter.getPrice().split("-");
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
        if(filter != null && filter.getStorage() != null) {
            String[] storageRange = filter.getStorage().split("-");
            int minStorage = Integer.parseInt(storageRange[0]);
            int maxStorage = Integer.parseInt(storageRange[1]);
            Predicate storagePredicate = criteriaBuilder
                    .or(
                            criteriaBuilder.equal(productDetailJoin.get("storage"), minStorage),
                            criteriaBuilder.equal(productDetailJoin.get("storage"), maxStorage),
                            criteriaBuilder.between(productDetailJoin.get("storage"), minStorage, maxStorage)
                    );
            predicates.add(storagePredicate);
        }
        if(filter != null && filter.getScreenSize() != null) {
            String[] screenFilter = filter.getScreenSize().split("-");
            String operator = screenFilter[0];
            float screenSize = Float.parseFloat(screenFilter[1]);
            if(operator.equals("over")) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(productDetailJoin.get("screenSize"), screenSize));
            }else{
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(productDetailJoin.get("screenSize"), screenSize));
            }
        }
        if(filter != null && filter.getScreenType() != null) {
            String screenType = filter.getScreenType();
            predicates.add(criteriaBuilder.equal(productDetailJoin.get("screenType"), screenType));
        }
        if(filter != null && filter.getScreenRefreshRate() != null) {
            int screenSize = Integer.parseInt(filter.getScreenRefreshRate());
            predicates.add(criteriaBuilder.equal(productDetailJoin.get("screenRefreshRate"), screenSize));
        }
        if(filter != null && filter.getRate() != null) {
            Join<Product, Rating> productRatingJoin = root.join("ratings", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(productRatingJoin.get("rate"), filter.getRate()));

        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
