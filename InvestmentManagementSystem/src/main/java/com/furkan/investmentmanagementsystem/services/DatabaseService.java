package com.furkan.investmentmanagementsystem.services;

import com.furkan.investmentmanagementsystem.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
    private static final String DB_URL = "jdbc:sqlite:investment_management.db";
    private static DatabaseService instance;
    private Connection connection;

    private DatabaseService() {
        initializeDatabase();
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    last_login TIMESTAMP,
                    is_active BOOLEAN DEFAULT TRUE
                )
            """);

            // Portfolios table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS portfolios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    owner_id INTEGER NOT NULL,
                    creation_date TIMESTAMP NOT NULL,
                    last_modified_date TIMESTAMP NOT NULL,
                    description TEXT,
                    strategy TEXT,
                    total_value REAL DEFAULT 0,
                    total_cost REAL DEFAULT 0,
                    total_profit_loss REAL DEFAULT 0,
                    total_profit_loss_percent REAL DEFAULT 0,
                    is_active BOOLEAN DEFAULT TRUE,
                    risk_profile TEXT,
                    target_return REAL,
                    max_drawdown REAL,
                    sharpe_ratio REAL,
                    FOREIGN KEY (owner_id) REFERENCES users(id)
                )
            """);

            // Portfolio items table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS portfolio_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    portfolio_id INTEGER NOT NULL,
                    asset_name TEXT NOT NULL,
                    asset_type TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    open_price_usd REAL NOT NULL,
                    last_price_usd REAL NOT NULL,
                    open_date TIMESTAMP NOT NULL,
                    position TEXT NOT NULL,
                    profit_loss_percent REAL DEFAULT 0,
                    total_value_usd REAL DEFAULT 0,
                    total_revenue_usd REAL DEFAULT 0,
                    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)
                )
            """);

            // Transactions table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id TEXT PRIMARY KEY,
                    asset_name TEXT NOT NULL,
                    asset_type TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    price_usd REAL NOT NULL,
                    timestamp TIMESTAMP NOT NULL,
                    transaction_type TEXT NOT NULL,
                    status TEXT NOT NULL,
                    total_value_usd REAL NOT NULL,
                    user_id INTEGER NOT NULL,
                    notes TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Reports table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    creation_date TIMESTAMP NOT NULL,
                    last_modified_date TIMESTAMP NOT NULL,
                    author_id INTEGER NOT NULL,
                    report_type TEXT NOT NULL,
                    status TEXT NOT NULL,
                    target_audience TEXT,
                    is_public BOOLEAN DEFAULT FALSE,
                    tags TEXT,
                    FOREIGN KEY (author_id) REFERENCES users(id)
                )
            """);

            // Comments table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS comments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    content TEXT NOT NULL,
                    timestamp TIMESTAMP NOT NULL,
                    author_id INTEGER NOT NULL,
                    parent_id INTEGER,
                    target_type TEXT NOT NULL,
                    target_id TEXT NOT NULL,
                    is_edited BOOLEAN DEFAULT FALSE,
                    last_edit_time TIMESTAMP,
                    likes INTEGER DEFAULT 0,
                    is_deleted BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (author_id) REFERENCES users(id),
                    FOREIGN KEY (parent_id) REFERENCES comments(id)
                )
            """);

            // Notifications table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    message TEXT NOT NULL,
                    timestamp TIMESTAMP NOT NULL,
                    recipient_id INTEGER NOT NULL,
                    sender_id INTEGER,
                    type TEXT NOT NULL,
                    is_read BOOLEAN DEFAULT FALSE,
                    action_url TEXT,
                    priority TEXT NOT NULL,
                    is_deleted BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (recipient_id) REFERENCES users(id),
                    FOREIGN KEY (sender_id) REFERENCES users(id)
                )
            """);

            // Market data table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS market_data (
                    symbol TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    asset_type TEXT NOT NULL,
                    current_price REAL NOT NULL,
                    open_price REAL NOT NULL,
                    high_price REAL NOT NULL,
                    low_price REAL NOT NULL,
                    volume REAL NOT NULL,
                    market_cap REAL,
                    change_percent REAL NOT NULL,
                    last_updated TIMESTAMP NOT NULL,
                    currency TEXT NOT NULL,
                    exchange TEXT NOT NULL,
                    previous_close REAL NOT NULL,
                    fifty_two_week_high REAL,
                    fifty_two_week_low REAL,
                    pe_ratio REAL,
                    dividend_yield REAL
                )
            """);
        }
    }

    // User operations
    public Optional<InvestmentUser> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by email: " + email, e);
        }
        return Optional.empty();
    }

    public List<InvestmentUser> getAllUsers() {
        List<InvestmentUser> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all users", e);
        }
        return users;
    }

    public void saveUser(InvestmentUser user) {
        String sql = "INSERT OR REPLACE INTO users (id, email, password, role, first_name, last_name, last_login, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserType());
            pstmt.setString(5, user.getFirstName());
            pstmt.setString(6, user.getLastName());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // Use current time as last login
            pstmt.setBoolean(8, true); // Default to active
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving user: " + user.getEmail(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    private InvestmentUser mapUserFromResultSet(ResultSet rs) throws SQLException {
        String id = String.valueOf(rs.getInt("id"));
        String email = rs.getString("email");
        String password = rs.getString("password");
        InvestmentUser user = new InvestmentUser(id, email, password);
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        return user;
    }

    // Portfolio operations
    public ObservableList<Portfolio> getUserPortfolios(int userId) {
        ObservableList<Portfolio> portfolios = FXCollections.observableArrayList();
        String sql = "SELECT * FROM portfolios WHERE owner_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                portfolios.add(mapPortfolioFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting portfolios for user: " + userId, e);
        }
        return portfolios;
    }

    public void savePortfolio(Portfolio portfolio) {
        String sql = "INSERT OR REPLACE INTO portfolios (id, name, owner_id, creation_date, last_modified_date, " +
                    "description, strategy, total_value, total_cost, total_profit_loss, total_profit_loss_percent, " +
                    "is_active, risk_profile, target_return, max_drawdown, sharpe_ratio) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, portfolio.getId());
            pstmt.setString(2, portfolio.getName());
            pstmt.setInt(3, Integer.parseInt(portfolio.getUserId()));
            pstmt.setTimestamp(4, Timestamp.valueOf(portfolio.getCreationDate()));
            pstmt.setTimestamp(5, Timestamp.valueOf(portfolio.getLastModifiedDate()));
            pstmt.setString(6, portfolio.getDescription());
            pstmt.setString(7, portfolio.getStrategy());
            pstmt.setDouble(8, portfolio.getTotalValue());
            pstmt.setDouble(9, portfolio.getTotalCost());
            pstmt.setDouble(10, portfolio.getTotalProfitLoss());
            pstmt.setDouble(11, portfolio.getTotalProfitLossPercent());
            pstmt.setBoolean(12, portfolio.isActive());
            pstmt.setString(13, portfolio.getRiskProfile().name());
            pstmt.setDouble(14, portfolio.getTargetReturn());
            pstmt.setDouble(15, portfolio.getMaxDrawdown());
            pstmt.setDouble(16, portfolio.getSharpeRatio());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving portfolio: " + portfolio.getName(), e);
            throw new RuntimeException("Failed to save portfolio", e);
        }
    }

    private Portfolio mapPortfolioFromResultSet(ResultSet rs) throws SQLException {
        Portfolio portfolio = new Portfolio(String.valueOf(rs.getInt("owner_id")));
        portfolio.setId(rs.getInt("id"));
        portfolio.nameProperty().set(rs.getString("name"));
        portfolio.setDescription(rs.getString("description"));
        portfolio.setStrategy(rs.getString("strategy"));
        portfolio.setTotalCost(rs.getDouble("total_cost"));
        portfolio.setTotalProfitLoss(rs.getDouble("total_profit_loss"));
        portfolio.setRiskProfile(Portfolio.RiskProfile.valueOf(rs.getString("risk_profile")));
        portfolio.setTargetReturn(rs.getDouble("target_return"));
        portfolio.setMaxDrawdown(rs.getDouble("max_drawdown"));
        portfolio.setSharpeRatio(rs.getDouble("sharpe_ratio"));
        portfolio.creationDateProperty().set(rs.getTimestamp("creation_date").toLocalDateTime());
        portfolio.lastModifiedDateProperty().set(rs.getTimestamp("last_modified_date").toLocalDateTime());
        portfolio.setActive(rs.getBoolean("is_active"));
        return portfolio;
    }

    // Transaction operations
    public ObservableList<Transaction> getUserTransactions(int userId) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting transactions for user: " + userId, e);
        }
        return transactions;
    }

    public void saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (id, asset_name, asset_type, quantity, price_usd, timestamp, " +
                    "transaction_type, status, total_value_usd, user_id, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getId());
            pstmt.setString(2, transaction.getAssetName());
            pstmt.setString(3, transaction.getType());
            pstmt.setDouble(4, transaction.getQuantity());
            pstmt.setDouble(5, transaction.getPrice());
            pstmt.setTimestamp(6, Timestamp.valueOf(transaction.getTimestamp()));
            pstmt.setString(7, transaction.getType());
            pstmt.setString(8, transaction.getStatus());
            pstmt.setDouble(9, transaction.getTotalAmount());
            pstmt.setString(10, "0"); // Default user ID since it's not in the Transaction model
            pstmt.setString(11, transaction.getNotes());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving transaction: " + transaction.getId(), e);
            throw new RuntimeException("Failed to save transaction", e);
        }
    }

    private Transaction mapTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction(
            rs.getString("asset_name"),
            Transaction.TransactionType.valueOf(rs.getString("transaction_type")),
            rs.getDouble("quantity"),
            rs.getDouble("price_usd")
        );
        transaction.setStatus(Transaction.TransactionStatus.valueOf(rs.getString("status")));
        transaction.setNotes(rs.getString("notes"));
        return transaction;
    }

    // Report operations
    public ObservableList<Report> getUserReports(int userId) {
        ObservableList<Report> reports = FXCollections.observableArrayList();
        String sql = "SELECT * FROM reports WHERE author_id = ? ORDER BY creation_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reports.add(mapReportFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reports for user: " + userId, e);
        }
        return reports;
    }

    public void saveReport(Report report) {
        String sql = "INSERT OR REPLACE INTO reports (id, title, content, creation_date, last_modified_date, " +
                    "author_id, report_type, status, target_audience, is_public, tags) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, report.getIntId());
            pstmt.setString(2, report.getTitle());
            pstmt.setString(3, report.getContent());
            pstmt.setTimestamp(4, Timestamp.valueOf(report.getCreationDate()));
            pstmt.setTimestamp(5, Timestamp.valueOf(report.getLastModifiedDate()));
            pstmt.setInt(6, report.getIntAuthorId());
            pstmt.setString(7, report.getReportTypeEnum().name());
            pstmt.setString(8, report.getStatus());
            pstmt.setString(9, report.getTargetAudience());
            pstmt.setBoolean(10, report.isPublic());
            pstmt.setString(11, report.getTags());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving report: " + report.getTitle(), e);
            throw new RuntimeException("Failed to save report", e);
        }
    }

    private Report mapReportFromResultSet(ResultSet rs) throws SQLException {
        Report report = new Report(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getTimestamp("creation_date").toLocalDateTime(),
            rs.getInt("author_id"),
            Report.ReportType.valueOf(rs.getString("report_type"))
        );
        report.setStatus(Report.ReportStatus.valueOf(rs.getString("status")));
        report.setTargetAudience(rs.getString("target_audience"));
        report.setPublic(rs.getBoolean("is_public"));
        String tags = rs.getString("tags");
        if (tags != null && !tags.isEmpty()) {
            report.setTags(tags);
        }
        return report;
    }

    // Comment operations
    public ObservableList<Comment> getCommentsForTarget(String targetType, String targetId) {
        ObservableList<Comment> comments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM comments WHERE target_type = ? AND target_id = ? AND is_deleted = FALSE " +
                    "ORDER BY timestamp DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, targetType);
            pstmt.setString(2, targetId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comments.add(mapCommentFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting comments for target: " + targetType + "/" + targetId, e);
        }
        return comments;
    }

    public void saveComment(Comment comment) {
        String sql = "INSERT OR REPLACE INTO comments (id, content, timestamp, author_id, parent_id, " +
                    "target_type, target_id, is_edited, last_edit_time, likes, is_deleted) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, comment.getIntId());
            pstmt.setString(2, comment.getContent());
            pstmt.setTimestamp(3, Timestamp.valueOf(comment.getTimestamp()));
            pstmt.setInt(4, comment.getIntAuthorId());
            pstmt.setObject(5, comment.getIntParentId());
            pstmt.setString(6, comment.getTargetTypeEnum().name());
            pstmt.setString(7, comment.getTargetId());
            pstmt.setBoolean(8, comment.isEdited());
            pstmt.setTimestamp(9, comment.getLastEditTime() != null ? 
                Timestamp.valueOf(comment.getLastEditTime()) : null);
            pstmt.setInt(10, comment.getLikes());
            pstmt.setBoolean(11, comment.isDeleted());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving comment: " + comment.getIntId(), e);
            throw new RuntimeException("Failed to save comment", e);
        }
    }

    private Comment mapCommentFromResultSet(ResultSet rs) throws SQLException {
        Comment comment = new Comment(
            rs.getInt("id"),
            rs.getString("content"),
            rs.getTimestamp("timestamp").toLocalDateTime(),
            rs.getInt("author_id"),
            rs.getObject("parent_id", Integer.class),
            Comment.TargetType.valueOf(rs.getString("target_type")),
            rs.getString("target_id")
        );
        comment.setEdited(rs.getBoolean("is_edited"));
        Timestamp lastEditTime = rs.getTimestamp("last_edit_time");
        if (lastEditTime != null) {
            comment.setLastEditTime(lastEditTime.toLocalDateTime());
        }
        comment.setLikes(rs.getInt("likes"));
        comment.setDeleted(rs.getBoolean("is_deleted"));
        return comment;
    }

    // Notification operations
    public ObservableList<Notification> getUserNotifications(int userId) {
        ObservableList<Notification> notifications = FXCollections.observableArrayList();
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? AND is_deleted = FALSE " +
                    "ORDER BY timestamp DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting notifications for user: " + userId, e);
        }
        return notifications;
    }

    public void saveNotification(Notification notification) {
        String sql = "INSERT INTO notifications (id, title, message, timestamp, recipient_id, sender_id, " +
                    "type, is_read, action_url, priority, is_deleted) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getIntId());
            pstmt.setString(2, notification.getTitle());
            pstmt.setString(3, notification.getMessage());
            pstmt.setTimestamp(4, Timestamp.valueOf(notification.getTimestamp()));
            pstmt.setInt(5, notification.getIntRecipientId());
            pstmt.setObject(6, notification.getIntSenderId());
            pstmt.setString(7, notification.getTypeEnum().name());
            pstmt.setBoolean(8, notification.isRead());
            pstmt.setString(9, notification.getActionUrl());
            pstmt.setString(10, notification.getPriorityEnum().name());
            pstmt.setBoolean(11, notification.isDeleted());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving notification: " + notification.getIntId(), e);
            throw new RuntimeException("Failed to save notification", e);
        }
    }

    private Notification mapNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("message"),
            rs.getTimestamp("timestamp").toLocalDateTime(),
            rs.getInt("recipient_id"),
            rs.getObject("sender_id", Integer.class),
            Notification.NotificationType.valueOf(rs.getString("type")),
            Notification.NotificationPriority.valueOf(rs.getString("priority"))
        );
        notification.setRead(rs.getBoolean("is_read"));
        notification.setActionUrl(rs.getString("action_url"));
        notification.setDeleted(rs.getBoolean("is_deleted"));
        return notification;
    }

    // Market data operations
    public void saveMarketData(MarketData marketData) {
        String sql = "INSERT OR REPLACE INTO market_data (symbol, name, asset_type, current_price, open_price, " +
                    "high_price, low_price, volume, market_cap, change_percent, last_updated, currency, exchange, " +
                    "previous_close, fifty_two_week_high, fifty_two_week_low, pe_ratio, dividend_yield) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, marketData.getSymbol());
            pstmt.setString(2, marketData.getName());
            pstmt.setString(3, marketData.getAssetType());
            pstmt.setDouble(4, marketData.getCurrentPrice());
            pstmt.setDouble(5, marketData.getOpenPrice());
            pstmt.setDouble(6, marketData.getHighPrice());
            pstmt.setDouble(7, marketData.getLowPrice());
            pstmt.setDouble(8, marketData.getVolume());
            pstmt.setDouble(9, marketData.getMarketCap());
            pstmt.setDouble(10, marketData.getChangePercent());
            pstmt.setTimestamp(11, Timestamp.valueOf(marketData.getLastUpdated()));
            pstmt.setString(12, marketData.getCurrency());
            pstmt.setString(13, marketData.getExchange());
            pstmt.setDouble(14, marketData.getPreviousClose());
            pstmt.setDouble(15, marketData.getFiftyTwoWeekHigh());
            pstmt.setDouble(16, marketData.getFiftyTwoWeekLow());
            pstmt.setDouble(17, marketData.getPeRatio());
            pstmt.setDouble(18, marketData.getDividendYield());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving market data: " + marketData.getSymbol(), e);
            throw new RuntimeException("Failed to save market data", e);
        }
    }

    public Optional<MarketData> getMarketData(String symbol) {
        String sql = "SELECT * FROM market_data WHERE symbol = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, symbol);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapMarketDataFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting market data for symbol: " + symbol, e);
        }
        return Optional.empty();
    }

    private MarketData mapMarketDataFromResultSet(ResultSet rs) throws SQLException {
        MarketData marketData = new MarketData(
            rs.getString("symbol"),
            rs.getString("name"),
            rs.getString("asset_type"),
            rs.getDouble("current_price"),
            rs.getDouble("open_price"),
            rs.getDouble("high_price"),
            rs.getDouble("low_price"),
            rs.getDouble("volume"),
            rs.getTimestamp("last_updated").toLocalDateTime(),
            rs.getString("currency"),
            rs.getString("exchange"),
            rs.getDouble("previous_close")
        );
        marketData.setMarketCap(rs.getDouble("market_cap"));
        marketData.setFiftyTwoWeekHigh(rs.getDouble("fifty_two_week_high"));
        marketData.setFiftyTwoWeekLow(rs.getDouble("fifty_two_week_low"));
        marketData.setPeRatio(rs.getDouble("pe_ratio"));
        marketData.setDividendYield(rs.getDouble("dividend_yield"));
        return marketData;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing database connection", e);
        }
    }
} 