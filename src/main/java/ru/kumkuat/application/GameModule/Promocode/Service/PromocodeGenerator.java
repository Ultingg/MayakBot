package ru.kumkuat.application.GameModule.Promocode.Service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PromocodeGenerator {

    private static String DICTIONARY = "QWERTYUIOPLKJHGFDSAZXCVBNMmnbvcxzlkjhgfdsapoiuytrewq1234567890";


    public String generateValueForDisposalPromocode() {

        char[] array = DICTIONARY.toCharArray();
        char[] result = new char[10];
        Random random = new Random();
            for (int i = 0; i <= 9; i++) {
                int randomValue = random.ints(0, 61).findFirst().getAsInt();
                result[i] = array[randomValue];
            }
        return String.copyValueOf(result);
    }
}
