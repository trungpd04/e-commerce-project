package com.nhom7.ecommercebackend;

import com.github.javafaker.Faker;
import com.nhom7.ecommercebackend.request.ProductDTO;
import com.nhom7.ecommercebackend.request.ProductDetailDTO;
import com.nhom7.ecommercebackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class FakeProductDataSeeder {

//    private final ProductService productService;
//    private final Faker faker = new Faker();
//    private final Random random = new Random();
//
//    // Categories: 1 - Điện thoại (Phones), 2 - Laptop (Laptops)
//    private final List<Long> categories = Arrays.asList(1L, 2L);
//
//    // Subcategories for phones and laptops
//    private final List<Long> subcategoriesDienThoaiApple = Arrays.asList(1L);  // 1 - iPhone
//    private final List<Long> subcategoriesDienThoaiSamsung = Arrays.asList(2L);  // 2 - Samsung
//    private final List<Long> subcategoriesLaptopLenovo = Arrays.asList(4L);  // 4 - Lenovo (Laptops)
//    private final List<Long> subcategoriesLaptopAsus = Arrays.asList(3L);  // 3 - Asus (Laptops)
//
//    // Predefined product names for each subcategory
//    private final Set<String> uniqueProductNames = new HashSet<>(); // To ensure unique product names
//
//    // Smartphone models for iPhone and Samsung
//    private final List<String> iphoneModels = Arrays.asList("iPhone 15 Pro Max", "iPhone 15", "iPhone 14", "iPhone 13", "iPhone SE", "iPhone 12 Mini", "iPhone 11 Pro", "iPhone XR", "iPhone X", "iPhone 8 Plus");
//    private final List<String> samsungModels = Arrays.asList("Samsung Galaxy S22 Ultra", "Samsung Galaxy S21", "Samsung Galaxy Z Fold 3", "Samsung Galaxy A72", "Samsung Galaxy Note 20", "Samsung Galaxy S20", "Samsung Galaxy A52", "Samsung Galaxy S10", "Samsung Galaxy Z Flip", "Samsung Galaxy A50");
//
//    // Laptop models for Lenovo and Asus
//    private final List<String> lenovoLaptopModels = Arrays.asList("Lenovo ThinkPad X1 Carbon", "Lenovo Legion 5 Pro", "Lenovo Yoga 9i", "Lenovo IdeaPad 3", "Lenovo ThinkPad T14", "Lenovo Yoga Slim 7", "Lenovo Legion Y740", "Lenovo ThinkBook 15", "Lenovo IdeaPad Gaming 3", "Lenovo ThinkPad P1");
//    private final List<String> asusLaptopModels = Arrays.asList("Asus ROG Zephyrus G14", "Asus ZenBook Pro Duo", "Asus VivoBook S15", "Asus TUF Dash F15", "Asus ROG Strix G15", "Asus ZenBook 14", "Asus ExpertBook B9", "Asus ProArt StudioBook", "Asus VivoBook Flip 14", "Asus TUF Gaming A15");
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Create 500 fake products with unique names
//        for (int i = 0; i < 500; i++) {
//            ProductDTO productDTO = generateUniqueProduct();
//
//            if (productDTO != null) {
//                try {
//                    productService.createProduct(productDTO);
//                    System.out.println("Product " + (i + 1) + " created successfully: " + productDTO.getName());
//                } catch (Exception e) {
//                    System.out.println("Failed to create product " + (i + 1) + ": " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    private ProductDTO generateUniqueProduct() {
//        Long categoryId;
//        List<Long> subcategoryIds;
//        String productName = "";
//
//        // Randomly select a category (Phone or Laptop)
//        if (random.nextBoolean()) {
//            categoryId = 1L; // Điện thoại (Smartphones)
//
//            // Randomly choose between iPhone and Samsung
//            if (random.nextBoolean()) {
//                subcategoryIds = subcategoriesDienThoaiApple;  // Subcategory: Apple (iPhone)
//                productName = getRandomUniqueModel(iphoneModels);
//            } else {
//                subcategoryIds = subcategoriesDienThoaiSamsung;  // Subcategory: Samsung
//                productName = getRandomUniqueModel(samsungModels);
//            }
//        } else {
//            categoryId = 2L; // Laptop
//
//            // Randomly choose between Lenovo and Asus (Laptop models)
//            if (random.nextBoolean()) {
//                subcategoryIds = subcategoriesLaptopLenovo;  // Subcategory: Lenovo (Laptops)
//                productName = getRandomUniqueModel(lenovoLaptopModels);
//            } else {
//                subcategoryIds = subcategoriesLaptopAsus;  // Subcategory: Asus (Laptops)
//                productName = getRandomUniqueModel(asusLaptopModels);
//            }
//        }
//
//        if (productName == null) return null; // If no unique name available, skip this product
//
//        // Build the ProductDetailDTO
//        ProductDetailDTO productDetailDTO = ProductDetailDTO.builder()
//                .cpu(generateRandomCpu())
//                .ram(random.nextInt(32) + 1) // Random RAM from 1 to 32 GB
//                .screenSize(generateRandomScreenSize())
//                .screenType(generateRandomScreenType())
//                .screenRefreshRate(generateRandomScreenRefreshRate())
//                .osName(generateRandomOs())
//                .batteryCapacity(random.nextInt(3000, 6000))
//                .manufacturer(faker.company().name())
//                .color(faker.color().name())
//                .quantity(random.nextInt(100) + 1) // Random quantity between 1 and 100
//                .designDescription(faker.lorem().sentence())
//                .guaranteeMonth(random.nextInt(12) + 1) // Random guarantee from 1 to 12 months
//                .build();
//
//        // Build the ProductDTO
//        return ProductDTO.builder()
//                .name(productName)
//                .description(faker.lorem().sentence())
//                .price(Float.parseFloat(faker.commerce().price(100.0, 2000.0)))
//                .thumbnail(null) // Assuming no thumbnail for fake products
//                .categoryId(categoryId)
//                .subcategory(subcategoryIds) // Add subcategories
//                .productDetailDTO(productDetailDTO)
//                .build();
//    }
//
//    // Helper methods for generating random product names
//    private String getRandomUniqueModel(List<String> models) {
//        List<String> availableModels = new ArrayList<>(models);
//        Collections.shuffle(availableModels);  // Shuffle the list for randomness
//
//        for (String model : availableModels) {
//            if (!uniqueProductNames.contains(model)) {
//                uniqueProductNames.add(model); // Mark as used
//                return model;
//            }
//        }
//        return null; // No unique names left in this category
//    }
//
//    // Helper methods for generating random CPU and OS
//    private String generateRandomCpu() {
//        List<String> cpus = Arrays.asList("Qualcomm Snapdragon 888", "Exynos 2100", "Apple A14 Bionic", "Intel Core i7", "AMD Ryzen 7");
//        return cpus.get(random.nextInt(cpus.size()));
//    }
//
//    private String generateRandomOs() {
//        List<String> operatingSystems = Arrays.asList("IOS", "Android", "Windows 10", "Windows 11");
//        return operatingSystems.get(random.nextInt(operatingSystems.size()));
//    }
//    private String generateRandomScreenType() {
//        List<String> screenTypes = Arrays.asList("IPS", "OLED");
//        return screenTypes.get(random.nextInt(screenTypes.size())); // Randomly select IPS or OLED
//    }
//    private int generateRandomScreenRefreshRate() {
//        List<Integer> refreshRates = Arrays.asList(60, 90, 120);
//        return refreshRates.get(random.nextInt(refreshRates.size())); // Randomly select 60Hz, 90Hz, or 120Hz
//    }
//    private float generateRandomScreenSize() {
//        float minSize = 5.5f;
//        float maxSize = 7.5f;
//        float randomSize = minSize + random.nextFloat() * (maxSize - minSize);
//        return Math.round(randomSize * 10) / 10.0f;  // Round to one decimal place
//    }


}

