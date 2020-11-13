package watchDog.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

/** 
* Java递归遍历XML所有元素 
* 
* @author 李许民 
* @date 2017年1月16日15:01:14 
*/ 
public class XMLparseUtil 
{ 
	//存储xml元素信息的容器 
	private static ArrayList<Leaf> elemList = new ArrayList<Leaf>(); 
	
	/** 
	* 获取节点所有属性值 
	* <功能详细描述> 
	* @param element 
	* @return 
	* @see [类、类#方法、类#成员] 
	*/
	public static Map<String, String> XMLparse(String xmlStr) {
		XMLparseUtil xmLparseUtil = new XMLparseUtil();
		Map<String, String> mapRerult = new HashMap<String, String>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xmlStr);
		} catch (DocumentException e) {
			e.printStackTrace();
		} 
		// 获取XML根元素 
		Element root = doc.getRootElement();
		xmLparseUtil.getElementList(root); 
		for (Leaf leaf : elemList) {
			mapRerult.put(leaf.getElementName(), leaf.getValue());
		}
		return mapRerult;
	}
	/**
	* 获取节点的属性
	* @param element
	* @return
	*/
	public String getNoteAttribute(Element element) 
	{ 
		String xattribute = ""; 
		DefaultAttribute e = null; 
		List<?> list = element.attributes(); 
		for (int i = 0; i < list.size(); i++) 
		{ 
			e = (DefaultAttribute)list.get(i); 
			xattribute += " [name = " + e.getName() + ", value = " + e.getText() + "]"; 
		} 
		return xattribute; 
	} 
	
	/** 
	* 递归遍历方法 
	* <功能详细描述> 
	* @param element 
	* @see [类、类#方法、类#成员] 
	*/ 
	public void getElementList(Element element) 
	{ 
		List<?> elements = element.elements(); 
		// 没有子元素 
		if (elements.isEmpty()) 
		{ 
			String xpath = element.getPath(); 
			String value = element.getTextTrim(); 
			elemList.add(new Leaf(getNoteAttribute(element),element.getName(), xpath, value)); 
		} 
		else 
		{ 
			// 有子元素 
			Iterator<?> it = elements.iterator(); 
			while (it.hasNext()) 
			{ 
				Element elem = (Element)it.next(); 
				// 递归遍历 
				getElementList(elem); 
			} 
		} 
	} 
	/**
	* 解析测试 
	* @param args
	* @throws DocumentException
	*/
	public static void main(String args[]) 
	throws DocumentException 
	{ 
		String testStr1 = "<responses><response type=\"parametersList\"><device name=\"MPXPRO v4 (MX*) - 1\" iddevice=\"28\"  >    <variable name=\"Relay alarm status\" value=\"1.0\" type=\"1\" idvar=\"39284\" islogic=\"FALSE\" readwrite=\"1\" minvalue=\"0\" maxvalue=\"1\" shortdescr=\"\" longdescr=\"\"  />    <variable name=\"Relay inverse alarm status\" value=\"0.0\" type=\"1\" idvar=\"39291\" islogic=\"FALSE\" readwrite=\"1\" minvalue=\"0\" maxvalue=\"1\" shortdescr=\"\" longdescr=\"\"  /></device></response></responses>";
		String testStr = "<?xml version='1.0' encoding='UTF-8'?>"
		+ "<epay>"
		+ "<retcode>0</retcode>"
		+ "<sign_type>MD5</sign_type>"
		+ "<service>query_order_service</service>"
		+ "<service_version>1.0"
		+ "</service_version>"
		+ "<sign>82c9636fac495ceb0c378a36a6a43914</sign>"
		+ "<input_charset>UTF-8</input_charset>"
		+ "<fee_type>1</fee_type>"
		+ "<attach>test</attach>"
		+ "<bank_transno>123123123123123</bank_transno>"
		+ "<out_trade_no>00000002</out_trade_no>"
		+ "<total_fee>300</total_fee>"
		+ "<product_fee>50</product_fee>"
		+ "<transport_fee>50</transport_fee>"
		+ "<discount>5.51</discount>"
		+ "<reconciliation_state>false</reconciliation_state>"
		+ "<time_end>20131005 17:10:49</time_end>"
		+ "<trade_mode>0001</trade_mode>"
		+ "<trade_state>0</trade_state>"
		+ "<transaction_id>201310045</transaction_id>"
		+ "</epay>";
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(testStr1);
		} catch (DocumentException e) {
			e.printStackTrace();
		} 
		// 获取XML根元素 
		Element root = doc.getRootElement();
		List<Element> variables = root.selectNodes("//variable");
		for(Element v:variables)
		{
			int id = Integer.valueOf(v.attributeValue("idvar"));
			double value = Double.valueOf(v.attributeValue("value"));
		}
	} 
} 
	
	/** 
	* xml节点数据结构 
	*/ 
class Leaf 
{ 
	// 节点属性 
	private String xattribute; 
	//节点名字
	private String elementName;
	
	// 节点PATH 
	private String xpath; 
	
	// 节点值 
	private String value; 
	
	public Leaf(String xattribute, String elementName , String xpath, String value) 
	{ 
		this.xattribute = xattribute;
		this.elementName=elementName;
		this.xpath = xpath; 
		this.value = value; 
	} 
	
	public String getXpath() 
	{ 
		return xpath; 
	} 
	
	public void setXpath(String xpath) 
	{ 
		this.xpath = xpath; 
	} 
	
	public String getValue() 
	{ 
		return value; 
	} 
	
	public void setValue(String value) 
	{ 
		this.value = value; 
	} 
	
	public String getXattribute() 
	{ 
		return xattribute; 
	} 
	
	public void setXattribute(String xattribute) 
	{ 
		this.xattribute = xattribute; 
	} 
		public String getElementName() {
		return elementName;
	}
	
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
}