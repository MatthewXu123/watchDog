package watchDog.wechat.util.sender;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpMessage;
import me.chanjar.weixin.cp.bean.WxCpMessage.WxArticle;
import me.chanjar.weixin.cp.bean.messagebuilder.NewsBuilder;
import me.chanjar.weixin.cp.bean.messagebuilder.TextBuilder;
import watchDog.listener.Dog;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.WechatUtil;

public class SenderWechat extends Sender {
	private static final Logger logger = Logger.getLogger(SenderWechat.class);
	private WxCpService wxService = WechatService.getInstance().getWxCpService();
	private WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
	private static final Dog DOG = Dog.getInstance();
	
	@Override
	public boolean sendIM(WechatMsg wechatMsg) {
		if(StringUtils.isBlank(configStorage.getDebug())){
			// Here use '&' not '&&'
			logger.info(wechatMsg.toString());
			boolean result = sendIM(WECHAT_MSG_TYPE_DEPT, wechatMsg.getDeptIds(), wechatMsg.getAgentId(), wechatMsg.getContent())
			& sendIM(WECHAT_MSG_TYPE_TAG, wechatMsg.getTagIds(), wechatMsg.getAgentId(), wechatMsg.getContent())
			& sendIM(WECHAT_MSG_TYPE_USER, wechatMsg.getUserIds(), wechatMsg.getAgentId(), wechatMsg.getContent());
			if(!result)
			{
			    logger.info("msg not sent");
			}
		}
		return true;
	}
	
	private boolean sendIM(int type, String[] targetIds, String agentId, String content){
		boolean sendOK = true;
		try {
			if (ObjectUtils.isArrayNotEmpty(targetIds) && StringUtils.isNotBlank(agentId)) {
				TextBuilder b = WxCpMessage.TEXT().agentId(agentId);
				for (String targetId : targetIds) {
				    if(StringUtils.isBlank(targetId))
                        continue;
					if (type == WECHAT_MSG_TYPE_TAG)
						b = b.toTag(targetId);
					else if (type == WECHAT_MSG_TYPE_USER && DOG.getWechatApplicationThread().isUserExist(targetId))
						b = b.toUser(targetId);
					else if (type == WECHAT_MSG_TYPE_DEPT)//&& DOG.getWechatApplicationThread().isDeptNotEmptyOfMembers(targetId)
						b = b.toParty(targetId);
					else
						return false;
					WxCpMessage msg = b.content(content).build();
					wxService.messageSend(msg);
				}
			}
		} catch (WxErrorException ex) {
			sendOK = false;
			logger.error("type:" + type + ",targetIds:" + Arrays.toString(targetIds) + ",agentId:" + agentId + ",content:" + content, ex);
		}
		
		return sendOK;
	}


	@Override
	public boolean sendIMReport(WechatMsg wechatMsg) {
		if(StringUtils.isBlank(configStorage.getDebug())){
			return sendIMReport(WECHAT_MSG_TYPE_DEPT, wechatMsg.getDeptIds(), wechatMsg.getAgentId(), wechatMsg.getTitle(), wechatMsg.getContent())
			&& sendIMReport(WECHAT_MSG_TYPE_TAG, wechatMsg.getTagIds(), wechatMsg.getAgentId(), wechatMsg.getTitle(), wechatMsg.getContent())
			&& sendIMReport(WECHAT_MSG_TYPE_USER, wechatMsg.getUserIds(), wechatMsg.getAgentId(), wechatMsg.getTitle(), wechatMsg.getContent());
		}
		return true;
	}

	private boolean sendIMReport(int type, String[] targetIds, String agentId, String title, String content){
		boolean sendOK = true;
		try {
			WxArticle article = getArticle(title, content);
			NewsBuilder b = WxCpMessage.NEWS().agentId(agentId);
			for (String targetId : targetIds) {
			    if(StringUtils.isBlank(targetId))
                    continue;
				if (type == WECHAT_MSG_TYPE_TAG)
					b = b.toTag(targetId);
				else if (type == WECHAT_MSG_TYPE_USER && WechatUtil.isUserExist(targetId))
					b = b.toUser(targetId);
				else if (type == WECHAT_MSG_TYPE_DEPT)//  && !WechatUtil.isDeptEmptyOfMember(targetId)
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
			logger.info(content);
		return sendOK;
	}
	
	@Override
	public  boolean sendIMToSales(WechatMsg wechatMsg){
		wechatMsg.setAgentId(configStorage.getSalesAgentId());
		wechatMsg.setDeptIds(new String[]{configStorage.getSalesDepartmentId()});
		return sendIM(wechatMsg);
	}
	
	@Override
	public boolean sendIMOfflineMsg(WechatMsg wechatMsg) {
		wechatMsg.setAgentId(configStorage.getOfflineAgentId());
		wechatMsg.setDeptIds(new String[]{configStorage.getAdminDepartmentId()});
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
	public String getURL(String url,Integer agentId) {
		try {
			return getURL_(URLEncoder.encode(url, "utf-8"),agentId);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getURL_("https://" + configStorage.getDomainName() + "/watchDog/servlet/auth");
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

	private String getURL_(String url,Integer agentId)
	{
	    return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + configStorage.getCorpId()
        + "&redirect_uri=" + url + "&response_type=code&scope=snsapi_base&agentid=" +(agentId==null? configStorage.getAgentId():agentId)
        + "#wechat_redirect";
	}
	private String getURL_(String url) {
		return getURL_(url,null);
	}

}
