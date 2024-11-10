package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.ContactUs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {

    Page<ContactUs> findAll(Pageable pageable);
//    Optional<ContactUs> findById(Long id);
}
