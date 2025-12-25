package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.transactionservice.TransactionService;
import org.curryware.transactionservice.TransactionRecord;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    private static final Logger logger = LogManager.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {this.transactionService = transactionService;}

    @GetMapping("/get_transactions")
    public @ResponseBody List<TransactionRecord> get(
            @RequestParam(name = "gameId") int gameId,
            @RequestParam(name = "leagueId") int leagueId) {
        logger.debug("get_transactions called with gameId: {} and leagueId: {}", gameId, leagueId);
        return transactionService.getTransactions(gameId, leagueId);
    }
}
