package watchDog.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @Author: WuChao
 * @ClassName:com.yuuxin.wx.common
 * @Description: 采用单例设计--调用时请使用instance函数调用方法
 * @date 2018/7/6
 */
public class HttpSendUtil {

	private static final Logger logger = Logger.getLogger(HttpSendUtil.class);

	public static final HttpSendUtil INSTANCE = new HttpSendUtil();
	public static final String APPLICATION_URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";
	public static final String APPLICATION_JSON = "application/json;charset=UTF-8";
	public static final String APPLICATION_TEXT_PLAIN = "text/plain;charset=UTF-8";
	public static final String APPLICATION_FORM_DATA = "multipart/form-data;charset=UTF-8";
	public static final String APPLICATION_XML = "application/xml;charset=UTF-8";

	public static final String CHAR_ENCODING_UTF8 = "UTF-8";

	private HttpSendUtil() {
	}

	/**
	 * @Author:WuChao
	 * @Description:使用HttpURLConnection 发送POST
	 * @params: [url, params, encodType]
	 * @Return: java.lang.String
	 * @date:11:23 2018/7/6
	 */
	public String sendPost(String sendUrl, String params, String encodType, String contentType) {
		StringBuffer receive = new StringBuffer();
		HttpURLConnection connection = null;
		BufferedWriter bw = null;
		BufferedReader br = null;

		try {
			URL url = new URL(sendUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(true);
			HttpURLConnection.setFollowRedirects(true);
			connection.setInstanceFollowRedirects(true);

			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));
			connection.setRequestProperty("Accept-Encoding", "gzip");
			connection.connect();
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(params.getBytes(CHAR_ENCODING_UTF8));
            dos.flush();

			if (200 == connection.getResponseCode()) {
				InputStream is = connection.getInputStream();
				if (StringUtils.isNotBlank(connection.getContentEncoding())) {
					String encode = connection.getContentEncoding().toLowerCase();
					if (StringUtils.isNotBlank(encode) && encode.contains("gzip"))
						is = new GZIPInputStream(is);
				}

				br = new BufferedReader(new InputStreamReader(is));
				String line;
				while ((line = br.readLine()) != null) {
					receive.append(line).append("\r\n");
				}

			}
			br.close();

		} catch (java.io.IOException e) {
			logger.error(receive.append(e.getMessage()), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ex) {
					bw = null;
					ex.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
						connection = null;
					}
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
					throw new RuntimeException(e);
				} finally {
					if (connection != null) {
						connection.disconnect();
						connection = null;
					}
				}
			}
		}

		return receive.toString();
	}

	/**
	 * @Author:WuChao
	 * @Description:使用HttpURLConnection 发送GET
	 * @params: [sendUrl, encodType]
	 * @Return: java.lang.String
	 * @date:11:50 2018/7/6
	 */
	public String sendGet(String sendUrl, String encodType) {
		StringBuffer receive = new StringBuffer();
		BufferedReader br = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(sendUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.connect();

			br = new BufferedReader(new InputStreamReader(connection.getInputStream(), encodType));
			String line;
			while ((line = br.readLine()) != null) {
				receive.append(line).append("\r\n");
			}

		} catch (IOException e) {
			logger.error(receive.append(e.getMessage()), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (java.io.IOException ex) {
					br = null;
					ex.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
						connection = null;
					}
				}
			}
		}

		return receive.toString();
	}
}