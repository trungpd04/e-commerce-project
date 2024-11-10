package com.nhom7.ecommercebackend.service;

import com.nhom7.ecommercebackend.model.ContactUs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ContactUsService {
    ContactUs createContactUs(ContactUs contactUs);
    void deleteContactUs(Long id);
    Page<ContactUs> getAllContactUs(PageRequest pageRequest);
}
