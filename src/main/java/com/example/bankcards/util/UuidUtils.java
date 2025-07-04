package com.example.bankcards.util;

import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class UuidUtils {

    public String generateUuid(){

        Random random = new Random();

        String format = "%04d%04d%04d%04d";

        return String.format(format,
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
    }

    public String maskUuid(String decryptedNumber) {

        return "**** **** **** " + decryptedNumber.substring(12);
    }
}
