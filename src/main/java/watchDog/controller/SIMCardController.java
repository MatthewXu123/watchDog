
package watchDog.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import watchDog.bean.register.SIMCard;
import watchDog.bean.register.SIMCardStatus;
import watchDog.bean.register.SIMCardType;
import watchDog.bean.result.ResultFactory;
import watchDog.dao.SIMCardDAO;
import watchDog.util.HttpServletUtil;
import watchDog.util.ObjectUtils;
import watchDog.wechat.bean.WechatUser;

/**
 * Description:
 * 
 * @author Matthew Xu
 * @date Dec 9, 2020
 */
@WebServlet(urlPatterns = { "/simcard/get", "/simcard/create"})
public class SIMCardController extends HttpServlet implements BaseController{

	private static final long serialVersionUID = -5031685751594766916L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SIMCardController.class);

	private SIMCardDAO simCardDAO = SIMCardDAO.INSTANCE;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Method method = HttpServletUtil.INSTANCE.getMethod(req, resp, this);
		try {
			method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp){
		try {
			List<SIMCard> list = simCardDAO.getAll();
			BaseController.returnResult(resp, JSONObject.toJSONString(list));
		} catch (Exception e) {
			LOGGER.error("", e);
			BaseController.returnFailure(resp);
		}
	}
	
	private void create(HttpServletRequest req, HttpServletResponse resp){
		try {
			String contentType = req.getContentType();
			JSONObject simcardObj = JSONObject.parseObject(req.getParameter("simcard"));
			BigDecimal startCardNumber = new BigDecimal(simcardObj.getString("startCardNumber"));
			int cardNumberCount = simcardObj.getIntValue("cardNumberCount");
			int simcardType = simcardObj.getIntValue("simcardType");
			List<SIMCard> simcardList = new ArrayList<>();
			for(int i = 0; i < cardNumberCount; i++){
				SIMCard simCard = new SIMCard();
				simCard.setCardNumber(startCardNumber.add(new BigDecimal(i)).toString());
				simCard.setSimCardStatus(SIMCardStatus.UNUSED);
				simCard.setSimCardType(SIMCardType.getOneByCode(simcardType));
				simcardList.add(simCard);
			}
			
			List<SIMCard> currentAllSimcards = simCardDAO.getAllByType(SIMCardType.getOneByCode(simcardType));
			List<SIMCard> copiedSimCardList = new ArrayList<>();
			copiedSimCardList.addAll(simcardList);
			// To see if two lists have the same card numbers.
			copiedSimCardList.retainAll(currentAllSimcards);
			// To make sure that the saved card numbers are all unique.
			simcardList.removeAll(currentAllSimcards);
			if(ObjectUtils.isCollectionNotEmpty(simcardList))
				simCardDAO.saveAll(simcardList);
			
			Map<String,List<SIMCard>> map = new HashMap<>();
			map.put("successful", simcardList);
			map.put("failed", copiedSimCardList);
			
			BaseController.returnResult(resp, JSONObject.toJSONString(ResultFactory.getSuccessResult(map)));
		} catch (Exception e) {
			LOGGER.error("", e);
			BaseController.returnFailure(resp);
		}
	}
	
	public static void main(String[] args) {
		BigDecimal bigDecimal = new BigDecimal("89860427102090711021");
		bigDecimal = bigDecimal.add(new BigDecimal(1));
		System.out.println(bigDecimal.toString());
	}
	
}
