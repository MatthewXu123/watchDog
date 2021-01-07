
package watchDog.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 1, 2020
 */
public class ExcelUtils {
	
	private static final Logger LOGGER = Logger.getLogger(ExcelUtils.class);
	
	public static Workbook getWorkBook(String filePath){
    	try {
			return WorkbookFactory.create(new FileInputStream(new File(filePath)));
		} catch (EncryptedDocumentException | IOException e) {
			LOGGER.error("",e);
		}
    	return null;
	}
	
	public static Workbook getWorkBook(File file){
    	try {
			return WorkbookFactory.create(new FileInputStream(file));
		} catch (EncryptedDocumentException | IOException e) {
			LOGGER.error("",e);
		}
    	return null;
	}
	
	public static String getStringFromCell(Cell cell){
		return cell != null ? cell.toString() : null;
	}
	
	public static Integer getIntFromCell(Cell cell){
		return cell != null ? Integer.valueOf(cell.toString()) : null;
	}
	
	public static Integer getIntFromCell2(Cell cell){
		if(cell != null && StringUtils.isNotBlank(cell.toString()))
			return Double.valueOf(cell.toString()).intValue();
		return 0;
	}
	
	public static Date getDateFromCell(Cell cell){
		String dateStr = getStringFromCell(cell);
		try {
			if(StringUtils.isBlank(dateStr) || dateStr.length() < 8 || dateStr.length() > 11)
				return null;
			 if(dateStr.indexOf("-") != -1){
				 String[] dateSplit = dateStr.split("-");
				 if(dateSplit.length != 3)
					 return null;
				 String month = dateSplit[1];
				 String monthNum = "0";
				 switch (month) {
				 	case "Jan":
				 		monthNum = "1";
				 		break;
				 	case "Feb":
				 		monthNum = "2";
				 		break;
				 	case "Mar":
				 		monthNum = "3";
				 		break;
				 	case "Apr":
				 		monthNum = "4";
				 		break;
				 	case "May":
				 		monthNum = "5";
				 		break;
				 	case "Jun":
				 		monthNum = "6";
				 		break;
				 	case "Jul":
				 		monthNum = "7";
				 		break;
				 	case "Aug":
				 		monthNum = "8";
				 		break;
				 	case "Sep":
				 		monthNum = "9";
				 		break;
				 	case "Oct":
				 		monthNum = "10";
				 		break;
				 	case "Nov":
				 		monthNum = "11";
				 		break;
				 	case "Dec":
				 		monthNum = "12";
				 		break;
				default:
					break;
				}
				 return DateTool.parse(dateSplit[2] + "-" + monthNum + "-" + dateSplit[0]);
			 }
			 
			 if(dateStr.indexOf(".") != -1)
				 if(dateStr.split(".").length == 3)
					 return DateTool.parse(dateStr, "yyyy.MM.dd");
			 
			 if(dateStr.indexOf("//") != -1)
				 if(dateStr.split("//").length != 3)
					 return DateTool.parse(dateStr, "MM/dd/yy");
			 
			 return null;
		} catch (Exception e) {
			LOGGER.error("该日期不符合规范:" + dateStr ,e);
			return null;
		}
	 }
}
