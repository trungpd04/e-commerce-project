package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.ContactUs;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.ContactUsService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/contact_us")
public class ContactUsController {

    private final ContactUsService contactUsService;

    @PostMapping("")
    public ApiResponse createContactUs(
            @RequestBody ContactUs contact
    ) {
        return ApiResponse.builder()
                .message("Create contact us successfully!")
                .status(HTTP_OK)
                .data(contactUsService.createContactUs(contact))
                .build();
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse getContactUs(
            @RequestParam(value = "size", defaultValue = "5", required = false) int size,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "sort_by", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sort_dir", defaultValue = "asc", required = false) String sortDir
    ) {
        Sort.Direction sortDirection = sortDir.trim().equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy.trim().toLowerCase());
        PageRequest request = PageRequest.of(page, size, sort);
        Page<ContactUs> contactUsPage = contactUsService
                .getAllContactUs(request);
        return ApiResponse.builder()
                .data(contactUsPage)
                .status(HTTP_OK)
                .message("Get contact us successfully!")
                .build();
    }
    @DeleteMapping("/{contactUsId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ApiResponse deleteContactUs(
            @PathVariable("contactUsId") Long contactUsId
    ) {
        contactUsService.deleteContactUs(contactUsId);
        return ApiResponse.builder()
                .status(HTTP_OK)
                .message("Delete contact us successfully!")
                .build();
    }
}
