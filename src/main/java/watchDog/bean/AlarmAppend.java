package watchDog.bean;

import watchDog.database.Record;
import watchDog.service.ShortURLMgr;

public class AlarmAppend extends Alarm {

	int last24hCNT = 0;
	int last7CNT = 0;
	int last30CNT = 0;
	public AlarmAppend(Record r) {
		super(r);
		if(r.get("last24hCNT".toLowerCase()) != null)
			last24hCNT = (int)r.get("last24hCNT".toLowerCase());
		if(r.get("last7CNT".toLowerCase()) != null)
			last7CNT = (int)r.get("last7CNT".toLowerCase());
		if(r.get("last30CNT".toLowerCase()) != null)
			last30CNT = (int)r.get("last30CNT".toLowerCase());
	}
	public int getLast24hCNT() {
		return last24hCNT;
	}
	public int getLast7CNT() {
		return last7CNT;
	}
	public int getLast30CNT() {
		return last30CNT;
	}
	public String getSiteURL()
	{
		return ShortURLMgr.getInstance().getSiteURL(ip, idSite);
	}
	public String getDeviceURL()
	{
		return ShortURLMgr.getInstance().getDeviceURL(ip, idSite,idDevice);
	}
}
