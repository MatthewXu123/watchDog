
package watchDog.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.bean.SiteInfo;
import watchDog.controller.UploadController;
import watchDog.dao.SiteInfoDAO;
import watchDog.util.CSVUtils;
import watchDog.util.ObjectUtils;

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
	
	private List<TrafficInfo> getDataFromCsv() {
		List<TrafficInfo> totalList = new ArrayList<>();
		try {
			File file = new File(UploadController.UPLOAD_DIRECTORY + UploadController.PATH_TRAFFIC);
			File[] listFiles = file.listFiles();
			List<String[]> traffiCsv = CSVUtils.readCsv(listFiles[0],'\t');
			
			List<TrafficInfo> unusedList = new ArrayList<>();
			List<TrafficInfo> removedList = new ArrayList<>();
			List<TrafficInfo> normalList = new ArrayList<>();
			Map<String, List<String>> cardNumberInfoMap = SiteInfoDAO.INSTANCE.getSiteInfoWithRinfo();
			for (String[] trafficRowArray : traffiCsv) {
				String cardNumber = trafficRowArray[2].trim();
				double trafficCount = Double.parseDouble(trafficRowArray[3].trim());
				
				List<String> infoList = cardNumberInfoMap.get(cardNumber);
				TrafficInfo trafficInfo = new TrafficInfo();
				trafficInfo.setCardNumber(cardNumber);
				trafficInfo.setTrafficCount(trafficCount);
				if(ObjectUtils.isStringCollectionStrictEmpty(infoList)){
					// We can't get the project info about the simcard,
					// but the data traffic has created.
					if(trafficCount > 0){
						trafficInfo.setComment(UNUSED_WITH_TRAFFIC);
						unusedList.add(trafficInfo);
					}else
						normalList.add(trafficInfo);
				}else{
					String registerationProject = infoList.get(0);
					String remoteProProject = infoList.get(1);
					// If we find the project by the card number in the registration info and we can't find
					// the project in the remotepro.
					if(StringUtils.isNotBlank(registerationProject) && StringUtils.isBlank(remoteProProject)){
						trafficInfo.setComment(PROJECT_REMOVED);
						removedList.add(trafficInfo);
					}else{
						SiteInfo siteInfo = new SiteInfo();
						siteInfo.setDescription(remoteProProject);
						siteInfo.setManDescription(infoList.get(2));
						siteInfo.setCusDescription(infoList.get(3));
						trafficInfo.setSiteInfo(siteInfo);
						trafficInfo.setDeadline(infoList.get(4));
						normalList.add(trafficInfo);
					}
				}
					
			}
			Collections.sort(normalList);
			totalList.addAll(unusedList);
			totalList.addAll(removedList);
			totalList.addAll(normalList);
		} catch (IOException e) {
			LOGGER.error("",e);
		}

		return totalList;
	}
	
	private List<List<Object>> getDataTraffic(){
		List<List<Object>> dataTrafficList = new ArrayList<>();
		List<TrafficInfo> dataFromCsv = getDataFromCsv();
		for (TrafficInfo trafficInfo : dataFromCsv) {
			List<Object> list = new ArrayList<>();
			list.add(trafficInfo.getCardNumber());
			SiteInfo siteInfo = trafficInfo.getSiteInfo();
			list.add(siteInfo != null ? siteInfo.getDescription() : "");
			list.add(siteInfo != null ? siteInfo.getManDescription() : "");
			list.add(siteInfo != null ? siteInfo.getCusDescription() : "");
			list.add(trafficInfo.getDeadline());
			list.add(trafficInfo.getTrafficCount());
			list.add(trafficInfo.getComment());
			
			dataTrafficList.add(list);
		}
		return dataTrafficList;
	}
	
	private class TrafficInfo implements Comparable<TrafficInfo>{
		private String cardNumber;
		private SiteInfo siteInfo;
		private String deadline;
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
		public String getDeadline() {
			return deadline;
		}
		public void setDeadline(String deadline) {
			this.deadline = deadline;
		}
		@Override
		public int compareTo(TrafficInfo o) {
			return (int) (this.trafficCount - o.getTrafficCount());
		}
		
	}
	
	public static void main(String[] args) {
		DataTrafficService.INSTANCE.getDataTraffic();
	}
	
}
