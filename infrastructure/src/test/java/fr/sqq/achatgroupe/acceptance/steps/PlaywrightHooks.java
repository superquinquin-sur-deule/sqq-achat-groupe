package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PlaywrightHooks {

    private static Playwright playwright;
    private static Browser browser;
    private static Page currentPage;
    private static String currentTestUrl;

    @BeforeAll
    public static void startBrowser() {
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(
                System.getProperty("quarkus.playwright.headless", "true"));
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @AfterAll
    public static void stopBrowser() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    @Before("@browser")
    public void openPage() {
        currentTestUrl = System.getProperty("test.url", "http://localhost:8081");
        currentPage = browser.newPage();
    }

    @After("@browser")
    public void closePage() {
        if (currentPage != null) {
            currentPage.close();
            currentPage = null;
        }
    }

    public static Page page() {
        return currentPage;
    }

    public static String testUrl() {
        return currentTestUrl;
    }
}
