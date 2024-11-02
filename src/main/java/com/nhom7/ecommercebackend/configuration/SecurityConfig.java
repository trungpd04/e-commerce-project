package com.nhom7.ecommercebackend.configuration;

import com.nhom7.ecommercebackend.exception.DataNotFoundException;
import com.nhom7.ecommercebackend.exception.MessageKeys;
import com.nhom7.ecommercebackend.model.AuthProvider;
import com.nhom7.ecommercebackend.model.User;
import com.nhom7.ecommercebackend.repository.UserRepository;
import com.nhom7.ecommercebackend.service.UserService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final RsaKeyProperties rsaKeyProperties;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Value("${api.prefix}")
    protected String API_PREFIX;
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return subject -> {
//          org.springframework.security.core.context.SecurityContext context = SecurityContextHolder.getContext();
//            Object object = context.getAuthentication().getDetails();
//            if(object instanceof UserDetails) {
//                User user = (User) object;
//                String userName = user.getUsername();
//                AuthProvider provider = user.getProvider();
//                String providerId = user.getProviderId();
//                Optional<User> googleUser = userRepository.findByProviderAndProviderId(provider, providerId);
//                if(googleUser.isPresent()) {
//                    return googleUser.get();
//                }
//                Optional<User> facebookUser = userRepository.findByProviderAndProviderId(provider, providerId);
//                if(facebookUser.isPresent()) {
//                    return facebookUser.get();
//                }
//                Optional<User> userWithEmail = userRepository.findByEmail(userName);
//                if(userWithEmail.isPresent()) {
//                    return userWithEmail.get();
//                }
//                Optional<User> userWithPhoneNumber = userRepository.findByProviderAndProviderId(provider, providerId);
//                if(userWithPhoneNumber.isPresent()) {
//                    return userWithPhoneNumber.get();
//                }
//            }
//            throw new UsernameNotFoundException(MessageKeys.USER_NOT_EXIST.toString());
//        };
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        final String[] PUBLIC_ENDPOINT_GET = {
                String.format("%s/products/**", API_PREFIX),
                String.format("%s/categories/**",API_PREFIX),
                String.format("%s/orders/checkout/vn-pay/callback", API_PREFIX),
                String.format("%s/users/oauth2/social/callback", API_PREFIX),
                String.format("%s/sub_categories/**",API_PREFIX),
                String.format("%s/ratings/**", API_PREFIX),
                String.format("%s/users/oauth2/userinfo", API_PREFIX),
                String.format("%s/api-docs",API_PREFIX),
                String.format("%s/api-docs/**", API_PREFIX),
                String.format("%s/swagger-resources",API_PREFIX),
                String.format("%s/swagger-resources/**",API_PREFIX),
                String.format("%s/configuration/ui",API_PREFIX),
                String.format("%s/configuration/security",API_PREFIX),
                String.format("%s/swagger-ui/**",API_PREFIX),
                String.format("%s/swagger-ui.html", API_PREFIX),
                String.format("%s/webjars/swagger-ui/**", API_PREFIX),
                String.format("%s/swagger-ui/index.html", API_PREFIX)
        };
        final String[] PUBLIC_ENDPOINT_POST = {
                String.format("%s/users/register",API_PREFIX),
                String.format("%s/users/login",API_PREFIX),
                String.format("%s/users/introspect", API_PREFIX),
                String.format("%s/users/refresh", API_PREFIX),
                String.format("%s/users/logout", API_PREFIX),
                String.format("%s/users/outbound/login", API_PREFIX),
                String.format("%s/users/oauth2/login", API_PREFIX),
                String.format("%s/users/forgot_password", API_PREFIX),
                String.format("%s/users/reset_password", API_PREFIX)
        };
        httpSecurity.authorizeHttpRequests(config ->
                config.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll()
                        .anyRequest().authenticated());
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.oauth2ResourceServer(oath2 -> oath2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(customBearerTokenAuthenticationEntryPoint)
                .accessDeniedHandler(customBearerTokenAccessDeniedHandler)
        );
        return httpSecurity.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey
                .Builder(rsaKeyProperties.rsaPublicKey())
                .privateKey(rsaKeyProperties.rsaPrivateKey()).build();
        JWKSet jwkSet = new JWKSet(jwk);
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.rsaPublicKey()).build();
    }
}
