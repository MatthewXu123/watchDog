
package watchDog.service;

import static watchDog.util.ExcelUtils.getWorkBook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import watchDog.bean.RetailProject;
import watchDog.dao.RetailProjectDAO;
import watchDog.util.ExcelUtils;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 29, 2020
 */
public class RetailProjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetailProjectService.class);
	
	private static final String FILE_FOLDER_PATH  = "C:\\watchDog\\files\\retail\\";
	
	private RetailProjectDAO retailProjectDAO = RetailProjectDAO.INSTANCE;
	
	public static final RetailProjectService INSTANCE = new RetailProjectService();
	
	private RetailProjectService(){
		
	}
	
	/*public List<Map<String, String>> getDataFromExcel(){
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
	}*/
	
	/**
	 * 
	 * Description: Save the data retrieved from excel.
	 * @author Matthew Xu
	 * @date Jan 7, 2021
	 */
	public void saveAllFromExcel(){
		File file = new File(FILE_FOLDER_PATH);
		File[] listFiles = file.listFiles();
		// Get the first file in the folder
		Workbook workBook = getWorkBook(listFiles[0]);
		Sheet sheet = workBook.getSheetAt(0);
		int rowNum = sheet.getLastRowNum(); 
		List<RetailProject> list = new ArrayList<>();
		
		for(int rowIndex = 1; rowIndex <= rowNum; rowIndex ++){
			Row row = sheet.getRow(rowIndex);
			if(row.getCell(0) == null)
				break;
			RetailProject retailProject = new RetailProject();
			retailProject.setCustomer(ExcelUtils.getStringFromCell(row.getCell(0)));
			retailProject.setDescription(ExcelUtils.getStringFromCell(row.getCell(1)));
			retailProject.setIp(ExcelUtils.getStringFromCell(row.getCell(2)));
			retailProject.setProvince(ExcelUtils.getStringFromCell(row.getCell(3)));
			retailProject.setPurchaser(ExcelUtils.getStringFromCell(row.getCell(4)));
			retailProject.setManufacturer(ExcelUtils.getStringFromCell(row.getCell(5)));
			retailProject.setCabnietSupplier(ExcelUtils.getStringFromCell(row.getCell(6)));
			retailProject.setProjectType(ExcelUtils.getStringFromCell(row.getCell(7)));
			retailProject.setContactPerson(ExcelUtils.getStringFromCell(row.getCell(8)));
			retailProject.setSales(ExcelUtils.getStringFromCell(row.getCell(9)));
			retailProject.setContactMobile(ExcelUtils.getStringFromCell(row.getCell(10)));
			retailProject.setDeliveryTime(ExcelUtils.getDateFromCell(row.getCell(11)));
			retailProject.setCommissionPlannedTime(ExcelUtils.getDateFromCell(row.getCell(12)));
			retailProject.setProjectStatus(ExcelUtils.getStringFromCell(row.getCell(13)));
			retailProject.setCstPerson(ExcelUtils.getStringFromCell(row.getCell(14)));
			retailProject.setCommissionStartTime(ExcelUtils.getDateFromCell(row.getCell(15)));
			retailProject.setProjectComment(ExcelUtils.getStringFromCell(row.getCell(16)));
			retailProject.setProjectAddress(ExcelUtils.getStringFromCell(row.getCell(17)));
			retailProject.setWarrantyStartTime(ExcelUtils.getDateFromCell(row.getCell(18)));
			retailProject.setWarrantyPeriod(ExcelUtils.getDateFromCell(row.getCell(19)));
			retailProject.setWarrantyEndTime(ExcelUtils.getDateFromCell(row.getCell(20)));
			
			list.add(retailProject);
		}
		retailProjectDAO.saveAll(list);
	}
	
	
	public static void main(String[] args) {
		RetailProjectService instance2 = RetailProjectService.INSTANCE;
		instance2.saveAllFromExcel();
	}
}
