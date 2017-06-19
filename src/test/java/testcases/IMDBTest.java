package testcases;

import org.testng.annotations.Test;

import db.DBOperations;
import page.Top250Movies;

import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

public class IMDBTest {

	public static WebDriver wbdv = null;
	public static EventFiringWebDriver driver = null;
	public static Properties CONFIG = null;
	public static DesiredCapabilities dc = null;
	public static String failTest = "Fail";
	
	@BeforeClass
	public void deleteDB() {
		// Delete the SQLite db
		try {

			File file = new File(System.getProperty("user.dir") + "\\database\\imdb.db");

			if (file.exists()) {
				file.delete();
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	@BeforeMethod
	public void setUp() throws IOException {

		// Locates and loads the config properties
		CONFIG = new Properties();

		FileInputStream fs = new FileInputStream(
				System.getProperty("user.dir") + "/src/test/java/configuration/config.properties");
		CONFIG.load(fs);

		if (wbdv == null) {

			try {

				if (CONFIG.getProperty("test_browser").toLowerCase().contains("internet explorer")
						|| CONFIG.getProperty("test_browser").toLowerCase().contains("ie")) {

					dc = DesiredCapabilities.internetExplorer();
					dc.setCapability("silent", true);
					wbdv = new InternetExplorerDriver(dc);
					driver = new EventFiringWebDriver(wbdv);

				}

				else if (CONFIG.getProperty("test_browser").toLowerCase().contains("firefox")
						|| CONFIG.getProperty("test_browser").toLowerCase().contains("ff")) {

					ProfilesIni allProfiles = new ProfilesIni();
					FirefoxProfile profile = allProfiles.getProfile("default");
					profile.setAcceptUntrustedCertificates(true);
					profile.setAssumeUntrustedCertificateIssuer(false);
					wbdv = new FirefoxDriver(profile);
					driver = new EventFiringWebDriver(wbdv);

				}

				else if (CONFIG.getProperty("test_browser").toLowerCase().contains("safari")) {

					dc = DesiredCapabilities.safari();

				}

				else if (CONFIG.getProperty("test_browser").toLowerCase().contains("chrome")) {

					System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
					dc = DesiredCapabilities.chrome();
					wbdv = new ChromeDriver(dc);
					driver = new EventFiringWebDriver(wbdv);

				}

			}

			catch (Throwable initBrowserException) {
				System.out
						.println("Error came while creating a browser instance : " + initBrowserException.getMessage());
			}
		}

		System.out.println("Created browser instance successfully");
		// Delete the SQLite db if already present
		try {

			File file = new File(System.getProperty("user.dir") + "\\database\\imdb.db");

			if (file.exists()) {
				file.delete();
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	@Test(priority = 0)
	public void preRequisite() throws InterruptedException, IOException {

		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		// Fetch movie details from the page and store in database
		top.storeData();

	}

	@Test(priority = 1)
	public void verifyMoviesCount() throws InterruptedException, IOException {
		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		int count = top.movieNamesEl.size();
		if (count < 250) {
			System.out.println(failTest + " : There are only " + count + " movies on the page");
		} else
			System.out.println("Pass : Movies count is correct on the page.");
	}

	@Test(priority = 2)
	public void verifyMovieReleaseYear() throws InterruptedException, IOException {

		String movie = "The Shawshank Redemption";
		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		// Get movie release year
		String actualYear = top.releaseYearFromPage(movie);

		DBOperations db = new DBOperations();
		String expectedYear = db.getDetails("year", movie);

		// Assert Movie Release Year displayed on page with DB value
		try {

			// Assert that expected value matches with actual value
			Assert.assertEquals(expectedYear.trim(), actualYear.trim());

		} catch (Throwable assertTextException) {

			System.out.println(failTest + " : Error while Asserting movie release year.");

		}

	}

	@Test(priority = 3)
	public void verifyMovieRating() throws InterruptedException, IOException {

		String movie = "The Shawshank Redemption";
		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		// Get movie release year
		String actualRating = top.ratingFromPage(movie);

		DBOperations db = new DBOperations();
		String expectedRating = db.getDetails("rating", movie);
		System.out.println(actualRating + " " + expectedRating);

		// Assert Movie Release Year displayed on page with DB value
		try {

			// Assert that expected value matches with actual value
			Assert.assertEquals(expectedRating.trim(), actualRating.trim());

		} catch (Throwable assertTextException) {

			System.out.println(failTest + " : Error while Asserting movie rating.");

		}

	}

	@Test(priority = 4)
	public void verifyDuplicateMovieData() throws InterruptedException, IOException {
		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		HashSet<String> unique = new HashSet<String>();
		String str = null;
		for (WebElement e : top.movieNamesEl) {
			str = e.getText();
			if (unique.contains(str)) {
				System.out.println(failTest + " : IMDB page has duplicate movie names in the Top 250 movie list.");
				break;
			} else
				unique.add(str);
		}
	}

	@Test(priority = 5)
	public void verifyRankingSorting() throws InterruptedException, IOException {

		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		try {
			// Locate drop-down field
			Select select = new Select(driver.findElement(By.name("sort")));

			// Select value from drop-down
			select.selectByVisibleText("Ranking");

		} catch (NoSuchElementException ex) {
			System.out.println("Sort by element is not present");
		}
		List<WebElement> rankEl = driver.findElements(By.xpath("//td[@class='titleColumn']"));

		int temp = 0;

		for (WebElement el : rankEl) {

			if (Integer.parseInt(el.getText().split("\\.")[0]) < temp) {

				System.out.println(failTest + " : Page is not sorted on the basis of rank.");
				break;
			}
			temp = Integer.parseInt(el.getText().split("\\.")[0]);
		}
	}

	@Test(priority = 6)
	public void verifyRatingSorting() throws InterruptedException, IOException {

		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();
		try {
			// Locate drop-down field
			Select select = new Select(driver.findElement(By.name("sort")));

			// Select value from drop-down
			select.selectByVisibleText("IMDb Rating");

		} catch (NoSuchElementException ex) {
			System.out.println("Sort by element is not present");
		}
		List<WebElement> ratingEl = driver.findElements(By.xpath("//td[contains(@class,'imdbRating')]"));

		double temp = 12.0;

		for (WebElement el : ratingEl) {

			if (Double.parseDouble(el.getText()) > temp) {
				System.out.println(failTest + " : Page is not sorted on the basis of rating.");
				break;
			}
			temp = Double.parseDouble(el.getText());
		}
	}

	@Test(priority = 7)
	public void verifyReleaseDateSorting() throws InterruptedException, IOException {

		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		try {
			// Locate drop-down field
			Select select = new Select(driver.findElement(By.name("sort")));

			// Select value from drop-down
			select.selectByVisibleText("Release Date");

		} catch (NoSuchElementException ex) {
			System.out.println("Sort by element is not present");
		}
		List<WebElement> dateEl = driver.findElements(By.xpath("//td[@class='titleColumn']//span"));

		int temp = 20000;

		for (WebElement el : dateEl) {

			if (Integer.parseInt(el.getText().replaceAll("[^0-9]+", "")) > temp) {

				System.out.println(failTest + " : Page is not sorted on the basis of release year.");

				break;
			}
			temp = Integer.parseInt(el.getText().replaceAll("[^0-9]+", ""));
		}
	}

	@AfterMethod
	public void tearDown() {

		// Close the driver
		try {

			driver.quit();
			wbdv = null;

		} catch (Throwable closeDriverException) {

			System.out.println("Fail : Error came while closing driver : " + closeDriverException.getMessage());

		}

	}
	
}