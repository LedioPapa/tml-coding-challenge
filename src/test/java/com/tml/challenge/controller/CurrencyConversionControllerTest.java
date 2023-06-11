package com.tml.challenge.controller;

import com.tml.challenge.service.api.CurrencyConversionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConversionService conversionService;

    @Test
    public void testConvertCurrencyWithInvalidBaseCurrency() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "")
                        .param("targetCurrency", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: baseCurrency."));
    }

    @Test
    public void testConvertCurrencyWithInvalidTargetCurrency() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "USD")
                        .param("targetCurrency", "")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: targetCurrency."));
    }

    @Test
    public void testConvertCurrencyWithInvalidAmount() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "USD")
                        .param("targetCurrency", "EUR")
                        .param("amount", "tst"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: amount."));
    }

    @Test
    public void testConvertCurrencyWithNullBaseCurrency() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("targetCurrency", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: baseCurrency."));
    }

    @Test
    public void testConvertCurrencyWithNullTargetCurrency() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "USD")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: targetCurrency."));
    }

    @Test
    public void testConvertCurrencyWithNullAmount() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "USD")
                        .param("targetCurrency", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: amount."));
    }

    @Test
    public void testConvertCurrencyWithIncorrectLengthBaseCurrency() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "US")
                        .param("targetCurrency", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: baseCurrency."));
    }

    @Test
    public void testConvertCurrencyWithIncorrectLengthTargetCurrency() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "USD")
                        .param("targetCurrency", "EURO")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: targetCurrency."));
    }

    @Test
    public void testConvertCurrencyWithNegativeAmount() throws Exception {
        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "USD")
                        .param("targetCurrency", "EUR")
                        .param("amount", "-100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value for parameter: amount."));
    }

    @Test
    public void testConvertCurrencySuccessfully() throws Exception {
        Mockito.when(conversionService.convertCurrency("USD", "EUR", BigDecimal.valueOf(100)))
                .thenReturn(BigDecimal.valueOf(85.50));

        mockMvc.perform(get("/v1/convert")
                        .param("baseCurrency", "USD")
                        .param("targetCurrency", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("85.5"));
    }
}
