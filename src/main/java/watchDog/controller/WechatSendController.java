package watchDog.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import watchDog.bean.ResultObj;
import watchDog.service.AlarmManageService;
import watchDog.service.PasswordMgr;
import watchDog.util.AESUtils;
import watchDog.util.EncryContent;
import watchDog.util.HttpServletUtil;
import watchDog.wechat.bean.WechatMsg;
import watchDog.wechat.service.WechatService;
import watchDog.wechat.service.WechatService.WxXmlCpInMemoryConfigStorage;
import watchDog.wechat.util.sender.Sender;

@WebServlet(urlPatterns = { "/wechat/send"})
public class WechatSendController extends HttpServlet{
    private static final Logger logger = Logger.getLogger(WechatSendController.class);
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
        Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
        try {
            method.invoke(this, req, resp);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    private ResultObj send(HttpServletRequest request, HttpServletResponse resp)
    {
        String client = request.getParameter("client");
        String password = PasswordMgr.getInstance().getPassword(client);
        if(StringUtils.isBlank(password))
            return new ResultObj(0,"no client");
        String encrypt = request.getParameter("encrypt");
        String content2 = AESUtils.decrypt(encrypt, password);
        if(!EncryContent.getContent().equals(content2))
            return new ResultObj(0,"wrong encrypt");
        String user = request.getParameter("userid");
        String content = request.getParameter("content");
        Sender wx = Sender.getInstance();
        WxXmlCpInMemoryConfigStorage configStorage = WechatService.getInstance().getStorage();
        WechatMsg.Builder b = new WechatMsg.Builder(content,configStorage.getCallingMsgAgentId(),new String[]{user})
                .type(Sender.WECHAT_MSG_TYPE_USER);
        wx.sendIM(b.build());
        logger.info("WechatSendService  client:"+client+" user:"+user+" content:"+content);
        return null;
    }
}
