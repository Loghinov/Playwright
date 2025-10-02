package org.examples.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.examples.base.BaseTest;
import org.examples.pages.DashboardPage;
import org.examples.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class DashboardLinksTest extends BaseTest {
    private static final Logger log = Logger.getLogger(DashboardLinksTest.class.getName());

    @Test
    void checkDashboardlinks(){
        log.info("Starting DashboardLinksTest");
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigateToLogin();
        loginPage.login("Admin", "admin123");

        waitForDashboardBanner();
        DashboardPage dashboardPage = new DashboardPage(page);
        dashboardPage.clickLink("Leave");
        log.info("Clicked 'Leave' link");
        Assertions.assertTrue(page.url().contains("/leave/viewLeaveList"), "Leave URL mismatch. Actual: " + page.url());

        dashboardPage.clickLink("Dashboard");
        log.info("Clicked 'Dashboard' link");
        page.waitForSelector("img[alt='client brand banner']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        dashboardPage.clickLink("Maintenance");
        boolean popupHandled = dashboardPage.waitForAdminPopupAndCancel(5000);
        // Проверка, что после Cancel мы на Dashboard
        log.info("Admin popup handled: " + popupHandled);
        Assertions.assertTrue(popupHandled, "After Cancel we are NOT on the Dashboard page");
        Assertions.assertTrue(page.url().contains("/dashboard"), "URL does not indicate Dashboard page");

        page.waitForSelector("img[alt='client brand banner']",
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        log.info("DashboardLinksTest finished");
    }
}
