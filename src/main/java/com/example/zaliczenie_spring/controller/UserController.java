package com.example.zaliczenie_spring.controller;

import com.example.zaliczenie_spring.model.Authority;
import com.example.zaliczenie_spring.repo.UserRepository;
import com.example.zaliczenie_spring.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // Secret key used for signing the JWT
    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @GetMapping("/users")
    public Iterable<Users> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public LoginResponse login(@RequestBody Users user) {
        Users dbUser = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (dbUser == null) {
            throw new RuntimeException("Invalid login");
    }

        String token = getJWTToken(user.getEmail());
        System.out.println("Token: " + dbUser.getEmail());
        return new LoginResponse(user.getEmail(), token);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public LoginResponse addUser(@RequestBody Users user) {
        Users existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User with this email already exists");
        }

        // Tworzenie obiektu Authority i przypisywanie go do obiektu Users
        Authority authority = new Authority();
        user.setAuthorities(Collections.singletonList(authority));

        String token = getJWTToken(user.getEmail());

        // Zapisywanie użytkownika w repozytorium
        Users savedUser = userRepository.save(user);
        savedUser.setAuthorities(Collections.singletonList(authority)); // Ustawienie authorities w Users

        return new LoginResponse(savedUser.getEmail(), token);
    }

    private String getJWTToken(String username) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(secretKey)
                .compact();

        return "Bearer " + token;
    }
}

class LoginResponse {
    private String username;
    private String token;

    public LoginResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

