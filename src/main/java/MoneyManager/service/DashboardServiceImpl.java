package MoneyManager.service;

import MoneyManager.exception.APIException;
import MoneyManager.payload.ExpenseDTO;
import MoneyManager.payload.IncomeDTO;
import MoneyManager.payload.RecentTransactionDTO;
import MoneyManager.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    IncomeService incomeService;

    @Autowired
    ExpenseService expenseService;

    @Override
    public Map<String, Object> getDashboardData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)){
            throw new APIException("No user logged in !!");
        }
        Long profileId = userDetails.getProfileId();

        List<IncomeDTO> latestIncomes = incomeService.getLatest5Incomes();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5Expenses();

        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                latestIncomes.stream().map(income ->
                        RecentTransactionDTO.builder()
                                .recentTransactionId(income.getIncomeId())
                                .profileId(profileId)
                                .icon(income.getIcon())
                                .name(income.getIncomeName())
                                .amount(income.getIncomeAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("Income")
                                .build()),
                latestExpenses.stream().map(expense ->
                        RecentTransactionDTO.builder()
                                .recentTransactionId(expense.getExpenseId())
                                .profileId(profileId)
                                .icon(expense.getIcon())
                                .name(expense.getExpenseName())
                                .amount(expense.getExpenseAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("Expense")
                                .build())
        ).sorted((a, b) -> {
            int cmp = b.getDate().compareTo(a.getDate());
            if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return cmp;
        }).toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("totalBalance",incomeService.getTotalIncomeAmount().subtract(expenseService.getTotalExpenseAmount()));
        body.put("totalIncome",incomeService.getTotalIncomeAmount());
        body.put("totalExpense",expenseService.getTotalExpenseAmount());
        body.put("recent5Expenses",latestExpenses);
        body.put("recent5Income",latestIncomes);
        body.put("recentTransactions", recentTransactions);
        return body;
    }
}