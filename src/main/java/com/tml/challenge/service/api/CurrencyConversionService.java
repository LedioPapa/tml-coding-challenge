package com.tml.challenge.service.api;

import java.math.BigDecimal;
import java.util.Currency;

public interface CurrencyConversionService {
    /**
     * Converts the specified amount from the base currency to the target currency.
     *
     * @param baseCurrency   the base currency code
     * @param targetCurrency the target currency code
     * @param amount         the amount to be converted
     * @return the converted amount
     */
    BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal amount);

    /**
     * Checks if the given currency code is valid.
     *
     * @param currencyCode the currency code to be checked
     * @return true if the currency code is valid, false otherwise
     */
    static boolean isValidCurrencyCode(String currencyCode) {
        try {
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
