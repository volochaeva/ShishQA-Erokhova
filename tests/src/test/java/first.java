import io.github.cdimascio.dotenv.Dotenv;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class first {
    private WebDriver driver;
    private String baseUrl;
    private String orderNumber;
    private String totalAmount;
    private String currency;

    @Before
    public void setUp() throws Exception {
        Dotenv dotenv = Dotenv.load();
        driver = new ChromeDriver();
        System.setProperty("webdriver.chrome.driver", dotenv.get("WEB_DRIVER_PATH", "."));
        baseUrl = "https://sandbox.cardpay.com/MI/cardpayment2.html?orderXml=PE9SREVSIFdBTExFVF9JRD0nODI5OScgT1JERVJfTlVNQkVSPSc0NTgyMTEnIEFNT1VOVD0nMjkxLjg2JyBDVVJSRU5DWT0nRVVSJyAgRU1BSUw9J2N1c3RvbWVyQGV4YW1wbGUuY29tJz4KPEFERFJFU1MgQ09VTlRSWT0nVVNBJyBTVEFURT0nTlknIFpJUD0nMTAwMDEnIENJVFk9J05ZJyBTVFJFRVQ9JzY3NyBTVFJFRVQnIFBIT05FPSc4NzY5OTA5MCcgVFlQRT0nQklMTElORycvPgo8L09SREVSPg==&sha512=998150a2b27484b776a1628bfe7505a9cb430f276dfa35b14315c1c8f03381a90490f6608f0dcff789273e05926cd782e1bb941418a9673f43c47595aa7b8b0d";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(baseUrl);
        orderNumber=driver.findElement(By.id("order-number")).getText();
        totalAmount = driver.findElement(By.id("total-amount")).getText();
        currency = driver.findElement(By.id("currency")).getText();

    }

    @Test
    public void testValidDataTestCase() throws Exception {
        driver.get(baseUrl);

        WebElement InputcardNumber = driver.findElement(By.id("input-card-number"));
        WebElement InputcardHolder = driver.findElement(By.id("input-card-holder"));
        Select cardExpiresMonth = new Select(driver.findElement(By.id("card-expires-month")));
        Select cardExpiresYear = new Select(driver.findElement(By.id("card-expires-year")));
        WebElement InputCardCvc = driver.findElement(By.id("input-card-cvc"));
        WebElement actionSubmit = driver.findElement(By.id("action-submit"));

        InputcardNumber.sendKeys("4000000000000002");
        InputcardHolder.sendKeys("Jon Down");
        cardExpiresMonth.selectByIndex(7);
        cardExpiresYear.selectByValue("2037");
        InputCardCvc.sendKeys("777");

        actionSubmit.click();
        driver.findElement(By.id("success")).click();

        assertEquals("Confirmed", driver.findElement(By.xpath("//*[@id=\"payment-item-status\"]/div[2]")).getText());
        assertEquals(orderNumber, driver.findElement(By.xpath("//*[@id=\"payment-item-ordernumber\"]/div[2]")).getText());
        assertEquals(totalAmount, driver.findElement(By.xpath("//*[@id=\"payment-item-total-amount\"]")).getText());
        assertEquals(currency, driver.findElement(By.xpath("//*[@id=\"payment-item-total\"]/div[2]")).getText().split("\s")[0]);
    }

    @Test
    public void testInvalidCardNumberTestCase() throws Exception {
        driver.get(baseUrl);

        WebElement InputcardNumber = driver.findElement(By.id("input-card-number"));
        WebElement InputcardHolder = driver.findElement(By.id("input-card-holder"));
        Select cardExpiresMonth = new Select(driver.findElement(By.id("card-expires-month")));
        Select cardExpiresYear = new Select(driver.findElement(By.id("card-expires-year")));
        WebElement InputCardCvc = driver.findElement(By.id("input-card-cvc"));
        WebElement actionSubmit = driver.findElement(By.id("action-submit"));

        InputcardNumber.sendKeys("400000000000002"); // invalid value
        InputcardHolder.sendKeys("Jon Down");
        cardExpiresMonth.selectByIndex(7);
        cardExpiresYear.selectByValue("2037");
        InputCardCvc.sendKeys("777");

        actionSubmit.click();

        assertEquals("Card number is not valid", driver.findElement(By.xpath("//*[@id=\"card-number-field\"]/div/label")).getText());
    }

    @Test
    public void testSubmitEmptyFormTestCase() throws Exception {
        driver.get(baseUrl);

        WebElement actionSubmit = driver.findElement(By.id("action-submit"));

        actionSubmit.click();

        assertEquals("Card number is required", driver.findElement(By.xpath("//*[@id=\"card-number-field\"]/div/label")).getText());
        assertEquals("Cardholder name is required", driver.findElement(By.xpath("//*[@id=\"card-holder-field\"]/div/label")).getText());
        assertEquals("Expiration Date is required", driver.findElement(By.xpath("//*[@id=\"card-expires-field\"]/div/label")).getText());
        assertEquals("CVV2/CVC2/CAV2 is required", driver.findElement(By.xpath("//*[@id=\"card-cvc-field\"]/div/label")).getText());
    }

    @Test
    public void testScreenshot() throws IOException {
        driver.get(baseUrl);

        WebElement cvcHintToggle = driver.findElement(By.id("cvc-hint-toggle"));

        Actions action = new Actions(driver);
        action.moveToElement(cvcHintToggle).build().perform();

        Screenshot screenshot = new AShot().takeScreenshot(driver);

        ImageIO.write(screenshot.getImage(), "png", new File("sc.png"));
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}
