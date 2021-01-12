
package watchDog.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.bean.constant.CommonConstants;
import watchDog.controller.UploadController;
import watchDog.dao.SiteInfoDAO;
import watchDog.util.CSVUtils;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Jan 12, 2021
 */
public class DataTrafficService {

	private static final Logger LOGGER = Logger.getLogger(DataTrafficService.class);

	public static final DataTrafficService INSTANCE = new DataTrafficService();

	private static final String UNUSED_WITH_TRAFFIC = "未使用，但已有流量";
	private static final String PROJECT_REMOVED = "项目已停止";
	
	public void getDataFromCsv() {
		try {
			File file = new File(UploadController.UPLOAD_DIRECTORY + UploadController.PATH_TRAFFIC);
			File[] listFiles = file.listFiles();
			Iterator<String[]> traffiCsv = CSVUtils.readCsv(listFiles[0], CommonConstants.CHARSET_UTF8);
			Map<String, SiteInfo> cardNumberSiteMap = SiteInfoDAO.INSTANCE.getSiteInfoWithRinfo();
			List<TrafficInfo> unusedList = new ArrayList<>();
			List<TrafficInfo> removedList = new ArrayList<>();
			List<TrafficInfo> normalList = new ArrayList<>();
			while(traffiCsv.hasNext()){
				String[] trafficRowArray = traffiCsv.next();
				String cardNumber = trafficRowArray[2];
				double trafficCount = Double.parseDouble(trafficRowArray[3]);
				SiteInfo siteInfo = cardNumberSiteMap.get(cardNumber);
				TrafficInfo trafficInfo = new TrafficInfo();
				trafficInfo.setCardNumber(cardNumber);
				if(siteInfo == null){
					if(trafficCount > 0){
						trafficInfo.setComment(UNUSED_WITH_TRAFFIC);
						unusedList.add(trafficInfo);
					}else{
						
					}
						
				}else{
					trafficInfo.setSiteInfo(siteInfo);
					normalList.add(trafficInfo);
				}
					
			}
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	private class TrafficInfo implements Comparable<TrafficInfo>{
		private String cardNumber;
		private SiteInfo siteInfo;
		private double trafficCount;
		private String comment;
		
		public String getCardNumber() {
			return cardNumber;
		}
		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}
		public SiteInfo getSiteInfo() {
			return siteInfo;
		}
		public void setSiteInfo(SiteInfo siteInfo) {
			this.siteInfo = siteInfo;
		}
		public double getTrafficCount() {
			return trafficCount;
		}
		public void setTrafficCount(double trafficCount) {
			this.trafficCount = trafficCount;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		
		@Override
		public int compareTo(TrafficInfo o) {
			return (int) (this.trafficCount - o.getTrafficCount());
		}
		
	}
	
}
