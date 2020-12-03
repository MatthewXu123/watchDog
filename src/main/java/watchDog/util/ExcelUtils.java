
package watchDog.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 1, 2020
 */
public class ExcelUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);
	
	public static Workbook getWorkBook(String filePath){
    	try {
			return WorkbookFactory.create(new FileInputStream(new File(filePath)));
		} catch (EncryptedDocumentException | IOException e) {
			LOGGER.error("",e);
		}
    	return null;
	}
	
	public static String getStringFromCell(Cell cell){
		if(cell != null)
			return cell.toString();
		return null;
	}
	
	public static Integer getIntFromCell(Cell cell){
		if(cell != null)
			return Integer.valueOf(cell.toString());
		return 0;
	}
	
	public static Integer getIntFromCell2(Cell cell){
		if(cell != null && StringUtils.isNotBlank(cell.toString()))
			return Double.valueOf(cell.toString()).intValue();
		return 0;
	}
	
	public static void main(String[] args) throws EncryptedDocumentException, IOException {
		ArrayList<String> baseFiles = new ArrayList<String>(); 
		ArrayList<String> subFiles = new ArrayList<String>(); 
        for (String baseFile : baseFiles) {
        	FileInputStream fis = new FileInputStream(new File(baseFile));
        	Workbook baseWb = WorkbookFactory.create(fis);
        	Sheet baseSheet = baseWb.getSheetAt(0);
			Integer baseRowNum = baseSheet.getLastRowNum();
        	for (String subFile : subFiles) {
        		FileInputStream fis2 = new FileInputStream(new File(subFile));
        		Workbook subWb = WorkbookFactory.create(fis2);
        		Sheet subSheet = subWb.getSheetAt(0);
    			Integer subRowNum = subSheet.getLastRowNum();
    			int j = 1;
    			for(int i = 6; i < subRowNum; i++){
    				Row createRow = baseSheet.createRow(baseRowNum + j++);
    				Row row = subSheet.getRow(i);
    				short firstCellNum = row.getFirstCellNum();
    				short lastCellNum = row.getLastCellNum();
    				for(int k = firstCellNum; k < lastCellNum; k++){
    					createRow.createCell(k);
    					createRow.getCell(k).setCellValue(row.getCell(k).toString());
    				}
    			}
    			FileOutputStream fileOut=new FileOutputStream(baseFile);
    			baseWb.write(fileOut);
    	        fileOut.close();
			}
		}
	}

}
