package org.examples.tests;

import org.examples.base.BaseTest;
import org.examples.pages.LoginPage;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.logging.Logger;

public class LoginTest extends BaseTest {
    private static final Logger log = Logger.getLogger(LoginTest.class.getName());

    @Test
    void testValidLogin() {
        log.info("Starting LoginTest");
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigateToLogin();
        loginPage.loginAsAdmin();

        waitForDashboardBanner();

        Assertions.assertTrue(
                page.locator("img[alt='client brand banner']").isVisible(),
                "Client brand banner (logo) is not visible after login"
        );
        log.info("LoginTest finished");
    }
}

