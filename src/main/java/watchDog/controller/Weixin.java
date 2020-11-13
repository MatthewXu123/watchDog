package watchDog.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import watchDog.wechat.aes.AesException;
import watchDog.wechat.aes.WXBizMsgCrypt;
import watchDog.wechat.service.CoreService;
import watchDog.wechat.util.sender.Sender;
import watchDog.wechat.util.sender.SenderWechat;

public class Weixin extends HttpServlet{
	private static final Logger logger = Logger.getLogger(Sender.class);
	public Weixin()
	{
		super();
	}
	@Override  
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
        request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");  
        CoreService.processRequest(request,response);
    }
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		// 微信加密签名 

        String sVerifyMsgSig = request.getParameter("msg_signature");

        // 时间戳

        String sVerifyTimeStamp = request.getParameter("timestamp");

        // 随机数

        String sVerifyNonce = request.getParameter("nonce");

        // 随机字符串

        String sVerifyEchoStr = request.getParameter("echostr");

        String sEchoStr; //需要返回的明文

        PrintWriter out = response.getWriter();  

        WXBizMsgCrypt wxcpt;

        try {

        	SenderWechat sender = (SenderWechat)Sender.getInstance(Sender.CHANNEL_WECHAT);
            wxcpt = new WXBizMsgCrypt(sender.RESP_MESSAGE_TOKEN(), sender.RESP_MESSAGE_ENCODINGAESKEY(), sender.getCorpId());

            sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,sVerifyNonce, sVerifyEchoStr);

            // 验证URL成功，将sEchoStr返回
            //logger.info(sEchoStr);
            out.print(sEchoStr);  

        } catch (AesException e1) {

            e1.printStackTrace();
            //logger.info(e1);
        }
	}
}
