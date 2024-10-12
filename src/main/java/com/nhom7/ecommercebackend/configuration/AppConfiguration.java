package com.nhom7.ecommercebackend.configuration;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.order.OrderDTO;
import com.nhom7.ecommercebackend.response.user.UserDetailResponse;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AppConfiguration {

    private final UserRepository userRepository;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(User.class, UserDetailResponse.class);
        modelMapper.createTypeMap(OrderDTO.class, Order.class)
                .addMappings(map -> map.skip(Order::setId));
        return modelMapper;
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return subject -> {
            Optional<User> existingEmailUser = userRepository.findByEmail(subject);
            if(existingEmailUser.isPresent()){
                return existingEmailUser.get();
            }
            Optional<User> existingPhoneNumberUser = userRepository.findByPhoneNumber(subject);
            if(existingPhoneNumberUser.isPresent()) {
                return existingPhoneNumberUser.get();
            }
            throw new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString());
        };
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
