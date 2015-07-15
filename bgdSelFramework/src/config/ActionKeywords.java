package config;

import java.util.concurrent.TimeUnit;

import static executionEngine.DriverScript.OR;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import config.Constants;
import executionEngine.DriverScript;
import utility.Log;

public class ActionKeywords {
	
		public static WebDriver driver;
		
			
	public static void openBrowser(String object,String data){		
		Log.info("Opening Browser");
		try{				
			if(data.equals("Mozilla")){
				driver=new FirefoxDriver();
				Log.info("Mozilla browser started");				
				}
			else if(data.equals("IE")){
				System.setProperty("webdriver.ie.driver", Constants.Path_IE);
				driver=new InternetExplorerDriver();
				Log.info("IE browser started");
				}
			else if(data.equals("Chrome")){
				System.setProperty("webdriver.chrome.driver", Constants.Path_Chrome);
				driver=new ChromeDriver();
				Log.info("Chrome browser started");
				}
			
			int implicitWaitTime=(14);
			//set implicit wait time
			driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
			//maximize browser
			driver.manage().window().maximize();
		}catch (Exception e){
			Log.info("Not able to open the Browser --- " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public static void navigate(String object, String data){
		try{
			Log.info("Navigating to URL");
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get(data);
		}catch(Exception e){
			Log.info("Not able to navigate --- " + e.getMessage());
			DriverScript.bResult = false;
			}
		}
	
	public static void click(String object, String data){
		try{
			Log.info("Clicking on web element "+ object);
			driver.findElement(getLocator(OR.getProperty(object))).click();
		 }catch(Exception e){
 			Log.error("Not able to click --- " + e.getMessage());
 			DriverScript.bResult = false;
         	}
		}
	
	public static void input(String object, String data){
		try{
			Log.info("Entering the text in " + object);
			driver.findElement(getLocator(OR.getProperty(object))).clear();
			driver.findElement(getLocator(OR.getProperty(object))).sendKeys(data);
		 }catch(Exception e){
			 Log.error("Not able to Enter Data --- " + e.getMessage());
			 DriverScript.bResult = false;
		 	}
		}
	
	public static void verify(String object, String data){
		try{
			Log.info("Verifying the object " + object + " contains the text " + data);
			Assert.assertEquals(driver.findElement(getLocator(OR.getProperty(object))).getText(),data);
		}catch(AssertionError e){
			Log.error("Unable to verify text --- " + e.getMessage());
			DriverScript.bResult = false;
		}catch(Exception e){
			Log.error("Unable to verify text --- " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public static void dropDownSelect(String object, String data){
		try{
			Log.info("Selecting the value " + data + " from the drop down " + object);
			Select droplist = new Select(driver.findElement(getLocator(OR.getProperty(object))));
			droplist.selectByVisibleText(data);
		}catch(Exception e){
			Log.error("Not able to select the value " + data + " from the drop down " + object + " --- " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public static void hover(String object, String data){
		try{
			Log.info("Hover over the object " + object);
			Actions actions = new Actions(driver);
			WebElement hoverObject = driver.findElement(getLocator(OR.getProperty(object)));
			actions.moveToElement(hoverObject).perform();
		}
		catch(Exception e){
			Log.error("Not able to hover" + object + " --- " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	
	public static void clearCache(String object, String data){
		try{
			Log.info("clearing cache");
			driver.manage().deleteAllCookies();
			Thread.sleep(5000);
		}
		catch(Exception e){
			Log.error("Not able to clear cache --- " + e.getMessage());
			DriverScript.bResult = false;
		}
	}
	

	public static void waitFor(String object, String data) throws Exception{
		try{
			Log.info("Wait for 5 seconds");
			Thread.sleep(5000);
		 }catch(Exception e){
			 Log.error("Not able to Wait --- " + e.getMessage());
			 DriverScript.bResult = false;
         	}
		}

	public static void closeBrowser(String object, String data){
		try{
			Log.info("Closing the browser");
			driver.quit();
		 }catch(Exception e){
			 Log.error("Not able to Close the Browser --- " + e.getMessage());
			 DriverScript.bResult = false;
         	}
		}
	
	
	//Separate element type and locator from OR	
	public static By getLocator(String strElement) throws Exception {
         
        // extract the locator type and value from the object
        String locatorType = strElement.split(":")[0];
        String locatorValue = strElement.split(":")[1];
         
        // return a instance of the By class based on the type of the locator
        if(locatorType.toLowerCase().equals("id"))
            return By.id(locatorValue);
        else if(locatorType.toLowerCase().equals("name"))
            return By.name(locatorValue);
        else if(locatorType.toLowerCase().equals("class"))
            return By.className(locatorValue);
        else if(locatorType.toLowerCase().equals("link"))
            return By.linkText(locatorValue);
        else if(locatorType.toLowerCase().equals("partiallinktext"))
            return By.partialLinkText(locatorValue);
        else if(locatorType.toLowerCase().equals("css"))
            return By.cssSelector(locatorValue);
        else if(locatorType.toLowerCase().equals("xpath"))
            return By.xpath(locatorValue);
        else
            throw new Exception("Unknown locator type '" + locatorType + "' for value '" + locatorValue + "'");
    	}

	}