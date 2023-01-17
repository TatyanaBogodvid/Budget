package com.example.budgetapp.controllers;

import com.example.budgetapp.model.Category;
import com.example.budgetapp.model.Transaction;
import com.example.budgetapp.services.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Транзакции", description = "CRUD - операции и другие эндпоинты для работы с транзакциями")
public class TransactionController {
    private final BudgetService budgetService;

    public TransactionController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }


    @PostMapping
    public ResponseEntity<Long> addTransaction(@RequestBody Transaction transaction){
       long id = budgetService.addTransaction(transaction);
       return ResponseEntity.ok().body(id);
    }

    @GetMapping
    @Operation(
            summary = "Поиск по месяцу и/или категории",
            description = "Можно искать по одному параметру, обоим или без параметров"
    )
    @Parameters(value = {
            @Parameter(name = "month", example = "Декабрь")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Транзакции были найдены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Transaction.class))
                            )
                    }
            )
    }

    )
    public ResponseEntity<Transaction> getAllTransactions(@RequestParam(required = false)Month month,
                                                          @RequestParam(required = false)Category category){
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable long id){
        Transaction transaction = budgetService.getTransaction(id);
        if(transaction == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> editTransaction(@PathVariable long id, @RequestBody Transaction newTransaction) {
        Transaction transaction = budgetService.editTransaction(id, newTransaction);
        if(transaction == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable long id){
       if (budgetService.deleteTransaction(id)){
           return ResponseEntity.ok().build();
       }
       return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTransaction() {
        budgetService.deleteAllTransaction();
        return ResponseEntity.ok().build();
    }

}
