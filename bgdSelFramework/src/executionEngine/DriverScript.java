package executionEngine;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
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
 
public class DriverScript {
	
	public static Properties OR;
	public static ActionKeywords actionKeywords;
	public static String sActionKeyword;
	public static String sPageObject;
	public static Method method[];
		
	public static int iExecution;
	public static int iSuiteRow;
	public static int iTestStep;
	public static int iTestLastStep;
	public static int iTestSuiteCol;
	public static int iTestLastSuite;
	public static String sTestSuiteID;
	public static String sTestCaseID;
	public static String sRunMode;
	public static String sData;
	public static boolean bResult;
	
	private static ExtentReports extent;
	public static ExtentTest eTest;
	
	
	public DriverScript() throws NoSuchMethodException, SecurityException{
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();	
	}
	
    public static void main(String[] args) throws Exception {
    	System.out.println("started");
    	ExcelUtils.setExcelFile(Constants.Path_TestData);
    	//DOMConfigurator.configure("log4j.xml");
    	String Path_OR = Constants.Path_OR;
		FileInputStream fs = new FileInputStream(Path_OR);
		OR= new Properties(System.getProperties());
		OR.load(fs);
		
		DriverScript startEngine = new DriverScript();
		startEngine.execute_TestCase();
		
    }
		
    private void execute_TestCase() throws Exception {
    	
    	//create date format to attach to end of html file name
    	DateFormat df = new SimpleDateFormat("ddMMyyHHmm");
    	Date dateobj = new Date();
    	//name file eReport + date + time + .html
    	File createHtml = new File(Constants.Path_ExtentReports+"//eReport"+df.format(dateobj)+".html");
    	createHtml.createNewFile();
    	extent = new ExtentReports(Constants.Path_ExtentReports+"//"+createHtml.getName(), true);
    	extent.config()
        .documentTitle("BGD Automation Report")
        .reportName("BGD Automation Report");

    	int iTotalExecutions = ExcelUtils.getRowCount(Constants.Sheet_Execution);
    	iExecution = 1;
    	outerloop:
    	for(;iExecution<iTotalExecutions;iExecution++){
    		bResult = true;
    		sTestSuiteID = ExcelUtils.getCellData(iExecution, Constants.Col_ExecutionID, Constants.Sheet_Execution); 
			sRunMode = ExcelUtils.getCellData(iExecution, Constants.Col_RunMode,Constants.Sheet_Execution);
			if (sRunMode.equals("Yes")){
				iTestSuiteCol = ExcelUtils.getColContains(sTestSuiteID, Constants.Row_TestSuiteID, Constants.Sheet_TestSuites);
				iTestLastSuite = ExcelUtils.getTestCasesCount(Constants.Sheet_TestSuites, sTestSuiteID, iTestSuiteCol);
				iSuiteRow = 1;
				bResult=true;
				for (;iSuiteRow<iTestLastSuite;iSuiteRow++){
					sTestCaseID = ExcelUtils.getCellData(iSuiteRow, iTestSuiteCol, Constants.Sheet_TestSuites); 
					eTest = extent.startTest(sTestCaseID, "");
					iTestStep = ExcelUtils.getRowContains(sTestCaseID, Constants.Col_TestCaseID, Constants.Sheet_TestSteps);
					iTestLastStep = ExcelUtils.getTestStepsCount(Constants.Sheet_TestSteps, sTestCaseID, iTestStep);
					bResult=true;
					for (;iTestStep<iTestLastStep;iTestStep++){
			    		sActionKeyword = ExcelUtils.getCellData(iTestStep, Constants.Col_ActionKeyword,Constants.Sheet_TestSteps);
			    		sPageObject = ExcelUtils.getCellData(iTestStep, Constants.Col_PageObject, Constants.Sheet_TestSteps);
			    		sData = ExcelUtils.getCellData(iTestStep, Constants.Col_DataSet, Constants.Sheet_TestSteps);
			    		execute_Actions();
						if(bResult==false){
							ExcelUtils.setCellData(Constants.KEYWORD_FAIL,iExecution,Constants.Col_ExecutionResult,Constants.Sheet_Execution);
							System.out.println("failed");
							//breaking the outerloop will end the entire test suite!
							break outerloop;
						}						
					}
					if(bResult==true){
					ExcelUtils.setCellData(Constants.KEYWORD_PASS,iExecution,Constants.Col_ExecutionResult,Constants.Sheet_Execution);
					eTest.log(LogStatus.PASS, "Pass");
					extent.endTest(eTest);
					}					
				}
			}
    	}
    	extent.flush();
		System.out.println("completed");
    }

     
     private static void execute_Actions() throws Exception {
	
		for(int i=0;i<method.length;i++){
			
			if(method[i].getName().equals(sActionKeyword)){
				method[i].invoke(actionKeywords,sPageObject, sData);
				if(bResult==true){
					ExcelUtils.setCellData(Constants.KEYWORD_PASS, iTestStep, Constants.Col_TestStepResult, Constants.Sheet_TestSteps);
					break;
				}else{
					ExcelUtils.setCellData(Constants.KEYWORD_FAIL, iTestStep, Constants.Col_TestStepResult, Constants.Sheet_TestSteps);
					ActionKeywords.closeBrowser("","");
					break;
					}
				}
			}
     }
     
}