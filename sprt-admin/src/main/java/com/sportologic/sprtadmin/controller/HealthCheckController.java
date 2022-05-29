package com.sportologic.sprtadmin.controller;

import com.sportologic.sprtadmin.exception.SprtValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check REST API.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class HealthCheckController {

    /**
     * Validation of customer name.
     */
    @GetMapping("/health-check")
    public ResponseEntity<String> validateCustName() {
        return new ResponseEntity<String>("{\"status\": \"Alles gute\"}", HttpStatus.OK);
    }
}
