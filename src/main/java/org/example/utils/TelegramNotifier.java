package org.example.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Telegram notification utility for sending test results
 * Simplified version using HttpURLConnection
 */
public class TelegramNotifier {
    private final String botToken;
    private final String chatId;

    public TelegramNotifier(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    /**
     * Send test completion notification
     */
    public void sendTestResults(String testName, boolean passed, long duration, String reportPath) {
        String message = buildTestResultMessage(testName, passed, duration, reportPath);
        sendMessage(message);
    }

    /**
     * Send custom message using simple HTTP
     */
    public void sendMessage(String message) {
        try {
            String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            
            // Create POST data
            String postData = "chat_id=" + URLEncoder.encode(chatId, StandardCharsets.UTF_8) +
                            "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            
            // Send POST data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("✅ Telegram notification sent successfully");
            } else {
                System.err.println("❌ Failed to send Telegram notification. Status: " + responseCode);
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error sending Telegram notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build formatted test result message
     */
    private String buildTestResultMessage(String testName, boolean passed, long duration, String reportPath) {
        StringBuilder message = new StringBuilder();
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        
        // Header with status
        if (passed) {
            message.append("🎉 TEST PASSED 🎉\n\n");
        } else {
            message.append("❌ TEST FAILED ❌\n\n");
        }
        
        // Test details
        message.append("📋 Test: ").append(testName).append("\n");
        message.append("⏰ Time: ").append(timestamp).append("\n");
        message.append("⏱️ Duration: ").append(formatDuration(duration)).append("\n");
        message.append("🌐 Environment: Production (Flipkart)\n");
        
        // Status specific message
        if (passed) {
            message.append("\n✅ All test steps executed successfully!");
            message.append("\n🔍 Search functionality working as expected");
        } else {
            message.append("\n💥 Test execution encountered issues");
            message.append("\n🔍 Please check the detailed report");
        }
        
        // Footer
        message.append("\n\n🤖 Automated by BasicAutomationFramework");
        
        return message.toString();
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

    /**
     * Send test start notification
     */
    public void sendTestStartNotification(String testName) {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String message = String.format(
            "🚀 TEST STARTED 🚀\n\n" +
            "📋 Test: %s\n" +
            "⏰ Started at: %s\n" +
            "🌐 Environment: Production (Flipkart)\n\n" +
            "⏳ Test execution in progress...",
            testName, timestamp
        );
        sendMessage(message);
    }

    /**
     * Send daily test schedule notification
     */
    public void sendScheduledTestNotification() {
        String message = 
            "⏰ *SCHEDULED TEST EXECUTION* ⏰\n\n" +
            "🕐 Daily automated test starting at 4:00 PM\n" +
            "🌐 Environment: Production (Flipkart)\n" +
            "🧪 Test: Nike Shoes Search\n\n" +
            "📊 Results will be shared shortly...";
        sendMessage(message);
    }
}
