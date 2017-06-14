package page;

import java.io.IOException;

import java.util.List;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import db.DBOperations;
import testcases.IMDBTest;

public class Top250Movies extends IMDBTest{

	EventFiringWebDriver driver;
	String imdbTop250PageTitle = "IMDb Top 250 - IMDb";

	@FindAll({ @FindBy(how = How.XPATH, using = "//td[@class='titleColumn']//a") })
	public List<WebElement> movieNamesEl;

	@FindAll({ @FindBy(how = How.XPATH, using = "//td[@class='titleColumn']//span") })
	public List<WebElement> yearEl;

	@FindAll({ @FindBy(how = How.XPATH, using = "//td[contains(@class,'imdbRating')]") })
	public List<WebElement> ratingEl;

	//Constructor
	public Top250Movies(EventFiringWebDriver driver) {

		this.driver = driver;
		PageFactory.initElements(driver, this);

	}

	// Navigate to the test site
	public void navigate() throws InterruptedException, IOException {

		try {

			// Implicitly wait for 30 seconds for browser to open
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

			// Delete all browser cookies
			driver.manage().deleteAllCookies();

			// Navigate to IMDB Top 250 page
			driver.navigate().to(CONFIG.getProperty("testSiteURL"));

			// Maximize browser window
			System.out.println("Maximizing Browser window...");
			driver.manage().window().maximize();
			System.out.println("Browser window is maximized");

		}

		catch (Throwable navigationError) {

			System.out.println("Error came while navigating to the test site : " + navigationError.getMessage());

		}

		// Verify Login page appears
		String expectedTitle = imdbTop250PageTitle;
		try {

			// Assert that expected value matches with actual value
			Assert.assertEquals(expectedTitle.trim(), driver.getTitle().trim());

		}

		catch (Throwable assertTextException) {

			System.out.println("Fail : Error while Asserting title for IMDB Top 250 Movies page.");

		}
			System.out.println("Successfully navigated to the IMDb Top 250 - IMDb page.");

	}

	//Create database, store all the 250 movie details in SQLite db and display table data
	public void storeData() {

		DBOperations db = new DBOperations();
		
		//Create imdb database
		db.createNewDatabase();
		
		//Create imdb250 table in imdb db
		db.createTable();
		
		//Insert movie details in the table
		for (int i = 0; i < 250; i++) {
			db.insert(movieNamesEl.get(i).getText(), yearEl.get(i).getText().replaceAll("[^0-9]+", ""),
					ratingEl.get(i).getText());
		}

		//Display table data
		db.displayTable();
	}

}
