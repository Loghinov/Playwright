# OrangeHRM Playwright Automation Homework

## Project Overview
This project automates test scenarios for the OrangeHRM demo site:  
[https://opensource-demo.orangehrmlive.com/web/index.php/auth/login](https://opensource-demo.orangehrmlive.com/web/index.php/auth/login)

The tests are written in Java using Playwright and JUnit 5.  
Playwright setup and teardown are handled in `BaseTest` class (hooks).

---

## Test Scenarios

### Scenario 1: Login and Add a New User
**Description:** Automate login as Admin and add a new user.

**Acceptance Criteria:**
1. A success message appears after submitting the user with valid data.
2. The newly created user can be found in the users table.

**Implemented Test:**
- **Test class & method:** `AddUserTest.loginViaAdmin_thenAddUser_roleESS()`
- **Steps automated:**
    - Login as Admin
    - Navigate to **Admin → Users**
    - Click “Add” and fill in user details (Role, Status, Employee Name, Username, Password)
    - Submit and verify success toast
    - Verify the user exists in the table

---

### Scenario 2: Dashboard Links Navigation
**Description:** Click each link in the dashboard (e.g., Admin, PIM, Leave) and verify navigation.

**Acceptance Criteria:**
1. Clicking on **Leave** redirects to `https://opensource-demo.orangehrmlive.com/web/index.php/leave/viewLeaveList`.
2. If the user doesn’t have permission for a page, the test should return to the main dashboard page (e.g., Directory).

**Implemented Test:**
- **Test class & method:** `DashboardLinksTest.checkDashboardlinks()`
- **Steps automated:**
    - Login as Admin
    - Click each link in the dashboard
    - Verify the URL matches expected
    - Handle restricted pages by checking pop-ups and returning to Dashboard

---

## Notes
- Playwright is initialized in `BaseTest.setUp()` and closed in `BaseTest.tearDown()`.
- Logging is implemented to show critical steps and test status in the console.
- Screenshots and detailed tracking can be added if a test fails (currently disabled).

---

## How to Run
1. Clone the project.
2. Ensure Java 21+ is installed.
3. Install dependencies with Maven.
4. Run tests via IDE or:
   ```bash
   mvn test
