package com.furkan.investmentmanagementsystem;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.furkan.investmentmanagementsystem.models.PortfolioItem;
import com.furkan.investmentmanagementsystem.models.Transaction;

public class MainApp extends Application {

    private Stage primaryStage;

    // --- MODELS ---

    public static class User {
        private final String email, password, role;
        public User(String email, String password, String role) {
            this.email = email; this.password = password; this.role = role;
        }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
    }

    public static class InvestmentUser {
        private final int id;
        private final String email, role;
        public InvestmentUser(int id, String email, String role) {
            this.id = id; this.email = email; this.role = role;
        }
        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }

    public static class Forecast {
        private final String economist;
        private final String interestRate;
        private final String inflation;
        private final String usdTry;
        private final String comment;
        public Forecast(String economist, String interestRate, String inflation, String usdTry, String comment) {
            this.economist = economist;
            this.interestRate = interestRate;
            this.inflation = inflation;
            this.usdTry = usdTry;
            this.comment = comment;
        }
        public String getEconomist() { return economist; }
        public String getInterestRate() { return interestRate; }
        public String getInflation() { return inflation; }
        public String getUsdTry() { return usdTry; }
        public String getComment() { return comment; }
    }

    public static class AnalystReport {
        private final String analystEmail, assetName, outlook, reportText;
        private final Date timestamp;

        public AnalystReport(String analystEmail, String assetName, String outlook, String reportText) {
            this.analystEmail = analystEmail;
            this.assetName = assetName;
            this.outlook = outlook;
            this.reportText = reportText;
            this.timestamp = new Date();
        }
        public String getAnalystEmail() {
            return analystEmail;
        }
        public Date getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return analystEmail + " (" + sdf.format(timestamp) + ")\n" +
                    "Asset: " + assetName + " | Outlook: " + outlook + "\n" +
                    reportText;
        }
    }

    // --- Analyst Expectation Model ---
    public static class AnalystExpectation {
        private final String analyst;
        private final String assetType;
        private final String assetName;
        private final String comment;
        public AnalystExpectation(String analyst, String assetType, String assetName, String comment) {
            this.analyst = analyst;
            this.assetType = assetType;
            this.assetName = assetName;
            this.comment = comment;
        }
        public String getAnalyst() { return analyst; }
        public String getAssetType() { return assetType; }
        public String getAssetName() { return assetName; }
        public String getComment() { return comment; }
    }

    // --- DATA ---

    private final ObservableList<User> loginUsers = FXCollections.observableArrayList(
            new User("ahmet@example.com", "1234", "NormalUser"),
            new User("elif@example.com", "abcd", "Economist"),
            new User("melis@example.com", "pass", "FinancialAnalyst"),
            new User("emre@example.com", "pass", "PortfolioManager")
    );

    private final ObservableList<InvestmentUser> investmentUsers = FXCollections.observableArrayList(
            new InvestmentUser(1, "ahmet@example.com", "NormalUser"),
            new InvestmentUser(2, "elif@example.com", "Economist"),
            new InvestmentUser(3, "melis@example.com", "FinancialAnalyst"),
            new InvestmentUser(4, "emre@example.com", "PortfolioManager")
    );

    private InvestmentUser currentUser = null;

    private final Map<String, Double> fixedPrices = new LinkedHashMap<>();

    private final Map<String, ObservableList<PortfolioItem>> userPortfolios = new HashMap<>();
    private final Map<String, ObservableList<Transaction>> userTransactions = new HashMap<>();

    private final ObservableList<Forecast> economistForecasts = FXCollections.observableArrayList(
            new Forecast("Dr. Smith", "Increase", "Stable", "Increase", "Expecting monetary tightening."),
            new Forecast("Dr. Lee", "Decrease", "Increase", "Stable", "Inflationary pressures remain."),
            new Forecast("Dr. Brown", "Stable", "Decrease", "Decrease", "Currency stabilization expected."),
            new Forecast("You", "Stable", "Stable", "Stable", "Initial forecast.")
    );

    // PM tarafından NormalUser'a özel raporlar (email → metin)
    private final Map<String, String> portfolioManagerReports = new HashMap<>();

    // --- UI COMPONENTS ---

    // Normal User
    private ObservableList<PortfolioItem> currentUserPortfolioItems;
    private ObservableList<Transaction> currentUserTransactionItems;

    private TableView<PortfolioItem> portfolioTable;
    private TableView<Transaction> transactionTable;
    private PieChart assetTypePieChart;
    private BarChart<String, Number> profitLossBarChart;
    private Label totalPortfolioValueLabel;
    private Label totalPLLabel;
    private Label totalPLPercentLabel;
    private TextArea pmReportsForNormalUserArea;
    private BarChart<String, Number> profitLossChart;

    // Analyst
    private ComboBox<String> analystAssetTypeCombo;
    private ComboBox<String> analystAssetNameCombo;
    private ComboBox<String> analystOutlookCombo;
    private TextArea analystReportInputArea;
    private TextArea allAnalystReportsArea;

    // Portfolio Manager
    private ComboBox<String> pmUserSelectCombo;
    private TextArea pmAllEconomistForecastsArea;
    private TextArea pmAllAnalystReportsArea;

    // Macro economic indicators
    private HBox macroDataBox;

    // --- Scenes ---
    private Scene welcomeScene, loginScene, registerScene, mainScene;

    // --- Mock Data ---
    private final Map<String, Double> mockPrices = new HashMap<>();
    private final Map<String, String> assetTypes = new HashMap<>();
    private final List<String> assetList = new ArrayList<>();
    private final List<String> stockList = new ArrayList<>();
    private final List<String> cryptoList = new ArrayList<>();

    // --- Observable Data ---
    private final ObservableList<PortfolioItem> portfolio = FXCollections.observableArrayList();
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    // --- UI Components ---
    private PieChart assetPieChart;
    private Label totalValueLabel;

    // --- Analyst Expectation Data ---
    private final ObservableList<AnalystExpectation> analystExpectations = FXCollections.observableArrayList(
            new AnalystExpectation("Alice", "Stock", "AAPL", "Expecting strong Q2 earnings."),
            new AnalystExpectation("Bob", "Crypto", "BTC", "Volatility likely to increase."),
            new AnalystExpectation("Carol", "Forex", "USD/TRY", "Possible stabilization in coming months.")

    );

    // Add new fields for price animation and economic data
    private Timeline priceUpdateTimeline;
    private final Random random = new Random();
    private final Map<String, Double> currentPrices = new HashMap<>();
    private final ObservableList<EconomicEvent> economicCalendar = FXCollections.observableArrayList();
    private final ObservableList<InflationData> monthlyInflationData = FXCollections.observableArrayList();
    private final ObservableList<MarketIndexData> sp500Data = FXCollections.observableArrayList();
    private final ObservableList<MarketIndexData> bist100Data = FXCollections.observableArrayList();

    // New model classes for additional data
    public static class EconomicEvent {
        private final String date, event, impact, country;
        public EconomicEvent(String date, String event, String impact, String country) {
            this.date = date; this.event = event; this.impact = impact; this.country = country;
        }
        public String getDate() { return date; }
        public String getEvent() { return event; }
        public String getImpact() { return impact; }
        public String getCountry() { return country; }
    }

    public static class InflationData {
        private final String month;
        private final double value;
        public InflationData(String month, double value) {
            this.month = month; this.value = value;
        }
        public String getMonth() { return month; }
        public double getValue() { return value; }
    }

    public static class MarketIndexData {
        private final String date;
        private final double value;
        public MarketIndexData(String date, double value) {
            this.date = date; this.value = value;
        }
        public String getDate() { return date; }
        public double getValue() { return value; }
    }

    // --- START ---

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Investment Management System");
        loadFixedPrices();
        loadMockData();
        startPriceUpdates();

        // Populate mockPrices and assetTypes
        mockPrices.put("AKBNK", 8.3); mockPrices.put("ISCTR", 6.5); mockPrices.put("AAPL", 170.5); mockPrices.put("MSFT", 310.0);
        mockPrices.put("AMZN", 140.2); mockPrices.put("GOOGL", 130.75); mockPrices.put("META", 320.1); mockPrices.put("TSLA", 195.25);
        mockPrices.put("NVDA", 280.5); mockPrices.put("BTC", 28300.0); mockPrices.put("ETH", 1860.5); mockPrices.put("USD/TRY", 27.45);
        mockPrices.put("EUR/TRY", 29.85); mockPrices.put("BIMAS", 122.75); mockPrices.put("SAHOL", 6.75);
        assetTypes.put("AKBNK", "Stock"); assetTypes.put("ISCTR", "Stock"); assetTypes.put("AAPL", "Stock"); assetTypes.put("MSFT", "Stock");
        assetTypes.put("AMZN", "Stock"); assetTypes.put("GOOGL", "Stock"); assetTypes.put("META", "Stock"); assetTypes.put("TSLA", "Stock");
        assetTypes.put("NVDA", "Stock"); assetTypes.put("BTC", "Crypto"); assetTypes.put("ETH", "Crypto"); assetTypes.put("BIMAS", "Stock"); assetTypes.put("SAHOL", "Stock");
        assetList.addAll(mockPrices.keySet());
        // Categorize assets
        for (String a : assetList) {
            String t = assetTypes.getOrDefault(a, "Other");
            if (t.equals("Stock")) stockList.add(a);
            if (t.equals("Crypto")) cryptoList.add(a);
        }
        // Mock initial portfolio
        portfolio.addAll(
                new PortfolioItem("AAPL", "Stock", 100, 170.5),
                new PortfolioItem("BTC", "Crypto", 2, 28300),
                new PortfolioItem("ETH", "Crypto", 1000, 1860.5)
        );
        transactions.addAll(
                new Transaction("AAPL", Transaction.TransactionType.BUY, 100, 170.5),
                new Transaction("BTC", Transaction.TransactionType.BUY, 2, 28300),
                new Transaction("ETH", Transaction.TransactionType.BUY, 1000, 1860.5)
        );

        welcomeScene = createWelcomeScene();
        loginScene = createLoginScene();
        registerScene = createRegisterScene();

        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void loadFixedPrices() {
        fixedPrices.put("ASELS", 28.40); fixedPrices.put("THYAO", 14.90); fixedPrices.put("KRDMD", 9.80);
        fixedPrices.put("SISE", 7.15); fixedPrices.put("EKGYO", 1.45); fixedPrices.put("AKBNK", 8.30);
        fixedPrices.put("ISCTR", 6.50); fixedPrices.put("GARAN", 9.60); fixedPrices.put("BIMAS", 122.75);
        fixedPrices.put("SAHOL", 6.75); fixedPrices.put("AAPL", 170.50); fixedPrices.put("MSFT", 310.00);
        fixedPrices.put("AMZN", 140.20); fixedPrices.put("GOOGL", 130.75); fixedPrices.put("META", 320.10);
        fixedPrices.put("TSLA", 195.25); fixedPrices.put("NVDA", 280.50); fixedPrices.put("BRK.B", 310.75);
        fixedPrices.put("JPM", 140.50); fixedPrices.put("V", 235.60); fixedPrices.put("BTC", 28300.00);
        fixedPrices.put("ETH", 1860.50); fixedPrices.put("USDT", 1.00); fixedPrices.put("BNB", 310.25);
        fixedPrices.put("SOL", 23.45); fixedPrices.put("EUR/USD", 1.12); fixedPrices.put("USD/TRY", 27.45);
        fixedPrices.put("GBP/USD", 1.31); fixedPrices.put("USD/JPY", 134.50); fixedPrices.put("AUD/USD", 0.66);
        fixedPrices.put("GOLD", 1900.00);
    }

    private String getAssetCategory(String asset) {
        if (asset == null) return "Unknown";
        if (Arrays.asList("BTC", "ETH", "BNB", "SOL", "USDT").contains(asset)) return "Crypto";
        if (Arrays.asList("EUR/USD", "USD/TRY", "GBP/USD", "USD/JPY", "AUD/USD").contains(asset)) return "Forex";
        return "Stock";
    }

    // --- LOGIN / REGISTER / WELCOME SCENES ---

    private Scene createWelcomeScene() {
        VBox vbox = new VBox(30);
        vbox.setPadding(new Insets(50));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle(String.format("-fx-background-color: %s;", MODERN_COLOR_BACKGROUND));

        Label label = new Label("Welcome to Investment Management System");
        label.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 28));
        label.setStyle(String.format("-fx-text-fill: %s;", MODERN_COLOR_TEXT));

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        loginBtn.setPrefSize(200, 50);
        registerBtn.setPrefSize(200, 50);

        applyModernStyle(loginBtn);
        applyModernStyle(registerBtn);

        loginBtn.setOnAction(e -> primaryStage.setScene(loginScene));
        registerBtn.setOnAction(e -> primaryStage.setScene(registerScene));

        vbox.getChildren().addAll(label, loginBtn, registerBtn);
        return addFooterToScene(new Scene(vbox, 500, 400), vbox);
    }

    private Scene createLoginScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40));
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle(String.format("-fx-background-color: %s;", MODERN_COLOR_BACKGROUND));

        Label titleLabel = new Label("Login");
        titleLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 24));
        titleLabel.setStyle(String.format("-fx-text-fill: %s;", MODERN_COLOR_TEXT));
        GridPane.setColumnSpan(titleLabel, 2);
        grid.add(titleLabel, 0, 0);

        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        TextField emailField = new TextField();
        emailField.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        emailField.setPrefSize(250, 35);

        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        PasswordField passField = new PasswordField();
        passField.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        passField.setPrefSize(250, 35);

        Button loginBtn = new Button("Login");
        loginBtn.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        loginBtn.setPrefSize(150, 40);
        Button backBtn = new Button("Back");
        backBtn.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        backBtn.setPrefSize(150, 40);

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 14));

        grid.add(emailLabel, 0, 1); grid.add(emailField, 1, 1);
        grid.add(passLabel, 0, 2); grid.add(passField, 1, 2);
        grid.add(loginBtn, 1, 3); grid.add(backBtn, 0, 3);
        grid.add(messageLabel, 1, 4); GridPane.setColumnSpan(messageLabel, 2);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passField.getText();

            Optional<User> foundUser = loginUsers.stream()
                    .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                    .findFirst();

            if (foundUser.isPresent()) {
                currentUser = investmentUsers.stream()
                        .filter(iu -> iu.getEmail().equals(email))
                        .findFirst()
                        .orElse(null);

                if (currentUser != null) {
                    currentUserPortfolioItems = userPortfolios.computeIfAbsent(currentUser.getEmail(), k -> FXCollections.observableArrayList());
                    currentUserTransactionItems = userTransactions.computeIfAbsent(currentUser.getEmail(), k -> FXCollections.observableArrayList());

                    mainScene = createMainSceneForRole(currentUser.getRole());
                    primaryStage.setScene(mainScene);
                    primaryStage.setTitle("Investment Dashboard - " + currentUser.getEmail() + " (" + currentUser.getRole() + ")");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Investment user profile not found for " + email);
                    return;
                }
                emailField.clear();
                passField.clear();
                messageLabel.setText("");
            } else {
                messageLabel.setText("Invalid email or password!");
                messageLabel.setTextFill(Color.RED);
            }
        });

        backBtn.setOnAction(e -> {
            emailField.clear();
            passField.clear();
            messageLabel.setText("");
            primaryStage.setScene(welcomeScene);
        });

        return addFooterToScene(new Scene(grid, 500, 350), grid);
    }

    private Scene createRegisterScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40));
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        TextField emailField = new TextField();
        emailField.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        emailField.setPrefSize(250, 35);

        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        PasswordField passField = new PasswordField();
        passField.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        passField.setPrefSize(250, 35);

        Label roleLabel = new Label("Role:");
        roleLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("NormalUser", "Economist", "FinancialAnalyst", "PortfolioManager");
        roleCombo.setPromptText("Select Role");
        roleCombo.setPrefSize(250, 35);

        Button registerBtn = new Button("Register");
        registerBtn.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        registerBtn.setPrefSize(150, 40);

        Button backBtn = new Button("Back");
        backBtn.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 16));
        backBtn.setPrefSize(150, 40);

        Label messageLabel = new Label();
        messageLabel.setFont(Font.font(MODERN_FONT_FAMILY, FontWeight.BOLD, 14));

        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(roleLabel, 0, 2);
        grid.add(roleCombo, 1, 2);

        HBox buttonsBox = new HBox(15, backBtn, registerBtn);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonsBox, 1, 3);
        grid.add(messageLabel, 0, 4, 2, 1);

        registerBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passField.getText();
            String role = roleCombo.getValue();

            if (email.isEmpty() || password.isEmpty() || role == null) {
                messageLabel.setText("All fields are required.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (loginUsers.stream().anyMatch(u -> u.getEmail().equals(email))) {
                messageLabel.setText("Email already registered.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            loginUsers.add(new User(email, password, role));
            int newId = investmentUsers.isEmpty() ? 1 : investmentUsers.get(investmentUsers.size() - 1).getId() + 1;
            investmentUsers.add(new InvestmentUser(newId, email, role));

            messageLabel.setText("Registration successful! Please wait.");
            messageLabel.setTextFill(Color.GREEN);

            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(ev -> {
                emailField.clear();
                passField.clear();
                roleCombo.getSelectionModel().clearSelection();
                messageLabel.setText("");
                primaryStage.setScene(loginScene);
            });
            pause.play();
        });

        backBtn.setOnAction(e -> {
            emailField.clear();
            passField.clear();
            roleCombo.getSelectionModel().clearSelection();
            messageLabel.setText("");
            primaryStage.setScene(welcomeScene);
        });

        return addFooterToScene(new Scene(grid, 500, 380), grid);
    }

    // --- ROLE DASHBOARDS ---

    private Scene createMainSceneForRole(String role) {
        Scene scene = switch (role) {
            case "NormalUser" -> new Scene(createNormalUserDashboard(), 1200, 800);
            case "Economist" -> createEconomistDashboard();
            case "FinancialAnalyst" -> createFinancialAnalystDashboard();
            case "PortfolioManager" -> createPortfolioManagerDashboard();
            default -> {
                VBox box = new VBox(new Label("Role: " + role + " - Dashboard not implemented."));
                box.setAlignment(Pos.CENTER);
                yield new Scene(box, 600, 400);
            }
        };
        return addFooterToScene(scene, scene.getRoot());
    }

    // --- NORMAL USER DASHBOARD ---

    private BorderPane createNormalUserDashboard() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        VBox topSection = new VBox(10);
        HBox topBar = new HBox(20);
        Label titleLabel = new Label("Normal User Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("Log Out");
        logoutBtn.setOnAction(e -> logoutAndGoToWelcome());
        topBar.getChildren().addAll(titleLabel, spacer, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topSection.getChildren().add(topBar);
        macroDataBox = createMacroEconomicDataBox();
        topSection.getChildren().add(macroDataBox); // Macro data at the top
        topSection.getChildren().add(createScrollingAssetTicker()); // Flowing band below macro data
        root.setTop(topSection);
        root.setMargin(macroDataBox, new Insets(10, 0, 20, 0));
        TabPane tabs = new TabPane();
        Tab portfolioTab = new Tab("My Portfolio", createPortfolioTabContent());
        portfolioTab.setClosable(false);
        Tab transactionsTab = new Tab("My Transactions", createTransactionTabContent());
        transactionsTab.setClosable(false);
        Tab pmReportsTab = new Tab("Portfolio Manager Reports", createPMReportsTabContent());
        pmReportsTab.setClosable(false);
        tabs.getTabs().addAll(portfolioTab, transactionsTab, pmReportsTab);
        root.setCenter(tabs);
        updateNormalUserUI();
        return root;
    }

    private VBox createPortfolioTabContent() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        // Transaction form
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        ComboBox<String> assetTypeComboNU = new ComboBox<>(FXCollections.observableArrayList("Stock", "Crypto", "Forex"));
        assetTypeComboNU.setPromptText("Asset Type");

        ComboBox<String> assetNameComboNU = new ComboBox<>();
        assetNameComboNU.setPromptText("Asset Name");

        ComboBox<String> transactionTypeComboNU = new ComboBox<>(FXCollections.observableArrayList("Buy", "Sell"));
        transactionTypeComboNU.setPromptText("Buy/Sell");

        TextField quantityFieldNU = new TextField();
        quantityFieldNU.setPromptText("Quantity");

        Button executeTransactionBtn = new Button("Execute Transaction");

        formGrid.add(new Label("Asset Type:"), 0, 0);
        formGrid.add(assetTypeComboNU, 1, 0);
        formGrid.add(new Label("Asset Name:"), 2, 0);
        formGrid.add(assetNameComboNU, 3, 0);
        formGrid.add(new Label("Buy/Sell:"), 0, 1);
        formGrid.add(transactionTypeComboNU, 1, 1);
        formGrid.add(new Label("Quantity:"), 2, 1);
        formGrid.add(quantityFieldNU, 3, 1);
        formGrid.add(executeTransactionBtn, 4, 0, 1, 2);
        formGrid.setAlignment(Pos.CENTER_LEFT);

        assetTypeComboNU.setOnAction(e -> {
            assetNameComboNU.getItems().clear();
            String type = assetTypeComboNU.getValue();
            if (type != null) {
                fixedPrices.keySet().stream()
                        .filter(asset -> getAssetCategory(asset).equals(type))
                        .forEach(assetNameComboNU.getItems()::add);
            }
        });

        executeTransactionBtn.setOnAction(e -> {
            String type = assetTypeComboNU.getValue(); // Asset type (Stock, Crypto, Forex)
            String asset = assetNameComboNU.getValue(); // Asset name
            String transType = transactionTypeComboNU.getValue(); // Transaction type (Buy/Sell)
            String qtyText = quantityFieldNU.getText(); // Quantity (string)

            // Make sure all fields are filled
            if (type == null || asset == null || transType == null || qtyText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "All transaction fields are required.");
                return;
            }

            int qty;
            try {
                qty = Integer.parseInt(qtyText);
                if (qty <= 0) throw new NumberFormatException("Quantity must be positive.");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid quantity: " + ex.getMessage());
                return;
            }

            Double price = fixedPrices.get(asset); // Price is taken from constants
            if (price == null) {
                showAlert(Alert.AlertType.ERROR, "Price for " + asset + " not found.");
                return;
            }

            executeTransaction(type, asset, transType, qty, price);

            // Clear the form
            assetTypeComboNU.getSelectionModel().clearSelection();
            assetNameComboNU.getSelectionModel().clearSelection();
            assetNameComboNU.getItems().clear();
            transactionTypeComboNU.getSelectionModel().clearSelection();
            quantityFieldNU.clear();

            showAlert(Alert.AlertType.INFORMATION, transType + " of " + qty + " " + asset + " executed successfully.");
        });

        portfolioTable = new TableView<>(currentUserPortfolioItems);
        setupPortfolioTableColumns();
        portfolioTable.setPrefHeight(250);

        totalPortfolioValueLabel = new Label();
        totalPortfolioValueLabel.setFont(Font.font(16));
        totalPLLabel = new Label();
        totalPLLabel.setFont(Font.font(16));
        totalPLPercentLabel = new Label();
        totalPLPercentLabel.setFont(Font.font(16));
        HBox summaryBox = new HBox(30, totalPortfolioValueLabel, totalPLLabel, totalPLPercentLabel);
        summaryBox.setAlignment(Pos.CENTER_RIGHT);

        // Initialize pie chart with improved implementation
        assetTypePieChart = new PieChart();
        assetTypePieChart.setPrefHeight(250);
        updateAssetTypePieChart();

        profitLossBarChart = createProfitLossBarChart();
        profitLossChart = createProfitLossChart();

        HBox chartsBox = new HBox(20, assetTypePieChart, profitLossBarChart, profitLossChart);
        chartsBox.setAlignment(Pos.CENTER);

        box.getChildren().addAll(formGrid, portfolioTable, summaryBox, new Separator(), chartsBox);

        return box;
    }

    private VBox createTransactionTabContent() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        transactionTable = new TableView<>(currentUserTransactionItems);
        setupTransactionTableColumns();

        box.getChildren().add(transactionTable);
        return box;
    }

    private VBox createPMReportsTabContent() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        pmReportsForNormalUserArea = new TextArea();
        pmReportsForNormalUserArea.setEditable(false);
        pmReportsForNormalUserArea.setWrapText(true);
        pmReportsForNormalUserArea.setPrefHeight(400);

        // PM raporu, currentUser email bazlı gösterilir
        String report = portfolioManagerReports.getOrDefault(currentUser.getEmail(), "No report or notes from Portfolio Manager.");
        pmReportsForNormalUserArea.setText(report);

        box.getChildren().addAll(new Label("Portfolio Manager's Reports and Notes:"), pmReportsForNormalUserArea);
        return box;
    }

    private void updateNormalUserUI() {
        if (currentUser == null || !"NormalUser".equals(currentUser.getRole())) return;
        if (portfolioTable != null) portfolioTable.refresh();
        if (transactionTable != null) transactionTable.refresh();
        updateSummaryLabels();
        updateProfitLossBarChart();
        updateProfitLossChart();
        if (pmReportsForNormalUserArea != null) {
            String report = portfolioManagerReports.getOrDefault(currentUser.getEmail(), "No report or notes from Portfolio Manager.");
            pmReportsForNormalUserArea.setText(report);
        }
    }

    private void updateSummaryLabels() {
        double totalVal = currentUserPortfolioItems.stream().mapToDouble(pi -> pi.getTotalValue()).sum();
        double totalRev = currentUserPortfolioItems.stream().mapToDouble(pi -> pi.getTotalRevenue()).sum();
        double totalPLPercent = currentUserPortfolioItems.stream()
            .mapToDouble(PortfolioItem::getProfitLossPercent)
            .average()
            .orElse(0.0);
        totalPortfolioValueLabel.setText(String.format("Total Portfolio Value: $%,.2f", totalVal));
        totalPLLabel.setText(String.format("Total Unrealized P/L: $%,.2f", totalRev));
        totalPLLabel.setTextFill(totalRev >= 0 ? Color.DARKGREEN : Color.RED);
        totalPLPercentLabel.setText(String.format("%.2f%%", totalPLPercent));
    }

    @SuppressWarnings("unchecked")
    private void setupPortfolioTableColumns() {
        TableColumn<PortfolioItem, String> assetCol = new TableColumn<>("Asset");
        assetCol.setCellValueFactory(new PropertyValueFactory<>("asset"));

        TableColumn<PortfolioItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<PortfolioItem, Double> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<PortfolioItem, String> posCol = new TableColumn<>("Position");
        posCol.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<PortfolioItem, Double> openPriceCol = new TableColumn<>("Open Price (USD)");
        openPriceCol.setCellValueFactory(new PropertyValueFactory<>("openPrice"));
        applyCurrencyFormat(openPriceCol);

        TableColumn<PortfolioItem, Double> lastPriceCol = new TableColumn<>("Last Price (USD)");
        lastPriceCol.setCellValueFactory(new PropertyValueFactory<>("lastPrice"));
        applyCurrencyFormat(lastPriceCol);

        TableColumn<PortfolioItem, Double> plPercentCol = new TableColumn<>("P/L %");
        plPercentCol.setCellValueFactory(new PropertyValueFactory<>("profitLossPercent"));
        applyPercentageFormat(plPercentCol);

        TableColumn<PortfolioItem, Double> totalPLCol = new TableColumn<>("Total P/L (USD)");
        totalPLCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        applyCurrencyFormat(totalPLCol);
        applyProfitLossColoring(totalPLCol);

        TableColumn<PortfolioItem, Double> totalValueCol = new TableColumn<>("Total Value (USD)");
        totalValueCol.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        applyCurrencyFormat(totalValueCol);

        portfolioTable.getColumns().setAll(assetCol, typeCol, qtyCol, posCol, openPriceCol, lastPriceCol, plPercentCol, totalPLCol, totalValueCol);
    }

    @SuppressWarnings("unchecked")
    private void setupTransactionTableColumns() {
        TableColumn<Transaction, String> assetCol = new TableColumn<>("Asset");
        assetCol.setCellValueFactory(new PropertyValueFactory<>("assetName"));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Transaction, String> positionCol = new TableColumn<>("Position");
        positionCol.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            String position = transaction.getQuantity() >= 0 ? "Long" : "Short";
            return new SimpleStringProperty(position);
        });

        TableColumn<Transaction, String> transTypeCol = new TableColumn<>("Transaction Type");
        transTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        transTypeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(type);
                    setTextFill(type.equals("Buy") ? Color.GREEN : Color.RED);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Transaction, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty || quantity == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", Math.abs(quantity)));
                    setTextFill(quantity >= 0 ? Color.GREEN : Color.RED);
                }
            }
        });

        TableColumn<Transaction, Double> openPriceCol = new TableColumn<>("Open Price (USD)");
        openPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        applyCurrencyFormat(openPriceCol);

        TableColumn<Transaction, Double> lastPriceCol = new TableColumn<>("Last Price (USD)");
        lastPriceCol.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            PortfolioItem item = currentUserPortfolioItems.stream()
                .filter(pi -> pi.getAsset().equals(transaction.getAssetName()))
                .findFirst()
                .orElse(null);
            Double lastPrice = item != null ? item.getLastPrice() : transaction.getPrice();
            return new SimpleObjectProperty<>(lastPrice);
        });
        applyCurrencyFormat(lastPriceCol);

        TableColumn<Transaction, Double> plCol = new TableColumn<>("P/L %");
        plCol.setCellValueFactory(cellData -> {
            Transaction transaction = cellData.getValue();
            PortfolioItem item = currentUserPortfolioItems.stream()
                .filter(pi -> pi.getAsset().equals(transaction.getAssetName()))
                .findFirst()
                .orElse(null);
            Double plPercent = 0.0;
            if (item != null) {
                plPercent = ((item.getLastPrice() - transaction.getPrice()) / transaction.getPrice()) * 100;
            }
            return new SimpleObjectProperty<>(plPercent);
        });

        TableColumn<Transaction, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
            }
        });

        transactionTable.getColumns().setAll(
            assetCol, typeCol, positionCol, transTypeCol, quantityCol, 
            openPriceCol, lastPriceCol, plCol, dateCol
        );
    }

    // --- FORMAT HELPERS ---

    private <S> void applyCurrencyFormat(TableColumn<S, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : String.format("$%,.2f", val));
            }
        });
    }

    private <S> void applyPercentageFormat(TableColumn<S, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f%%", val));
                    setTextFill(val >= 0 ? Color.GREEN : Color.RED);
                }
            }
        });
    }

    private <S> void applyProfitLossColoring(TableColumn<S, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("$%,.2f", val));
                    setTextFill(val >= 0 ? Color.GREEN : Color.RED);
                }
            }
        });
    }

    // --- CHARTS ---

    private PieChart.Data createPieChartDataSafe(String name, double value, String color) {
        PieChart.Data slice = new PieChart.Data(name, Math.max(0, value));
        Platform.runLater(() -> {
            if (slice.getNode() != null) {
                slice.getNode().setStyle("-fx-pie-color: " + color + ";");
            }
        });
        slice.nodeProperty().addListener((obs, oldN, newN) -> {
            if (newN != null) newN.setStyle("-fx-pie-color: " + color + ";");
        });
        return slice;
    }

    private ObservableList<PieChart.Data> getAssetTypeDistribution() {
        Map<String, Double> typeValueMap = new HashMap<>();
        if (currentUserPortfolioItems == null) return FXCollections.observableArrayList();

        // Sum up values per asset type
        for (PortfolioItem item : currentUserPortfolioItems) {
            double value = item.getTotalValue();
            typeValueMap.merge(item.getType(), value, Double::sum);
        }
        double total = typeValueMap.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, String> colorMap = Map.of(
            "Stock",  "#EF5350",  // Red
            "Crypto", "#FFC107",  // Yellow
            "Forex",  "#2196F3"   // Blue
        );

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (var e : typeValueMap.entrySet()) {
            if (e.getValue() > 0) {
                double pct = total == 0 ? 0 : e.getValue() / total * 100;
                data.add(createPieChartDataSafe(
                    String.format("%s (%.1f%%)", e.getKey(), pct),
                    e.getValue(),
                    colorMap.getOrDefault(e.getKey(), "#9E9E9E")
                ));
            }
        }
        return data;
    }

    private void updateAssetTypePieChart() {
        if (assetTypePieChart == null) return;
        assetTypePieChart.setData(getAssetTypeDistribution());
        assetTypePieChart.setTitle("Portfolio Asset Type Distribution (by Value)");
        // Always keep the pie chart visible, even if empty
        if (assetTypePieChart.getData().isEmpty()) {
            assetTypePieChart.getData().add(createPieChartDataSafe("No Assets", 1, "#9E9E9E"));
        }
    }

    private BarChart<String, Number> createProfitLossBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (USD)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Profit & Loss Summary");
        chart.setLegendVisible(false);
        chart.setPrefSize(350, 300);
        chart.setAnimated(false);
        return chart;
    }

    private void updateProfitLossBarChart() {
        if (profitLossBarChart == null || currentUserPortfolioItems == null) return;

        double gains = 0, losses = 0;
        for (PortfolioItem pi : currentUserPortfolioItems) {
            double rev = pi.getTotalRevenue();
            if (rev >= 0) gains += rev;
            else losses += Math.abs(rev);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        XYChart.Data<String, Number> gainsData = new XYChart.Data<>("Unrealized Gains", gains);
        XYChart.Data<String, Number> lossesData = new XYChart.Data<>("Unrealized Losses", losses);
        XYChart.Data<String, Number> netData = new XYChart.Data<>("Net P/L", gains - losses);

        series.getData().addAll(gainsData, lossesData, netData);
        profitLossBarChart.getData().setAll(series);

        Platform.runLater(() -> {
            if (gainsData.getNode() != null) gainsData.getNode().setStyle("-fx-bar-fill: #66BB6A;");
            if (lossesData.getNode() != null) lossesData.getNode().setStyle("-fx-bar-fill: #EF5350;");
            if (netData.getNode() != null) netData.getNode().setStyle("-fx-bar-fill: #42A5F5;");
        });
    }

    private BarChart<String, Number> createProfitLossChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Profit/Loss %");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Profit/Loss Distribution");
        chart.setLegendVisible(false);
        chart.setPrefSize(350, 300);
        chart.setAnimated(false);
        return chart;
    }

    private void updateProfitLossChart() {
        if (profitLossChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Profit/Loss %");
            
            for (PortfolioItem item : currentUserPortfolioItems) {
                series.getData().add(new XYChart.Data<>(
                    item.getAsset(),
                    item.getProfitLossPercent()
                ));
            }
            
            profitLossChart.getData().clear();
            profitLossChart.getData().add(series);
        }
    }

    // --- ECONOMIST DASHBOARD ---
    private Scene createEconomistDashboard() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        HBox topBar = new HBox(20);
        Label titleLabel = new Label("Economist Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("Log Out");
        logoutBtn.setOnAction(e -> logoutAndGoToWelcome());
        topBar.getChildren().addAll(titleLabel, spacer, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(topBar);

        HBox macroBoxRow = new HBox(20,
                macroBox("Interest Rate (%)", 8.5),
                macroBox("Inflation (%)", 15.2),
                macroBox("USD/TRY", 27.45),
                macroBox("EUR/TRY", 29.85)
        );
        macroBoxRow.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(macroBoxRow);

        TabPane tabs = new TabPane();
        Tab forecastTab = new Tab("Forecast", createForecastTab());
        forecastTab.setClosable(false);
        Tab expectationsTab = new Tab("Expectations", createExpectationsTab());
        expectationsTab.setClosable(false);
        Tab allForecastsTab = createAllForecastsTab();
        tabs.getTabs().addAll(forecastTab, expectationsTab, allForecastsTab);
        root.getChildren().add(tabs);

        return new Scene(root, 1200, 800);
    }

    private VBox createForecastTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        // Forecast input grid
        GridPane forecastGrid = new GridPane();
        forecastGrid.setHgap(20);
        forecastGrid.setVgap(15);
        forecastGrid.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        ComboBox<String> interestCombo = new ComboBox<>(FXCollections.observableArrayList("Increase", "Decrease", "Stable"));
        interestCombo.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5);
                    Label arrow = new Label();
                    switch (item) {
                        case "Increase": arrow.setText("↑"); break;
                        case "Decrease": arrow.setText("↓"); break;
                        case "Stable": arrow.setText("→"); break;
                    }
                    box.getChildren().addAll(arrow, new Label(item));
                    setGraphic(box);
                }
            }
        });
        ComboBox<String> inflationCombo = new ComboBox<>(FXCollections.observableArrayList("Increase", "Decrease", "Stable"));
        inflationCombo.setCellFactory(interestCombo.getCellFactory());
        ComboBox<String> usdTryCombo = new ComboBox<>(FXCollections.observableArrayList("Increase", "Decrease", "Stable"));
        usdTryCombo.setCellFactory(interestCombo.getCellFactory());
        interestCombo.setValue("Stable");
        inflationCombo.setValue("Stable");
        usdTryCombo.setValue("Stable");

        Label commentLabel = new Label("Forecast Comment:");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Add your comment or explanation here...");
        commentArea.setPrefRowCount(3);
        commentArea.setWrapText(true);
        Button saveForecastBtn = new Button("Save Forecast");
        Label saveMessage = new Label();
        saveMessage.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        forecastGrid.add(new Label("Interest Rate Forecast:"), 0, 0);
        forecastGrid.add(interestCombo, 1, 0);
        forecastGrid.add(new Label("Inflation Forecast:"), 0, 1);
        forecastGrid.add(inflationCombo, 1, 1);
        forecastGrid.add(new Label("USD/TRY Forecast:"), 0, 2);
        forecastGrid.add(usdTryCombo, 1, 2);
        forecastGrid.add(commentLabel, 0, 3);
        forecastGrid.add(commentArea, 1, 3);
        forecastGrid.add(saveForecastBtn, 1, 4);
        forecastGrid.add(saveMessage, 1, 5);

        // Economic Calendar
        VBox calendarBox = new VBox(10);
        calendarBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-color: #dee2e6; -fx-border-radius: 5;");
        TableView<EconomicEvent> calendarTable = new TableView<>(economicCalendar);
        TableColumn<EconomicEvent, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<EconomicEvent, String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        TableColumn<EconomicEvent, String> impactCol = new TableColumn<>("Impact");
        impactCol.setCellValueFactory(new PropertyValueFactory<>("impact"));
        calendarTable.getColumns().addAll(dateCol, eventCol, impactCol);
        calendarTable.setPrefHeight(180);
        calendarBox.getChildren().addAll(new Label("Economic Calendar"), calendarTable);

        // Dynamic Inflation Chart
        VBox inflationBox = new VBox(10);
        inflationBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-color: #dee2e6; -fx-border-radius: 5;");
        LineChart<String, Number> inflationChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        inflationChart.setPrefSize(260, 140);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Inflation");
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        double val = 15.2 + (Math.random() - 0.5) * 2;
        for (String m : months) {
            val += (Math.random() - 0.5) * 1.2; // more dynamic
            double rounded = Math.round(val * 10.0) / 10.0;
            XYChart.Data<String, Number> data = new XYChart.Data<>(m, rounded);
            series.getData().add(data);
            Tooltip.install(data.getNode(), new Tooltip(m + ": " + rounded + "%"));
        }
        inflationChart.getData().add(series);
        inflationBox.getChildren().addAll(new Label("Monthly Inflation Trend"), inflationChart);

        // HBox for side-by-side layout
        HBox hbox = new HBox(30, forecastGrid, calendarBox, inflationBox);
        hbox.setAlignment(Pos.TOP_CENTER);
        box.getChildren().add(hbox);

        saveForecastBtn.setOnAction(e -> {
            String commentText = commentArea.getText().trim();
            Forecast newForecast = new Forecast(
                    currentUser.getEmail(),
                    interestCombo.getValue(),
                    inflationCombo.getValue(),
                    usdTryCombo.getValue(),
                    commentText
            );
            economistForecasts.add(newForecast);
            saveMessage.setText("Saved!");
            saveMessage.setTextFill(Color.GREEN);
            commentArea.clear();
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> saveMessage.setText(""));
            pause.play();
            // Update piecharts and comments in tab 2
            if (economistExpectationsTabVBox != null) updateEconomistExpectationsTab();
            updateAllForecastsListView();
        });

        return box;
    }

    // --- Dynamic Pie Chart and List Update for Economist ---
    private VBox economistExpectationsTabVBox = null;
    private void updateEconomistExpectationsTab() {
        if (economistExpectationsTabVBox != null) {
            HBox pieChartsRow = new HBox(30);
            pieChartsRow.setAlignment(Pos.CENTER);
            pieChartsRow.setPrefHeight(200);
            pieChartsRow.getChildren().setAll(
                pieChartOnlyWithNumbers("Interest Rate", f -> f.getInterestRate()),
                pieChartOnlyWithNumbers("Inflation", f -> f.getInflation()),
                pieChartOnlyWithNumbers("USD/TRY", f -> f.getUsdTry())
            );
            economistExpectationsTabVBox.getChildren().setAll(
                new Label("Forecast Distribution (All Economists):"),
                pieChartsRow
            );
        }
    }

    private PieChart pieChartOnlyWithNumbers(String label, java.util.function.Function<Forecast, String> extractor) {
        Map<String, Integer> counts = new HashMap<>();
        for (Forecast f : economistForecasts) {
            counts.merge(extractor.apply(f), 1, Integer::sum);
        }
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        int total = economistForecasts.size();

        // Define colors for forecast types
        Map<String, String> forecastColors = Map.of(
            "Increase", "#4CAF50",  // Green
            "Decrease", "#EF5350",  // Red
            "Stable",   "#FFC107"   // Yellow
        );

        for (String opt : List.of("Increase", "Decrease", "Stable")) {
            int count = counts.getOrDefault(opt, 0);
            if (count > 0) {
                PieChart.Data slice = new PieChart.Data(opt + " (" + count + ")", count);
                String color = forecastColors.getOrDefault(opt, "#9E9E9E");
                Platform.runLater(() -> {
                    if (slice.getNode() != null) {
                        slice.getNode().setStyle("-fx-pie-color: " + color + ";");
                    }
                });
                slice.nodeProperty().addListener((obs, oldN, newN) -> {
                    if (newN != null) newN.setStyle("-fx-pie-color: " + color + ";");
                });
                data.add(slice);
            }
        }

        PieChart chart = new PieChart(data);
        chart.setTitle(label);
        chart.setLegendVisible(true);
        chart.setPrefSize(320, 320);
        chart.setMinSize(320, 320);
        chart.setMaxSize(320, 320);
        for (PieChart.Data d : chart.getData()) {
            Tooltip.install(d.getNode(), new Tooltip(d.getName()));
        }
        return chart;
    }

    private VBox createExpectationsTab() {
        economistExpectationsTabVBox = new VBox(20);
        economistExpectationsTabVBox.setPadding(new Insets(20));
        updateEconomistExpectationsTab();
        return economistExpectationsTabVBox;
    }

    private ListView<String> allForecastsListView = null;
    private void updateAllForecastsListView() {
        if (allForecastsListView != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Forecast f : economistForecasts) {
                items.add(String.format("%s | Interest: %s, Inflation: %s, USD/TRY: %s\nComment: %s",
                        f.getEconomist(), f.getInterestRate(), f.getInflation(), f.getUsdTry(), f.getComment()));
            }
            allForecastsListView.setItems(items);
        }
    }

    private Tab createAllForecastsTab() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        Label title = new Label("All Forecasts and Comments");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        allForecastsListView = new ListView<>();
        updateAllForecastsListView();
        allForecastsListView.setPrefHeight(350);
        allForecastsListView.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14;");
        box.getChildren().addAll(title, new Separator(), allForecastsListView);
        Tab tab = new Tab("All Forecasts & Comments", box);
        tab.setClosable(false);
        return tab;
    }

    // --- FINANCIAL ANALYST DASHBOARD ---

    private Scene createFinancialAnalystDashboard() {
        BorderPane root = new BorderPane();
        VBox topBox = new VBox(10);
        // Title bar
        HBox titleBar = new HBox();
        Label title = new Label("Financial Analyst");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> logoutAndGoToWelcome());
        titleBar.getChildren().addAll(title, spacer, logoutBtn);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        topBox.getChildren().add(titleBar);
        topBox.setPadding(new Insets(10, 20, 10, 20));
        root.setTop(topBox);
        // Tabs
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                new Tab("Asset Analysis", createAssetAnalysisTab()),
                new Tab("Other Analyst Expectations", createOtherAnalystTab())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));
        root.setCenter(tabs);
        return new Scene(root, 1000, 700);
    }

    private VBox createAssetAnalysisTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Stock", "Crypto", "Forex"));
        typeCombo.setPromptText("Asset Type");
        ComboBox<String> assetCombo = new ComboBox<>();
        assetCombo.setPromptText("Asset");
        ComboBox<String> suggestionCombo = new ComboBox<>(FXCollections.observableArrayList("Buy", "Sell", "Hold"));
        suggestionCombo.setPromptText("Suggestion");
        typeCombo.setOnAction(e -> {
            assetCombo.getItems().clear();
            String type = typeCombo.getValue();
            if (type == null) return;
                fixedPrices.keySet().stream()
                        .filter(asset -> getAssetCategory(asset).equals(type))
                    .forEach(assetCombo.getItems()::add);
        });
        // Realistic asset price trend chart (no symbols)
        LineChart<String, Number> assetChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        assetChart.setTitle("Asset Price Trend");
        assetChart.setPrefSize(350, 200);
        assetChart.setCreateSymbols(false); // Remove points/markers
        assetCombo.setOnAction(e -> {
            assetChart.getData().clear();
            String asset = assetCombo.getValue();
            if (asset != null) {
                XYChart.Series<String, Number> assetSeries = new XYChart.Series<>();
                assetSeries.setName(asset);
                double base = fixedPrices.getOrDefault(asset, 100.0);
                for (int i = 1; i <= 30; i++) {
                    double trend = (i - 15) * 0.3;
                    double noise = (Math.random() - 0.5) * 2.0;
                    double price = base + trend + noise;
                    assetSeries.getData().add(new XYChart.Data<>("Day " + i, Math.round(price * 100.0) / 100.0));
                }
                assetChart.getData().add(assetSeries);
            }
        });
        VBox comboBoxCol = new VBox(10, new Label("Select Asset Type:"), typeCombo,
                new Label("Select Asset:"), assetCombo,
                new Label("Buy/Sell/Hold Suggestion:"), suggestionCombo);
        comboBoxCol.setAlignment(Pos.TOP_LEFT);
        topRow.getChildren().addAll(comboBoxCol, assetChart);
        HBox.setHgrow(assetChart, Priority.ALWAYS);
        HBox.setHgrow(comboBoxCol, Priority.NEVER);
        VBox.setVgrow(topRow, Priority.ALWAYS);
        // Comment area at the bottom, larger
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write your expectation/comment about the selected asset...");
        commentArea.setPrefRowCount(6);
        commentArea.setPrefHeight(120);
        Button saveBtn = new Button("Save");
        Label saveMsg = new Label();
        saveBtn.setOnAction(e -> {
            String type = typeCombo.getValue();
            String asset = assetCombo.getValue();
            String suggestion = suggestionCombo.getValue();
            String comment = commentArea.getText().trim();
            if (type == null || asset == null || suggestion == null || comment.isEmpty()) {
                saveMsg.setText("Please select asset type, asset, suggestion and enter a comment.");
                saveMsg.setTextFill(Color.RED);
                return;
            }
            analystExpectations.add(new AnalystExpectation(
                currentUser != null ? currentUser.getEmail() : "Unknown", type, asset, suggestion + ": " + comment
            ));
            saveMsg.setText("Saved!");
            saveMsg.setTextFill(Color.GREEN);
            commentArea.clear();
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> saveMsg.setText(""));
            pause.play();
            if (otherAnalystTabList != null) {
                updateOtherAnalystTabList();
            }
        });
        VBox.setVgrow(commentArea, Priority.ALWAYS);
        box.getChildren().addAll(topRow, new Label("Your Expectation/Comment:"), commentArea, saveBtn, saveMsg);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }
    // --- Heatmap and live update for other analyst tab ---
    private ListView<String> otherAnalystTabList = null;
    private void updateOtherAnalystTabList() {
        if (otherAnalystTabList != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            for (AnalystExpectation ex : analystExpectations) {
                items.add(String.format("%s (%s - %s): %s", ex.getAnalyst(), ex.getAssetType(), ex.getAssetName(), ex.getComment()));
            }
            otherAnalystTabList.setItems(items);
        }
    }
    private VBox createOtherAnalystTab() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);
        splitPane.setPrefHeight(400);
        VBox listBox = new VBox(10);
        listBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-color: #dee2e6; -fx-border-radius: 5;");
        otherAnalystTabList = new ListView<>();
        updateOtherAnalystTabList();
        otherAnalystTabList.setPrefHeight(300);
        listBox.getChildren().addAll(new Label("All Analyst Expectations:"), otherAnalystTabList);
        // Heatmap of mock assets
        VBox heatmapBox = new VBox(10);
        heatmapBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-color: #dee2e6; -fx-border-radius: 5;");
        GridPane heatmap = new GridPane();
        heatmap.setHgap(8);
        heatmap.setVgap(8);
        List<String> mockAssets = new ArrayList<>(fixedPrices.keySet());
        Collections.shuffle(mockAssets);
        int row = 0, col = 0;
        for (int i = 0; i < Math.min(18, mockAssets.size()); i++) {
            String asset = mockAssets.get(i);
            double change = (Math.random() - 0.5) * 4.0; // -2% to +2%
            Label cell = new Label(asset + "\n" + String.format("%.2f%%", change));
            cell.setPrefSize(70, 40);
            cell.setAlignment(Pos.CENTER);
            cell.setStyle("-fx-background-color: " + (change >= 0 ? "#b6fcb6" : "#ffb3b3") + "; -fx-border-color: #bbb; -fx-border-radius: 6; -fx-font-weight: bold;");
            heatmap.add(cell, col, row);
            col++;
            if (col == 6) { col = 0; row++; }
        }
        heatmapBox.getChildren().addAll(new Label("Asset Heatmap"), heatmap);
        splitPane.getItems().addAll(listBox, heatmapBox);
        VBox wrapper = new VBox(splitPane);
        return wrapper;
    }

    // --- Portfolio Manager Dashboard ---
    private Scene createPortfolioManagerDashboard() {
        BorderPane root = new BorderPane();
        VBox topBox = new VBox(10);
        // Title bar
        HBox titleBar = new HBox();
        Label title = new Label("Portfolio Manager");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> logoutAndGoToWelcome());
        titleBar.getChildren().addAll(title, spacer, logoutBtn);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        topBox.getChildren().add(titleBar);
        topBox.setPadding(new Insets(10, 20, 10, 20));
        root.setTop(topBox);
        // Tabs
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                new Tab("Users and Portfolios", createUsersPortfoliosTab()),
                new Tab("Economist & Analyst Expectations", createCombinedExpectationsTab())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));
        root.setCenter(tabs);
        return new Scene(root, 1000, 800);
    }

    private VBox createUsersPortfoliosTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        // Live user list and portfolios
        ObservableList<String> users = FXCollections.observableArrayList();
        Map<String, String> riskProfiles = new HashMap<>();
        Map<String, List<String>> portfolios = new HashMap<>();
        for (InvestmentUser iu : investmentUsers) {
            if ("NormalUser".equals(iu.getRole())) {
                users.add(iu.getEmail());
                ObservableList<PortfolioItem> pItems = userPortfolios.getOrDefault(iu.getEmail(), FXCollections.observableArrayList());
                List<String> pList = new ArrayList<>();
                for (PortfolioItem pi : pItems) {
                    pList.add(pi.getAsset() + ": " + pi.getQuantity());
                }
                portfolios.put(iu.getEmail(), pList);
                // Simple risk profile logic
                double total = 0, crypto = 0, stock = 0, forex = 0;
                for (PortfolioItem pi : pItems) {
                    double val = pi.getTotalValue();
                    total += val;
                    switch (pi.getType()) {
                        case "Crypto" -> crypto += val;
                        case "Stock" -> stock += val;
                        case "Forex" -> forex += val;
                    }
                }
                String risk = "No Investments";
                if (total > 0) {
                    double cryptoRatio = crypto / total;
                    if (cryptoRatio > 0.6) risk = "Very High Risk (Crypto Heavy)";
                    else if (cryptoRatio > 0.3) risk = "High Risk (Significant Crypto)";
                    else if (stock / total > 0.7) risk = "Medium-High Risk (Stock Biased)";
                    else if ((stock / total) > 0.4 && (forex / total) > 0.3) risk = "Medium Risk (Balanced)";
                    else risk = "Low to Medium Risk (Diversified or Forex/Fixed Income Heavy)";
                }
                riskProfiles.put(iu.getEmail(), risk);
            }
        }
        TableView<String> userTable = new TableView<>(users);
        TableColumn<String, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        userCol.setPrefWidth(180);
        TableColumn<String, String> portfolioCol = new TableColumn<>("Portfolio");
        portfolioCol.setCellValueFactory(data -> new SimpleStringProperty(String.join(", ", portfolios.getOrDefault(data.getValue(), List.of()))));
        portfolioCol.setPrefWidth(400);
        userTable.getColumns().setAll(userCol, portfolioCol);
        userTable.setPrefHeight(200);
        // User selection and risk profile
        HBox userSelectBox = new HBox(10);
        ComboBox<String> userCombo = new ComboBox<>(users);
        userCombo.setPromptText("Select User");
        Label riskProfileLabel = new Label("Risk Profile: -");
        riskProfileLabel.setStyle("-fx-border-color: #bbb; -fx-padding: 6 16; -fx-background-radius: 6; -fx-border-radius: 6; -fx-background-color: #f8f8f8;");
        userCombo.setOnAction(e -> {
            String user = userCombo.getValue();
            riskProfileLabel.setText("Risk Profile: " + riskProfiles.getOrDefault(user, "-"));
        });
        userSelectBox.getChildren().addAll(new Label("User:"), userCombo, riskProfileLabel);
        userSelectBox.setAlignment(Pos.CENTER_LEFT);
        // Comment box
        TextArea pmCommentArea = new TextArea();
        pmCommentArea.setPromptText("Write your comment/suggestion for the selected user's portfolio...");
        pmCommentArea.setPrefRowCount(3);
        Button sendBtn = new Button("Send/Save");
        Label sendMsg = new Label();
        sendBtn.setOnAction(e -> {
            String user = userCombo.getValue();
            String comment = pmCommentArea.getText().trim();
            if (user == null || comment.isEmpty()) {
                sendMsg.setText("Please select a user and enter a comment.");
                sendMsg.setTextFill(Color.RED);
                return;
            }
            portfolioManagerReports.put(user, comment);
            sendMsg.setText("Comment sent to " + user + ".");
            sendMsg.setTextFill(Color.GREEN);
            pmCommentArea.clear();
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> sendMsg.setText(""));
            pause.play();
        });
        HBox commentBox = new HBox(10, pmCommentArea, sendBtn, sendMsg);
        commentBox.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(userTable, userSelectBox, commentBox);
        return box;
    }

    private HBox createCombinedExpectationsTab() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);
        splitPane.setPrefHeight(500);
        VBox pieChartsBox = new VBox(20);
        pieChartsBox.setPadding(new Insets(20));
        pieChartsBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
        pieChartsBox.getChildren().addAll(
            new Label("Economist Expectations:"),
            pieChartRow("Interest Rate", f -> f.getInterestRate()),
            pieChartRow("Inflation", f -> f.getInflation()),
            pieChartRow("USD/TRY", f -> f.getUsdTry())
        );
        VBox.setVgrow(pieChartsBox, Priority.ALWAYS);
        VBox analystBox = new VBox(20);
        analystBox.setPadding(new Insets(20));
        analystBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
        analystBox.getChildren().add(new Label("Financial Analyst Expectations:"));
        ListView<String> analystList = new ListView<>();
        ObservableList<String> analystItems = FXCollections.observableArrayList();
        for (AnalystExpectation ex : analystExpectations) {
            String formatted = String.format(
                "%s\nAsset: %s (%s)\n%s\n%s",
                ex.getAnalyst(), ex.getAssetName(), ex.getAssetType(),
                ex.getComment(), "-----------------------------"
            );
            analystItems.add(formatted);
        }
        analystList.setItems(analystItems);
        analystList.setPrefHeight(400);
        analystList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setWrapText(true);
                setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14;");
            }
        });
        VBox.setVgrow(analystList, Priority.ALWAYS);
        analystBox.getChildren().add(analystList);
        VBox.setVgrow(analystBox, Priority.ALWAYS);
        splitPane.getItems().setAll(pieChartsBox, analystBox);
        splitPane.setDividerPositions(0.5);
        HBox wrapper = new HBox(splitPane);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        return wrapper;
    }

    // --- COMMON METHODS ---

    private HBox createMacroEconomicDataBox() {
        HBox box = new HBox(20);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(String.format(
            "-fx-background-color: white;" +
            "-fx-border-color: %s;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);",
            MODERN_COLOR_BORDER
        ));

        VBox usdBox = createMacroDataItem("USD/TRY", fixedPrices.getOrDefault("USD/TRY", 0.0));
        VBox goldBox = createMacroDataItem("GOLD/USD", fixedPrices.getOrDefault("GOLD", 0.0));
        VBox interestBox = createMacroDataItem("Interest (%)", fixedPrices.getOrDefault("Interest", 8.5));
        VBox inflationBox = createMacroDataItem("Inflation (%)", fixedPrices.getOrDefault("Inflation", 15.2));
        VBox sp500Box = createMacroDataItem("S&P 500", 4950.0);
        VBox nasdaqBox = createMacroDataItem("NASDAQ", 15800.0);
        VBox bistBox = createMacroDataItem("BIST100", 8800.0);
        box.getChildren().addAll(usdBox, goldBox, interestBox, inflationBox, sp500Box, nasdaqBox, bistBox);
        return box;
    }

    private VBox createMacroDataItem(String label, double value) {
        VBox box = new VBox(5);
        Label title = new Label(label);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label valueLabel = new Label(String.format("%.2f", value));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        box.getChildren().addAll(title, valueLabel);
        box.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f5f5f5; -fx-border-radius: 5;");
        return box;
    }

    private void logoutAndGoToWelcome() {
        currentUser = null;
        currentUserPortfolioItems = null;
        currentUserTransactionItems = null;
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Investment Management System");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }

    // Macro economic indicator box helper (ensure this is present and accessible)
    private VBox macroBox(String label, double value) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #bbb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: #f8f8f8;");
        Label l1 = new Label(label);
        l1.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label l2 = new Label(String.format("%.2f", value));
        l2.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        box.getChildren().addAll(l1, l2);
        return box;
    }

    private void loadMockData() {
        // Load economic calendar
        economicCalendar.addAll(
            new EconomicEvent("2024-04-15", "Interest Rate Decision", "High", "Turkey"),
            new EconomicEvent("2024-04-20", "Inflation Data", "High", "Turkey"),
            new EconomicEvent("2024-04-25", "GDP Growth", "Medium", "Turkey"),
            new EconomicEvent("2024-04-10", "FED Meeting", "High", "USA")
        );

        // Load inflation data
        monthlyInflationData.addAll(
            new InflationData("Jan", 15.2),
            new InflationData("Feb", 14.8),
            new InflationData("Mar", 14.5),
            new InflationData("Apr", 14.2)
        );

        // Load market index data
        sp500Data.addAll(
            new MarketIndexData("2024-01", 4800.0),
            new MarketIndexData("2024-02", 4850.0),
            new MarketIndexData("2024-03", 4900.0),
            new MarketIndexData("2024-04", 4950.0)
        );

        bist100Data.addAll(
            new MarketIndexData("2024-01", 8500.0),
            new MarketIndexData("2024-02", 8600.0),
            new MarketIndexData("2024-03", 8700.0),
            new MarketIndexData("2024-04", 8800.0)
        );

        // Initialize current prices
        fixedPrices.forEach((asset, price) -> currentPrices.put(asset, price));
    }

    private ScrollPane createScrollingAssetTicker() {
        HBox tickerBox = new HBox(30);
        tickerBox.setPadding(new Insets(12));
        tickerBox.setAlignment(Pos.CENTER_LEFT);
        tickerBox.setStyle(String.format(
            "-fx-background-color: white;" +
            "-fx-border-color: %s;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;",
            MODERN_COLOR_BORDER
        ));
        for (Map.Entry<String, Double> entry : fixedPrices.entrySet()) {
            Label lbl = new Label(entry.getKey() + ": $" + String.format("%.2f", entry.getValue()));
            lbl.setFont(Font.font(16));
            lbl.setStyle("-fx-border-color: #4285f4; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-color: white;");
            tickerBox.getChildren().add(lbl);
        }
        ScrollPane scrollPane = new ScrollPane(tickerBox);
        scrollPane.setPrefHeight(60);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        Platform.runLater(() -> {
            double contentWidth = tickerBox.getWidth();
            double viewportWidth = scrollPane.getViewportBounds().getWidth();
            if (contentWidth > 0 && viewportWidth > 0 && contentWidth < 2 * viewportWidth) {
                List<Node> copy = new ArrayList<>(tickerBox.getChildren());
                tickerBox.getChildren().addAll(copy);
            }
            // SLOWER: Increase duration to 40 seconds
            Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(scrollPane.hvalueProperty(), 0)),
                new KeyFrame(Duration.seconds(40), new KeyValue(scrollPane.hvalueProperty(), scrollPane.getHmax()))
            );
            tl.setCycleCount(Animation.INDEFINITE);
            tl.play();
            scrollPane.hvalueProperty().addListener((obs, oldV, newV) -> {
                if (newV.doubleValue() >= scrollPane.getHmax()) {
                    scrollPane.setHvalue(0);
                }
            });
        });
        return scrollPane;
    }

    private void startPriceUpdates() {
        priceUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            currentPrices.replaceAll((asset, oldPrice) -> {
                double change = (random.nextDouble() - 0.5) * 0.02; // ±1% change
                return oldPrice * (1 + change);
            });
            updateNormalUserUI();
        }));
        priceUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        priceUpdateTimeline.play();
    }

    private HBox pieChartRow(String label, java.util.function.Function<Forecast, String> extractor) {
        Map<String, Integer> counts = new HashMap<>();
        for (Forecast f : economistForecasts) {
            counts.merge(extractor.apply(f), 1, Integer::sum);
        }
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (String opt : List.of("Increase", "Decrease", "Stable")) {
            int count = counts.getOrDefault(opt, 0);
            if (count > 0) data.add(new PieChart.Data(opt + " (" + count + ")", count));
        }
        PieChart chart = new PieChart(data);
        chart.setTitle(label);
        chart.setLegendVisible(true);
        chart.setPrefSize(250, 200);
        HBox row = new HBox(20, new Label(label + ":"), chart);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Add this after the class declaration, before any other methods
    private Node createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setMaxWidth(Double.MAX_VALUE);
        footer.setPadding(new Insets(12, 20, 12, 20));
        footer.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-border-color: %s;" +
            "-fx-border-width: 1px 0 0 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, -1);",
            MODERN_COLOR_BACKGROUND,
            MODERN_COLOR_BORDER
        ));

        Label footerText = new Label("Designed and Developed by Furkan Celik");
        footerText.setStyle(String.format(
            "-fx-font-family: %s;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: %s;" +
            "-fx-font-weight: 500;",
            MODERN_FONT_FAMILY,
            MODERN_COLOR_TEXT
        ));

        footer.getChildren().add(footerText);
        return footer;
    }

    private Scene addFooterToScene(Scene scene, Node content) {
        BorderPane root = new BorderPane();
        root.setCenter(content);
        root.setBottom(createFooter());
        return new Scene(root, scene.getWidth(), scene.getHeight());
    }

    // Add these style constants at the class level
    private static final String MODERN_COLOR_PRIMARY = "#2563eb";    // Modern blue
    private static final String MODERN_COLOR_SECONDARY = "#3b82f6";  // Lighter blue
    private static final String MODERN_COLOR_BACKGROUND = "#f8fafc"; // Light background
    private static final String MODERN_COLOR_TEXT = "#1e293b";       // Dark text
    private static final String MODERN_COLOR_BORDER = "#e2e8f0";     // Border color
    private static final String MODERN_FONT_FAMILY = "'Inter', 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif";

    private void applyModernStyle(Node node) {
        if (node instanceof Button) {
            Button button = (Button) node;
            button.setStyle(String.format(
                "-fx-font-family: %s;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-background-color: %s;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);" +
                "-fx-transition: all 0.2s ease-in-out;",
                MODERN_FONT_FAMILY,
                MODERN_COLOR_PRIMARY
            ));
            button.setOnMouseEntered(e -> button.setStyle(button.getStyle().replace(MODERN_COLOR_PRIMARY, MODERN_COLOR_SECONDARY)));
            button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace(MODERN_COLOR_SECONDARY, MODERN_COLOR_PRIMARY)));
        } else if (node instanceof Label) {
            Label label = (Label) node;
            if (!label.getStyle().contains("-fx-font-family")) {
                label.setStyle(String.format(
                    "-fx-font-family: %s;" +
                    "-fx-text-fill: %s;",
                    MODERN_FONT_FAMILY,
                    MODERN_COLOR_TEXT
                ));
            }
        } else if (node instanceof TextField || node instanceof PasswordField) {
            node.setStyle(String.format(
                "-fx-font-family: %s;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px;" +
                "-fx-background-color: white;" +
                "-fx-border-color: %s;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;",
                MODERN_FONT_FAMILY,
                MODERN_COLOR_BORDER
            ));
        } else if (node instanceof ComboBox) {
            ComboBox<?> combo = (ComboBox<?>) node;
            combo.setStyle(String.format(
                "-fx-font-family: %s;" +
                "-fx-font-size: 14px;" +
                "-fx-background-color: white;" +
                "-fx-border-color: %s;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;",
                MODERN_FONT_FAMILY,
                MODERN_COLOR_BORDER
            ));
        }
    }

    private void executeTransaction(String type, String asset, String transType, int qty, double price) {
        // Check if the same asset exists in the portfolio
        PortfolioItem existingItem = null;
        for (PortfolioItem pi : currentUserPortfolioItems) {
            if (pi.getAsset().equals(asset)) {
                existingItem = pi;
                break;
            }
        }

        // Show error if there is no asset for sale
        if (existingItem == null && transType.equals("Sell")) {
            showAlert(Alert.AlertType.ERROR, "You don't own any units of " + asset + " to sell.");
            return;
        }

        // Update portfolio
        if (existingItem != null) {
            double newQty = transType.equals("Buy") ? existingItem.getQuantity() + qty : existingItem.getQuantity() - qty;
            if (newQty == 0) {
                currentUserPortfolioItems.remove(existingItem);
                updateAssetTypePieChart();
            } else if (newQty < 0) {
                showAlert(Alert.AlertType.ERROR, "Insufficient quantity to sell.");
                return;
            } else {
                existingItem.setQuantity(newQty);
                existingItem.simulatePriceChange(); // Simulate a new price change
            }
        } else {
            // Add new purchase (not for sale)
            if (transType.equals("Buy")) {
                PortfolioItem newItem = new PortfolioItem(asset, type, qty, price);
                currentUserPortfolioItems.add(newItem);
                updateAssetTypePieChart();
            }
        }

        // Add to transaction list with proper transaction type
        Transaction.TransactionType transactionType = transType.equals("Buy") 
            ? Transaction.TransactionType.BUY 
            : Transaction.TransactionType.SELL;
        Transaction transaction = new Transaction(asset, transactionType, qty, price);
        currentUserTransactionItems.add(transaction);

        // Update UI
        updateNormalUserUIData();
        updateTransactionTable();
        updatePortfolioTable();
        updateAssetTypePieChart();
        updateProfitLossChart();
    }

    private void updateTransactionTable() {
        if (transactionTable != null) {
            transactionTable.setItems(currentUserTransactionItems);
            transactionTable.refresh();
        }
    }

    private void updatePortfolioTable() {
        if (portfolioTable != null) {
            portfolioTable.setItems(currentUserPortfolioItems);
            portfolioTable.refresh();
        }
    }

    private void updateNormalUserUIData() {
        // Update total portfolio value
        double totalValue = currentUserPortfolioItems.stream()
            .mapToDouble(PortfolioItem::getTotalValue)
            .sum();
        totalValueLabel.setText(String.format("$%.2f", totalValue));

        // Update total P/L
        double totalPL = currentUserPortfolioItems.stream()
            .mapToDouble(PortfolioItem::getTotalRevenue)
            .sum();
        totalPLLabel.setText(String.format("$%.2f", totalPL));

        // Update P/L percentage
        double totalPLPercent = currentUserPortfolioItems.stream()
            .mapToDouble(PortfolioItem::getProfitLossPercent)
            .average()
            .orElse(0.0);
        totalPLPercentLabel.setText(String.format("%.2f%%", totalPLPercent));
    }

}
