module com.furkan.investmentmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires java.desktop;
    requires java.logging;

    // Open packages for JavaFX, testing, and reflection
    opens com.furkan.investmentmanagementsystem to 
        javafx.fxml, 
        javafx.graphics,
        org.junit.jupiter.api,
        org.testfx.framework.junit5;
    opens com.furkan.investmentmanagementsystem.models to 
        javafx.base, 
        javafx.graphics,
        org.junit.jupiter.api,
        org.testfx.framework.junit5;
    opens com.furkan.investmentmanagementsystem.services to 
        javafx.base, 
        javafx.graphics,
        org.junit.jupiter.api,
        org.testfx.framework.junit5;
    
    // Export packages
    exports com.furkan.investmentmanagementsystem;
    exports com.furkan.investmentmanagementsystem.models;
    exports com.furkan.investmentmanagementsystem.services;
}