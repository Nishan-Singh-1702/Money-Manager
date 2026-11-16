package MoneyManager.repository;

import MoneyManager.model.Income;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByProfile_ProfileIdAndDateBetweenOrderByDateDesc(Long profileId, LocalDate fromDate, LocalDate to);

    Optional<Income> findByProfile_ProfileIdAndIncomeId(Long profileId, Long incomeId);

    List<Income> findTop5ByProfile_ProfileIdOrderByCreatedAtDesc(Long profileId);

    @Query("SELECT SUM(e.incomeAmount) FROM Income e WHERE e.profile.profileId = :profileId")
    BigDecimal findTotalIncomeAmountByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT SUM(e.incomeAmount) FROM Income e WHERE e.profile.profileId = :profileId AND e.date BETWEEN :fromDate AND :toDate")
    BigDecimal findTotalIncomeAmountByProfileIdAndDateBetween(@Param("profileId") Long profileId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    List<Income> findByProfile_ProfileIdAndDateBetweenAndIncomeNameContainingIgnoreCase(Long profileId, LocalDate fromDate, LocalDate tillDate, String searchKeyword, Sort sort);
}
