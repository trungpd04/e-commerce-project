package com.nhom7.ecommercebackend.controller;

import com.nhom7.ecommercebackend.model.SubCategory;
import com.nhom7.ecommercebackend.request.SubCategoryDTO;
import com.nhom7.ecommercebackend.response.ApiResponse;
import com.nhom7.ecommercebackend.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("${api.prefix}/sub_categories")
@RequiredArgsConstructor
public class SubCategoryController {

}
