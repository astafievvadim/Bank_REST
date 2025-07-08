package com.example.bankcards.config;

import com.example.bankcards.entity.*;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.repository.CustomUserRepo;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.UuidUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class DatabaseSeeder {

    private final CustomUserRepo customUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final CardRepo cardRepo;
    private final EncryptionUtil encryptionUtil;
    private final UuidUtils uuidUtils;

    @Autowired
    public DatabaseSeeder(CustomUserRepo customUserRepo, PasswordEncoder passwordEncoder, CardRepo cardRepo, EncryptionUtil encryptionUtil, UuidUtils uuidUtils) {
        this.customUserRepo = customUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.cardRepo = cardRepo;
        this.encryptionUtil = encryptionUtil;
        this.uuidUtils = uuidUtils;
    }

    @PostConstruct
    public void seedDatabase() {
        if (customUserRepo.count() == 0) {

            List<CustomUser> customUsers = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
                CustomUser customUser = new CustomUser();
                customUser.setName("CustomUser" + i);
                customUser.setLastname("Lastname" + i);
                customUser.setBirthDate(new Date());
                customUser.setEmail("customUser" + i + "@mail.com");
                customUser.setPassword(passwordEncoder.encode("password" + i));
                customUser.setRole(RoleEnum.USER);
                customUser.setRegistrationDate(new Date());
                customUserRepo.save(customUser);
                customUsers.add(customUser);

                System.out.println("USER TO TRY: " + "customUser" + i + "@mail.com " + "password"+i);
            }

            for (CustomUser customUser : customUsers) {
                for (int j = 0; j < 3; j++) {
                    String plainUuid = uuidUtils.generateUuid();
                    String encryptedUuid = encryptionUtil.encrypt(plainUuid);

                    Card card = new Card();
                    card.setUser(customUser);
                    card.setEncryptedUuid(encryptedUuid);
                    card.setExpirationDate(LocalDate.now().plusYears(5));
                    card.setStatus(CardStatusEnum.ACTIVE);
                    card.setBalance(BigDecimal.valueOf(new Random().nextInt(5000) + 1000));

                    cardRepo.save(card);
                }
            }
        }

        System.out.println("IT WORKS");
    }
}
