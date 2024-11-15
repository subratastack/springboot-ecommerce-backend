package com.example.ecommerce_backend.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.ecommerce_backend.api.dto.ResetPasswordRequestDto;
import com.example.ecommerce_backend.api.dto.UserLoginRequestDto;
import com.example.ecommerce_backend.api.dto.UserRegistrationDto;
import com.example.ecommerce_backend.api.security.service.EncryptionService;
import com.example.ecommerce_backend.api.security.service.JwtService;
import com.example.ecommerce_backend.exception.*;
import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.ResetPasswordToken;
import com.example.ecommerce_backend.model.VerificationToken;
import com.example.ecommerce_backend.model.repository.LocalUserRepository;
import com.example.ecommerce_backend.model.repository.ResetPasswordTokenRepository;
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
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;
    private final EmailService emailService;

    public UserServiceImpl(
            LocalUserRepository localUserRepository,
            EncryptionService encryptionService,
            JwtService jwtService,
            EmailService emailService,
            VerificationTokenRepository verificationTokenRepository,
            ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.localUserRepository = localUserRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    @Override
    @Transactional
    public LocalUser registerUser(UserRegistrationDto userRegistrationDto)
            throws UserAlreadyExistsException, EmailFailureException {

        if (localUserRepository.findByEmailIgnoreCase(userRegistrationDto.getEmail()).isPresent()
                || localUserRepository.findByUsernameIgnoreCase(userRegistrationDto.getUsername()).isPresent()) {
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

    @Override
    @Transactional
    public void forgotPassword(String email) throws EmailNotFoundException {
        Optional<LocalUser> optionalUser = localUserRepository.findByEmailIgnoreCase(email);

        if (optionalUser.isPresent()) {
            LocalUser user = optionalUser.get();
            ResetPasswordToken token = createResetPasswordToken(user);
            resetPasswordTokenRepository.save(token);
            emailService.sendResetPasswordEmail(user, token.getToken());
        } else {
            throw new EmailNotFoundException();
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) throws ExpiredTokenException, UserNotFoundException, TokenExpiredException {
        String email = jwtService.getResetPasswordEmail(resetPasswordRequestDto.getToken());
        Optional<LocalUser> optionalLocalUser = localUserRepository.findByEmailIgnoreCase(email);

        if (optionalLocalUser.isPresent()) {
            LocalUser user = optionalLocalUser.get();
            Optional<ResetPasswordToken> optionalResetPasswordToken = resetPasswordTokenRepository
                    .findByTokenAndUser_Id(resetPasswordRequestDto.getToken(), user.getId());
            if (optionalResetPasswordToken.isPresent()) {
                String token = optionalResetPasswordToken.get().getToken();
                user.setPassword(encryptionService.encryptPassword(resetPasswordRequestDto.getPassword()));
                resetPasswordTokenRepository.deleteByTokenAndUser(token, user);
                localUserRepository.save(user);
                return;
            } else {
                throw new ExpiredTokenException("Reset password token is invalid.");
            }
        }

        throw new UserNotFoundException("User not found for email address: " + email);
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

    private ResetPasswordToken createResetPasswordToken(LocalUser user) {
        ResetPasswordToken token = ResetPasswordToken.builder()
                .token(jwtService.generatePasswordResetJwt(user))
                .user(user)
                .createdTimestamp(new Timestamp(System.currentTimeMillis()))
                .build();
        user.getResetPasswordTokens().add(token);
        return token;
    }
}
