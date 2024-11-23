package com.nhom7.ecommercebackend;

import com.github.javafaker.Faker;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductAttribute;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.repository.SubCategoryRepository;
import com.nhom7.ecommercebackend.request.category.CategoryDTO;
import com.nhom7.ecommercebackend.request.category.SubCategoryDTO;
import com.nhom7.ecommercebackend.request.product.AttributeDTO;
import com.nhom7.ecommercebackend.request.product.ProductAttributeValueDTO;
import com.nhom7.ecommercebackend.request.product.ProductDTO;
import com.nhom7.ecommercebackend.service.CategoryService;
import com.nhom7.ecommercebackend.service.ProductAttributeService;
import com.nhom7.ecommercebackend.service.ProductService;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FakeProductDataSeeder implements CommandLineRunner {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;
    private final ProductAttributeService productAttributeService;
    private final List<String> categoryName = Arrays.asList("Điện thoại", "Laptop");
    private final List<String> subCategoryName = Arrays.asList("Iphone", "Samsung", "Lenovo", "Asus");
    private final List<String> attributes = Arrays.asList(
            "mobile_ram",
            "mobile_storage",
            "mobile_cpu",
            "mobile_screen_type",
            "mobile_screen_size",
            "mobile_screen_refresh_rate",
            "mobile_battery_capacity",
            "mobile_color",
            "mobile_design_description",
            "mobile_os",
            "mobile_manufacturer",
            "mobile_guarantee_month",
            "laptop_ram",
            "laptop_storage",
            "laptop_cpu",
            "laptop_screen_size",
            "laptop_screen_refresh_rate",
            "laptop_battery_capacity",
            "laptop_color",
            "laptop_design_description",
            "laptop_os",
            "laptop_manufacturer",
            "laptop_guarantee_month",
            "mobile_hot",
            "laptop_hot"
    );
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
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;


    @Override
    public void run(String... args) throws Exception {
        for(String categoryName : this.categoryName) {
            try{
                Category category = categoryService.creatCategory(
                        CategoryDTO.builder()
                                .name(categoryName)
                                .build()
                );
                System.out.println("Category created: " + categoryName);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        for(String subcategoryName : this.subCategoryName) {
            try{
                if(subcategoryName == "Lenovo" || subcategoryName == "Asus"){
                    Category category = categoryRepository.findById(2L).get();
                    List<SubCategory> subCategories = new ArrayList<>();
                    SubCategory subCategory = null;
                    try{
                        subCategory = subCategoryService.createSubCategory(SubCategoryDTO.builder().subCategoryName(subcategoryName).build());
                        subCategory.setCategory(category);
                    }catch (Exception e) {
                        System.out.println("Subcategory created: " + subcategoryName);
                    }
                    subCategories.add(subCategory);
                    category.setSubCategoryList(subCategories);
                    categoryRepository.save(category);
                }else{
                    Category category = categoryRepository.findById(1L).get();
                    List<SubCategory> subCategories = new ArrayList<>();
                    SubCategory subCategory = null;
                    try{
                        subCategory = subCategoryService.createSubCategory(SubCategoryDTO.builder().subCategoryName(subcategoryName).build());
                        subCategory.setCategory(category);
                    }catch (Exception e) {
                        System.out.println("Subcategory created: " + subcategoryName);
                    }
                    subCategories.add(subCategory);
                    category.setSubCategoryList(subCategories);
                    categoryRepository.save(category);
                }
                System.out.println("Category created: " + subcategoryName);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        for(String name : this.attributes) {
            try{
                ProductAttribute productAttribute = productAttributeService
                        .createProductAttribute(AttributeDTO.builder()
                                .name(name)
                                .build());
                System.out.println("Product Attribute created: " + name);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        for (int i = 0; i < 2000; i++) {
            ProductDTO productDTO = generateUniqueProduct();
            if (productDTO != null) {
                try {
                    Product product = productService.createProduct(productDTO);

                    System.out.println("Product " + (i + 1) + " created: " + productDTO.getName());
                } catch (Exception e) {

                    System.out.println("Failed to create product " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
    }

    public List<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
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
            attributeValues.add(createAttributeValue("mobile_cpu", generateRandomCpu()));
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
                .price(BigDecimal.valueOf(Float.parseFloat(faker.commerce().price(1000000, 4000000))))
                .quantity(random.nextLong(10))
                .thumbnail(null)
                .isHot(getRandomBoolean())
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
    public boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

}
