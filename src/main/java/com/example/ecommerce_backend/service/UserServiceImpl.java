package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.api.dto.UserLoginRequestDto;
import com.example.ecommerce_backend.api.dto.UserRegistrationDto;
import com.example.ecommerce_backend.api.security.service.EncryptionService;
import com.example.ecommerce_backend.api.security.service.JwtService;
import com.example.ecommerce_backend.exception.EmailFailureException;
import com.example.ecommerce_backend.exception.UserAlreadyExistsException;
import com.example.ecommerce_backend.exception.UserNotFoundException;
import com.example.ecommerce_backend.exception.UserNotVerifiedException;
import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.VerificationToken;
import com.example.ecommerce_backend.model.repository.LocalUserRepository;
import com.example.ecommerce_backend.model.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final LocalUserRepository localUserRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;
    private final EmailService emailService;

    public UserServiceImpl(
            LocalUserRepository localUserRepository,
            EncryptionService encryptionService,
            JwtService jwtService,
            EmailService emailService,
            VerificationTokenRepository verificationTokenRepository) {
        this.localUserRepository = localUserRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    @Transactional
    public LocalUser registerUser(UserRegistrationDto userRegistrationDto)
            throws UserAlreadyExistsException, EmailFailureException {

        if (localUserRepository.findByEmailIgnoreCase(userRegistrationDto.getEmail()).isPresent()
                && localUserRepository.findByUsernameIgnoreCase(userRegistrationDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists: " + userRegistrationDto.getUsername());
        }

        LocalUser user = LocalUser.builder()
                .username(userRegistrationDto.getUsername())
                .email(userRegistrationDto.getEmail())
                .firstName(userRegistrationDto.getFirstName())
                .lastName(userRegistrationDto.getLastName())
                .password(encryptionService.encryptPassword(userRegistrationDto.getPassword()))
                .build();

        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);

        return localUserRepository.save(user);
    }

    @Override
    public String loginUser(UserLoginRequestDto userLoginRequestDto) throws UserNotFoundException {
        Optional<LocalUser> optionalLocalUser = localUserRepository
                .findByUsernameIgnoreCase(userLoginRequestDto.getUsername());

        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("Login failed for " + userLoginRequestDto.getUsername());
        }

        LocalUser user = optionalLocalUser.get();

        if (encryptionService.verifyPassword(userLoginRequestDto.getPassword(), user.getPassword())) {
            if (user.getEmailVerified()) {
                return jwtService.generateJwtToken(user);
            } else {
                Set<VerificationToken> verificationTokens = user.getVerificationTokens();
                boolean resend = verificationTokens.isEmpty() ||
                        verificationTokens.iterator().next().getCreatedTimestamp()
                                .before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));

                if (resend) {
                    VerificationToken verificationToken = createVerificationToken(user);
                    verificationTokenRepository.save(verificationToken);
                    emailService.sendVerificationEmail(verificationToken);
                }

                throw new UserNotVerifiedException("User: " + user.getUsername() + " not verified.", resend);
            }
        } else {
            throw new UserNotFoundException("Login failed for " + userLoginRequestDto.getUsername());
        }
    }

    @Override
    @Transactional
    public Boolean verifyUser(String token) {
        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findByToken(token);
        if (optionalVerificationToken.isPresent()) {
            VerificationToken verificationToken = optionalVerificationToken.get();
            LocalUser user = verificationToken.getUser();

            if (!user.getEmailVerified()) {
                user.setEmailVerified(true);
                localUserRepository.save(user);
                verificationTokenRepository.deleteByUser(user);

                return true;
            }
        }

        return false;
    }

    private VerificationToken createVerificationToken(LocalUser user) {
        VerificationToken token = VerificationToken.builder()
                .token(jwtService.generateVerificationJwt(user))
                .user(user)
                .createdTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();
        user.getVerificationTokens().add(token);
        return token;
    }
}
