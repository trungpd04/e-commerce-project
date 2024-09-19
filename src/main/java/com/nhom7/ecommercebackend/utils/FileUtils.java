package com.nhom7.ecommercebackend.utils;

import ch.qos.logback.core.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

public class FileUtils {
    private static final String UPLOAD_DIR = "uploads";
    public static String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        String extension = FilenameUtils.getExtension(fileName);

        String uniqueFile = UUID.randomUUID() + "_" + System.nanoTime() + "." + extension;

        Path uploadDir = Paths.get(UPLOAD_DIR);

        if(!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = Paths.get(uploadDir.toString(), uniqueFile);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFile;
    }
    public static void deleteFile(String filename) throws IOException {

        java.nio.file.Path uploadDir = Paths.get(UPLOAD_DIR);

        java.nio.file.Path filePath = uploadDir.resolve(filename);


        if (Files.exists(filePath)) {

            Files.delete(filePath);
        } else {

        }
    }
}
