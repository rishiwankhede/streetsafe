package com.streetsafe.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class LicenseService {

    /** Exactly 14 digits, the first of which is 1 or 2. */
    private static final Pattern FSSAI_PATTERN = Pattern.compile("^[1-2][0-9]{13}$");

    /**
     * Validates the format of an FSSAI licence / registration number.
     *
     * @param fssai the number entered by the vendor
     * @return true if it is exactly 14 digits starting with 1 or 2
     */
    public boolean verifyFSSAI(String fssai) {
        return fssai != null && FSSAI_PATTERN.matcher(fssai).matches();
    }
}
