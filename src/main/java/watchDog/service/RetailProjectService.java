
package watchDog.service;

import static watchDog.util.ExcelUtils.getWorkBook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 29, 2020
 */
public class RetailProjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetailProjectService.class);
	
	public static final RetailProjectService INSTANCE = new RetailProjectService();
	
	private static String filePath = "C:\\watchDog\\files\\retail\\";
	
	private RetailProjectService(){
		
	}
	
	public List<Map<String, String>> getDataFromExcel(){
		File file = new File(filePath);
		File[] listFiles = file.listFiles();
		Workbook workBook = getWorkBook(listFiles[0]);
		Sheet sheet = workBook.getSheetAt(0);
		int rowNum = sheet.getLastRowNum(); 
		Row firstRow = sheet.getRow(0);
		int columnNum = firstRow.getPhysicalNumberOfCells();
		List<Map<String, String>> list = new ArrayList<>();
		boolean reachLastRow = false;
		for(int rowIndex = 1; rowIndex <= rowNum; rowIndex ++){
			if(reachLastRow)
				break;
			Row row = sheet.getRow(rowIndex);
			Map<String,String> map = new HashMap<>();
			for(int columnIndex = 0; columnIndex <= columnNum; columnIndex++){
				if(columnIndex == 0 && row.getCell(columnIndex) == null){
					reachLastRow = true;
					break;
				}
				map.put("c" + columnIndex, row.getCell(columnIndex) == null ? "" : row.getCell(columnIndex).toString());
			}
			list.add(map);
		}
		return list;
	}
	
}
