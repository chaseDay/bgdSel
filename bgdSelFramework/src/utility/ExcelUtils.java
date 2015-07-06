package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;

import config.Constants;
import executionEngine.DriverScript;
    public class ExcelUtils {
                private static XSSFSheet ExcelWSheet;
                private static XSSFWorkbook ExcelWBook;
                private static org.apache.poi.ss.usermodel.Cell Cell;
                private static XSSFRow Row;
                //private static XSSFRow Row;
           
            public static void setExcelFile(String Path) throws Exception {
            	try {
                    FileInputStream ExcelFile = new FileInputStream(Path);
                    ExcelWBook = new XSSFWorkbook(ExcelFile);
            	} catch (Exception e){
            		Log.error("Class Utils | Method setExcelFile | Exception desc : "+e.getMessage());
            		DriverScript.bResult = false;
                	}
            	}
            
			public static String getCellData(int RowNum, int ColNum, String SheetName ) throws Exception{
                try{
                	ExcelWSheet = ExcelWBook.getSheet(SheetName);
                   	Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
                   	//numeric values in cells will be set as strings
                   	Cell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
                   	String CellData = Cell.getStringCellValue();
               		return CellData;                  	
                   	
                 }catch (Exception e){
                	 if	(e.getMessage() == null){
         				//empty cell
         			 }
         			 else{
         				Log.error("Class Utils | Method getCellData | Exception desc : "+e.getMessage());
         			 }
                     DriverScript.bResult = false;
                     return"";
                     }
                 }
            
        	
			public static int getRowCount(String SheetName){
        		int iNumber=0;
        		try {
        			ExcelWSheet = ExcelWBook.getSheet(SheetName);
        			iNumber=ExcelWSheet.getLastRowNum()+1;
        		} catch (Exception e){
        			if	(e.getMessage() == null){
        				//empty cell
         			}
         			else{
         				 Log.error("Class Utils | Method getRowCount | Exception desc : "+e.getMessage());
         			}
        			DriverScript.bResult = false;
        			}
        		return iNumber;
        		}
        	
			public static int getRowContains(String sTestCaseName, int colNum,String SheetName) throws Exception{
        		int iRowNum=0;	
        		try {
        		    //ExcelWSheet = ExcelWBook.getSheet(SheetName);
        			int rowCount = ExcelUtils.getRowCount(SheetName);
        			for (; iRowNum<rowCount; iRowNum++){
        				if  (ExcelUtils.getCellData(iRowNum,colNum,SheetName).equalsIgnoreCase(sTestCaseName)){
        					break;
        				}
        			}       			
        		} catch (Exception e){
        			if	(e.getMessage() == null){
        				//empty cell
         			}
         			else{
         				Log.error("Class Utils | Method getRowContains | Exception desc : "+e.getMessage());
         			}
        			DriverScript.bResult = false;
        			}
        		return iRowNum;
        		}
        	
			public static int getTestStepsCount(String SheetName, String sTestCaseID, int iTestCaseStart) throws Exception{
        		try {
	        		for(int i=iTestCaseStart;i<=ExcelUtils.getRowCount(SheetName);i++){
	        			if(!sTestCaseID.equals(ExcelUtils.getCellData(i, Constants.Col_TestCaseID, SheetName))){
	        				int number = i;
	        				return number;      				
	        				}
	        			}
	        		ExcelWSheet = ExcelWBook.getSheet(SheetName);
	        		int number=ExcelWSheet.getLastRowNum()+1;
	        		return number;
        		} catch (Exception e){
        			Log.error("Class Utils | Method getRowContains | Exception desc : "+e.getMessage());
        			DriverScript.bResult = false;
        			return 0;
                }
        	}
        	
        	public static void setCellData(String Result,  int RowNum, int ColNum, String SheetName) throws Exception    {
                   try{
                	   
                	   ExcelWSheet = ExcelWBook.getSheet(SheetName);
                	   //Added style to prevent POI from removing borders
                       CellStyle style = ExcelWBook.createCellStyle();
                   	   style.setBorderBottom(CellStyle.BORDER_THIN);
                   	   style.setBorderTop(CellStyle.BORDER_THIN);
                   	   style.setBorderRight(CellStyle.BORDER_THIN);
                   	   style.setBorderLeft(CellStyle.BORDER_THIN);
                       Row  = ExcelWSheet.getRow(RowNum);
                       Cell = Row.getCell(ColNum, org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL);
                       if (Cell == null) {
                    	   Cell = Row.createCell(ColNum);
                    	   Cell.setCellValue(Result);
                        } else {
                            Cell.setCellValue(Result);
                        }
                         Cell.setCellStyle(style);
                         FileOutputStream fileOut = new FileOutputStream(Constants.Path_TestData);
                         ExcelWBook.write(fileOut);
                         //fileOut.flush();
                         fileOut.close();
                         ExcelWBook = new XSSFWorkbook(new FileInputStream(Constants.Path_TestData));
                     }catch(Exception e){
                    	 DriverScript.bResult = false;
              
                     }
                }

    	}