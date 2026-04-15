package com.nhom7.ecommercebackend.repository.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private Map<String, String> attributeValueMap;
    private Boolean allProduct;
    // getAllActiveProductFilter(Filter filter, PageRequest pageRequest)
    // phải có equal / hashcode không thì mỗi lần filter khởi tạo sẽ tạo ra 1 object mới khiến cho spring cache
    // luôn luôn bị miss cache

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(attributeValueMap, filter.attributeValueMap) && Objects.equals(allProduct, filter.allProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeValueMap, allProduct);
    }
}
