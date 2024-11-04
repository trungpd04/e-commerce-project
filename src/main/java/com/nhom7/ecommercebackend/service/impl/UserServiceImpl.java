package com.nhom7.ecommercebackend.service.impl;

import com.nhom7.ecommercebackend.exception.*;
import com.nhom7.ecommercebackend.model.*;
import com.nhom7.ecommercebackend.repository.OrderRepository;
import com.nhom7.ecommercebackend.repository.ResetPasswordRepository;
import com.nhom7.ecommercebackend.repository.RoleRepository;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.request.user.*;
import com.nhom7.ecommercebackend.response.order.OrderResponse;
import com.nhom7.ecommercebackend.response.user.UserDetailResponse;
import com.nhom7.ecommercebackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ResetPasswordRepository resetPasswordRepository;

    @Override
    @Transactional

    public User register(UserDTO userDTO) throws PermissionDenyException, PasswordCreationException {
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
        if(!userDTO.getPassword().equalsIgnoreCase(userDTO.getRetypePassword())) {
            throw new PasswordCreationException(MessageKeys.PASSWORD_NOT_MATCH.toString());
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') ")
    public User updateUser(User user, UpdateUserDTO updatedUserDTO) {

        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));

        if(userRepository.existsByEmail(updatedUserDTO.getEmail())
            && !existingUser.getEmail().equals(updatedUserDTO.getEmail())
        ) {
            throw new DataIntegrityViolationException("This email has already exist!");
        }

        if(userRepository.existsByPhoneNumber(updatedUserDTO.getPhoneNumber())
            && !existingUser.getPhoneNumber().equals(updatedUserDTO.getPhoneNumber())
        ) {
            throw new DataIntegrityViolationException("This phone number has already exist!");
        }

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
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        user.setActive(false);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDetailResponse> getAllUsers(String keyword, PageRequest pageRequest) {
        Page<User> users = userRepository.findAllByFullNameContaining(keyword, pageRequest);
        return users.map(UserDetailResponse::fromUser);
    }

    @Override
    public Page<OrderResponse> getMyOrders(Long userId, PageRequest pageRequest) {
        Page<Order> orders = orderRepository.findAllByUserId(userId, pageRequest);
        if(orders.isEmpty()) {
            throw new DataNotFoundException("You have not placed any order yet!");
        }
        return orders.map(OrderResponse::fromOrder);
    }

    @Override
    public UserDetailResponse getUserDetail() {
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        SecurityContext context = SecurityContextHolder.getContext();

        User user = (User) loadUserByUsername(context.getAuthentication().getName());
        if (Objects.isNull(user)) {
            throw new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString());
        }
        modelMapper.map(user ,userDetailResponse);
        userDetailResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));
        return userDetailResponse;
    }

    @Override
    @Transactional
    public void createPassword(PasswordCreationDTO request) throws PasswordCreationException {
        SecurityContext context = SecurityContextHolder.getContext();
        User user = (User) loadUserByUsername(context.getAuthentication().getName());
        if (Objects.isNull(user)) {
            throw new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString());
        }
        if(StringUtils.hasText(user.getPassword())) {
            throw new PasswordCreationException(MessageKeys.PASSWORD_EXISTED.toString());
        }
        if(!request.getPassword().equalsIgnoreCase(request.getRetypePassword())) {
            throw new PasswordCreationException(MessageKeys.PASSWORD_NOT_MATCH.toString());
        }
        user.setPassword(passwordEncoder.encode(request.getRetypePassword()));
        userRepository.save(user);
    }

    @Override
    public String updateResetPasswordToken(String email) {
        User user = userRepository.findByEmailAndPasswordNotNull(email)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_EXIST.toString()));
        Optional<ResetPasswordToken> existResetToken = resetPasswordRepository
                .findByUser(user);
        existResetToken.ifPresent(resetPasswordRepository::delete);
        ResetPasswordToken resetPasswordToken = ResetPasswordToken.builder()
                .expiryDate(Date.from(Instant.now().plus(ResetPasswordToken.EXPIRY_TIME, ChronoUnit.SECONDS)))
                .token(UUID.randomUUID().toString())
                .user(user)
                .build();
        return resetPasswordRepository.save(resetPasswordToken).getToken();
    }

    @Override
    public User getByResetPasswordToken(String token) throws TokenException {
        ResetPasswordToken resetPasswordToken = resetPasswordRepository.findByToken(token)
                .orElseThrow(() -> new TokenException("Invalid Token"));
        return resetPasswordToken.getUser();
    }

    @Override
    public void updatePassword(User user, ResetPasswordDTO dto) throws TokenException, PasswordCreationException {
        ResetPasswordToken resetPasswordToken = resetPasswordRepository.findByUser(user)
                .orElseThrow(() -> new TokenException("Invalid Token"));
        if(!resetPasswordToken.getExpiryDate().after(new Date())) {
            resetPasswordRepository.delete(resetPasswordToken);
            throw new TokenException("Invalid Token");
        }
        if(!dto.getPassword().equals(dto.getRetypePassword())) {
            throw new PasswordCreationException("Retype Password does not match password!");
        }
        String newPassword = passwordEncoder.encode(dto.getRetypePassword());
        user.setPassword(newPassword);
        userRepository.save(user);
        resetPasswordRepository.delete(resetPasswordToken);
    }
    @Override
    @Transactional
    public void changeProfileImage(Long userId, String imageName) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setProfileImage(imageName);
        userRepository.save(existingUser);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) throws PasswordCreationException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        if(!passwordEncoder.matches(dto.getPassword(), existingUser.getPassword())) {
            throw new PasswordCreationException("Wrong password!");
        }
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordCreationException("Confirm password does not match new password!");
        }
        existingUser.setPassword(passwordEncoder.encode(dto.getConfirmPassword()));
        userRepository.save(existingUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Optional<User> googleUser = userRepository.findByProviderAndProviderId(AuthProvider.google, username);
            if(googleUser.isPresent()) {
                return googleUser.get();
            }
            Optional<User> facebookUser = userRepository.findByProviderAndProviderId(AuthProvider.facebook, username);
            if(facebookUser.isPresent()) {
                return facebookUser.get();
            }
            Optional<User> userWithEmail = userRepository.findByEmailAndPasswordNotNull(username);
            if(userWithEmail.isPresent()) {
                return userWithEmail.get();
            }
            Optional<User> userWithPhoneNumber = userRepository.findByPhoneNumberAndPasswordNotNull(username);
            if(userWithPhoneNumber.isPresent()) {
                return userWithPhoneNumber.get();
            }
            throw new UsernameNotFoundException(MessageKeys.USER_NOT_EXIST.toString());
    }
}
