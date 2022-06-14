package com.sportologic.sprtadmin.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public final class SprtAdminUtils {

    private SprtAdminUtils(){}

    /**
     * Normalize input string
     * - to lowercase
     * - remove whitespaces and czech characters.
     * @param customerName
     * @return
     */
    public static String normToLowerCaseWithoutCZChars(String customerName) {
        Objects.requireNonNull(customerName);

        String custNameNormalized = Normalizer.normalize(customerName, Normalizer.Form.NFD);
        custNameNormalized = custNameNormalized.replaceAll("\\p{M}", "");
        // Remove all special characters.
        custNameNormalized = custNameNormalized.replaceAll("[^a-zA-Z0-9]", " ");
        return custNameNormalized.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }
}
