package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String roleName);
    boolean existsByName(String name);
}
