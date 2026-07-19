package org.rudreshwar.codesync.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.user.dto.LoginRequest;
import org.rudreshwar.codesync.user.dto.SignupRequest;
import org.rudreshwar.codesync.user.dto.AuthResponse;
import org.rudreshwar.codesync.common.exception.BadRequestException;
import org.rudreshwar.codesync.common.exception.ResourceNotFoundException;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.rudreshwar.codesync.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BadRequestException("Email is already taken!");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        user = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupRequest.getUsername(), signupRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(loginRequest.getUsernameOrEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", loginRequest.getUsernameOrEmail()));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }

}
