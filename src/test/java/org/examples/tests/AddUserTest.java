package org.examples.tests;

import org.examples.base.BaseTest;
import org.examples.pages.DashboardPage;
import org.examples.pages.LoginPage;
import org.examples.pages.UsersPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class AddUserTest extends BaseTest {
    private static final Logger log = Logger.getLogger(AddUserTest.class.getName());

    @Test
    void loginViaAdmin_thenAddUser_roleESS() {
        // 1) login as Admin
        log.info("Starting test: login via Admin and add user");
        LoginPage login = new LoginPage(page);
        login.navigateToLogin();
        login.loginAsAdmin();
        log.info("Logged in as Admin");

        waitForDashboardBanner();

        DashboardPage dashboard = new DashboardPage(page);
        dashboard.openAdminMenuAndWaitForAdd();

        // 4) click Add and fill form
        UsersPage users = new UsersPage(page);
        users.clickAddUser();
        log.info("Clicked 'Add' to open user form");

        users.selectUserRole("ESS");
        users.selectStatus("Enabled");
        users.fillEmployeeNameAndSelectFirstFromList("1");

        String username = "auto.user." + System.currentTimeMillis();
        users.fillUsername(username);
        String password = "Ser1234";
        users.fillPasswordAndConfirm(password);

        users.saveUser();

        // после users.saveUser() и проверки toast
        users.waitForRecordsFound(3000);                // дождаться, что список загрузился (если есть)
        users.searchByUsernameIfFilterExists(username); // если есть фильтр — применим его
        boolean found = users.waitAndFindUsernameInTable(username, 5000); // подождём до 5 сек

        Assertions.assertTrue(found, "Created user not found in table: " + username);
        if (found){
            log.info("User successfully created and found in table: " + username);
        }
        log.info("Test finished");
    }
}
