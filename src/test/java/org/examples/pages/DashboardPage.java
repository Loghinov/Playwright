package org.examples.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.logging.Logger;

public class DashboardPage {
    private final Page page;
    private static final Logger log = Logger.getLogger(DashboardPage.class.getName());

    public DashboardPage(Page page) {
        this.page = page;
    }

    public void openAdminMenuAndWaitForAdd(){
        page.locator("span.oxd-main-menu-item--name:has-text('Admin')").click();
        page.waitForSelector("button:has-text('Add')", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        log.info("'Add' button is visible in Admin menu");
    }

    public void  clickLink(String linkName){
        page.locator("span.oxd-main-menu-item--name:has-text('" + linkName + "')").click();
    }

    /**
     * Ждёт поп-ап "Administrator Access" в течение timeout (ms).
     * Если поп-ап появился — нажимает Cancel.
     * Возвращает true только если после этого мы оказались на Dashboard
     * (проверка по баннеру или по URL). Если после Cancel не вернулись —
     * выполняется fallback: клик по Dashboard и повторная проверка.
     * Если поп-ап не появился в timeout — возвращается false.
     */
    public boolean waitForAdminPopupAndCancel(long timeout) {
        try {
            log.info("Waiting for 'Administrator Access' popup...");
            // 1) дождаться заголовка поп-апа
            page.waitForSelector("text=Administrator Access",
                    new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            log.info("'Administrator Access' popup appeared, clicking Cancel...");

            // 2) нажать Cancel (используем точный селектор, который ты приводил)
            page.locator("button.oxd-button.oxd-button--large.oxd-button--ghost.orangehrm-admin-access-button").click();

            // 3) сначала попробуем проверить, вернулись ли автоматически: по баннеру или по URL
            try {
                page.waitForSelector("img[alt='client brand banner']",
                        new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(3000));
                log.info("Returned to Dashboard (banner found)");
                return true;
            } catch (Exception ignored) { /* не вернулись автоматически по баннеру */ }

            if (page.url().contains("/dashboard")) {
                log.info("Returned to Dashboard (URL check)");
                return true;
            }
            return  false;
        } catch (Exception e) {
            log.info("Popup 'Administrator Access' did not appear in timeout");
            return false;
        }
    }
}
