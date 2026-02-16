package com.furkan.investmentmanagementsystem.models;

/**
 * Abstract base class for all user types in the system.
 */
public abstract class User {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String userType;

    protected User(String id, String email, String userType, String password) {
        this.id = id;
        this.email = email;
        this.userType = userType;
        this.password = password;
    }

    /**
     * Returns the type of dashboard this user should see.
     * Must be implemented by concrete user classes.
     */
    public abstract String getDashboardType();

    /**
     * Validates the user's credentials.
     * Must be implemented by concrete user classes.
     */
    public abstract boolean validateUser();

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserType() {
        return userType;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %s, Email: %s)", getFullName(), id, email);
    }
} 