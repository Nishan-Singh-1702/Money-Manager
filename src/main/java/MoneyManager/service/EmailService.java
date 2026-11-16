package MoneyManager.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailForExpenseSummary(String to, String subject, String body);
}
