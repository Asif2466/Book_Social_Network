package com.book_social_network.book_network.auth;

import com.book_social_network.book_network.email.EmailService;
import com.book_social_network.book_network.email.EmailTemplateName;
import com.book_social_network.book_network.repository.RoleRepository;
import com.book_social_network.book_network.repository.TokenRepository;
import com.book_social_network.book_network.repository.UserRepository;
import com.book_social_network.book_network.security.JWT_Service;
import com.book_social_network.book_network.token.Token;
import com.book_social_network.book_network.user.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JWT_Service jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(@Valid RegistrationRequest request) throws MessagingException {

        var userRole = roleRepository.findByName("USER")
                // todo -> a better exception handling
                .orElseThrow(() -> new IllegalStateException("Role USER was not initialized"));

        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);

        sendEmailValidation(user);
    }

    private void sendEmailValidation(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        // send Email

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"
                );
    }

    private String generateAndSaveActivationToken(User user) {
        //generate token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();// It makes sure that the generated value is cryptographically secure
        for (int i = 0; i < length; i++) {
            //codeBuilder.append(characters.charAt(random.nextInt(characters.length())));
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var claims = new HashMap<String, Object >();
        var user = (User) auth.getPrincipal();
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(jwtToken).build();

    }

//    @Transactional
    public void activateAccount(String token) throws MessagingException {

        var savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendEmailValidation(savedToken.getUser());
            throw new RuntimeException("Activation Token is expired, A new token has been sent to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

    }
}
