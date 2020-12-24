package watchDog.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.chanjar.weixin.common.util.StringUtils;
import watchDog.bean.Property;
import watchDog.database.DataBaseException;
import watchDog.database.DatabaseMgr;
import watchDog.database.Record;
import watchDog.database.RecordSet;

public class PropertyMgr {
	public static final String LAST_QUERY_TIME = "last_query_time";
	public static final String SCRIPT_VERSION = "resource_version";
	public static final String LAST_REPORT_TIME = "last_report_time";
	public static final String IS_FIRST_REPORT = "is_first_report";
	public static final String LAST_FAX_QUERY_TIME = "last_fax_query_time";
	public static final String WECHAT_ACCESS_TOKEN = "wechat_access_token";
	public static final String WECHAT_EXPIRE_TIME = "wechat_expire_time";
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyMgr.class);
	Map<String, Property> map = new HashMap<>();
	private static PropertyMgr me = null;

	public static PropertyMgr getInstance() {
		if (me == null) {
			me = new PropertyMgr();
			me.initMap();
		}
		return me;
	}

	private PropertyMgr() {
	}

	private void initMap() {
		String sql = "SELECT * from PUBLIC.private_property";
		try {
			RecordSet rs = DatabaseMgr.getInstance().executeQuery(sql);
			if (rs != null && rs.size() > 0) {
				for (int i = 0; i < rs.size(); i++) {
					Record r = rs.get(i);
					String key = (String) r.get("key");
					String value = (String) r.get("value");
					Date time = (Date) r.get("time");
					Property p = new Property(key, value, time);
					map.put(key, p);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("", ex);
		}
	}

	public Property getProperty(String key) {
		return map.get(key);
	}

	public void update(String key, String value) {
		if (key == null || StringUtils.isBlank(key))
			return;
		try {
			String sql = null;
			Object[] params = null;
			if (map.containsKey(key)) {
				sql = "update private_property set value =?,time=current_timestamp where key=?";
				params = new Object[] { value, key };
				DatabaseMgr.getInstance().executeUpdate(sql, params);
				map.put(key, new Property(key, value, new Date()));
			} else {
				sql = "insert into private_property(key,value,time) values(?,?,?)";
				params = new Object[] { key, value, new Date() };
				DatabaseMgr.getInstance().executeUpdate(sql, params);
				map.put(key, new Property(key, value, new Date()));
			}
		} catch (DataBaseException e) {
			LOGGER.error("", e);
		}
	}
}
