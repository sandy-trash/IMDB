package testcases;

import org.testng.annotations.Test;

import db.DBOperations;
import page.Top250Movies;

import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

public class IMDBTest {

	public static WebDriver wbdv = null;
	public static EventFiringWebDriver driver = null;
	public static Properties CONFIG = null;
	public static DesiredCapabilities dc = null;
	public static String failTest = "Fail";

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

	}
	
	@Test(priority = 0)
	public void preRequisite() throws InterruptedException, IOException {
		
		// Create page object
		Top250Movies top = new Top250Movies(driver);

		// Navigate to the IMDB top 250 movies page
		top.navigate();

		// Fetch movie details from the page and store in database
		top.storeData();
		driver.quit();
		wbdv = null;
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
		System.out.println(actualYear+ " "+expectedYear);

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
		System.out.println(actualRating + " "+expectedRating);

		// Assert Movie Release Year displayed on page with DB value
		try {

			// Assert that expected value matches with actual value
			Assert.assertEquals(expectedRating.trim(), actualRating.trim());

		} catch (Throwable assertTextException) {

			System.out.println(failTest + " : Error while Asserting movie rating.");

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

	@AfterClass
	public void deleteDB() {
		// Delete the SQLite db
		try {

			File file = new File(System.getProperty("user.dir") + "/database/imdb.db");

			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}