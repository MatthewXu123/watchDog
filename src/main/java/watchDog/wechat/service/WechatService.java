
package watchDog.wechat.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import me.chanjar.weixin.cp.api.WxCpInMemoryConfigStorage;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.WxCpServiceImpl;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Jun 10, 2020
 */
public class WechatService {

	private static final Logger logger = Logger.getLogger(WechatService.class);

	private static final WechatService INSTANCE = new WechatService();

	private static final String WECHAT_CONFIG_PATH = "c:/test-config.xml";

	private WxXmlCpInMemoryConfigStorage storage;

	private WxCpService wxCpService;

	private WechatService() {
	}

	public static WechatService getInstance() {
		try {
			if (INSTANCE.getWxCpService() == null || INSTANCE.getStorage() == null) {
				InputStream is = new BufferedInputStream(new FileInputStream(WECHAT_CONFIG_PATH));
				WxXmlCpInMemoryConfigStorage config = fromXml(WxXmlCpInMemoryConfigStorage.class, is);
				WxCpService wxCpService = new WxCpServiceImpl();
				wxCpService.setWxCpConfigStorage(config);
				
				INSTANCE.setWxCpService(wxCpService);
				INSTANCE.setStorage(config);
			}
		} catch (FileNotFoundException e) {
			logger.error("", e);
		}
		return INSTANCE;
	}

	public WxXmlCpInMemoryConfigStorage getStorage() {
		return storage;
	}

	public void setStorage(WxXmlCpInMemoryConfigStorage storage) {
		this.storage = storage;
	}

	public WxCpService getWxCpService() {
		return wxCpService;
	}

	public void setWxCpService(WxCpService wxCpService) {
		this.wxCpService = wxCpService;
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXml(Class<T> clazz, InputStream is) {
		XStream xstream = XStreamInitializer.getInstance();
		xstream.alias("xml", clazz);
		xstream.processAnnotations(clazz);
		return (T) xstream.fromXML(is);
	}

	@XStreamAlias("xml")
	public static class WxXmlCpInMemoryConfigStorage extends WxCpInMemoryConfigStorage {

		protected String userId;

		protected String adminDepartmentId;

		protected String tagId;

		protected String offlineAgentId;

		protected String salesAgentId;
		
		protected String callingMsgAgentId;

		protected String salesDepartmentId;

		protected String RESP_MESSAGE_TOKEN;

		protected String RESP_MESSAGE_ENCODINGAESKEY;

		protected String domainName;

		protected String debug;

		protected String rootDepartmentId;

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getAdminDepartmentId() {
			return adminDepartmentId;
		}

		public void setAdminDepartmentId(String adminDepartmentId) {
			this.adminDepartmentId = adminDepartmentId;
		}

		public String getTagId() {
			return tagId;
		}

		public void setTagId(String tagId) {
			this.tagId = tagId;
		}

		public String getOfflineAgentId() {
			return offlineAgentId;
		}

		public void setOfflineAgentId(String offlineAgentId) {
			this.offlineAgentId = offlineAgentId;
		}

		public String getSalesAgentId() {
			return salesAgentId;
		}

		public void setSalesAgentId(String salesAgentId) {
			this.salesAgentId = salesAgentId;
		}

		public String getSalesDepartmentId() {
			return salesDepartmentId;
		}

		public void setSalesDepartmentId(String salesDepartmentId) {
			this.salesDepartmentId = salesDepartmentId;
		}

		public String getRESP_MESSAGE_TOKEN() {
			return RESP_MESSAGE_TOKEN;
		}

		public void setRESP_MESSAGE_TOKEN(String rESP_MESSAGE_TOKEN) {
			RESP_MESSAGE_TOKEN = rESP_MESSAGE_TOKEN;
		}

		public String getRESP_MESSAGE_ENCODINGAESKEY() {
			return RESP_MESSAGE_ENCODINGAESKEY;
		}

		public void setRESP_MESSAGE_ENCODINGAESKEY(String rESP_MESSAGE_ENCODINGAESKEY) {
			RESP_MESSAGE_ENCODINGAESKEY = rESP_MESSAGE_ENCODINGAESKEY;
		}

		public String getDomainName() {
			return domainName;
		}

		public void setDomainName(String domainName) {
			this.domainName = domainName;
		}

		public String getDebug() {
			return debug;
		}

		public void setDebug(String debug) {
			this.debug = debug;
		}

		public String getRootDepartmentId() {
			return rootDepartmentId;
		}

		public void setRootDepartmentId(String rootDepartmentId) {
			this.rootDepartmentId = rootDepartmentId;
		}

		public String getCallingMsgAgentId() {
            return callingMsgAgentId;
        }

        public void setCallingMsgAgentId(String callingMsgAgentId) {
            this.callingMsgAgentId = callingMsgAgentId;
        }

        @Override
		public String toString() {
			return super.toString() + " > WxXmlCpConfigStorage{" + "userId='" + userId + '\'' + ", departmentId='"
					+ adminDepartmentId + '\'' + ", tagId='" + tagId + '\'' + '}';
		}
	}
}
