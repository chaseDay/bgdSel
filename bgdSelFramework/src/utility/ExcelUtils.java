package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;

import com.relevantcodes.extentreports.LogStatus;

import config.Constants;
import executionEngine.DriverScript;

    public class ExcelUtils {
                private XSSFSheet ExcelWSheet;
                private XSSFWorkbook ExcelWBook;
                private org.apache.poi.ss.usermodel.Cell Cell;
                private XSSFRow Row;
                public DriverScript thisThread;
                
                
            public void setExcelFile(String Path) throws Exception {
            	try {
                    FileInputStream ExcelFile = new FileInputStream(Path);
                    ExcelWBook = new XSSFWorkbook(ExcelFile);
            	} catch (Exception e){
            		if(thisThread != null){
	            		thisThread.eTest.log(LogStatus.ERROR,"Class Utils | Method setExcelFile | Exception desc : "+e.getMessage());
	            		System.out.println("Class Utils | Method setExcelFile | Exception desc : "+e.getMessage());
	            		thisThread.bResult = false;
            		}
                }
            }
            
			public String getCellData(int RowNum, int ColNum, String SheetName ) throws Exception{
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
         				System.out.println("Class Utils | Method getCellData | Exception desc : "+e.getMessage());
         			 }
                	 	if(thisThread != null){
                	 			thisThread.bResult = false;
                	 	}
                     return"";
                     }
                 }
            
        	
			public int getRowCount(String SheetName){
        		int iNumber=0;
        		try {
        			ExcelWSheet = ExcelWBook.getSheet(SheetName);
        			iNumber=ExcelWSheet.getLastRowNum()+1;
        		} catch (Exception e){
        			if	(e.getMessage() == null){
        				//empty cell
         			}
         			else{
         				System.out.println("Class Utils | Method getRowCount | Exception desc : "+e.getMessage());
         			} 
        			thisThread.bResult = false;
        			}
        		return iNumber;
        		}
			
			
			public int getColContains(String caseName, int rowNum, String SheetName) throws Exception{
				int iColNum=0;
				try {
					int colCount = 50;
					for(;iColNum<colCount; iColNum++){
						if(getCellData(rowNum, iColNum, SheetName).equalsIgnoreCase(caseName)){
							break;
						}
					}
				} catch (Exception e){
					System.out.println("Class Utils | Method getColContains | Exception desc : "+e.getMessage());
					thisThread.bResult = false;
				}
				return iColNum;
			}
			
			
			public int getRowContains(String sTestCaseName, int colNum,String SheetName) throws Exception{
        		int iRowNum=0;	
        		try {
        		    //ExcelWSheet = ExcelWBook.getSheet(SheetName);
        			int rowCount = getRowCount(SheetName);
        			for (; iRowNum<rowCount; iRowNum++){
        				if  (getCellData(iRowNum,colNum,SheetName).equalsIgnoreCase(sTestCaseName)){
        					break;
        				}
        			}       			
        		} catch (Exception e){
        			if	(e.getMessage() == null){
        				//empty cell
         			}
         			else{
         				
         			}
        			System.out.println("Class Utils | Method getRowContains | Exception desc : "+e.getMessage());
        			thisThread.bResult = false;
        			}
        		return iRowNum;
        		}
        	
			
			public int getTestCasesCount(String SheetName, String sTestSuiteID, int colNum) throws Exception{
				try {
					ExcelWSheet = ExcelWBook.getSheet(SheetName);
					for(int i = 1;i<getRowCount(SheetName);i++){
						Cell = ExcelWSheet.getRow(i).getCell(colNum);
						//blank cells are sometimes recognized as cell_type_string so we check for a string length of 0 for a blank cell
						if(Cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK || Cell.getStringCellValue().length() == 0){
							int number = i;
							return number;
						}
					}
	        		int number=ExcelWSheet.getLastRowNum()+1;
	        		return number;
        		} catch (Exception e){
        			System.out.println("Class Utils | Method getRowContains | Exception desc : "+e.getMessage());
        			thisThread.bResult = false;
        			return 0;
				}
			}
			
			
			public int getTestStepsCount(String SheetName, String sTestCaseID, int iTestCaseStart) throws Exception{
        		try {
	        		for(int i=iTestCaseStart;i<=getRowCount(SheetName);i++){
	        			if(!sTestCaseID.equals(getCellData(i, Constants.Col_TestCaseID, SheetName))){
	        				int number = i;
	        				return number;      				
	        				}
	        			}
	        		ExcelWSheet = ExcelWBook.getSheet(SheetName);
	        		int number=ExcelWSheet.getLastRowNum()+1;
	        		return number;
        		} catch (Exception e){
        			System.out.println("Class Utils | Method getRowContains | Exception desc : "+e.getMessage());
        			thisThread.bResult = false;
        			return 0;
                }
        	}
        	
        	public void setCellData(String Result,  int RowNum, int ColNum, String SheetName) throws Exception    {
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
                    	 System.out.println("Class Utils | Method setCellData | Exception desc : "+e.getMessage());
                    	 thisThread.bResult = false;
              
                     }
                }

    	}