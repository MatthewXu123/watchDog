package watchDog.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

public class ValueRetrieve {

	private static final Logger logger = Logger.getLogger(ValueRetrieve.class);
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	public static String sendHttpsPost(String postUrl, String params) throws IOException {
		HttpURLConnection con = null;
		// Create a trust manager that does not validate certificate chains
		trustAllHosts();

		URL url = new URL(postUrl);
		DataOutputStream out = null;
		String str = "";
		BufferedReader reader = null;
		try {
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			if (url.getProtocol().toLowerCase().equals("https")) {
				https.setHostnameVerifier(DO_NOT_VERIFY);
				con = https;
			} else {
				con = (HttpURLConnection) url.openConnection();
			}
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setUseCaches(false);
			con.setInstanceFollowRedirects(true);
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			con.setRequestProperty("Content-Type ", " application/x-www-form-urlencoded ");

			out = new DataOutputStream(con.getOutputStream());
			// out.writeBytes(URLEncoder.encode(params, "UTF-8"));
			out.writeBytes(params);
			out.flush();
			out.close();

			reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line;

			while ((line = reader.readLine()) != null) {
				str += line;
			}
			reader.close();
			con.disconnect();
		} catch (Exception e) {
			logger.error(postUrl + "," + e.getMessage());
		} finally {
			try {
				if (out != null)
					out.close();
				if (reader != null)
					reader.close();
				if (con != null)
					con.disconnect();
			} catch (Exception ex) {
			}
		}

		return str;
	}

	public static String httpGet(String getUrl) {
		StringBuffer tempStr = new StringBuffer();
		String responseContent = "";
		HttpURLConnection conn = null;
		try {
			// Create a trust manager that does not validate certificate chains
			trustAllHosts();

			URL url = new URL(getUrl);
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			if (url.getProtocol().toLowerCase().equals("https")) {
				https.setHostnameVerifier(DO_NOT_VERIFY);
				conn = https;
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.connect();

			InputStream in = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String tempLine;
			while ((tempLine = rd.readLine()) != null) {
				tempStr.append(tempLine);
			}
			responseContent = tempStr.toString();
			rd.close();
			in.close();
		} catch (Exception e) {
			logger.error(getUrl + "," + e.getMessage());
			;
		}
		return responseContent;
	}

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) {

			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
					throws CertificateException {
				// TODO Auto-generated method stub

			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> getValue(String ip, String devCode, String... codes) {
		String devCodes[] = { devCode };
		return getValue(ip, devCodes, codes);
	}

	public static Map<String, String> getValue(String ip, String[] devCodes, String... codes) {
		if (codes == null || codes.length == 0)
			return null;
		Map<String, String> result = new HashMap<String, String>();
		String url = "https://" + ip + "/boss/servlet/MasterXML";
		// String url = "https://localhost/PlantVisorPRO/servlet/MasterXML";
		String params = "input=<requests><login userName=\"xml\" password=\"xml_query_3#\" /><request type=\"getVarsValues\" language=\"EN_en\">";
		for (String devCode : devCodes) {
			for (String code : codes) {
				params += "<element devcode=\"" + devCode + "\" code=\"" + code + "\" />";
				// params += "<element devcode=\"1.001\" code=\""+code+"\" />";
			}
		}
		params += "</request></requests>";
		try {
			String xml = sendHttpsPost(url, params);
			result = getValue(xml, devCodes.length > 1);
			return result;

		} catch (Exception ex) {
			// logger.error("getValue:",ex);
		}
		return null;
	}

	private static Map<String, String> getValue(String xml) {
		return getValue(xml, false);
	}

	private static Map<String, String> getValue(String xml, boolean devices) {
		Map<String, String> result = new HashMap<String, String>();
		String testStr1 = xml;// "<responses><response
								// type=\"parametersList\"><device name=\"MPXPRO
								// v4 (MX*) - 1\" iddevice=\"28\" > <variable
								// name=\"Relay alarm status\" value=\"1.0\"
								// type=\"1\" idvar=\"39284\" islogic=\"FALSE\"
								// readwrite=\"1\" minvalue=\"0\" maxvalue=\"1\"
								// shortdescr=\"\" longdescr=\"\" /> <variable
								// name=\"Relay inverse alarm status\"
								// value=\"0.0\" type=\"1\" idvar=\"39291\"
								// islogic=\"FALSE\" readwrite=\"1\"
								// minvalue=\"0\" maxvalue=\"1\" shortdescr=\"\"
								// longdescr=\"\"
								// /></device></response></responses>";
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(testStr1);
		} catch (DocumentException e) {
			logger.info(e);
			return null;
		}
		// ��ȡXML��Ԫ��
		Element root = doc.getRootElement();
		List<Element> variables = root.selectNodes("//var");
		for (Element v : variables) {
			String devcode = "";
			if (devices)
				devcode = (String) v.attributeValue("devcode") + ";";

			String code = (String) v.attributeValue("code");
			String value = (String) v.attributeValue("value");
			if (value != null && value.endsWith(".0"))
				value = value.replace(".0", "");
			result.put(devcode + code, value);
		}
		return result;
	}

}