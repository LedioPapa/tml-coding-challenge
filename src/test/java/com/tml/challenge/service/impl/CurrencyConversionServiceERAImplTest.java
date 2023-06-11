package com.tml.challenge.service.impl;

import com.tml.challenge.model.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CurrencyConversionServiceERAImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyConversionServiceERAImpl conversionService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(conversionService, "restTemplate", restTemplate);
    }

    @Test
    public void testConvertCurrencyWithSameBaseAndTarget() {
        BigDecimal convertedAmount = conversionService.convertCurrency("USD", "USD", BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), convertedAmount);
    }

    @Test
    public void testConvertCurrencySuccessfully() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setSuccess(true);
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("GBP", BigDecimal.valueOf(0.72));
        rates.put("EUR", BigDecimal.valueOf(0.83));
        response.setRates(rates);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(response);

        BigDecimal convertedAmount = conversionService.convertCurrency("GBP", "EUR", BigDecimal.valueOf(100));
        assertTrue(convertedAmount.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testConvertCurrencyWithFailedExchangeRateFetch() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setSuccess(false);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(response);

        try {
            conversionService.convertCurrency("GBP", "EUR", BigDecimal.valueOf(100));
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("Failed to fetch exchange rate."));
        }
    }


    @Test
    public void testConvertCurrencyWithSuccessfulResponseButMissingRates() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setSuccess(true);
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("GBP", BigDecimal.valueOf(0.72));
        response.setRates(rates);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(response);

        try {
            conversionService.convertCurrency("GBP", "EUR", BigDecimal.valueOf(100));
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("Failed to fetch exchange rate."));
        }
    }


    @Test
    public void testConvertCurrencyWithSuccessfulResponseButNullRates() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setSuccess(true);
        response.setRates(null);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(response);

        try {
            conversionService.convertCurrency("GBP", "EUR", BigDecimal.valueOf(100));
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("Failed to fetch exchange rate."));
        }
    }

    @Test
    public void testConvertCurrencyWithActualConversion() {
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setSuccess(true);
        response.setTimestamp(LocalDateTime.now().get(ChronoField.MILLI_OF_DAY));
        response.setBase("USD");
        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("USD", BigDecimal.valueOf(1));
        rates.put("EUR", BigDecimal.valueOf(0.83));
        response.setRates(rates);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(response);

        BigDecimal convertedAmount = conversionService.convertCurrency("USD", "EUR", BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(83).setScale(4), convertedAmount);
    }

    @Test
    public void testConvertCurrencyWithNullResponse() {
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(null);

        try {
            conversionService.convertCurrency("GBP", "EUR", BigDecimal.valueOf(100));
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("Failed to fetch exchange rate."));
        }
    }


    @Test
    public void testFallbackConvertCurrency() {
        BigDecimal fallbackResult = conversionService.fallbackConvertCurrency("GBP", "EUR", BigDecimal.valueOf(100), new RuntimeException("test exception"));
        assertEquals(BigDecimal.valueOf(-1), fallbackResult);
    }

}