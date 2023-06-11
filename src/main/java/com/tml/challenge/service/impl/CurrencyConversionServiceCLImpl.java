package com.tml.challenge.service.impl;

import com.tml.challenge.model.ExchangeRateResponse;
import com.tml.challenge.service.api.CurrencyConversionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Log4j2
@Profile("cl")
@Service
public class CurrencyConversionServiceCLImpl implements CurrencyConversionService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public CurrencyConversionServiceCLImpl(RestTemplate restTemplate, @Value("${api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }


    /**
     * Converts the given amount from the base currency to the target currency.
     * Uses caching and circuit breaker patterns.
     * Implementation for CurrencyLayer API
     *
     * @param baseCurrency   the base currency code
     * @param targetCurrency the target currency code
     * @param amount         the amount to be converted
     * @return the converted amount
     * @throws RuntimeException if the exchange rate cannot be fetched
     */
    @CircuitBreaker(name = "clService", fallbackMethod = "fallbackConvertCurrency")
    @Cacheable(value = "exchangeRatesCache", key = "{ #baseCurrency, #targetCurrency, #amount }")
    @Override
    public BigDecimal convertCurrency(String baseCurrency, String targetCurrency, BigDecimal amount) {
        if (baseCurrency.equals(targetCurrency)) {
            return amount;
        }
        String currencyPair = baseCurrency + "," + targetCurrency;
        String url = "/live?access_key=" + apiKey + "&source=USD&currencies=" + currencyPair;

        log.debug("Fetching exchange rate from CurrencyLayer API: " + url);
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
        log.debug("Exchange rate response: " + response);

        if (response != null && response.isSuccess()) {
            Map<String, BigDecimal> rates = response.getQuotes();
            if (List.of(baseCurrency, targetCurrency).contains("USD")) {
                rates.put("USDUSD", BigDecimal.ONE);
            }
            if (rates != null && rates.containsKey("USD" + baseCurrency) && rates.containsKey("USD" + targetCurrency)) {
                BigDecimal defaultToBaseRate = rates.get("USD" + baseCurrency);
                BigDecimal defaultToTargetRate = rates.get("USD" + targetCurrency);

                return amount.divide(defaultToBaseRate, 2, RoundingMode.HALF_UP).multiply(defaultToTargetRate);
            }
        }

        throw new RuntimeException("Failed to fetch exchange rate.");
    }


    /**
     * Circuit Breaker fallback method for convertCurrency.
     *
     * @param baseCurrency   the base currency code
     * @param targetCurrency the target currency code
     * @param amount         the amount to be converted
     * @param throwable      the exception thrown by the main method
     * @return -1
     */
    public BigDecimal fallbackConvertCurrency(String baseCurrency, String targetCurrency, BigDecimal amount, Throwable throwable) {
        log.error("Fallback method called for convertCurrency with exception: {}, params: baseCurrency: {}, targetCurrency: {}, amount: {}",
                throwable.toString(), baseCurrency, targetCurrency, amount, throwable);
        return BigDecimal.valueOf(-1);
    }
}
