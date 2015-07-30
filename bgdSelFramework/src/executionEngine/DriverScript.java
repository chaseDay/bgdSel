package executionEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import config.ActionKeywords;
import config.Constants;
import utility.ExcelUtils;
 
public class DriverScript extends Thread{
	
	public static Properties OR;
	public  ActionKeywords actionKeywords;
	public ExcelUtils thisExcelUtils;
	public  String sActionKeyword;
	public  String sPageObject;
	public  Method method[];
		
	public  int iExecution;
	public  int iSuiteRow;
	public  int iTestStep;
	public  int iTestLastStep;
	public  int iTestSuiteCol;
	public  int iTestLastSuite;
	public  String sTestSuiteID;
	public  String sTestCaseID;
	public  String sRunMode;
	public  String sData;
	public  boolean bResult;
	
	public ExtentReports extent;
	public ExtentTest eTest;
	public String reportTag;
	public File reportFolder;
	public int ssCount = 1;
	public int repCount = 1;
	public int threadNum;
	
    public void run() {
    	
    	DriverScript thisDriverScript = this;
    	thisExcelUtils = new ExcelUtils();
    	thisExcelUtils.thisThread = thisDriverScript;
    	actionKeywords = new ActionKeywords(thisDriverScript);
    	method = this.actionKeywords.getClass().getMethods();
    	
    	try {
			thisExcelUtils.setExcelFile(Constants.Path_TestData);
		} catch (Exception e) {
			System.out.println("initial set excel file error : "+e.getMessage());
		}
    	
    	String Path_OR = Constants.Path_OR;
		FileInputStream fs = null;
		OR= new Properties(System.getProperties());
		
		try {
			fs = new FileInputStream(Path_OR);
			OR.load(fs);
		} catch (IOException e) {
			System.out.println("OR load error : "+e.getMessage());
		}
		
		try {
			execute_TestCase();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
    }
		
    private void execute_TestCase() throws Exception {
    	
    	extentReportSetup();

    	int iTotalExecutions = thisExcelUtils.getRowCount(Constants.Sheet_Execution);
    	iExecution = 1;
    	outerloop:
    	for(;iExecution<iTotalExecutions;iExecution++){
    		bResult = true;
    		sTestSuiteID = thisExcelUtils.getCellData(iExecution, Constants.Col_ExecutionID, Constants.Sheet_Execution); 
			sRunMode = thisExcelUtils.getCellData(iExecution, Constants.Col_RunMode,Constants.Sheet_Execution);
			if (sRunMode.equals(String.valueOf(threadNum))){
				iTestSuiteCol = thisExcelUtils.getColContains(sTestSuiteID, Constants.Row_TestSuiteID, Constants.Sheet_TestSuites);
				iTestLastSuite = thisExcelUtils.getTestCasesCount(Constants.Sheet_TestSuites, sTestSuiteID, iTestSuiteCol);
				iSuiteRow = 1;
				bResult=true;
				for (;iSuiteRow<iTestLastSuite;iSuiteRow++){
					sTestCaseID = thisExcelUtils.getCellData(iSuiteRow, iTestSuiteCol, Constants.Sheet_TestSuites); 
					eTest = extent.startTest(sTestCaseID, "");
					iTestStep = thisExcelUtils.getRowContains(sTestCaseID, Constants.Col_TestCaseID, Constants.Sheet_TestSteps);
					iTestLastStep = thisExcelUtils.getTestStepsCount(Constants.Sheet_TestSteps, sTestCaseID, iTestStep);
					bResult=true;
					for (;iTestStep<iTestLastStep;iTestStep++){
			    		sActionKeyword = thisExcelUtils.getCellData(iTestStep, Constants.Col_ActionKeyword,Constants.Sheet_TestSteps);
			    		sPageObject = thisExcelUtils.getCellData(iTestStep, Constants.Col_PageObject, Constants.Sheet_TestSteps);
			    		sData = thisExcelUtils.getCellData(iTestStep, Constants.Col_DataSet, Constants.Sheet_TestSteps);
			    		execute_Actions();
						if(bResult==false){
							extent.endTest(eTest);
							System.out.println("a test has failed");							
							//breaking the outerloop will end the entire test suite
							break outerloop;
						}						
					}
					if(bResult==true){
						eTest.log(LogStatus.PASS, "Pass");
						extent.endTest(eTest);
					}					
				}
			}
    	}
    	extent.flush();
		System.out.println("completed");
    }
    
    public void extentReportSetup() throws Exception{
    	
    	DateFormat df = new SimpleDateFormat("MMddyy");
    	Date dateobj = new Date();
    	//create date format to attach to attach to the end of the html file name
    	reportTag = df.format(dateobj);
    	
    	//file name eReport_MMddyy_numberOfFolder
    	Path dailyRepPath = Paths.get(Constants.Path_ExtentReports+"/eReport_"+reportTag+"_"+repCount);
    	//check if this directory exists
    	if(!Files.exists(dailyRepPath)){
    		//if it does not exist create new directory
	    	boolean rfSuccess = new File(Constants.Path_ExtentReports+"/eReport_"+reportTag+"_"+repCount).mkdirs();
    		if(rfSuccess){
    			reportFolder = new File (Constants.Path_ExtentReports+"/eReport_"+reportTag+"_"+repCount);
    		}
    		//if it does exist, run again and add 1 to repCount (tag at end of directory name)
    		else{
    			repCount++;
    			extentReportSetup();
    			return;
    		}
    	}
    	//if it does exist, run again and add 1 to repCount (tag at end of directory name)
    	else{
    		repCount++;
    		extentReportSetup();
    		return;
    	}
    	
    	boolean rsSuccess = new File(reportFolder.getAbsolutePath()+"/screenshots").mkdirs();
		if(rsSuccess){
			//success
		}
		else{
			System.out.println("failed to create screenshot folder");
		}
    		
    	//name file + date + .html
    	File createHtml = new File(reportFolder.getAbsolutePath()+"//report_"+reportTag+"_"+repCount+".html");
    	createHtml.createNewFile();
    	extent = new ExtentReports(reportFolder.getAbsolutePath()+"//"+createHtml.getName(), true);
    	extent.config()
        .documentTitle("BGD Automation Report")
        .reportName("BGD Automation Report");
    }

     
     private void execute_Actions() throws Exception {
	
		for(int i=0;i<method.length;i++){
			
			if(method[i].getName().equals(sActionKeyword)){
				method[i].invoke(this.actionKeywords,sPageObject, sData);
				if(bResult==true){
					break;
				}else{
					//take a screenshot and add the same tag used for the report
					try{
						eTest.log(LogStatus.INFO, "Snapshot below: " + eTest.addScreenCapture(this.actionKeywords.takeScreenshot(Integer.toString(ssCount))));
						ssCount++;
					}catch(Exception e){
						System.out.println("unable to create screenshot: " + e.getMessage());
					}
					this.actionKeywords.closeBrowser("","");
					break;
				}
			}
		}
    }
}