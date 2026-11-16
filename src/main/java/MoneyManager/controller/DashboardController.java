package MoneyManager.controller;

import MoneyManager.config.AppConstant;
import MoneyManager.exception.APIException;
import MoneyManager.payload.ExpenseDTO;
import MoneyManager.service.DashboardService;
import MoneyManager.service.ExpenseService;
import MoneyManager.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    IncomeService incomeService;

    @GetMapping("/public/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(){
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }

    @GetMapping("/public/filter/expenses/incomes")
    public ResponseEntity<?> getFilteredExpensesAndIncomes(@RequestParam(name = "type") String type,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                          @RequestParam(required = false) String keyword,
                                                                          @RequestParam(name = "sortBy", defaultValue = AppConstant.sortBy,required = false)String sortBy,
                                                                          @RequestParam(name = "sortOrder", defaultValue = AppConstant.sortOrder,required = false)String sortOrder){


        if(type.equalsIgnoreCase("expense")) {
            return ResponseEntity.ok(expenseService.filterExpenses(fromDate, endDate, keyword, sortBy, sortOrder));
        }
        else if(type.equalsIgnoreCase("income")){
            return ResponseEntity.ok(incomeService.filterIncomes(fromDate, endDate, keyword, sortBy, sortOrder));
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIException("Type should be either income or expense"));
        }
    }
}
