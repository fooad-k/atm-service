package com.atmservice.controller;

import com.atmservice.model.DispenserDTO;
import com.atmservice.model.LoginDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/v1/atm")
public class ATMRestController {

    private static final String BANK_SERVICE = "bankService";

    @PostMapping("/cardValidation")
    @CircuitBreaker(name = BANK_SERVICE, fallbackMethod = "orderFallback")
    public  ResponseEntity<?> cardValidation(@RequestParam String cardNumber, HttpServletRequest request) {
        List<String> card = (List<String>) request.getSession().getAttribute("CARDS_SESSION");
        if (card == null) {
            card = new ArrayList<>();
            request.getSession().setAttribute("CARDS_SESSION", card);
        }
        //-------------------------------------------------------------------------------------------------------------
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:8081/v1/bank/account/card-exist";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("cardNumber", "{cardNumber}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("cardNumber", cardNumber);

        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        //-------------------------------------------------------------------------------------------------------------
        card.clear();
        if (Objects.equals(response.getBody(), "true")) {
            card.add(cardNumber);
            request.getSession().setAttribute("CARDS_SESSION", card);
            return new ResponseEntity<>("Card number is valid", HttpStatus.OK);
        } else {
            request.getSession().setAttribute("CARDS_SESSION", card);
            return new ResponseEntity<>("Card number is invalid", HttpStatus.OK);
        }
    }

    @PostMapping("/login")
    @CircuitBreaker(name = BANK_SERVICE, fallbackMethod = "orderFallback")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        List<String> card = (List<String>) session.getAttribute("CARDS_SESSION");
        if(card == null || card.isEmpty()) {
            return new ResponseEntity<>("Your card is invalid", HttpStatus.OK);
        }
        //-------------------------------------------------------------------------------------------------------------
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:8081/v1/bank/account/login";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("cardNumber", "{cardNumber}")
                .queryParam("pin", "{pin}")
                .queryParam("fingerPrint", "{fingerPrint}")
                .queryParam("authType", "{authType}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("cardNumber", card.get(0));
        params.put("pin", loginDTO.getPin());
        params.put("fingerPrint", loginDTO.getFingerPrint());
        params.put("authType", loginDTO.getAuthenticationType());

        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        //-------------------------------------------------------------------------------------------------------------
        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }


    @PostMapping("/dispenser/cash")
    @CircuitBreaker(name = BANK_SERVICE, fallbackMethod = "orderFallback")
    public ResponseEntity<?> cashDispenser(@Valid @RequestBody DispenserDTO dispenserDTO, HttpSession session) {
        List<String> card = (List<String>) session.getAttribute("CARDS_SESSION");
        if (card == null || card.isEmpty()) {
            return new ResponseEntity<>("Your card is invalid", HttpStatus.OK);
        }

        if (dispenserDTO.getConfirmed()) {
            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = "http://localhost:8081/v1/bank/account/dispenser/cash";
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("cardNumber", "{cardNumber}")
                    .queryParam("amount", "{amount}")
                    .encode()
                    .toUriString();

            Map<String, String> params = new HashMap<>();
            params.put("cardNumber", card.get(0));
            params.put("amount", dispenserDTO.getAmount().toString());


            HttpEntity<String> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    String.class,
                    params
            );
            if (Objects.equals(response.getBody(), "true")) {
                return new ResponseEntity<>("Your balance incremented", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed get back you money", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("Your money not was confirmed", HttpStatus.OK);
        }
    }

    @PostMapping("/dispenser/check")
    @CircuitBreaker(name = BANK_SERVICE, fallbackMethod = "orderFallback")
    public ResponseEntity<?> checkDispenser(@Valid @RequestBody DispenserDTO dispenserDTO, HttpSession session) {
        List<String> card = (List<String>) session.getAttribute("CARDS_SESSION");
        if (card == null || card.isEmpty()) {
            return new ResponseEntity<>("Your card is invalid", HttpStatus.OK);
        }

        if (dispenserDTO.getConfirmed()) {
            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = "http://localhost:8081/v1/bank/account/dispenser/check";
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("cardNumber", "{cardNumber}")
                    .queryParam("amount", "{amount}")
                    .encode()
                    .toUriString();

            Map<String, String> params = new HashMap<>();
            params.put("cardNumber", card.get(0));
            params.put("amount", dispenserDTO.getAmount().toString());


            HttpEntity<String> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    String.class,
                    params
            );
            if (Objects.equals(response.getBody(), "true")) {
                return new ResponseEntity<>("Your balance incremented", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed get back you check", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("Your check was not confirmed", HttpStatus.OK);
        }

    }


    @PostMapping("/withdraw")
    @CircuitBreaker(name = BANK_SERVICE, fallbackMethod = "orderFallback")
    public ResponseEntity<?> withdraw(@Valid @RequestBody DispenserDTO dispenserDTO, HttpSession session) {
        List<String> card = (List<String>) session.getAttribute("CARDS_SESSION");
        if (card == null || card.isEmpty()) {
            return new ResponseEntity<>("Your card is invalid", HttpStatus.OK);
        }

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:8081/v1/bank/account/withdraw";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("cardNumber", "{cardNumber}")
                .queryParam("amount", "{amount}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("cardNumber", card.get(0));
        params.put("amount", dispenserDTO.getAmount().toString());

        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);

    }



    @GetMapping("/withdraw/complete")
    @CircuitBreaker(name = BANK_SERVICE, fallbackMethod = "orderFallback")
    public ResponseEntity<?> completeWithdraw(@Valid @RequestParam Long transactionId, HttpSession session) {
        List<String> card = (List<String>) session.getAttribute("CARDS_SESSION");
        if (card == null || card.isEmpty()) {
            return new ResponseEntity<>("Your card is invalid", HttpStatus.OK);
        }

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:8081/v1/bank/account/withdraw/complete";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("cardNumber", "{cardNumber}")
                .queryParam("transactionId", "{transactionId}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("cardNumber", card.get(0));
        params.put("transactionId", transactionId.toString());

        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);

    }










    public ResponseEntity<String> orderFallback(Exception e) {
        return new ResponseEntity<String>("Bank service is down", HttpStatus.OK);
    }

    @PostMapping("/invalidate/session")
    public ResponseEntity<?> destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

}

