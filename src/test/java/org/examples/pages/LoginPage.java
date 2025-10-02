package org.examples.pages;

import com.microsoft.playwright.Page;

import java.util.logging.Logger;

public class LoginPage {
    private static final Logger log = Logger.getLogger(LoginPage.class.getName());

    private final Page page;
    private final String usernameInput = "input[name='username']";
    private final String passwordInput = "input[name='password']";
    private final String submitButton = "button[type='submit']";

    public LoginPage(Page page) {
        this.page = page;
    }

    public void navigateToLogin() {
        page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        log.info("Login page loaded");
    }

    public void loginAsAdmin() {
        login("Admin", "admin123");
    }

    public void login(String username, String password) {
        page.fill(usernameInput, username);
        page.fill(passwordInput, password);
        page.click(submitButton);
        log.info("Clicking submit button success!");
    }
}
