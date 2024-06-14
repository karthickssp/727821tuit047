package com.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.service.CalculatorService;

@RestController
@RequestMapping("/numbers")

public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/{numberid}")
    public ResponseEntity<Map<String, Object>> getNumbers(@PathVariable String numberid) {
        return ResponseEntity.ok(calculatorService.calculateAverage(numberid));
    }

}
