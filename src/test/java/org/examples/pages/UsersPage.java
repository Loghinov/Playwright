package org.examples.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.util.logging.Logger;

public class UsersPage {

    private final Page page;
    private static final Logger log = Logger.getLogger(UsersPage.class.getName());

    public UsersPage(Page page) {
        this.page = page;
    }

    private Locator addUserButton() {
        return page.locator("button:has-text('Add')");
    }

    private Locator userRoleDropdown() {
        return page.locator("div.oxd-select-text-input").nth(0);
    }

    private Locator statusDropdown() {
        return page.locator("div.oxd-select-text-input").nth(1);
    }

    private Locator employeeNameInput() {
        return page.locator("input[placeholder='Type for hints...']");
    }

    private Locator usernameInput() {
        return page.locator("input[autocomplete='off']").nth(0);
    }

    private Locator passwordInput() {
        return page.locator("input[type='password']").nth(0);
    }

    private Locator confirmPasswordInput() {
        return page.locator("input[type='password']").nth(1);
    }

    private Locator saveButton() {
        return page.locator("button:has-text('Save')");
    }

    private Locator toastContent() {
        return page.locator(".oxd-toast-content");
    }

    private Locator tableRows() {
        return page.locator("div[role='row']");
    }

    public void clickAddUser() {
        addUserButton().click();
    }

    public void selectUserRole(String role) {
        userRoleDropdown().click();
        page.locator("div[role='option']:has-text('" + role + "')").click();
    }

    public void selectStatus(String status) {
        statusDropdown().click();
        page.locator("div[role='option']:has-text('" + status + "')").click();
    }

    public boolean fillEmployeeNameAndSelectFirstFromList(String inputText) {
        employeeNameInput().fill("");
        employeeNameInput().click();
        employeeNameInput().type(inputText, new Locator.TypeOptions().setDelay(100));

        // набор вероятных селекторов для вариантов автокомплита
        String[] optionSelectors = new String[] {
                "div.oxd-autocomplete-option",                // основной в UI OrangeHRM
                "div[role='listbox'] div[role='option']",     // альтернативный
                "ul[role='listbox'] li",                      // ещё вариант
                ".oxd-autocomplete-dropdown div"              // fallback
        };

        int waitMs = 5000;

        try {
            // 1) ждем появления любой из опций (по каждому селектору)
            Locator firstOption = null;
            for (String sel : optionSelectors) {
                try {
                    page.waitForSelector(sel, new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(waitMs));
                    Locator found = page.locator(sel);
                    if (found.count() > 0) {
                        firstOption = found.first();
                        break;
                    }
                } catch (Exception ignored) {
                    // не найден — пробуем следующий селектор
                }
            }

            // 2) Если нашли опцию — кликаем по первой
            if (firstOption != null) {
                firstOption.click();
            } else {
                // 3) fallback: попробуем клавиатурой (ArrowDown + Enter)
                page.keyboard().press("ArrowDown");
                page.keyboard().press("Enter");

                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }

            try {
                // попробуем дождаться появления username input (если у тебя он появится после выбора)
                usernameInput().waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(4000));
                return true;
            } catch (Exception e) {
                // если username не появился — проверим value в поле employeeNameInput
                try {
                    String val = employeeNameInput().inputValue();
                    if (val != null && !val.trim().isEmpty()) return true;
                } catch (Exception ignored) {}
            }

            // если до сюда дошли — выбор, видимо, не сработал
            try {
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(java.nio.file.Paths.get("target", "screenshots", "employee_autocomplete_no_option.png")));
            } catch (Exception ignored) {}

            throw new RuntimeException("Autocomplete selection failed: no options appeared or selection not applied for input '" + inputText + "'");
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException(        "Error while trying to select employee autocomplete for input '" + inputText + "': " + ex.getMessage(), ex
            );
        }
    }



    public void fillUsername(String username) {
        usernameInput().fill(username);
    }

    public void fillPasswordAndConfirm(String password) {
        passwordInput().fill(password);
        confirmPasswordInput().fill(password);
    }

    public void saveUser() {
        saveButton().click();
        log.info("Clicking 'Save' button");
    }

    // Ждём, что появилась секция "Records Found" — означает, что список пользователей подгрузился.
    public void waitForRecordsFound(int timeoutMs) {
        try {
            page.waitForSelector("span.oxd-text:has-text('Records Found')",
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(timeoutMs));
            log.info("'Records Found' appeared");
        } catch (Exception ignored) {
            log.warning("'Records Found' section did not appear within " + timeoutMs + "ms");
        }
    }

    /**
     * Ждём и ищем username в таблице. Возвращает true если нашли в течение timeoutMs.
     * Ищем строку с ролью row, содержащую текст username — фильтруем заголовки.
     */
    public boolean waitAndFindUsernameInTable(String username, int timeoutMs) {
        log.info("Waiting and searching for username in table: " + username);
        // Ждём, что таблица (строки) хотя бы одна появилась
        try {
            page.waitForSelector("div[role='row']", new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(Math.min(2000, timeoutMs)));
        } catch (Exception ignored) {}

        try {
            // Ждём точного текста username (используем кавычки для спецсимволов)
            page.waitForSelector("text=\"" + username + "\"",
                    new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMs));

            // Убедимся, что это именно строка таблицы (не header)
            return tableRows().filter(new Locator.FilterOptions().setHasText(username)).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
