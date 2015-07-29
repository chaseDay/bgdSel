package config;

public class Constants {
	
	//System Variables
	public static final String URL = "http://bgd.softwaydev.com/";
	public static final String Path_TestData = System.getProperty("user.dir") + "//src//dataEngine//DataEngine.xlsx";
	public static final String Path_OR = System.getProperty("user.dir") + "//src//config//OR.txt";
	public static final String Path_IE = System.getProperty("user.dir") + "//drivers//IEDriverServer.exe";
	public static final String Path_Chrome = System.getProperty("user.dir") + "//drivers//chromedriver.exe";
	public static final String Path_ExtentReports = System.getProperty("user.dir") + "//extentReports";
	public static final String File_TestData = "DataEngine.xlsx";
	
	//Data Sheet Column Numbers
	public static final int Col_ExecutionID = 0;
	public static final int Row_TestSuiteID = 0;
	public static final int Col_TestSuiteID = 0;
	public static final int Col_TestCaseID = 0;
	public static final int Col_TestScenarioID =1 ;
	public static final int Col_PageObject =4 ;
	public static final int Col_ActionKeyword =5 ;
	public static final int Col_RunMode =2 ;
	public static final int Col_DataSet =6 ;
		
	// Data Engine Excel sheets
	public static final String Sheet_TestSteps = "Test Steps";
	public static final String Sheet_TestCases = "Test Cases";
	public static final String Sheet_TestSuites = "Test Suites";
	public static final String Sheet_Execution = "Execution";


	

}
