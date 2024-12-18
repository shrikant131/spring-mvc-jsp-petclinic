package org.springframework.samples.petclinic.web;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlaywrightTests {

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    public static void setUpAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    public static void tearDownAll() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    public void setUp() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    public void tearDown() {
        context.close();
    }

    @Test
    public void testHomePage() {
        page.navigate("http://localhost:8080");
        assertEquals("Welcome to the PetClinic Application", page.title());
    }

    @Test
    public void testFindOwnersPage() {
        page.navigate("http://localhost:8080/owners/find");
        assertEquals("Find Owners", page.title());
    }

    @Test
    public void testAddNewOwner() {
        page.navigate("http://localhost:8080/owners/new");
        page.fill("input[name='firstName']", "John");
        page.fill("input[name='lastName']", "Doe");
        page.fill("input[name='address']", "123 Main St");
        page.fill("input[name='city']", "Springfield");
        page.fill("input[name='telephone']", "1234567890");
        page.click("button[type='submit']");
        assertTrue(page.url().contains("/owners/"));
    }

    @Test
    public void testAddNewPet() {
        page.navigate("http://localhost:8080/owners/1/pets/new");
        page.fill("input[name='name']", "Buddy");
        page.fill("input[name='birthDate']", "2021-01-01");
        page.selectOption("select[name='type']", "dog");
        page.click("button[type='submit']");
        assertTrue(page.url().contains("/owners/1"));
    }

    @Test
    public void testAddNewVisit() {
        page.navigate("http://localhost:8080/owners/1/pets/1/visits/new");
        page.fill("input[name='date']", "2021-01-01");
        page.fill("input[name='description']", "Regular check-up");
        page.click("button[type='submit']");
        assertTrue(page.url().contains("/owners/1"));
    }
}
