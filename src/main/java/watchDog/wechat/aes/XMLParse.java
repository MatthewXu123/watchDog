/**
 * 瀵瑰叕浼楀钩鍙板彂閫佺粰鍏紬璐﹀彿鐨勬秷鎭姞瑙ｅ瘑绀轰緥浠ｇ爜.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package watchDog.wechat.aes;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * XMLParse class
 *
 * 鎻愪緵鎻愬彇娑堟伅鏍煎紡涓殑瀵嗘枃鍙婄敓鎴愬洖澶嶆秷鎭牸寮忕殑鎺ュ彛.
 */
class XMLParse {

	/**
	 * 鎻愬彇鍑簒ml鏁版嵁鍖呬腑鐨勫姞瀵嗘秷鎭�
	 * @param xmltext 寰呮彁鍙栫殑xml瀛楃涓�
	 * @return 鎻愬彇鍑虹殑鍔犲瘑娑堟伅瀛楃涓�
	 * @throws AesException 
	 */
	public static Object[] extract(String xmltext) throws AesException     {
		Object[] result = new Object[3];
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmltext);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Encrypt");
			NodeList nodelist2 = root.getElementsByTagName("ToUserName");
			result[0] = 0;
			result[1] = nodelist1.item(0).getTextContent();
			result[2] = nodelist2.item(0).getTextContent();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ParseXmlError);
		}
	}

	/**
	 * 鐢熸垚xml娑堟伅
	 * @param encrypt 鍔犲瘑鍚庣殑娑堟伅瀵嗘枃
	 * @param signature 瀹夊叏绛惧悕
	 * @param timestamp 鏃堕棿鎴�
	 * @param nonce 闅忔満瀛楃涓�
	 * @return 鐢熸垚鐨剎ml瀛楃涓�
	 */
	public static String generate(String encrypt, String signature, String timestamp, String nonce) {

		String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
				+ "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
				+ "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
		return String.format(format, encrypt, signature, timestamp, nonce);

	}
}
