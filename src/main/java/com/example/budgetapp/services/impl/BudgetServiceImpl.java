package com.example.budgetapp.services.impl;

import com.example.budgetapp.model.Transaction;
import com.example.budgetapp.services.BudgetService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class BudgetServiceImpl implements BudgetService {

    public static final int SALARY =30_000 - 9_750;

    public static final int SAVING = 3_000;

    public static final int DAILY_BUDGET = (SALARY - SAVING) / LocalDate.now().lengthOfMonth();

    public static int balance = 0;

//    public static final int AVG_SALARY = (10000+ 10000+ 10000+ 10000+ 10000+ 15000+ 15000+ 15000+ 15000+ 15000+ 15000+ 20000) / 12;
    public static final int AVG_SALARY = SALARY;
    public static final double AVG_DAYS = 29.3;

    private Map<Month, Map<Long, Transaction>> transactions = new TreeMap<>();
    private static long lastId = 0;

    @Override
    public int getDailyBudget() {
        return DAILY_BUDGET;
    }

    @Override
    public int getBalance() {
        return SALARY - SAVING - getAllSpend();
    }

    public void addTransaction(Transaction transaction){
        Map<Long, Transaction> monthTransactions = transactions.getOrDefault(LocalDate.now().getMonth(), new LinkedHashMap<>());
        monthTransactions.put(lastId++, transaction);
    }

    public int getDailyBalance(){
        return (DAILY_BUDGET * LocalDate.now().getDayOfMonth() - getAllSpend());
    }

    private int getAllSpend(){
        Map<Long, Transaction> monthTransactions = transactions.getOrDefault(LocalDate.now().getMonth(), new LinkedHashMap<>());
        int sum = 0;
        for(Transaction transaction : monthTransactions.values()){
            sum+= transaction.getSum();
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
}
