import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;

public class ApplitoolsExample {

	RemoteWebDriver driver;
	Eyes eyes;
	EyesRunner runner;
	static BatchInfo batch;
	Map<String, String> params;
	SoftAssert softAssertion = new SoftAssert();
	Configuration conf;

	@BeforeSuite
	void beforeSuite(ITestContext context) {
		params = context.getCurrentXmlTest().getAllParameters();
		batch = new BatchInfo(params.get("BatchName"));
		batch.addProperty("Demo Batch", "Yes");
	}

	@BeforeMethod
	void setUpDriver(Method method){
		try {
			createDriver(getConfigBool("ApplitoolsValidation"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

//	Creates the web driver and Eyes sessions
	public void createDriver(Boolean useEyes) throws MalformedURLException {
		if(useEyes){
			if (isVisualGridRunner()) {
				runner = new VisualGridRunner(new RunnerOptions().testConcurrency(Integer.parseInt(getParam("VisualGridConcurrentTests"))));
			} else {
				runner = new ClassicRunner();
			}

			Logger logger = new Logger();
			logger.setLogHandler(new StdoutLogHandler(true));

	//      Logging
			if (runner instanceof VisualGridRunner && getConfigBool("EnableVGResourceWriter")) {
				((VisualGridRunner) runner).setDebugResourceWriter(new FileDebugResourceWriter(logger, "target/logging/vg"));
			}

			eyes = new Eyes(runner);
			conf = new Configuration();
			conf.setTestName(getParam("TestName"))
					.setAppName(getParam("AppName"))
					.setApiKey(System.getProperty("APPLITOOLS_API_KEY") != null ? System.getProperty("APPLITOOLS_API_KEY") : getParam("APIKey"))
//					.setApiKey(getParam("APIKey") != null ? getParam("APIKey") : System.getenv("APPLITOOLS_API_KEY"))
					.setIgnoreDisplacements(true)
					.setBranchName(getParam("Branch"))
					.setParentBranchName(getParam("ParentBranch"))
					.setBatch(batch)
					.setMatchLevel(MatchLevel.STRICT);

			eyes.setWaitBeforeScreenshots(Integer.parseInt(getParam("WaitBeforeScreenshots")));
			eyes.setHideScrollbars(true);

			if (isVisualGridRunner()) {
				eyes.addProperty("Runner", "Ultrafast Grid");
				eyes.addProperty("Capture Viewport Size", getParam("LocalBrowserViewport"));
			} else {
				eyes.addProperty("Runner", "Classic");
			}

			if (isVisualGridRunner()) {
				if (getConfigBool("RunOnDesktop")) {
					for (BrowserType b : new TargetBrowsers().getBrowserList()) {
						for (String viewport : new TargetBrowsers().getViewports()) {
							conf.addBrowser(translateViewport(viewport, "width"), translateViewport(viewport, "height"), b);
						}
					}
				}

				//Add Android devices that run on Chrome emulation
				if (getConfigBool("RunOnMobile")) {
					for (DeviceName d : new TargetBrowsers().getAndroidDeviceNames()) {
						if (getConfigBool("MobilePortrait")) {
							conf.addDeviceEmulation(d, ScreenOrientation.PORTRAIT);
						}
						if (getConfigBool("MobileLandscape")) {
							conf.addDeviceEmulation(d, ScreenOrientation.LANDSCAPE);
						}
					}

					for (IosDeviceName device : new TargetBrowsers().getIosDeviceNames()) {
						if (getConfigBool("MobilePortrait")) {
							conf.addBrowser(new IosDeviceInfo(device, ScreenOrientation.PORTRAIT));
						}
						if (getConfigBool("MobileLandscape")) {
							conf.addBrowser(new IosDeviceInfo(device, ScreenOrientation.LANDSCAPE));
						}
					}
				}
			}

			if (getConfigBool("EnableEyesLogger")) {
				eyes.setLogHandler(new FileLogger("target/logging/" + getParam("targetEnvironment") + ".log", false, true));
			}
		}

		switch (getParam("ExecutionBrowser")) {
			case "chrome":
				ChromeOptions cOptions = new ChromeOptions();
				if(getConfigBool("RunHeadless")) {
					cOptions.addArguments("--headless");
				}

				try {
					driver = new ChromeDriver(cOptions);
				} catch (Exception e) {
					System.setProperty("webdriver.chrome.driver", getParam("ChromeDriver"));
					driver = new ChromeDriver(cOptions);
				}
				break;

			case "firefox":
				FirefoxOptions fOptions = new FirefoxOptions();
				if(getConfigBool("RunHeadless")){
					fOptions.setHeadless(true);
				}

				try {
					driver = new FirefoxDriver(fOptions);
				} catch (Exception e) {
					System.setProperty("webdriver.gecko.driver", getParam("GeckoDriver"));
					driver = new FirefoxDriver(fOptions);
				}
				break;
		}

		if(useEyes){
			eyes.setConfiguration(conf);
			eyes.open(driver, getParam("AppName"), getParam("TestName"), translateViewport(getParam("LocalBrowserViewport")));
		}

		System.out.println("_______________________________________________________________");
	}

	@Test
	public void CannedDemo(){
		if(getConfigBool("ClassicValidation")){
			ClassicValidation();
		}

		if(getConfigBool("ApplitoolsValidation")){
			ApplitoolsValidation();
		}
	}

	//10 TestNG Assertions
	//10 Locators
	//72 LOC (including comments and line breaks for readability)
	public void ClassicValidation() {
		try {
			driver.get(getURL());

			//Verify no error messages are displayed initially
			softAssertion.assertFalse(
					driver.findElementById("alert").isDisplayed(),
					"Validate no error messages on initial load"
			);

			//Assert username placeholder text
			softAssertion.assertEquals(
					driver.findElement(By.id("username")).getAttribute("placeholder"),
					"Enter your username",
					"Validate Username placeholder text"
			);

			//Assert username label is correct
			softAssertion.assertEquals(
					driver.findElement(By.xpath("//label[@for='username']")).getText(),
					"Username",
					"Validate Username label"
			);

			//Assert password placeholder text
			softAssertion.assertEquals(
					driver.findElement(By.id("password")).getAttribute("placeholder"),
					"Enter your password",
					"Validate Password placeholder text"
			);

			//Assert password label is correct
			softAssertion.assertEquals(
					driver.findElement(By.xpath("//label[@for='password']")).getText(),
					"Password",
					"Validate Password label"
			);

			//Assert if SignIn buttons label is "Sign In"
			softAssertion.assertEquals(
					driver.findElement(By.id("log-in")).getText(),
					"Sign In",
					"Validate Sign In button"
			);

			//Assert Remember Me checkbox exists
			softAssertion.assertEquals(
					driver.findElementsByXPath("//label[text()='Remember Me']/input[@type='checkbox']").size(),
					1,
					"Validate Remember Me checkbox"
			);

			//Assert if Twitter button exists
			softAssertion.assertEquals(
					driver.findElements(By.xpath("//img[contains(@src, 'twitter.png')]")).size(),
					1,
					"Validate Twitter button"
			);

			//Assert if Facebook button exists
			softAssertion.assertEquals(
					driver.findElements(By.xpath("//img[@id='facebook']")).size(),
					1,
					"Validate Facebook button"
			);

			//Check the error for no username/password
			//Click on the Login button
			driver.findElement(By.id("log-in")).click();

			//Assert the error text
			softAssertion.assertEquals(
					driver.findElement(By.id("alert")).getText().trim(),
					"Please enter username and password",
					"Validate No Username & Password error"
			);

		} catch (org.openqa.selenium.NoSuchElementException ex) {
			System.out.println("NoSuchElementException: " + ex.toString());
		}
	}

	//1 Locator
	//6 LOC including comments and line breaks for readability
	public void ApplitoolsValidation() {
		getParam("APIKey");

		driver.get(getURL());

		eyes.check("Initial Sign In Page", Target.window());

		//Check the error for no username/password
		//Click on the Login button
		driver.findElement(By.id("log-in")).click();

		eyes.check("No Username and Password error", Target.window());
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		if(getConfigBool("ApplitoolsValidation")){
			try {
				eyes.closeAsync();
				runner.getAllTestResults(true);
			} catch (DiffsFoundException dfe) {
				System.out.println(dfe.getMessage());
				Assert.fail("Differences Found");
			}
		}
	}

	@AfterTest(alwaysRun = true)
	void afterTest() {
		try {
			softAssertion.assertAll();
		} catch (AssertionError ex) {
			System.out.println(ex.getMessage());
//			System.out.println(ex.toString());
		}
	}

	@AfterSuite(alwaysRun = true)
	void afterSuite(ITestContext context) {
		try {
			driver.close();
			driver.quit();
		} catch (NoSuchSessionException ex) {
		}
	}

	Boolean isVisualGridRunner() {
		return getConfigBool("UseVisualGrid");
	}

	Boolean getConfigBool(String configName) {
		return Boolean.parseBoolean(getParam(configName));
	}

	String getParam(String paramName) {
		if(System.getProperty(paramName) == null){
			return params.get(paramName);
		} else {
			return System.getProperty(paramName);
		}
	}

	String getURL() {
		String url;
		url = getParam("URL") + "?version=" + getParam("AppVersion");
		if (getConfigBool("UseOften")) {
			url = url + "&useoften=true";
		}

		if (getConfigBool("SessionID")) {
			url = url + "&sessionid=true";
		}

		if (getConfigBool("ModifyFont")) {
			url = url + "&modifyfont=true";
		}

		if (getConfigBool("ChangeLogo")) {
			url = url + "&changelogo=true";
		}

		if (getConfigBool("ResolveContrastIssue")) {
			url = url + "&fixcontrast=true";
		}

		if (getConfigBool("ReferAFriend")) {
			url = url + "&refer=true";
		}

		return url;
	}

	RectangleSize translateViewport(String viewportString){
		String[] vpSize = viewportString.split("x");
		return new RectangleSize(Integer.parseInt(vpSize[0]), Integer.parseInt(vpSize[1]));
	}

	Integer translateViewport(String viewportString, String dimension){
		String[] vpSize = viewportString.split("x");
		Integer size = 0;

		if(dimension.equals("width")){
			size = Integer.parseInt(vpSize[0]);
		}

		if(dimension.equals("height")){
			size = Integer.parseInt(vpSize[1]);
		}

		return size;
	}
}