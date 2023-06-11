package com.tml.challenge.controller;

import com.tml.challenge.service.api.CurrencyConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

import static com.tml.challenge.service.api.CurrencyConversionService.isValidCurrencyCode;

@RestController
@RequestMapping("/v1")
public class CurrencyConversionController {

    private final CurrencyConversionService conversionService;

    @Autowired
    public CurrencyConversionController(CurrencyConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Converts the specified amount from the base currency to the target currency.
     *
     * @param baseCurrency   the base currency code
     * @param targetCurrency the target currency code
     * @param amount         the amount to be converted
     * @return the converted amount
     */
    @Operation(summary = "Convert Currency", description = "Converts the specified amount from the base currency to the target currency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful conversion"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping("/convert")
    public ResponseEntity<String> convertCurrency(
            @Parameter(description = "Base currency code", required = true) @RequestParam("baseCurrency") Optional<String> baseCurrency,
            @Parameter(description = "Target currency code", required = true) @RequestParam("targetCurrency") Optional<String> targetCurrency,
            @Parameter(description = "Amount to be converted", required = true) @RequestParam("amount") Optional<BigDecimal> amount
    ) {
        if (baseCurrency.isEmpty() || !isValidCurrencyCode(baseCurrency.get().toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid value for parameter: baseCurrency.");
        }
        if (targetCurrency.isEmpty() || !isValidCurrencyCode(targetCurrency.get().toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid value for parameter: targetCurrency.");
        }
        if (amount.isEmpty() || amount.get().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Invalid value for parameter: amount.");
        }
        BigDecimal convertedAmount = conversionService.convertCurrency(baseCurrency.get().toUpperCase(), targetCurrency.get().toUpperCase(), amount.get());
        return ResponseEntity.ok(convertedAmount.toString());
    }
}
