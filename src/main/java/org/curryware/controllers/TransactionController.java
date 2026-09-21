package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.transactionservice.TransactionRecord;
import org.curryware.transactionservice.TransactionService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private static final Logger logger = LogManager.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/get_transactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam("leagueId") String leagueId,
            @RequestParam("transactionTime") String transactionTime) {
        logger.debug("get_transactions called with leagueId={} transactionTime={}", leagueId, transactionTime);
        try {
            List<TransactionRecord> records = transactionService.getTransactions(leagueId, transactionTime);
            return ResponseEntity.ok(records);
        } catch (NumberFormatException ex) {
            logger.warn("Invalid numeric request parameter: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid numeric parameter."));
        } catch (DataAccessException ex) {
            Throwable rootCause = ex.getRootCause();
            logger.error("Database error: {}", rootCause != null ? rootCause.getMessage() : ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Database unavailable. Please try again later."));
        } catch (Exception ex) {
            logger.error("Unexpected error", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }
}
