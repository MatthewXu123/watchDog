package watchDog.wechat.util.sender;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpMessage;
import me.chanjar.weixin.cp.bean.WxCpMessage.WxArticle;
import me.chanjar.weixin.cp.bean.messagebuilder.NewsBuilder;
import me.chanjar.weixin.cp.bean.messagebuilder.TextBuilder;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.WechatUtil;

public class SenderWechat extends Sender {
	private static final Logger logger = Logger.getLogger(SenderWechat.class);
	private WxCpService wxService = WechatService.getInstance().getWxCpService();
	private WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
	
	@Override
	public boolean sendIM(WechatMsg wechatMsg) {
		boolean sendOK = true;
		if(StringUtils.isBlank(configStorage.getDebug())){
			int type = wechatMsg.getType();
			String[] targetIds = wechatMsg.getTargetIds();
			String agentId = wechatMsg.getAgentId();
			try {
				if (ObjectUtils.isArrayNotEmpty(targetIds) && StringUtils.isNotBlank(agentId)) {
					TextBuilder b = WxCpMessage.TEXT().agentId(agentId);
					for (String targetId : targetIds) {
						if (type == WECHAT_MSG_TYPE_TAG)
							b = b.toTag(targetId);
						else if (type == WECHAT_MSG_TYPE_USER && WechatUtil.isUserExist(targetId))
							b = b.toUser(targetId);
						else if (type == WECHAT_MSG_TYPE_DEPT && !WechatUtil.isDeptEmptyOfMember(targetId))
							b = b.toParty(targetId);
						else
							return false;
						WxCpMessage msg = b.content(wechatMsg.getContent()).build();
						wxService.messageSend(msg);
					}
				}else {
					logger.info("该消息未发送，因为未配置targetId或者agentId" + wechatMsg.getContent());
				}
			} catch (WxErrorException ex) {
				sendOK = false;
				logger.error("", ex);
			} catch (Exception ex) {
				sendOK = false;
				logger.error("", ex);
			}
		}
		if (sendOK)
			logger.info(wechatMsg.toString());
		return sendOK;
	}


	@Override
	public boolean sendIMReport(WechatMsg wechatMsg) {
		boolean sendOK = true;
		String agentId = configStorage.getAgentId();
		int type = wechatMsg.getType();
		String[] targetIds = wechatMsg.getTargetIds();
		try {
			WxArticle article = getArticle(wechatMsg.getTitle(), wechatMsg.getContent());
			NewsBuilder b = WxCpMessage.NEWS().agentId(agentId);
			for (String targetId : targetIds) {
				if (type == WECHAT_MSG_TYPE_TAG)
					b = b.toTag(targetId);
				else if (type == WECHAT_MSG_TYPE_USER && WechatUtil.isUserExist(targetId))
					b = b.toUser(targetId);
				else if (type == WECHAT_MSG_TYPE_DEPT  && !WechatUtil.isDeptEmptyOfMember(targetId))
					b = b.toParty(targetId);
				WxCpMessage msg = b.addArticle(article).build();
				wxService.messageSend(msg);
			}
		} catch (WxErrorException ex) {
			logger.error("", ex);
		} catch (Exception ex) {
			sendOK = false;
			logger.error("", ex);
		}
		if (sendOK)
			logger.info(wechatMsg.toString());
		return sendOK;
	}

	@Override
	public  boolean sendIMToSales(WechatMsg wechatMsg){
		wechatMsg.setAgentId(configStorage.getSalesAgentId());
		wechatMsg.setTargetIds(new String[]{configStorage.getSalesDepartmentId()});
		return sendIM(wechatMsg);
	}
	
	@Override
	public boolean sendIMOfflineMsg(WechatMsg wechatMsg) {
		wechatMsg.setAgentId(configStorage.getOfflineAgentId());
		wechatMsg.setTargetIds(new String[]{configStorage.getAdminDepartmentId()});
		return sendIM(wechatMsg);
	}


	public String RESP_MESSAGE_TOKEN() {
		return configStorage.getRESP_MESSAGE_TOKEN();
	}

	public String RESP_MESSAGE_ENCODINGAESKEY() {
		return configStorage.getRESP_MESSAGE_ENCODINGAESKEY();
	}

	public String getCorpId() {
		return configStorage.getCorpId();
	}

	public String getCorpSecret() {
		return configStorage.getCorpSecret();
	}

	@Override
	public String getURL(String url) {
		try {
			return getURL_(URLEncoder.encode(url, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getURL_("https://" + configStorage.getDomainName() + "/watchDog/servlet/auth");
	}

	@Override
	public String getDomainName() {
		return configStorage.getDomainName();
	}

	@Override
	public String isDebug() {
		return configStorage.getDebug();
	}

	public String getRootDepartmentId() {
		return configStorage.getRootDepartmentId();
	}

	private WxArticle getArticle(String title, String msg) {
		WxArticle article = new WxArticle();
		article.setUrl(msg.split(";")[1]);
		article.setPicUrl(msg.split(";")[0]);
		article.setTitle(title);
		return article;
	}

	private String getURL_(String url) {
		return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + configStorage.getCorpId()
				+ "&redirect_uri=" + url + "&response_type=code&scope=snsapi_base&agentid=" + configStorage.getAgentId()
				+ "#wechat_redirect";
	}

}
