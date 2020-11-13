
package watchDog.property.template;

import watchDog.bean.SiteInfo;
import watchDog.service.ShortURLMgr;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date May 12, 2020
 */
public class WechatReportMsgTemplate implements BaseWechatMsgTemplate {
	public static final String WEEK_REPORT_TITLE = "周报表";
	public static final String MONTH_REPORT_TITLE = "月报表";
	
	public static String getWeeklyReportContent(SiteInfo siteInfo) {
		String reportMsg = ShortURLMgr.getInstance().getReportImg(1);
		String reportURL = ShortURLMgr.getInstance().getReportMgr("w", siteInfo.getSupervisorId());
		return reportMsg + ";" + reportURL;
	}
	
	public static String getWeeklyReportTitle(SiteInfo siteInfo) {
		return WEEK_REPORT_TITLE + "-" + siteInfo.getDescription();
	}
	
	public static String getMonthlyReportContent(SiteInfo siteInfo) {
		String reportMsg = ShortURLMgr.getInstance().getReportImg(2);
		String reportURL = ShortURLMgr.getInstance().getReportMgr("m", siteInfo.getSupervisorId());
		return reportMsg + ";" + reportURL;
	}
	
	public static String getMonthlyReportTitle(SiteInfo siteInfo) {
		return MONTH_REPORT_TITLE + "-" + siteInfo.getDescription();
	}

	public static String getWeeklyReportHQContent() {
		String reportMsg = ShortURLMgr.getInstance().getReportImg(1);
		String reportURL = ShortURLMgr.getInstance().getReportHQ("w");
		return reportMsg + ";" + reportURL;
	}
	
	public static String getMonthlyReportHQContent() {
		String reportMsg = ShortURLMgr.getInstance().getReportImg(2);
		String reportURL = ShortURLMgr.getInstance().getReportHQ("m");
		return reportMsg + ";" + reportURL;
	}
}
