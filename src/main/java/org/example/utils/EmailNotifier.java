package org.example.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Email notification utility for sending test reports
 */
public class EmailNotifier {
    private final String smtpHost;
    private final int smtpPort;
    private final String username;
    private final String password;
    private final String fromName;
    private final String[] toRecipients;
    private final String subjectPrefix;

    public EmailNotifier() {
        this.smtpHost = ConfigReader.getProperty("email.smtp.host");
        this.smtpPort = Integer.parseInt(ConfigReader.getProperty("email.smtp.port"));
        this.username = getEmailUsername();
        this.password = getEmailPassword();
        this.fromName = ConfigReader.getProperty("email.from.name");
        this.toRecipients = getEmailRecipients();
        this.subjectPrefix = ConfigReader.getProperty("email.subject.prefix");
    }

    private String getEmailUsername() {
        String username = ConfigReader.getProperty("email.username");
        if (username != null && username.contains("${EMAIL_USERNAME}")) {
            String envUsername = System.getenv("EMAIL_USERNAME");
            if (envUsername == null || envUsername.isEmpty()) {
                System.err.println("⚠️ EMAIL_USERNAME environment variable not set! Email notifications will be disabled.");
                return null;
            }
            return envUsername;
        }
        return username;
    }

    private String getEmailPassword() {
        String password = ConfigReader.getProperty("email.password");
        if (password != null && password.contains("${EMAIL_APP_PASSWORD}")) {
            String envPassword = System.getenv("EMAIL_APP_PASSWORD");
            if (envPassword == null || envPassword.isEmpty()) {
                System.err.println("⚠️ EMAIL_APP_PASSWORD environment variable not set! Email notifications will be disabled.");
                return null;
            }
            return envPassword;
        }
        return password;
    }

    private String[] getEmailRecipients() {
        String recipients = ConfigReader.getProperty("email.to.recipients");
        if (recipients != null && recipients.contains("${EMAIL_TO_RECIPIENTS}")) {
            String envRecipients = System.getenv("EMAIL_TO_RECIPIENTS");
            if (envRecipients == null || envRecipients.isEmpty()) {
                System.err.println("⚠️ EMAIL_TO_RECIPIENTS environment variable not set! Email notifications will be disabled.");
                return null;
            }
            return envRecipients.split(",");
        }
        return recipients != null ? recipients.split(",") : null;
    }

    /**
     * Send test report via email
     */
    public void sendTestReport(String testName, boolean passed, long duration, String reportPath) {
        if (username == null || password == null || toRecipients == null) {
            System.err.println("⚠️ Email configuration incomplete. Skipping email notification.");
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, fromName));

            // Add recipients
            for (String recipient : toRecipients) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.trim()));
            }

            // Set subject
            String status = passed ? "✅ PASSED" : "❌ FAILED";
            String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            message.setSubject(subjectPrefix + " " + status + " - " + testName + " - " + timestamp);

            // Create email content
            MimeMultipart multipart = new MimeMultipart();

            // Add HTML body
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(buildEmailBody(testName, passed, duration, timestamp), "text/html");
            multipart.addBodyPart(htmlPart);

            // Add report attachment if exists
            File reportFile = new File(reportPath);
            if (reportFile.exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(reportFile);
                attachmentPart.setFileName("TestReport_" + timestamp.replace(" ", "_").replace(":", "-") + ".html");
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email notification sent successfully to: " + String.join(", ", toRecipients));

        } catch (Exception e) {
            System.err.println("❌ Error sending email notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build professional HTML email body
     */
    private String buildEmailBody(String testName, boolean passed, long duration, String timestamp) {
        String statusColor = passed ? "#28a745" : "#dc3545";
        String statusIcon = passed ? "✅" : "❌";
        String statusText = passed ? "PASSED" : "FAILED";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 20px; background-color: #f8f9fa; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { padding: 30px; }
                    .status { background-color: %s; color: white; padding: 15px; border-radius: 6px; text-align: center; font-weight: bold; margin: 20px 0; }
                    .details { background-color: #f8f9fa; padding: 20px; border-radius: 6px; margin: 20px 0; }
                    .detail-row { margin: 10px 0; }
                    .label { font-weight: bold; color: #495057; }
                    .value { color: #6c757d; }
                    .footer { background-color: #495057; color: white; padding: 20px; text-align: center; border-radius: 0 0 8px 8px; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚀 Automation Test Report</h1>
                        <p>Production Monitoring - Flipkart Testing</p>
                    </div>
                    
                    <div class="content">
                        <div class="status">
                            %s %s
                        </div>
                        
                        <div class="details">
                            <div class="detail-row">
                                <span class="label">Test Name:</span> 
                                <span class="value">%s</span>
                            </div>
                            <div class="detail-row">
                                <span class="label">Execution Time:</span> 
                                <span class="value">%s</span>
                            </div>
                            <div class="detail-row">
                                <span class="label">Duration:</span> 
                                <span class="value">%s</span>
                            </div>
                            <div class="detail-row">
                                <span class="label">Environment:</span> 
                                <span class="value">Production (Flipkart)</span>
                            </div>
                        </div>
                        
                        <p><strong>Report:</strong> Please find the detailed HTML report attached.</p>
                        
                        %s
                    </div>
                    
                    <div class="footer">
                        <p>🤖 Generated by BasicAutomationFramework</p>
                        <p>Automated Test Execution System</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            statusColor,
            statusIcon, statusText,
            testName,
            timestamp,
            formatDuration(duration),
            passed ? 
                "<p style='color: #28a745;'><strong>✅ All test steps completed successfully!</strong></p>" :
                "<p style='color: #dc3545;'><strong>❌ Test execution encountered issues. Please review the attached report for details.</strong></p>"
        );
    }

    /**
     * Format duration to human readable format
     */
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        if (minutes > 0) {
            return String.format("%d min %d sec", minutes, seconds);
        } else {
            return String.format("%d.%03d sec", seconds, milliseconds % 1000);
        }
    }
}
