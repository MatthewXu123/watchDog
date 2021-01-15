
package watchDog.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.dao.SiteInfoDAO;
import watchDog.util.CSVUtils;
import watchDog.util.DateTool;
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
	private static final String NORMAL = "情况正常";

	private List<TrafficInfo> getDataFromCsv(File file) throws IOException {
		List<TrafficInfo> totalList = new ArrayList<>();
		// File file = new File(UploadController.UPLOAD_DIRECTORY +
		// UploadController.PATH_TRAFFIC);
		// File[] listFiles = file.listFiles();
		List<String[]> traffiCsv = CSVUtils.readCsv(file, '\t');

		List<TrafficInfo> unusedList = new ArrayList<>();
		List<TrafficInfo> removedList = new ArrayList<>();
		List<TrafficInfo> normalList = new ArrayList<>();
		Map<String, List<String>> cardNumberInfoMap = SiteInfoDAO.INSTANCE.getSiteInfoWithRinfo();
		for (String[] trafficRowArray : traffiCsv) {
			String cardNumber = trafficRowArray[2].trim();
			double trafficCount = Double.parseDouble(trafficRowArray[3].trim());

			List<String> infoList = cardNumberInfoMap.get(cardNumber);
			String registerationProject = infoList.get(0);
			String remoteProProject = infoList.get(1);
			TrafficInfo trafficInfo = new TrafficInfo();
			trafficInfo.setCardNumber(cardNumber);
			trafficInfo.setProject(remoteProProject);
			trafficInfo.setIp(infoList.get(2));
			trafficInfo.setManDescription(infoList.get(3));
			trafficInfo.setCusDescription(infoList.get(4));
			trafficInfo.setDeadline(
					StringUtils.isNotBlank(infoList.get(5)) ? DateTool.parse(infoList.get(5)) : null);
			trafficInfo.setTrafficCount(trafficCount);
			if (ObjectUtils.isStringCollectionStrictEmpty(infoList)) {
				// We can't get the project info about the simcard,
				// but the data traffic has created.
				if (trafficCount > 0) {
					trafficInfo.setComment(UNUSED_WITH_TRAFFIC);
					unusedList.add(trafficInfo);
				} else
					normalList.add(trafficInfo);
			} else {
				// If we find the project by the card number in the registration
				// info and we can't find
				// the project in the remotepro.
				if (StringUtils.isNotBlank(registerationProject) && StringUtils.isBlank(remoteProProject)) {
					trafficInfo.setComment(PROJECT_REMOVED);
					removedList.add(trafficInfo);
				} else {
					trafficInfo.setComment(NORMAL);
					normalList.add(trafficInfo);
				}
			}

		}
		Collections.sort(normalList);
		Collections.sort(unusedList);
		Collections.sort(removedList);

		totalList.addAll(unusedList);
		totalList.addAll(removedList);
		totalList.addAll(normalList);

		return totalList;
	}

	public List<List<Object>> getDataTraffic(File file) throws IOException {
		List<List<Object>> dataTrafficList = new ArrayList<>();
		List<TrafficInfo> dataFromCsv = getDataFromCsv(file);
		for (TrafficInfo trafficInfo : dataFromCsv) {
			List<Object> list = new ArrayList<>();
			list.add("\t" + trafficInfo.getCardNumber());
			list.add(trafficInfo.getProject());
			list.add(trafficInfo.getIp());
			list.add(trafficInfo.getManDescription());
			list.add(trafficInfo.getCusDescription());
			list.add(trafficInfo.getDeadline() != null ? DateTool.format(trafficInfo.getDeadline()) : "");
			list.add(trafficInfo.getTrafficCount());
			list.add(trafficInfo.getComment());

			dataTrafficList.add(list);
		}
		return dataTrafficList;
	}

	private class TrafficInfo implements Comparable<TrafficInfo> {
		private String cardNumber;
		// cfsupervisors
		private String project;
		private String ip;
		private String manDescription;
		private String cusDescription;
		private Date deadline;
		private double trafficCount;
		private String comment;
		
		public String getCardNumber() {
			return cardNumber;
		}

		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public String getIp() {
			return ip;
		}


		public void setIp(String ip) {
			this.ip = ip;
		}


		public String getManDescription() {
			return manDescription;
		}


		public void setManDescription(String manDescription) {
			this.manDescription = manDescription;
		}


		public String getCusDescription() {
			return cusDescription;
		}


		public void setCusDescription(String cusDescription) {
			this.cusDescription = cusDescription;
		}


		public Date getDeadline() {
			return deadline;
		}


		public void setDeadline(Date deadline) {
			this.deadline = deadline;
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
			return (int) (o.trafficCount * 10000 - this.getTrafficCount() * 10000);
		}

	}

}
