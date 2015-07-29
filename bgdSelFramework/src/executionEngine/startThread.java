package executionEngine;

import config.Constants;
import utility.ExcelUtils;

public class startThread {
	
	private static ExcelUtils intialExcelUtils;
	private static String runMode;
	
	private static int targetThread = 1;

	public static void main(String[] args) throws Exception {
    	
		intialExcelUtils = new ExcelUtils();
		intialExcelUtils.setExcelFile(Constants.Path_TestData);
		
		int _iTotalExecutions = intialExcelUtils.getRowCount(Constants.Sheet_Execution);
    	int _iExecution = 1;
    	for(;_iExecution<_iTotalExecutions;_iExecution++){
			runMode = intialExcelUtils.getCellData(_iExecution, Constants.Col_RunMode,Constants.Sheet_Execution);
			if (runMode.equals(String.valueOf(targetThread))){
				startNewThread(targetThread);
				targetThread++;
			}
    	}		
    }
	
	public static void startNewThread(int passThreadNum){
		DriverScript newThread = new DriverScript();
		newThread.threadNum = passThreadNum;
		newThread.start();
	}
}
