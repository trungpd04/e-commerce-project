package com.nhom7.ecommercebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*
ALTER TABLE users
  MODIFY facebook_account_id VARCHAR(255),
  MODIFY google_account_id VARCHAR(255);
* */
public class User extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 10, nullable = true)
    private String phoneNumber;

    // ALTER TABLE users ADD COLUMN email VARCHAR(255) DEFAULT '';
    @Column(name = "email", length = 255, nullable = true)
    private String email;

    @Column(name = "address", length = 200)
    private String address;

    //ALTER TABLE users ADD COLUMN profile_image VARCHAR(255) DEFAULT '';
    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "password", length = 200, nullable = false)
    private String password;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
//        authorityList.add(new SimpleGrantedAuthority("ROLE_"+getRole().getName().toUpperCase()));
//        //authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//
//        return authorityList;
//    }
//    @Override
//    public String getUsername() {
//        if (phoneNumber != null && !phoneNumber.isEmpty()) {
//            return phoneNumber;
//        } else if (email != null && !email.isEmpty()) {
//            return email;
//        }
//        return "";
//    }
//    @Override
//    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }
    //Login facebook

//    @JsonManagedReference
//    private List<Comment> comments = new ArrayList<>();
}
