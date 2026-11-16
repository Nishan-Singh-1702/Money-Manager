package MoneyManager.repository;

import MoneyManager.model.Expense;
import MoneyManager.payload.ExpenseDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByProfile_ProfileIdAndDateBetweenOrderByDateDesc(Long profileId, LocalDate fromDate, LocalDate toDate);
    Optional<Expense> findByProfile_ProfileIdAndExpenseId(Long profileId, Long expenseId);

    List<Expense> findTop5ByProfile_ProfileIdOrderByCreatedAtDesc(Long profileId);

    @Query("SELECT SUM(e.expenseAmount) FROM Expense e WHERE e.profile.profileId = :profileId")
    BigDecimal findTotalExpenseAmountByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT SUM(e.expenseAmount) FROM Expense e WHERE e.profile.profileId = :profileId AND e.date BETWEEN :fromDate AND :toDate")
    BigDecimal findTotalExpenseAmountByProfileIdAndDateBetween(@Param("profileId") Long profileId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    List<Expense> findByProfile_ProfileIdAndDateBetweenAndExpenseNameContainingIgnoreCase(Long profileId, LocalDate fromDate, LocalDate tillDate, String searchKeyword, Sort sort);

    @EntityGraph(attributePaths = "category")
    List<Expense> findByProfile_ProfileIdAndDate(Long profileId, LocalDate date);

}
