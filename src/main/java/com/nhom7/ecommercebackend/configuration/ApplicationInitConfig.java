package com.nhom7.ecommercebackend.configuration;




import com.nhom7.ecommercebackend.model.Role;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.RoleRepository;
import com.nhom7.ecommercebackend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    static String ADMIN_PHONE = "0123456789";
    static String ADMIN_EMAIL = "admin@admin.com";
    static String ADMIN_PASSWORD = "admin";

    static String USER_PHONE = "0123456788";
    static String USER_EMAIL = "user@user.com";
    static String USER_PASSWORD = "123456789";

    @Bean
    ApplicationRunner applicationRunner(
            UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application ...");
        return args -> {
            if (userRepository.findByEmailAndPasswordNotNull(ADMIN_EMAIL).isEmpty()) {

                Role adminRole = Role.builder()
                        .name("ADMIN")
                        .build();
                if(!roleRepository.existsByName("ADMIN")) {
                    roleRepository.save(adminRole);
                }else{
                    log.info("Admin role already exists");
                }
                Role role = roleRepository.findByName("ADMIN");
                User user = User.builder()
                        .email(ADMIN_EMAIL)
                        .phoneNumber(ADMIN_PHONE)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .email(ADMIN_EMAIL)
                        .role(role)
                        .active(true)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
                Role userRole = Role.builder()
                        .name("USER")
                        .build();
                if(!roleRepository.existsByName("USER")) {
                    roleRepository.save(userRole);
                }else{
                    log.info("User role already exists");
                }
            }
            if(userRepository.findByEmailAndPasswordNotNull(USER_EMAIL).isEmpty()) {
                Role role = roleRepository.findByName("USER");
                User user = User.builder()
                        .email(USER_EMAIL)
                        .phoneNumber(USER_PHONE)
                        .password(passwordEncoder.encode(USER_PASSWORD))
                        .email(USER_EMAIL)
                        .role(role)
                        .active(true)
                        .build();
                userRepository.save(user);
                log.warn("user has been created with default account and password: user@user.com, 123456789");
            }
            log.info("Application initialization completed .....");
        };
    }
}

