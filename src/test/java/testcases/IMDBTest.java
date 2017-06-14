package testcases;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;


import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import page.Top250Movies;

public class IMDBTest {

	public static WebDriver wbdv = null;
	public static EventFiringWebDriver driver = null;
	public static Properties CONFIG = null;
	public static DesiredCapabilities dc = null;
	public static String failTest = "Fail";

	@Before
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

	@Test
	public void getMovie() throws InterruptedException, IOException {

		Top250Movies top = new Top250Movies(driver);
		top.navigate();
		top.storeData();

	}

	@After
	public void tearDown() {

		// Close the driver
		try {

			driver.quit();
			driver = null;

		} catch (Throwable closeDriverException) {

			System.out.println("Fail : Error came while closing driver : " + closeDriverException.getMessage());

		}

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
