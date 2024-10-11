package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.PermissionDenyException;
import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.model.Role;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.repository.RoleRepository;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.user.UserDTO;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.service.UserService;
import com.nhom7.ecommercebackend.utils.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User register(UserDTO userDTO) throws PermissionDenyException {
        if (!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataIntegrityViolationException("This email has already exist!");
        }
        if (!userDTO.getPhoneNumber().isBlank() && userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("This phone number has already exist!");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role does not exist!"));
        if (role.getName().equalsIgnoreCase("ADMIN")) {
            throw new PermissionDenyException("Registering admin accounts is not allowed!");
        }
        User user = User.builder()
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .active(true)
                .address(userDTO.getAddress())
                .profileImage(userDTO.getProfileImage())
                .fullName(userDTO.getFullName())
                .dateOfBirth(userDTO.getDateOfBirth())
                .role(role)
                .build();
        return userRepository.save(user);
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(Long userID, UserDTO updatedUserDTO) {
        User existingUser = userRepository.findById(userID)
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.USER_NOT_EXIST));
        if (updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (updatedUserDTO.getEmail() != null) {
            existingUser.setEmail(updatedUserDTO.getEmail());
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }
        if(updatedUserDTO.getProfileImage() != null) {
            existingUser.setProfileImage(updatedUserDTO.getProfileImage());
        }
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            if (!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.USER_NOT_EXIST));
        user.setActive(false);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.USER_NOT_EXIST));
    }

    @Override
    public Page<OrderResponse> getMyOrders(Long userId, PageRequest pageRequest) {
        Page<Order> orders = orderRepository.findAllByUserId(userId, pageRequest);
        if(orders.isEmpty()) {
            throw new DataNotFoundException("You have not placed any order yet!");
        }
        return orders.map(OrderResponse::fromOrder);
    }
}
