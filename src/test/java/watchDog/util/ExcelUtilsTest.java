
package watchDog.util;

import static watchDog.util.ExcelUtils.getIntFromCell2;
import static watchDog.util.ExcelUtils.getStringFromCell;
import static watchDog.util.ExcelUtils.getWorkBook;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import watchDog.bean.register.RegisterationInfo;
import watchDog.bean.register.SIMCard;
import watchDog.bean.register.SIMCardStatus;
import watchDog.bean.register.SIMCardType;
import watchDog.dao.RegisterationInfoDAO;
import watchDog.dao.SIMCardDAO;

/**
 * Description:
 * @author Matthew Xu
 * @date Dec 1, 2020
 */
public class ExcelUtilsTest {

	private RegisterationInfoDAO registerationInfoDAO = RegisterationInfoDAO.INSTANCE;
	
	private SIMCardDAO simCardDAO = SIMCardDAO.INSTANCE;
	
	private String filePath = "C:\\0AMatthewXu\\Files\\000WorkTemp\\2020\\1130-RPMS\\Register.xlsx";
	
	@Test
	public void saveAllRegisterationInfo() {
		Workbook workBook = getWorkBook(filePath);
		Sheet sheet = workBook.getSheetAt(3);
		int rowNum = sheet.getLastRowNum();
		List<String> unsavedCardNumber = new ArrayList<>();
		List<RegisterationInfo> infoList = new ArrayList<>();
		for(int i = 0; i <= rowNum; i ++){
			RegisterationInfo info = new RegisterationInfo();
			Row row = sheet.getRow(i);
			info.setRegisterationDate(ExcelUtils.getDateFromCell(row.getCell(0)));
			info.setPurchaser(getStringFromCell(row.getCell(1)));
			info.setProject(getStringFromCell(row.getCell(2)));
			info.setServicePeriod(getIntFromCell2(row.getCell(3)));
			info.setProductCode(getStringFromCell(row.getCell(4)));
			info.setProductMac(getStringFromCell(row.getCell(5)));
			info.setOriginalVersion(getStringFromCell(row.getCell(6)));
			info.setIsUpdated(getIsUpdated(getStringFromCell(row.getCell(7))));
			info.setIsConnected(getIsConnected(getStringFromCell(row.getCell(8))));
			info.setVpnAddress(getStringFromCell(row.getCell(9)));
			info.setRouterMac(getStringFromCell(row.getCell(10)));
			// Find the SIM card.
			String cardNumber = getStringFromCell(row.getCell(11));
			SIMCard simCard = simCardDAO.getOneByCardNumber(cardNumber);
			if(simCard.getId() == 0)
				unsavedCardNumber.add(cardNumber);
			info.setSimCard(simCard);
			info.setRouterManufacturer(getStringFromCell(row.getCell(12)));
			info.setComment(getStringFromCell(row.getCell(13)));
			infoList.add(info);
		}
		registerationInfoDAO.saveAll(infoList);
		System.out.println(unsavedCardNumber);
	}
	
	
	@Test
	public void saveAllSIMCard(){
		Workbook workBook = getWorkBook(filePath);
		Sheet sheet = workBook.getSheetAt(2);
		int rowNum = sheet.getLastRowNum();
		List<SIMCard> list = new ArrayList<>();
		for(int i = 0; i <= rowNum; i ++){
			Row row = sheet.getRow(i);
			SIMCard simCard = new SIMCard();
			String cardNumber = getStringFromCell(row.getCell(0));
			if(StringUtils.isBlank(cardNumber))
				continue;
			simCard.setCardNumber(cardNumber);
			simCard.setSimCardType(SIMCardType.CHINA_MOBILE);
			simCard.setSimCardStatus(SIMCardStatus.ENABLED);
			list.add(simCard);
		}
		
		simCardDAO.saveAll(list);
		
	}
	
	 private boolean getIsUpdated(String isUpdatedFlag){
		 return !isUpdatedFlag.equalsIgnoreCase("n") && StringUtils.isNotBlank(isUpdatedFlag);
	 }
	 
	 private boolean getIsConnected(String isConnectedFlag){
		 return !isConnectedFlag.equalsIgnoreCase("n") && StringUtils.isNotBlank(isConnectedFlag);
	 }
	 
}
