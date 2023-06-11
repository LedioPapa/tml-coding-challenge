package com.tml.challenge.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents the response from the exchange rate API.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExchangeRateResponse {
    private boolean success;
    private long timestamp;
    private String base;
    private String source;
    private Map<String, BigDecimal> rates;
    private Map<String, BigDecimal> quotes;
}
