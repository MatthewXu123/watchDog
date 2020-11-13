package watchDog.bean;

import java.util.Date;

import watchDog.database.Record;

/**
 * @author kevin.ge
 *
 */
public class ACKResult {
	String siteName;
	Date startDay;
	int mTot = 0;
	int mACK = 0;
	double mAvg = 0;
	int aTot = 0;
	int aACK = 0;
	double aAvg = 0;
	int eTot = 0;
	int eACK = 0;
	double eAvg = 0;
	int nTot = 0;
	int nACK = 0;
	double nAvg = 0;
	
	public ACKResult() {
		super();
	}

	public ACKResult(Record r)
	{
		siteName = (String)r.get("sitename");
		startDay = (Date)r.get("startday");
		mTot = (int)r.get("m_tot");
		mACK = (int)r.get("m_ACK".toLowerCase());
		mAvg = (double)r.get("m_Avg".toLowerCase());
		aTot = (int)r.get("a_tot");
		aACK = (int)r.get("a_ACK".toLowerCase());
		aAvg = (double)r.get("a_Avg".toLowerCase());
		eTot = (int)r.get("e_tot");
		eACK = (int)r.get("e_ACK".toLowerCase());
		eAvg = (double)r.get("e_Avg".toLowerCase());
		nTot = (int)r.get("n_tot");
		nACK = (int)r.get("n_ACK".toLowerCase());
		nAvg = (double)r.get("n_Avg".toLowerCase());
	}

	public String getSiteName() {
		return siteName;
	}

	public Date getStartDay() {
		return startDay;
	}

	public String getmTot() {
		if(mTot == 0)
			return "";
		return mTot+"";
	}

	public String getmACK() {
		if(mACK == 0)
			return "";
		return mACK+"";
	}

	public String getmAvg() {
		if(mAvg == 0)
			return "";
		int hour = (int)mAvg/60;
		int m = (int)mAvg%60;
		return (hour!=0?(hour+"时"):"")+(m!=0?m:"");
	}

	public String getaTot() {
		if(aTot == 0)
			return "";
		return aTot+"";
	}

	public String getaACK() {
		if(aACK == 0)
			return "";
		return aACK+"";
	}

	public String getaAvg() {
		if(aAvg == 0)
			return "";
		int hour = (int)aAvg/60;
		int m = (int)aAvg%60;
		return (hour!=0?(hour+"时"):"")+(m!=0?m:"");
	}

	public String geteTot() {
		if(eTot == 0)
			return "";
		return eTot+"";
	}

	public String geteACK() {
		if(eACK == 0)
			return "";
		return eACK+"";
	}

	public String geteAvg() {
		if(eAvg == 0)
			return "";
		int hour = (int)eAvg/60;
		int m = (int)eAvg%60;
		return (hour!=0?(hour+"时"):"")+(m!=0?m:"");
	}

	public String getnTot() {
		if(nTot == 0)
			return "";
		return nTot+"";
	}

	public String getnACK() {
		if(nACK == 0)
			return "";
		return nACK+"";
	}

	public String getnAvg() {
		if(nAvg == 0)
			return "";
		int hour = (int)nAvg/60;
		int m = (int)nAvg%60;
		return (hour!=0?(hour+"时"):"")+(m!=0?m:"");
	}

	@Override
	public String toString() {
		return "ACKResult [siteName=" + siteName + ", startDay=" + startDay + ", mTot=" + mTot + ", mACK=" + mACK
				+ ", mAvg=" + mAvg + ", aTot=" + aTot + ", aACK=" + aACK + ", aAvg=" + aAvg + ", eTot=" + eTot
				+ ", eACK=" + eACK + ", eAvg=" + eAvg + ", nTot=" + nTot + ", nACK=" + nACK + ", nAvg=" + nAvg + "]";
	}

	
}
