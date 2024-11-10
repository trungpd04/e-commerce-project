package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.ContactUs;
import com.nhom7.ecommercebackend.repository.ContactUsRepository;
import com.nhom7.ecommercebackend.service.ContactUsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactUsServiceImpl implements ContactUsService {

    private final ContactUsRepository contactUsRepository;

    @Override
    public ContactUs createContactUs(ContactUs contactUs) {
        return contactUsRepository.save(contactUs);
    }

    @Override
    public void deleteContactUs(Long id) {
        if(!contactUsRepository.existsById(id)) {
            throw new DataNotFoundException("Contact not found!");
        }
        contactUsRepository.deleteById(id);
    }

    @Override
    public Page<ContactUs> getAllContactUs(PageRequest pageRequest) {
        return contactUsRepository.findAll(pageRequest);
    }
}
