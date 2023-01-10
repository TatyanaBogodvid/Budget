package com.example.budgetapp.controllers;

import com.example.budgetapp.model.Transaction;
import com.example.budgetapp.services.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
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
