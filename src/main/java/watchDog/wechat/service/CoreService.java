package watchDog.wechat.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import watchDog.listener.Dog;
import watchDog.thread.WechatApplicationThread;
import watchDog.wechat.aes.WXBizMsgCrypt;
import watchDog.wechat.bean.msg.TextMessage;
import watchDog.wechat.util.MessageUtil;
import watchDog.wechat.util.sender.Sender;
import watchDog.wechat.util.sender.SenderWechat;
  
public class CoreService {  
	private static final Logger logger = Logger.getLogger(CoreService.class);
    public static void processRequest(HttpServletRequest request, HttpServletResponse response) {  
    	logger.info("new message");
        String sEncryptMsg = null;  
        try {  
            String respContent = "请求处理异常，请稍后尝试！";  
            // 解密postDate  
            // 微信加密签名  
            // 使用输入流获得正文  
            ServletInputStream in = request.getInputStream();  
            BufferedReader reader = new BufferedReader(  
                    new InputStreamReader(in));  
            String sReqData = "";  
            String itemStr = "";// 作为输出字符串的临时串，用于判断是否读取完毕  
            while (null != (itemStr = reader.readLine())) {  
                sReqData += itemStr;  
            }  
            // 对消息进行处理获得明文  
            WXBizMsgCrypt wxcpt;  
            String sMsg = null;  
            String sReqMsgSig = request.getParameter("msg_signature");  
            // 时间戳  
            String sReqTimeStamp = request.getParameter("timestamp");  
            // 随机数  
            String sReqNonce = request.getParameter("nonce");
            SenderWechat sender = (SenderWechat)Sender.getInstance(Sender.CHANNEL_WECHAT);
            String sToken = sender.RESP_MESSAGE_TOKEN();// 回调配置的TOKEN  
            String sCorpID = sender.getCorpId();// 回调提供的sCorpID  
            String sEncodingAESKey = sender.RESP_MESSAGE_ENCODINGAESKEY();// 回调配置的AESKey  
            wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);  
            sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce,  
                    sReqData);  
            // 回复消息  
            Map<String, String> requestMap = MessageUtil.parseXml(sMsg);  
            String fromUserName = requestMap.get("FromUserName");  
            String toUserName = requestMap.get("ToUserName");  
            String msgType = requestMap.get("MsgType");
            String site = requestMap.get("Content");
            logger.info("fromUserName:" + fromUserName + "--toUserName:"  
                    + toUserName + "--msgType" + msgType+"--Content"+site);
            int idsite = Integer.valueOf(site);
            if(msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT))
            {
            	List<String> msgList = new ArrayList<String>();
            	int validResult = Dog.getInstance().canUserAccessSite(fromUserName,idsite);
            	switch(validResult)
            	{
            		case	WechatApplicationThread.SITE_ACCESS_OK:
            			msgList = Dog.getInstance().getAlarmThread().alarmOfSite(idsite);
            			break;
            		case WechatApplicationThread.SITE_ACCESS_NOSITE:
            			msgList.add("站点不存在");
            			break;
            		case WechatApplicationThread.SITE_ACCESS_NOTMEMBER:
            			msgList.add("您无权查询该站点");
            			break;
            	}
            	
                if(msgList != null && msgList.size()>0)
                {
                	for(String msg :msgList)
                	{
			            // 回复文本消息  
			            TextMessage textMessage = new TextMessage();  
			  
			            textMessage.setToUserName(fromUserName);  
			            textMessage.setFromUserName(toUserName);  
			            textMessage.setCreateTime(new Date().getTime());  
			            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);  
			            textMessage.setFuncFlag(0);  
			            textMessage.setContent(msg);  
			            String sRespData = MessageUtil.textMessageToXml(textMessage);  
			            sEncryptMsg = wxcpt.EncryptMsg(sRespData,  
			                    Long.toString(new Date().getTime()), sReqNonce);
			            response.getWriter().print(sEncryptMsg);  
                	}
                }
            }
        } catch (Exception e) {  
        	logger.error("",e);
        }  
    }  
}   