package com.nhom7.ecommercebackend;

import com.github.javafaker.Faker;
import com.nhom7.ecommercebackend.request.product.ProductAttributeValueDTO;
import com.nhom7.ecommercebackend.request.product.ProductDTO;
import com.nhom7.ecommercebackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FakeProductDataSeeder implements CommandLineRunner {

    private final ProductService productService;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    // Set to store unique product names
    private final Set<String> uniqueProductNames = new HashSet<>();

    // Categories: 1 - Smartphones, 2 - Laptops
    private final List<Long> categories = Arrays.asList(1L, 2L);

    // Subcategories for phones and laptops
    private final List<Long> subcategoriesPhones = Arrays.asList(1L, 2L); // iPhone and Samsung
    private final List<Long> subcategoriesLaptops = Arrays.asList(3L, 4L); // Lenovo and Asus

    // Smartphone models for iPhone and Samsung
    private final List<String> iphoneModels = Arrays.asList("iPhone 15 Pro Max", "iPhone 15", "iPhone 14", "iPhone 13", "iPhone SE", "iPhone 12 Mini", "iPhone 11 Pro", "iPhone XR", "iPhone X", "iPhone 8 Plus");
    private final List<String> samsungModels = Arrays.asList("Samsung Galaxy S22 Ultra", "Samsung Galaxy S21", "Samsung Galaxy Z Fold 3", "Samsung Galaxy A72", "Samsung Galaxy Note 20", "Samsung Galaxy S20", "Samsung Galaxy A52", "Samsung Galaxy S10", "Samsung Galaxy Z Flip", "Samsung Galaxy A50");

    // Laptop models for Lenovo and Asus
    private final List<String> lenovoModels = Arrays.asList("Lenovo ThinkPad X1 Carbon", "Lenovo Legion 5 Pro", "Lenovo Yoga 9i", "Lenovo IdeaPad 3", "Lenovo ThinkPad T14", "Lenovo Yoga Slim 7", "Lenovo Legion Y740", "Lenovo ThinkBook 15", "Lenovo IdeaPad Gaming 3", "Lenovo ThinkPad P1");
    private final List<String> asusModels  = Arrays.asList("Asus ROG Zephyrus G14", "Asus ZenBook Pro Duo", "Asus VivoBook S15", "Asus TUF Dash F15", "Asus ROG Strix G15", "Asus ZenBook 14", "Asus ExpertBook B9", "Asus ProArt StudioBook", "Asus VivoBook Flip 14", "Asus TUF Gaming A15");


    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 2000; i++) {
            ProductDTO productDTO = generateUniqueProduct();
            if (productDTO != null) {
                try {
                    productService.createProduct(productDTO);
                    System.out.println("Product " + (i + 1) + " created: " + productDTO.getName());
                } catch (Exception e) {
                    System.out.println("Failed to create product " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
    }

    private ProductDTO generateUniqueProduct() {
        Long categoryId;
        List<Long> subcategoryIds;
        String productName;

        if (random.nextBoolean()) {
            // Smartphone category
            categoryId = 1L;
            subcategoryIds = random.nextBoolean() ? Collections.singletonList(1L) : Collections.singletonList(2L);
            productName = getRandomUniqueModel(subcategoryIds.get(0) == 1L ? iphoneModels : samsungModels);
        } else {
            // Laptop category
            categoryId = 2L;
            subcategoryIds = random.nextBoolean() ? Collections.singletonList(3L) : Collections.singletonList(4L);
            productName = getRandomUniqueModel(subcategoryIds.get(0) == 3L ? lenovoModels : asusModels);
        }

        if (productName == null) return null;

        // Generate attributes based on category
        List<ProductAttributeValueDTO> attributeValues = new ArrayList<>();
        if (categoryId == 1L) {
            // Smartphone attributes
            attributeValues.add(createAttributeValue("mobile_ram", String.valueOf(random.nextInt(8))));
            attributeValues.add(createAttributeValue("mobile_storage", String.valueOf(random.nextInt(256))));
            attributeValues.add(createAttributeValue("mobile_screen_type", generateRandomScreenType()));
            attributeValues.add(createAttributeValue("mobile_screen_size", String.valueOf(generateRandomScreenSize())));
            attributeValues.add(createAttributeValue("mobile_screen_refresh_rate", String.valueOf(generateRandomScreenRefreshRate())));
            attributeValues.add(createAttributeValue("mobile_battery_capacity", String.valueOf(random.nextInt(4000, 5000))));
            attributeValues.add(createAttributeValue("mobile_color", faker.color().name()));
            attributeValues.add(createAttributeValue("mobile_design_description", faker.lorem().sentence()));
            attributeValues.add(createAttributeValue("mobile_os", generateRandomOs()));
            attributeValues.add(createAttributeValue("mobile_manufacturer", faker.company().name()));
            attributeValues.add(createAttributeValue("mobile_guarantee_month", String.valueOf(random.nextInt(12) + 1)));

        } else {
            // Laptop attributes
            attributeValues.add(createAttributeValue("laptop_ram", String.valueOf(random.nextInt(16))));
            attributeValues.add(createAttributeValue("laptop_storage", String.valueOf(random.nextInt(512))));
            attributeValues.add(createAttributeValue("laptop_cpu", String.valueOf(generateRandomCpu())));
            attributeValues.add(createAttributeValue("laptop_screen_size", String.valueOf(generateRandomScreenSize())));
            attributeValues.add(createAttributeValue("laptop_screen_refresh_rate", String.valueOf(generateRandomScreenRefreshRate())));
            attributeValues.add(createAttributeValue("laptop_battery_capacity", String.valueOf(random.nextInt(5000, 8000))));
            attributeValues.add(createAttributeValue("laptop_color", faker.color().name()));
            attributeValues.add(createAttributeValue("laptop_design_description", faker.lorem().sentence()));
            attributeValues.add(createAttributeValue("laptop_os", generateRandomOs()));
            attributeValues.add(createAttributeValue("laptop_manufacturer", faker.company().name()));
            attributeValues.add(createAttributeValue("laptop_guarantee_month", String.valueOf(random.nextInt(12) + 1)));
        }

        // Build the ProductDTO
        return ProductDTO.builder()
                .name(productName)
                .description(faker.lorem().sentence())
                .price(BigDecimal.valueOf(Float.parseFloat(faker.commerce().price(1000.0, 3000.0))))
                .quantity(random.nextLong(10))
                .thumbnail(null)
                .categoryId(categoryId)
                .subcategory(subcategoryIds) // Associate with the selected subcategories
                .attributeValues(attributeValues)
                .build();
    }

    private ProductAttributeValueDTO createAttributeValue(String attributeName, String value) {
        return ProductAttributeValueDTO.builder()
                .attributeName(attributeName)
                .value(value)
                .build();
    }

    // Helper methods
    private String getRandomUniqueModel(List<String> models) {
        List<String> availableModels = new ArrayList<>(models);

        Collections.shuffle(availableModels);

        for (String model : availableModels) {
            if (!uniqueProductNames.contains(model)) {
                uniqueProductNames.add(model);
                return model;
            }
        }
        return null;
    }

    private String generateRandomCpu() {
        List<String> cpus = Arrays.asList("Intel Core i7", "AMD Ryzen 7", "Apple M1", "Intel Core i5");
        return cpus.get(random.nextInt(cpus.size()));
    }

    private String generateRandomScreenType() {
        List<String> screenTypes = Arrays.asList("IPS", "OLED");
        return screenTypes.get(random.nextInt(screenTypes.size()));
    }

    private int generateRandomScreenRefreshRate() {
        List<Integer> refreshRates = Arrays.asList(60, 90, 120);
        return refreshRates.get(random.nextInt(refreshRates.size()));
    }

    private float generateRandomScreenSize() {
        return (float) (Math.round((5.0 + random.nextFloat() * 5.0) * 10.0) / 10.0); // Random size between 5.0 and 10.0 inches
    }

    private String generateRandomOs() {
        List<String> operatingSystems = Arrays.asList("IOS", "Android", "Windows 10", "Windows 11");
        return operatingSystems.get(random.nextInt(operatingSystems.size()));
    }

}
