package com.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CalculatorService {

    private static final int WINDOW_SIZE = 10;
    private final Deque<Integer> window = new ConcurrentLinkedDeque<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TEST_SERVER_URL = "http://20.244.56.144/test";
    private static final String AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiZXhwIjoxNzE4MzUwNjE3LCJpYXQiOjE3MTgzNTAzMTcsImlzcyI6IkFmZm9yZG1lZCIsImp0aSI6IjljYmI5ZGU3LWQ4YjUtNGFhMC04NGQzLTZjMGRhMzYyZTlkNyIsInN1YiI6IjcyNzgyMXR1aXQwNDdAc2tjdC5lZHUuaW4ifSwiY29tcGFueU5hbWUiOiJTU1BTU1AiLCJjbGllbnRJRCI6IjljYmI5ZGU3LWQ4YjUtNGFhMC04NGQzLTZjMGRhMzYyZTlkNyIsImNsaWVudFNlY3JldCI6ImJmQWF1VUh3VnpwcFZRb24iLCJvd25lck5hbWUiOiJLYXJ0aGljayIsIm93bmVyRW1haWwiOiI3Mjc4MjF0dWl0MDQ3QHNrY3QuZWR1LmluIiwicm9sbE5vIjoiNzI3ODIxdHVpdDA0NyJ9.rGqJ6TFbEcGKp67vbFRNave1PjfKocpLP_Hoq6VnXj0";


    public Map<String, Object> calculateAverage(String numberid) {
        if (!Arrays.asList("primes", "fibo", "even", "rand").contains(numberid)) {
            throw new IllegalArgumentException("Invalid number ID");
        }

        List<Integer> prevState = new ArrayList<>(window);

        List<Integer> newNumbers = fetchNumbersFromTestServer(numberid);
        if (newNumbers.isEmpty()) {
            throw new RuntimeException("Failed to fetch");
        }

        for (Integer number : newNumbers) {
            if (!window.contains(number)) {
                if (window.size() >= WINDOW_SIZE) {
                    window.poll();
                }
                window.add(number);
            }
        }

        List<Integer> currState = new ArrayList<>(window);
        double average = window.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        Map<String, Object> response = new HashMap<>();
        response.put("numbers", newNumbers);
        response.put("windowPrevState", prevState);
        response.put("windowCurrState", currState);
        response.put("avg", average);

        return response;
    }

    private List<Integer> fetchNumbersFromTestServer(String numberid) {
    try {
        String url = TEST_SERVER_URL + "/" + numberid;
        long startTime = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + AUTH_TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
        long responseTime = System.currentTimeMillis() - startTime;

        if (responseTime > 500) {
            return Collections.emptyList();
        }
        List<Integer> numbers = response.getBody();
        return numbers != null ? numbers : Collections.emptyList();
    } catch (Exception e) {
        return Collections.emptyList();
    }
}
}
