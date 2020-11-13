package watchDog.bean;

import java.text.NumberFormat;
import java.util.Date;

import watchDog.database.Record;

/**
 * @author kevin.ge
 *
 */
public class SLAResult {
	String siteName;
	Date startDay;
	int htTot = 0;
	int htOutSLA = 0;
	int htReset = 0;
	int vhTot = 0;
	int vhOutSLA = 0;
	int vhReset = 0;
	int hTot = 0;
	int hOutSLA = 0;
	int hReset = 0;
	int mTot = 0;
	int mOutSLA = 0;
	int mReset = 0;
	int lTot = 0;
	int lOutSLA = 0;
	int lReset = 0;
	int amountSLA = 0;
	int amountOutSLA = 0;
	int persentOutSLA = 0;
	int amountMReset = 0;
	
	public SLAResult() {
		super();
	}

	public SLAResult(Record r)
	{
		siteName = (String)r.get("sitename");
		startDay = (Date)r.get("startday");
		htTot = (int)r.get("ht_tot");
		htOutSLA = (int)r.get("ht_outsla");
		htReset = (int)r.get("ht_Reset".toLowerCase());
		vhTot = (int)r.get("vh_Tot".toLowerCase());
		vhOutSLA = (int)r.get("vh_OutSLA".toLowerCase());
		vhReset = (int)r.get("vh_Reset".toLowerCase());
		hTot = (int)r.get("h_Tot".toLowerCase());
		hOutSLA = (int)r.get("h_OutSLA".toLowerCase());
		hReset = (int)r.get("h_Reset".toLowerCase());
		mTot = (int)r.get("m_Tot".toLowerCase());
		mOutSLA = (int)r.get("m_OutSLA".toLowerCase());
		mReset = (int)r.get("m_Reset".toLowerCase());
		lTot = (int)r.get("l_Tot".toLowerCase());
		lOutSLA = (int)r.get("l_OutSLA".toLowerCase());
		lReset = (int)r.get("l_Reset".toLowerCase());
	}

	public String getSiteName() {
		return siteName;
	}

	public Date getStartDay() {
		return startDay;
	}

	public String getHtTot() {
		return htTot+"";
	}

	public String getHtOutSLA() {
		return htOutSLA+"";
	}

	public String getHtReset() {
		return htReset+"";
	}

	public String getVhTot() {
		return vhTot+"";
	}

	public String getVhOutSLA() {
		return vhOutSLA+"";
	}

	public String getVhReset() {
		return vhReset+"";
	}

	public String gethTot() {
		return hTot+"";
	}

	public String gethOutSLA() {
		return hOutSLA+"";
	}

	public String gethReset() {
		return hReset+"";
	}

	public String getmTot() {
		return mTot+"";
	}

	public String getmOutSLA() {
		return mOutSLA+"";
	}

	public String getmReset() {
		return mReset+"";
	}

	public String getlTot() {
		return lTot+"";
	}

	public String getlOutSLA() {
		return lOutSLA+"";
	}

	public String getlReset() {
		return lReset+"";
	}
	
	public String gethtOutPercent()
	{
		if(htTot == 0 || htOutSLA== 0)
			return "0";
		else
		{
			NumberFormat num = NumberFormat.getPercentInstance(); 
			num.setMaximumIntegerDigits(3); 
			num.setMaximumFractionDigits(2); 
			double csdn = (double)htOutSLA/htTot; 
			return num.format(csdn);
		}
	}
	public String getvhOutPercent()
	{
		if(vhTot == 0 || vhOutSLA== 0)
			return "0";
		else
		{
			NumberFormat num = NumberFormat.getPercentInstance(); 
			num.setMaximumIntegerDigits(3); 
			num.setMaximumFractionDigits(2); 
			double csdn = (double)vhOutSLA/vhTot; 
			return num.format(csdn);
		}
	}
	public String gethOutPercent()
	{
		if(hTot == 0 || hOutSLA == 0)
			return "0";
		else
		{
			NumberFormat num = NumberFormat.getPercentInstance(); 
			num.setMaximumIntegerDigits(3); 
			num.setMaximumFractionDigits(2); 
			double csdn = (double)hOutSLA/hTot; 
			return num.format(csdn);
		}
	}
	public String getmOutPercent()
	{
		if(mTot == 0 || mOutSLA == 0)
			return "0";
		else
		{
			NumberFormat num = NumberFormat.getPercentInstance(); 
			num.setMaximumIntegerDigits(3); 
			num.setMaximumFractionDigits(2); 
			double csdn = (double)mOutSLA/mTot; 
			return num.format(csdn);
		}
	}
	public String getlOutPercent()
	{
		if(lTot == 0 || lOutSLA == 0)
			return "0";
		else
		{
			NumberFormat num = NumberFormat.getPercentInstance(); 
			num.setMaximumIntegerDigits(3); 
			num.setMaximumFractionDigits(2); 
			double csdn = (double)lOutSLA/lTot; 
			return num.format(csdn);
		}
	}

	public String getAmountSLA() { 
		int r= htTot+vhTot+hTot+mTot+lTot;
		return r+"";
	}

	public String getAmountOutSLA() {
		int r= htOutSLA+vhOutSLA+hOutSLA+mOutSLA+lOutSLA;
		return r+"";
	}

	public String getPersentOutSLA() {
		int outsla= htOutSLA+vhOutSLA+hOutSLA+mOutSLA+lOutSLA;
		int amount= htTot+vhTot+hTot+mTot+lTot;
		if(outsla == 0 || amount == 0)
			return "0";
		else
		{
			NumberFormat num = NumberFormat.getPercentInstance(); 
			num.setMaximumIntegerDigits(3); 
			num.setMaximumFractionDigits(2); 
			double persentOutSLA = (double)outsla/amount; 
			return num.format(persentOutSLA);
		}
	}

	public String getAmountMReset() {
		int r= htReset+vhReset+hReset+mReset+lReset;
		return  r+"";
	}

	@Override
	public String toString() {
		return "SLAResult [siteName=" + siteName + ", startDay=" + startDay + ", htTot=" + htTot + ", htOutSLA="
				+ htOutSLA + ", htReset=" + htReset + ", vhTot=" + vhTot + ", vhOutSLA=" + vhOutSLA + ", vhReset="
				+ vhReset + ", hTot=" + hTot + ", hOutSLA=" + hOutSLA + ", hReset=" + hReset + ", mTot=" + mTot
				+ ", mOutSLA=" + mOutSLA + ", mReset=" + mReset + ", lTot=" + lTot + ", lOutSLA=" + lOutSLA
				+ ", lReset=" + lReset + ", amountSLA=" + amountSLA + ", amountOutSLA=" + amountOutSLA
				+ ", persentOutSLA=" + persentOutSLA + ", amountMReset=" + amountMReset + "]";
	}
	
	
	
}
