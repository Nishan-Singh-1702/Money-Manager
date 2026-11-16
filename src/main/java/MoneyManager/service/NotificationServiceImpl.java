package MoneyManager.service;

import MoneyManager.model.Expense;
import MoneyManager.model.Profile;
import MoneyManager.repository.ExpenseRepository;
import MoneyManager.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl {
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final String CELL = "border:1px solid #ddd;padding:8px;";

    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    EmailService emailService;

    @Autowired
    ExpenseRepository expenseRepository;

    @Value("${spring.app.money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Job Started: sendDailyIncomeExpenseReminder()");
        for(Profile profile : profileRepository.findByIsActiveTrue()){
            try{
                String body = "Hi " + HtmlUtils.htmlEscape(profile.getFullName()) + ",<br><br>"
                        + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                        + "<a href='" + frontendUrl + "' style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;'>Go to Money Manager</a>"
                        + "<br><br>Best regards,<br>Money Manager Team";
                emailService.sendEmail(profile.getEmail(), "Daily Reminder: Add your income and expenses", body);
            }
            catch(Exception e){
                log.error("Reminder failed for profileId {}: {}", profile.getProfileId(), e.getMessage());
            }
        }
        log.info("Job completed: sendDailyIncomeExpenseReminder()");
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
    public void sendDailyExpenseSummary(){
        log.info("Job Started: sendDailyExpenseSummary()");

        for(Profile profile : profileRepository.findByIsActiveTrue()){
            try{

                List<Expense> todayExpenses = expenseRepository.findByProfile_ProfileIdAndDate(profile.getProfileId(), LocalDate.now(IST));
                if(todayExpenses.isEmpty()) {
                    continue;
                }

                StringBuilder table = new StringBuilder("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'>")
                        .append("<th style='").append(CELL).append("'>S.No</th>")
                        .append("<th style='").append(CELL).append("'>Name</th>")
                        .append("<th style='").append(CELL).append("'>Amount</th>")
                        .append("<th style='").append(CELL).append("'>Category</th></tr>");
                int i = 1;
                for(Expense e : todayExpenses){
                    String category = e.getCategory().getCategoryName() != null ? e.getCategory().getCategoryName() : "N/A";
                    table.append("<tr>")
                            .append("<td style='").append(CELL).append("'>").append(i++).append("</td>")
                            .append("<td style='").append(CELL).append("'>").append(HtmlUtils.htmlEscape(e.getExpenseName())).append("</td>")
                            .append("<td style='").append(CELL).append("'>").append(e.getExpenseAmount()).append("</td>")
                            .append("<td style='").append(CELL).append("'>").append(HtmlUtils.htmlEscape(category)).append("</td>")
                            .append("</tr>");
                }
                table.append("</table>");

                String body = "Hi " + HtmlUtils.htmlEscape(profile.getFullName()) + ",<br/><br/>"
                        + "Here is a summary of your expenses for today:<br/><br/>" + table
                        + "<br/><br/>Best regards,<br/>Money Manager Team";
                emailService.sendEmailForExpenseSummary(profile.getEmail(), "Your daily expense summary", body);
            }
            catch(Exception e){
                log.error("Summary failed for profileId {}: {}", profile.getProfileId(), e.getMessage());
            }
        }
        log.info("Job completed: sendDailyExpenseSummary()");
    }
}