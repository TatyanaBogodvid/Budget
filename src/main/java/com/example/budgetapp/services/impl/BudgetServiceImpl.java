package com.example.budgetapp.services.impl;

import com.example.budgetapp.model.Transaction;
import com.example.budgetapp.services.BudgetService;
import com.example.budgetapp.services.FilesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class BudgetServiceImpl implements BudgetService {

    final private FilesService filesService;

    public static final int SALARY =30_000 - 9_750;

    public static final int SAVING = 3_000;

    public static final int DAILY_BUDGET = (SALARY - SAVING) / LocalDate.now().lengthOfMonth();

    public static int balance = 0;

//    public static final int AVG_SALARY = (10000+ 10000+ 10000+ 10000+ 10000+ 15000+ 15000+ 15000+ 15000+ 15000+ 15000+ 20000) / 12;
    public static final int AVG_SALARY = SALARY;
    public static final double AVG_DAYS = 29.3;

    private static TreeMap<Month, LinkedHashMap<Long, Transaction>> transactions = new TreeMap<>();
    private static long lastId = 0;

    public BudgetServiceImpl(FilesService filesService) {
        this.filesService = filesService;
    }

    @PostConstruct
    private void init(){
        readFromFile();
    }

    @Override
    public int getDailyBudget() {
        return DAILY_BUDGET;
    }

    @Override
    public int getBalance() {
        return SALARY - SAVING - getAllSpend();
    }

    @Override
    public long addTransaction(Transaction transaction){
        LinkedHashMap<Long, Transaction> monthTransactions = transactions.getOrDefault(LocalDate.now().getMonth(), new LinkedHashMap<>());
        monthTransactions.put(lastId, transaction);
        transactions.put(LocalDate.now().getMonth(), monthTransactions);
        saveToFail();
        return lastId++;
    }

    @Override
    public Transaction getTransaction(long id){
        for(Map<Long, Transaction> transactionsByMonth : transactions.values()){
            Transaction transaction = transactionsByMonth.get(id);
            if (transaction != null){
                return transaction;
            }
        }
        return null;
    }

    @Override
    public Transaction editTransaction(long id, Transaction transaction){
        for(Map<Long, Transaction> transactionsByMonth : transactions.values()){
            if (transactionsByMonth.containsKey(id)) {
                transactionsByMonth.put(id, transaction);
                saveToFail();
                return transaction;
            }
        }
        return null;
    }

    @Override
    public boolean deleteTransaction(long id){
        for(Map<Long, Transaction> transactionsByMonth : transactions.values()){
            if (transactionsByMonth.containsKey(id)) {
                transactionsByMonth.remove(id);
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteAllTransaction() {
        transactions = new TreeMap<>();
    }

    @Override
    public int getDailyBalance(){
        return (DAILY_BUDGET * LocalDate.now().getDayOfMonth() - getAllSpend());
    }

    @Override
    public int getAllSpend(){
        Map<Long, Transaction> monthTransactions = transactions.getOrDefault(LocalDate.now().getMonth(), new LinkedHashMap<>());
        int sum = 0;
        for(Transaction transaction : monthTransactions.values()){
            sum += transaction.getSum();
        }
        return sum;
    }

    @Override
    public int getVacationBonus(int daysCount){
        double avgDaySalary = AVG_SALARY / AVG_DAYS;
        return (int) (daysCount * avgDaySalary);
    }

    @Override
    public int getSalaryWithVacation(int vacationDaysCount, int vacationWorkingDaysCount, int workingDaysInMonth){
        getVacationBonus(vacationDaysCount);
        int salary = SALARY / workingDaysInMonth * (workingDaysInMonth - vacationWorkingDaysCount);
        return salary + getVacationBonus(vacationDaysCount);
    }

    private void saveToFail(){
        try {
            String json = new ObjectMapper().writeValueAsString(transactions);
            filesService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile(){
        String json = filesService.readFromFile();
        try {
            transactions = new ObjectMapper().readValue(json, new TypeReference<TreeMap<Month, LinkedHashMap<Long, Transaction>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
