package config;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static executionEngine.DriverScript.OR;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.relevantcodes.extentreports.LogStatus;

import config.Constants;
import executionEngine.DriverScript;

public class ActionKeywords {
	
	public DriverScript thisThread;
	public WebDriver driver;
		
	public ActionKeywords(DriverScript passThread){
		thisThread = passThread;
	}
			
	public void openBrowser(String object,String data){		
		thisThread.eTest.log(LogStatus.INFO, "Opening Browser");
		try{	
			if(data.equals("Firefox")){
				driver=new FirefoxDriver();
				thisThread.eTest.log(LogStatus.INFO, "Mozilla browser started");			
				}
			else if(data.equals("IE")){
				System.setProperty("webdriver.ie.driver", Constants.Path_IE);
				driver=new InternetExplorerDriver();
				thisThread.eTest.log(LogStatus.INFO, "IE browser started");
				}
			else if(data.equals("Chrome")){
				System.setProperty("webdriver.chrome.driver", Constants.Path_Chrome);
				driver=new ChromeDriver();
				thisThread.eTest.log(LogStatus.INFO, "Chrome browser started");
				}
			int implicitWaitTime=(14);
			//set implicit wait time
			driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
			//maximize browser
			driver.manage().window().maximize();
		}catch (Exception e){
			thisThread.eTest.log(LogStatus.FAIL, "Not able to open the Browser --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
		}
	}
	
	public void navigate(String object, String data){
		try{
			thisThread.eTest.log(LogStatus.INFO, "Navigating to URL" + data);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get(data);
		}catch(Exception e){
			thisThread.eTest.log(LogStatus.FAIL, "Not able to navigate --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
			}
		}
	
	public  void click(String object, String data){
		try{
			thisThread.eTest.log(LogStatus.INFO, "Clicking "+object);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(getLocator(OR.getProperty(object))));
			element.click();
		}catch(Exception x){
			try{
				thisThread.eTest.log(LogStatus.WARNING, "First click on "+object+" didn't work. This sometimes occurs with the Chrome webdriver.  Trying again using Javascript");
				//the first click failed, just to be sure we pause 2 seconds and then scroll to y coordinates of the element
				//attempt to click using javascript instead
				//this will fix some issues that occur on the chrome browser
				Thread.sleep(2000);
				WebElement element = driver.findElement(getLocator(OR.getProperty(object)));
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0,"+element.getLocation().y+")");
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			}
			catch(Exception e){
				thisThread.eTest.log(LogStatus.FAIL, "Not able to click --- " + e.getMessage());
				System.out.println("actionkeywords : "+e.getMessage());
				thisThread.bResult = false;
			}
	    }
	}
	
	public  void input(String object, String data){
		try{
			thisThread.eTest.log(LogStatus.INFO, "Entering the text in " + object);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(OR.getProperty(object))));
			element.clear();
			element.sendKeys(data);
		 }catch(Exception e){
			 thisThread.eTest.log(LogStatus.FAIL, "Not able to Enter Data --- " + e.getMessage());
			 System.out.println("actionkeywords : "+e.getMessage());
			 thisThread.bResult = false;
		 	}
		}
	
	public  void verify(String object, String data){
		try{
			thisThread.eTest.log(LogStatus.INFO, "Verifying the object " + object + " contains the text " + data);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(OR.getProperty(object))));
			Assert.assertEquals(element.getText(),data);
		}catch(AssertionError e){
			thisThread.eTest.log(LogStatus.FAIL, "Unable to verify text --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
		}catch(Exception e){
			thisThread.eTest.log(LogStatus.FAIL, "Unable to verify text --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
		}
	}
	
	public  void dropDownSelect(String object, String data) throws InterruptedException{
		try{
			thisThread.eTest.log(LogStatus.INFO, "Selecting the value " + data + " from the drop down " + object);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(OR.getProperty(object))));
			Select droplist = new Select(element);
			droplist.selectByVisibleText(data);
		}catch(Exception e){
			thisThread.eTest.log(LogStatus.FAIL, "Not able to select the value " + data + " from the drop down " + object + " --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
		}
	}
	
	public  void hover(String object, String data){
		try{
			thisThread.eTest.log(LogStatus.INFO, "Hover over the object " + object);
			Actions actions = new Actions(driver);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(OR.getProperty(object))));
			actions.moveToElement(element).perform();
		}
		catch(Exception x){
			try{
				thisThread.eTest.log(LogStatus.WARNING, "First hover on "+object+" didn't work. This sometimes occurs with the Chrome webdriver.  Trying again using Javascript");
				WebElement element = driver.findElement(getLocator(OR.getProperty(object)));
				String mouseOverScript = 
						  "if(document.createEvent){"
						+ "var evObj = document.createEvent('MouseEvents');"
						+ "evObj.initEvent('mouseover', true, false);"
						+ " arguments[0].dispatchEvent(evObj);"
						+ "} else if(document.createEventObject) { "
						+ "arguments[0].fireEvent('onmouseover');}";
				((JavascriptExecutor)driver).executeScript(mouseOverScript,element);
			}
			catch(Exception e){
			thisThread.eTest.log(LogStatus.FAIL, "Not able to hover" + object + " --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
			}
		}
	}
	
	public  void clearCache(String object, String data){
		try{
			thisThread.eTest.log(LogStatus.INFO, "clearing cache");
			driver.manage().deleteAllCookies();
			Thread.sleep(5000);
		}
		catch(Exception e){
			thisThread.eTest.log(LogStatus.FAIL, "Not able to clear cache --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
		}
	}
	

	public  void waitFor(String object, String data) throws Exception{
		try{
			thisThread.eTest.log(LogStatus.INFO, "Wait for 5 seconds");
			Thread.sleep(5000);
		 }catch(Exception e){
			thisThread.eTest.log(LogStatus.FAIL, "Not able to Wait --- " + e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
			thisThread.bResult = false;
         }
	}

	public  void closeBrowser(String object, String data){
		try{
			thisThread.eTest.log(LogStatus.INFO, "Closing the browser");
			driver.quit();
		 }catch(Exception e){
			 thisThread.eTest.log(LogStatus.FAIL, "Not able to Close the Browser --- " + e.getMessage());
			 System.out.println("actionkeywords : "+e.getMessage());
			 thisThread.bResult = false;
         	}
		}
	
	public  String takeScreenshot (String tagName){
		String screenshotName = null;
		try{
			File scrnshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			//create relative path so image can be attached on other devices
			screenshotName = "./screenshots/screenshot"+tagName+".png";
			FileUtils.copyFile(scrnshot, new File(thisThread.reportFolder.getAbsolutePath()+"/screenshots/screenshot"+tagName+".png"));
		}catch(Exception e){
			System.out.println("screenshot failure : "+e.getMessage());
			System.out.println("actionkeywords : "+e.getMessage());
		}
		return screenshotName;
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