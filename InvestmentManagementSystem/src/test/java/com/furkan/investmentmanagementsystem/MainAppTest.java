package com.furkan.investmentmanagementsystem;

import com.furkan.investmentmanagementsystem.models.PortfolioItem;
import com.furkan.investmentmanagementsystem.models.Transaction;
import com.furkan.investmentmanagementsystem.services.PortfolioServiceImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class MainAppTest {
    private PortfolioServiceImpl portfolioService;
    private String testUserId;
    private ObservableList<PortfolioItem> testPortfolio;
    private ObservableList<Transaction> testTransactions;
    private static final double DELTA = 0.0001; // For floating point comparisons

    @BeforeAll
    public static void setupJavaFX() {
        // Initialize JavaFX Platform
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already started
        }
    }

    @BeforeEach
    void setUp() {
        // Run on JavaFX Application Thread
        Platform.runLater(() -> {
            portfolioService = new PortfolioServiceImpl();
            testUserId = "test@example.com";
            testPortfolio = FXCollections.observableArrayList();
            testTransactions = FXCollections.observableArrayList();
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testPortfolioItemCreation() {
        Platform.runLater(() -> {
            // Test valid portfolio item creation
            PortfolioItem item = new PortfolioItem("AAPL", "Stock", 100, 170.5);
            assertNotNull(item);
            assertEquals("AAPL", item.getAsset());
            assertEquals("Stock", item.getType());
            assertEquals(100, item.getQuantity());
            assertEquals(170.5, item.getLastPrice(), DELTA);

            // Test invalid portfolio item creation
            assertThrows(IllegalArgumentException.class, () -> 
                new PortfolioItem("AAPL", "InvalidType", 100, 170.5));
            assertThrows(IllegalArgumentException.class, () -> 
                new PortfolioItem("AAPL", "Stock", 0, 170.5));
            assertThrows(IllegalArgumentException.class, () -> 
                new PortfolioItem("AAPL", "Stock", 100, -170.5));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testTransactionCreation() {
        Platform.runLater(() -> {
            // Test valid transaction creation
            Transaction transaction = new Transaction("AAPL", Transaction.TransactionType.BUY, 100, 170.5);
            assertNotNull(transaction);
            assertEquals("AAPL", transaction.getAssetName());
            assertEquals(Transaction.TransactionType.BUY, transaction.getType()); // Compare enum directly
            assertEquals(100, transaction.getQuantity());
            assertEquals(170.5, transaction.getPrice(), DELTA);
            assertEquals(Transaction.TransactionStatus.PENDING, transaction.getStatus()); // Compare enum directly

            // Test transaction status changes
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            assertEquals(Transaction.TransactionStatus.COMPLETED, transaction.getStatus());

            transaction.setStatus(Transaction.TransactionStatus.CANCELLED);
            assertEquals(Transaction.TransactionStatus.CANCELLED, transaction.getStatus());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testPortfolioService() {
        Platform.runLater(() -> {
            // Test adding a new position
            PortfolioItem item = new PortfolioItem("AAPL", "Stock", 100, 170.5);
            assertTrue(portfolioService.addPosition(testUserId, item));
            
            // Test finding the position
            var foundItem = portfolioService.findPosition(testUserId, "AAPL");
            assertTrue(foundItem.isPresent());
            assertEquals(100, foundItem.get().getQuantity());

            // Test updating position
            assertTrue(portfolioService.updatePosition(testUserId, "AAPL", 150));
            foundItem = portfolioService.findPosition(testUserId, "AAPL");
            assertTrue(foundItem.isPresent());
            assertEquals(150, foundItem.get().getQuantity());

            // Test removing position
            assertTrue(portfolioService.removePosition(testUserId, "AAPL"));
            foundItem = portfolioService.findPosition(testUserId, "AAPL");
            assertTrue(foundItem.isEmpty());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testPortfolioCalculations() {
        Platform.runLater(() -> {
            // Create test portfolio items
            PortfolioItem stockItem = new PortfolioItem("AAPL", "Stock", 100, 170.5);
            PortfolioItem cryptoItem = new PortfolioItem("BTC", "Crypto", 2, 28300);
            
            // Add items to portfolio
            portfolioService.addPosition(testUserId, stockItem);
            portfolioService.addPosition(testUserId, cryptoItem);

            // Test total portfolio value
            double totalValue = portfolioService.getTotalPortfolioValue(testUserId);
            assertTrue(totalValue > 0);

            // Test profit/loss calculations
            double totalPL = portfolioService.getTotalProfitLoss(testUserId);
            assertNotNull(totalPL);

            // Test asset type distribution
            var distribution = portfolioService.getAssetTypeDistribution(testUserId);
            assertTrue(distribution.containsKey("Stock"));
            assertTrue(distribution.containsKey("Crypto"));
            assertTrue(distribution.get("Stock") > 0);
            assertTrue(distribution.get("Crypto") > 0);

            // Test risk profile
            String riskProfile = portfolioService.getPortfolioRiskProfile(testUserId);
            assertNotNull(riskProfile);
            assertTrue(riskProfile.length() > 0);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testTransactionOperations() {
        Platform.runLater(() -> {
            // Create and test a buy transaction
            Transaction buyTransaction = new Transaction("AAPL", Transaction.TransactionType.BUY, 100, 170.5);
            testTransactions.add(buyTransaction);
            assertEquals(1, testTransactions.size());
            assertEquals(Transaction.TransactionType.BUY, buyTransaction.getType()); // Compare enum directly
            assertEquals(17050.0, buyTransaction.getTotalAmount(), DELTA);

            // Create and test a sell transaction
            Transaction sellTransaction = new Transaction("AAPL", Transaction.TransactionType.SELL, 50, 180.13);
            testTransactions.add(sellTransaction);
            assertEquals(2, testTransactions.size());
            assertEquals(Transaction.TransactionType.SELL, sellTransaction.getType()); // Compare enum directly
            assertEquals(9006.5, sellTransaction.getTotalAmount(), DELTA);

            // Test transaction status workflow
            assertEquals(Transaction.TransactionStatus.PENDING, buyTransaction.getStatus()); // Compare enum directly
            buyTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            assertEquals(Transaction.TransactionStatus.COMPLETED, buyTransaction.getStatus());
            buyTransaction.setStatus(Transaction.TransactionStatus.CANCELLED);
            assertEquals(Transaction.TransactionStatus.CANCELLED, buyTransaction.getStatus());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
} 