package com.furkan.investmentmanagementsystem;

import com.furkan.investmentmanagementsystem.interfaces.*;
import com.furkan.investmentmanagementsystem.models.*;
import com.furkan.investmentmanagementsystem.services.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvestmentManagementSystemTest {

    @Mock
    private PortfolioOperations portfolioOperations;
    @Mock
    private AssetOperations assetOperations;
    @Mock
    private MarketDataOperations marketDataOperations;
    @Mock
    private RiskManagementOperations riskManagementOperations;
    @Mock
    private ReportingOperations reportingOperations;

    private Portfolio portfolio;
    private PortfolioItem portfolioItem;
    private Transaction transaction;
    private InvestmentUser user;
    private FinancialAnalyst analyst;
    private Economist economist;

    @BeforeAll
    void setUp() {
        // Initialize test data
        user = new InvestmentUser("user1", "user@test.com", "password123");
        analyst = new FinancialAnalyst("analyst1", "analyst@test.com", "password123");
        economist = new Economist("econ1", "econ@test.com", "password123");
        
        portfolio = new Portfolio("user1");
        portfolioItem = new PortfolioItem("AAPL", "Stock", 10, 150.0);
        transaction = new Transaction("AAPL", Transaction.TransactionType.BUY, 10, 150.0);
    }

    @Test
    void testPortfolioOperations() {
        // Test adding item
        when(portfolioOperations.addItem(any(PortfolioItem.class))).thenReturn(true);
        assertTrue(portfolioOperations.addItem(portfolioItem));

        // Test updating item
        when(portfolioOperations.updateItemQuantity("AAPL", 15)).thenReturn(true);
        assertTrue(portfolioOperations.updateItemQuantity("AAPL", 15));

        // Test executing transaction
        when(portfolioOperations.executeTransaction(any(Transaction.class))).thenReturn(true);
        assertTrue(portfolioOperations.executeTransaction(transaction));

        // Test getting total value
        when(portfolioOperations.getTotalValue()).thenReturn(1500.0);
        assertEquals(1500.0, portfolioOperations.getTotalValue());

        // Test getting asset distribution
        Map<String, Double> distribution = new HashMap<>();
        distribution.put("Stock", 1000.0);
        distribution.put("Bond", 500.0);
        when(portfolioOperations.getAssetTypeDistribution()).thenReturn(distribution);
        assertEquals(2, portfolioOperations.getAssetTypeDistribution().size());

        // Verify interactions
        verify(portfolioOperations).addItem(portfolioItem);
        verify(portfolioOperations).updateItemQuantity("AAPL", 15);
        verify(portfolioOperations).executeTransaction(transaction);
    }

    @Test
    void testAssetOperations() {
        // Test getting current price
        when(assetOperations.getCurrentPrice()).thenReturn(150.0);
        assertEquals(150.0, assetOperations.getCurrentPrice());

        // Test getting historical prices
        Map<LocalDateTime, Double> historicalPrices = new HashMap<>();
        historicalPrices.put(LocalDateTime.now(), 150.0);
        when(assetOperations.getHistoricalPrices(any(), any())).thenReturn(historicalPrices);
        assertEquals(1, assetOperations.getHistoricalPrices(LocalDateTime.now(), LocalDateTime.now()).size());

        // Test getting trading volume
        when(assetOperations.getTradingVolume(any(), any())).thenReturn(1000000.0);
        assertEquals(1000000.0, assetOperations.getTradingVolume(LocalDateTime.now(), LocalDateTime.now()));

        // Test getting volatility
        when(assetOperations.getVolatility(30)).thenReturn(0.15);
        assertEquals(0.15, assetOperations.getVolatility(30));

        // Test getting market data
        when(assetOperations.getMarketCap()).thenReturn(2000000000000.0);
        when(assetOperations.getDividendYield()).thenReturn(0.02);
        when(assetOperations.getPriceToEarningsRatio()).thenReturn(25.0);
        
        assertEquals(2000000000000.0, assetOperations.getMarketCap());
        assertEquals(0.02, assetOperations.getDividendYield());
        assertEquals(25.0, assetOperations.getPriceToEarningsRatio());
    }

    @Test
    void testMarketDataOperations() {
        // Test getting real-time data
        Map<String, Map<String, Object>> realTimeData = new HashMap<>();
        Map<String, Object> assetData = new HashMap<>();
        assetData.put("price", 150.0);
        assetData.put("volume", 1000000);
        realTimeData.put("AAPL", assetData);
        
        when(marketDataOperations.getRealTimeData(anyList())).thenReturn(realTimeData);
        assertEquals(1, marketDataOperations.getRealTimeData(Arrays.asList("AAPL")).size());

        // Test getting market indices
        Map<String, Double> indices = new HashMap<>();
        indices.put("S&P500", 4000.0);
        indices.put("NASDAQ", 15000.0);
        when(marketDataOperations.getMarketIndices(anyList())).thenReturn(indices);
        assertEquals(2, marketDataOperations.getMarketIndices(Arrays.asList("S&P500", "NASDAQ")).size());

        // Test getting exchange rates
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD/EUR", 0.85);
        rates.put("USD/GBP", 0.73);
        when(marketDataOperations.getExchangeRates(anyString(), anyList())).thenReturn(rates);
        assertEquals(2, marketDataOperations.getExchangeRates("USD", Arrays.asList("EUR", "GBP")).size());

        // Test market sentiment
        Map<String, Double> sentiment = new HashMap<>();
        sentiment.put("AAPL", 0.75);
        when(marketDataOperations.getMarketSentiment(anyList())).thenReturn(sentiment);
        assertEquals(0.75, marketDataOperations.getMarketSentiment(Arrays.asList("AAPL")).get("AAPL"));
    }

    @Test
    void testRiskManagementOperations() {
        // Test VaR calculation
        when(riskManagementOperations.calculateValueAtRisk(0.95, 1)).thenReturn(1000.0);
        assertEquals(1000.0, riskManagementOperations.calculateValueAtRisk(0.95, 1));

        // Test Expected Shortfall
        when(riskManagementOperations.calculateExpectedShortfall(0.95, 1)).thenReturn(1500.0);
        assertEquals(1500.0, riskManagementOperations.calculateExpectedShortfall(0.95, 1));

        // Test Beta calculation
        when(riskManagementOperations.calculateBeta("S&P500", 30)).thenReturn(1.2);
        assertEquals(1.2, riskManagementOperations.calculateBeta("S&P500", 30));

        // Test Sharpe ratio
        when(riskManagementOperations.calculateSharpeRatio(0.02, 30)).thenReturn(1.5);
        assertEquals(1.5, riskManagementOperations.calculateSharpeRatio(0.02, 30));

        // Test risk metrics
        Map<String, Double> riskMetrics = new HashMap<>();
        riskMetrics.put("VaR", 1000.0);
        riskMetrics.put("Beta", 1.2);
        when(riskManagementOperations.calculateRiskMetrics()).thenReturn(riskMetrics);
        assertEquals(2, riskManagementOperations.calculateRiskMetrics().size());

        // Test stress testing
        List<Map<String, Object>> scenarios = new ArrayList<>();
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("name", "Market Crash");
        scenario.put("impact", -0.2);
        scenarios.add(scenario);
        
        Map<String, Double> results = new HashMap<>();
        results.put("Market Crash", -20000.0);
        when(riskManagementOperations.performStressTesting(anyList())).thenReturn(results);
        assertEquals(-20000.0, riskManagementOperations.performStressTesting(scenarios).get("Market Crash"));
    }

    @Test
    void testReportingOperations() {
        // Test performance report generation
        Map<String, Object> performanceReport = new HashMap<>();
        performanceReport.put("totalReturn", 0.15);
        performanceReport.put("periodReturn", 0.05);
        
        when(reportingOperations.generatePerformanceReport(any(), any(), anyBoolean()))
            .thenReturn(performanceReport);
        assertEquals(0.15, reportingOperations.generatePerformanceReport(
            LocalDateTime.now(), LocalDateTime.now(), true).get("totalReturn"));

        // Test allocation report
        Map<String, Object> allocationReport = new HashMap<>();
        allocationReport.put("stocks", 0.6);
        allocationReport.put("bonds", 0.4);
        
        when(reportingOperations.generateAllocationReport(anyString()))
            .thenReturn(allocationReport);
        assertEquals(0.6, reportingOperations.generateAllocationReport("assetType").get("stocks"));

        // Test transaction report
        Map<String, Object> transactionReport = new HashMap<>();
        transactionReport.put("totalTrades", 10);
        transactionReport.put("totalVolume", 1000.0);
        
        when(reportingOperations.generateTransactionReport(any(), any(), any()))
            .thenReturn(transactionReport);
        assertEquals(10, reportingOperations.generateTransactionReport(
            LocalDateTime.now(), LocalDateTime.now(), new HashMap<>()).get("totalTrades"));

        // Test report export
        when(reportingOperations.exportReport(any(), anyString(), anyString()))
            .thenReturn(true);
        assertTrue(reportingOperations.exportReport(new HashMap<>(), "PDF", "report.pdf"));

        // Test report scheduling
        when(reportingOperations.scheduleReport(anyString(), any(), anyList()))
            .thenReturn("schedule123");
        assertEquals("schedule123", reportingOperations.scheduleReport(
            "performance", new HashMap<>(), Arrays.asList("user@test.com")));
    }

    @Test
    void testUserOperations() {
        // Test user validation
        assertTrue(user.validateUser());
        assertTrue(analyst.validateUser());
        assertTrue(economist.validateUser());

        // Test dashboard types
        assertEquals("NormalUser", user.getDashboardType());
        assertEquals("FinancialAnalyst", analyst.getDashboardType());
        assertEquals("Economist", economist.getDashboardType());

        // Test analyst operations
        AnalystReport report = new AnalystReport(
            analyst.getEmail(),
            "AAPL",
            "Bullish",
            "Strong buy recommendation based on recent earnings"
        );
        analyst.addReport(report);
        assertEquals(1, analyst.getReports().size());
        assertEquals(report, analyst.getLatestReportForAsset("AAPL"));

        // Test economist operations
        Forecast forecast = new Forecast(
            economist.getEmail(),
            "5.0",
            "3.0",
            "18.5",
            "Positive economic outlook"
        );
        economist.addForecast(forecast);
        assertEquals(1, economist.getForecasts().size());
    }

    @AfterAll
    void tearDown() {
        // Clean up resources if needed
        portfolio = null;
        portfolioItem = null;
        transaction = null;
        user = null;
        analyst = null;
        economist = null;
    }
} 