package org.examples.base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.logging.Logger;

public class BaseTest {

    private static  final Logger log = Logger.getLogger(BaseTest.class.getName());

    protected Playwright playwright;
    protected Browser browser;
    protected Page page;
    protected BrowserContext context;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(3000));
        log.info("Browser launched");
        context = browser.newContext();
        page = context.newPage();
        log.info("=== Test setup completed ===");
    }

    @AfterEach
    void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        log.info("=== Test teardown completed ===");
    }

    protected void waitForDashboardBanner() {
        page.waitForSelector("img[alt='client brand banner']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
        log.info("Dashboard banner is visible");
    }
}

