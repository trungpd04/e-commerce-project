package com.nhom7.ecommercebackend;

import com.github.javafaker.Faker;
import com.nhom7.ecommercebackend.model.Category;
import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductAttribute;
import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.repository.CategoryRepository;
import com.nhom7.ecommercebackend.repository.ProductRepository;
import com.nhom7.ecommercebackend.request.category.CategoryDTO;
import com.nhom7.ecommercebackend.request.category.SubCategoryDTO;
import com.nhom7.ecommercebackend.request.product.AttributeDTO;
import com.nhom7.ecommercebackend.request.product.ProductAttributeValueDTO;
import com.nhom7.ecommercebackend.request.product.ProductDTO;
import com.nhom7.ecommercebackend.request.product.ProductImageDTO;
import com.nhom7.ecommercebackend.service.CategoryService;
import com.nhom7.ecommercebackend.service.ProductAttributeService;
import com.nhom7.ecommercebackend.service.ProductService;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Profile("dev") // Chỉ chạy seeder này ở môi trường 'dev'
@RequiredArgsConstructor
public class FakeProductDataSeeder implements CommandLineRunner {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;
    private final ProductAttributeService productAttributeService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(FakeProductDataSeeder.class);

    private final List<String> categoryNames = Arrays.asList("Điện thoại", "Laptop");
    private final Map<String, Long> subCategoryToCategoryMap = Map.of(
            "Iphone", 1L,
            "Samsung", 1L,
            "Lenovo", 2L,
            "Asus", 2L
    );
    private final List<String> attributes = Arrays.asList(
            "mobile_ram", "mobile_storage", "mobile_cpu", "mobile_screen_type", "mobile_screen_size",
            "mobile_screen_refresh_rate", "mobile_battery_capacity", "mobile_color", "mobile_design_description",
            "mobile_os", "mobile_manufacturer", "mobile_guarantee_month", "laptop_ram", "laptop_storage",
            "laptop_cpu", "laptop_screen_size", "laptop_screen_refresh_rate", "laptop_battery_capacity",
            "laptop_color", "laptop_design_description", "laptop_os", "laptop_manufacturer",
            "laptop_guarantee_month", "mobile_hot", "laptop_hot"
    );
    private final Faker faker = new Faker();
    private final Random random = new Random();

    // Smartphone models
    private final List<String> iphoneModels = Arrays.asList("iPhone 15 Pro Max", "iPhone 15", "iPhone 14", "iPhone 13", "iPhone SE");
    private final List<String> samsungModels = Arrays.asList("Samsung Galaxy S22 Ultra", "Samsung Galaxy S21", "Samsung Galaxy Z Fold 3", "Samsung Galaxy A72");

    // Laptop models
    private final List<String> lenovoModels = Arrays.asList("Lenovo ThinkPad X1 Carbon", "Lenovo Legion 5 Pro", "Lenovo Yoga 9i", "Lenovo IdeaPad 3");
    private final List<String> asusModels = Arrays.asList("Asus ROG Zephyrus G14", "Asus ZenBook Pro Duo", "Asus VivoBook S15", "Asus TUF Dash F15");

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            logger.info("Data already exists. Skipping seeding.");
            return;
        }
        logger.info("Start seeding data...");

        // 1. Seed Categories
        for (String categoryName : this.categoryNames) {
            try {
                categoryService.creatCategory(CategoryDTO.builder().name(categoryName).build());
                logger.info("Category created: {}", categoryName);
            } catch (Exception e) {
                logger.warn("Category '{}' might already exist: {}", categoryName, e.getMessage());
            }
        }

        // 2. Seed SubCategories
        for (Map.Entry<String, Long> entry : this.subCategoryToCategoryMap.entrySet()) {
            try {
                String subCategoryName = entry.getKey();
                Long categoryId = entry.getValue();
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found for ID: " + categoryId));
                SubCategory subCategory = subCategoryService.createSubCategory(SubCategoryDTO.builder().subCategoryName(subCategoryName).build());
                subCategory.setCategory(category);
                // The relationship should be managed by the service, but if not, we save it here.
                // This part might need adjustment based on your service implementation.
                category.getSubCategoryList().add(subCategory);
                categoryRepository.save(category);
                logger.info("SubCategory '{}' created and linked to Category '{}'", subCategoryName, category.getName());
            } catch (Exception e) {
                logger.warn("SubCategory '{}' might already exist or failed to create: {}", entry.getKey(), e.getMessage());
            }
        }

        // 3. Seed Attributes
        for (String name : this.attributes) {
            try {
                productAttributeService.createProductAttribute(AttributeDTO.builder().name(name).active(true).build());
                logger.info("Product Attribute created: {}", name);
            } catch (Exception e) {
                logger.warn("Attribute '{}' might already exist: {}", name, e.getMessage());
            }
        }

        // 4. Seed Products
        List<String> productImages = getRandomImagesFromUploads("uploads/");
        if (productImages.isEmpty()) {
            logger.error("No images found in 'uploads/' directory. Cannot seed product images.");
            return;
        }

        logger.info("Start seeding 200,000 products... This will take a while.");
        for (int i = 0; i < 200_000; i++) {
            ProductDTO productDTO = generateUniqueProduct(i);
            if (productDTO != null) {
                try {
                    Product product = productService.createProduct(productDTO);
                    Collections.shuffle(productImages);
                    int imageCount = Math.min(5, productImages.size());
                    for (int a = 0; a < imageCount; a++) {
                        productService.createProductImage(
                                product.getId(),
                                ProductImageDTO.builder().imageUrl(productImages.get(a)).build()
                        );
                    }
                    if ((i + 1) % 5000 == 0) { // Log progress every 5000 products
                        logger.info("Created {} / 200,000 products.", i + 1);
                    }
                } catch (Exception e) {
                    logger.error("Failed to create product {}: {}", (i + 1), e.getMessage());
                }
            }
        }
        logger.info("Finished seeding 200,000 products.");
    }

    private List<String> getRandomImagesFromUploads(String dir) {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".webp"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to list files in directory '{}'. Please ensure it exists.", dir, e);
            return Collections.emptyList();
        }
    }

    private ProductDTO generateUniqueProduct(int sequence) {
        Long categoryId;
        List<Long> subcategoryIds;
        String baseProductName;

        if (random.nextBoolean()) {
            // Smartphone category
            categoryId = 1L;
            subcategoryIds = random.nextBoolean() ? Collections.singletonList(1L) : Collections.singletonList(2L);
            baseProductName = getRandomModel(subcategoryIds.get(0) == 1L ? iphoneModels : samsungModels);
        } else {
            // Laptop category
            categoryId = 2L;
            subcategoryIds = random.nextBoolean() ? Collections.singletonList(3L) : Collections.singletonList(4L);
            baseProductName = getRandomModel(subcategoryIds.get(0) == 3L ? lenovoModels : asusModels);
        }

        // Append a unique suffix to ensure the name is unique
        String productName = baseProductName + " #" + sequence + " " + UUID.randomUUID().toString().substring(0, 4);

        List<ProductAttributeValueDTO> attributeValues = new ArrayList<>();
        if (categoryId == 1L) {
            // Smartphone attributes
            attributeValues.add(createAttributeValue("mobile_ram", String.valueOf(random.nextInt(8) + 4) + "GB"));
            attributeValues.add(createAttributeValue("mobile_storage", String.valueOf(128 * (random.nextInt(4) + 1)) + "GB"));
            attributeValues.add(createAttributeValue("mobile_screen_type", generateRandomScreenType()));
            attributeValues.add(createAttributeValue("mobile_cpu", generateRandomCpu()));
            attributeValues.add(createAttributeValue("mobile_screen_size", String.valueOf(generateRandomScreenSize(5.5, 6.7))));
            attributeValues.add(createAttributeValue("mobile_screen_refresh_rate", String.valueOf(generateRandomScreenRefreshRate())));
            attributeValues.add(createAttributeValue("mobile_battery_capacity", String.valueOf(random.nextInt(1000) + 4000)));
            attributeValues.add(createAttributeValue("mobile_color", faker.color().name()));
            attributeValues.add(createAttributeValue("mobile_os", generateRandomOs(true)));
        } else {
            // Laptop attributes
            attributeValues.add(createAttributeValue("laptop_ram", String.valueOf(8 * (random.nextInt(4) + 1)) + "GB"));
            attributeValues.add(createAttributeValue("laptop_storage", String.valueOf(256 * (random.nextInt(4) + 1)) + "GB SSD"));
            attributeValues.add(createAttributeValue("laptop_cpu", generateRandomCpu()));
            attributeValues.add(createAttributeValue("laptop_screen_size", String.valueOf(generateRandomScreenSize(13.3, 16.0))));
            attributeValues.add(createAttributeValue("laptop_screen_refresh_rate", String.valueOf(generateRandomScreenRefreshRate())));
            attributeValues.add(createAttributeValue("laptop_battery_capacity", String.valueOf(random.nextInt(3000) + 5000)));
            attributeValues.add(createAttributeValue("laptop_color", faker.color().name()));
            attributeValues.add(createAttributeValue("laptop_os", generateRandomOs(false)));
        }

        return ProductDTO.builder()
                .name(productName)
                .description("Sản phẩm này được bán tại CyberTechShop. " + faker.lorem().paragraph())
                .price(BigDecimal.valueOf(faker.number().randomDouble(0, 1000000, 40000000)))
                .quantity(random.nextLong(100))
                .thumbnail(null)
                .isHot(random.nextBoolean())
                .subcategory(subcategoryIds)
                .attributeValues(attributeValues)
                .build();
    }

    private ProductAttributeValueDTO createAttributeValue(String attributeName, String value) {
        return ProductAttributeValueDTO.builder().attributeName(attributeName).value(value).build();
    }

    private String getRandomModel(List<String> models) {
        return models.get(random.nextInt(models.size()));
    }

    private String generateRandomCpu() {
        List<String> cpus = Arrays.asList("Intel Core i9", "Intel Core i7", "AMD Ryzen 9", "AMD Ryzen 7", "Apple M2", "Apple M1");
        return cpus.get(random.nextInt(cpus.size()));
    }

    private String generateRandomScreenType() {
        return random.nextBoolean() ? "OLED" : "IPS LCD";
    }

    private int generateRandomScreenRefreshRate() {
        List<Integer> refreshRates = Arrays.asList(60, 90, 120, 144);
        return refreshRates.get(random.nextInt(refreshRates.size()));
    }

    private float generateRandomScreenSize(double min, double max) {
        return (float) (Math.round((min + random.nextDouble() * (max - min)) * 10.0) / 10.0);
    }

    private String generateRandomOs(boolean isMobile) {
        if (isMobile) {
            return random.nextBoolean() ? "iOS" : "Android";
        }
        return random.nextBoolean() ? "Windows 11" : "macOS";
    }
}