package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.transactionservice.TransactionRecord;
import org.curryware.transactionservice.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private static final Logger logger = LogManager.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/get_transactions")
    public @ResponseBody List<TransactionRecord> getTransactions(
            @RequestParam("leagueId") String leagueId,
            @RequestParam("transactionTime") String transactionTime) {
        logger.debug("get_transactions called with leagueId={} transactionTime={}", leagueId, transactionTime);
        return transactionService.getTransactions(leagueId, transactionTime);
    }
}
