package com.yatraautomation;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class YatraAutomationScript {

    public static void main(String[] args) throws InterruptedException {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-notifications"); // disable notification in web pages
        options.addArguments("incognito"); // open incognito Chrome browser
        options.addArguments("--start-maximized"); // to maximize at the launch

        // Launch the Browser
        WebDriver wd = new ChromeDriver(options);
        wd.get("https://www.yatra.com/");
        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(10)); // Synchronizing webdriver

        // maximize the browser window after launching browser
        // wd.manage().window().maximize();
        clickDepartureButton(wait);

        WebElement currentMonthWebElement = selectMonthFromCalender(wait, 0);
        WebElement nextMonthWebElement = selectMonthFromCalender(wait, 1);

        Thread.sleep(2000);
        String lowesetPriceCurrentMonth = getMeLowestPrice(currentMonthWebElement);
        String lowesetPriceNextMonth = getMeLowestPrice(nextMonthWebElement);
        System.out.println(lowesetPriceCurrentMonth);
        System.out.println(lowesetPriceNextMonth);

        compareTwoMonthsPrices(lowesetPriceCurrentMonth, lowesetPriceNextMonth);

    }

    public static WebElement selectMonthFromCalender(WebDriverWait wait, int index) {
        By calenderMonthsLocator = By.xpath("//*[@class=\"react-datepicker__month-container\"]");
        // List<WebElement> calenderMonths = wd.findElements(calenderMonthsLocator);
        List<WebElement> calenderMonthsList = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calenderMonthsLocator));
        System.out.println(calenderMonthsList.size());

        // We want to focus on the current month
        WebElement currentMonthWebElement = calenderMonthsList.get(index);
        return currentMonthWebElement;

    }

    public static String getMeLowestPrice(WebElement monthWebElement) {
        By dateLocator = By.xpath(".//div[contains(@class, 'react-datepicker__day')]");
        By priceLocator = By.xpath(".//span[contains(@class, 'custom-day-content')]");
        List<WebElement> currentMonthDatesList = monthWebElement.findElements(dateLocator);
        List<WebElement> currentMonthPriceList = monthWebElement.findElements(priceLocator);

        int lowestPrice = Integer.MAX_VALUE;
        WebElement priceElement = null;
        for (WebElement price : currentMonthPriceList) {
            // System.out.println(price.getText());

            String priceString = price.getText();
            if (priceString.length() > 0) {
                priceString = priceString.replace("₹", "").replace(",", "");
                int priceInt = Integer.parseInt(priceString);
                // Find the lowest price
                if (priceInt < lowestPrice) {
                    lowestPrice = priceInt;
                    priceElement = price;
                }
            }
        }
        WebElement dateElement = priceElement.findElement(By.xpath(".//../.."));

        String result = dateElement.getAttribute("aria-label") + " ----- Price is Rs: " + lowestPrice;
        return result;
    }

    public static void compareTwoMonthsPrices(String currentMonthPrice, String nextMonthPrice) {
        int currentMonthColonIndex = currentMonthPrice.indexOf(":");
        int nextMonthColonIndex = nextMonthPrice.indexOf(":");

        String currentPrice = currentMonthPrice.substring(currentMonthColonIndex + 2);
        String nextPrice = nextMonthPrice.substring(nextMonthColonIndex + 2);

        int current = Integer.parseInt(currentPrice);
        int next = Integer.parseInt(nextPrice);

        if (current < next) {
            System.out.println("The lowest price for two months is:" + current);
        } else if (current == next) {
            System.out.println("The price is same for both month! choose whatever you prefer");
        } else {
            System.out.println("The lowest price for two months is:" + next);
        }
    }

    public static void clickDepartureButton(WebDriverWait wait) {
        // Locators return type is By
        By departureDateLocator = By.xpath("//div[@aria-label='Departure Date inputbox']");

        // WebElement departureDateButton = wd.findElement(departureDateLocator);
        WebElement departureDateButton = wait
                .until(ExpectedConditions.elementToBeClickable(departureDateLocator));
        departureDateButton.click();

    }

    public static void closePopUp(WebDriverWait wait) {
        // Check for popup
        By popUpLocator = By.xpath("//div[contains(@class, 'style_popup')]");

        try {
            WebElement popUpElement = wait.until(ExpectedConditions.visibilityOfElementLocated(popUpLocator));
            WebElement crossButton = popUpElement.findElement(By.xpath(".//img[@al='cross']"));
            crossButton.click();

        } catch (TimeoutException e) {
            System.out.println("Popup not shown on the screen");
        }
    }
}
