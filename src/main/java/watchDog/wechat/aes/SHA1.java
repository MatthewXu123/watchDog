/**
 * 瀵瑰叕浼楀钩鍙板彂閫佺粰鍏紬璐﹀彿鐨勬秷鎭姞瑙ｅ瘑绀轰緥浠ｇ爜.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package watchDog.wechat.aes;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * SHA1 class
 *
 * 璁＄畻鍏紬骞冲彴鐨勬秷鎭鍚嶆帴鍙�
 */
class SHA1 {

	/**
	 * 鐢⊿HA1绠楁硶鐢熸垚瀹夊叏绛惧悕
	 * @param token 绁ㄦ嵁
	 * @param timestamp 鏃堕棿鎴�
	 * @param nonce 闅忔満瀛楃涓�
	 * @param encrypt 瀵嗘枃
	 * @return 瀹夊叏绛惧悕
	 * @throws AesException 
	 */
	public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException
			  {
		try {
			String[] array = new String[] { token, timestamp, nonce, encrypt };
			StringBuffer sb = new StringBuffer();
			// 瀛楃涓叉帓搴�
			Arrays.sort(array);
			for (int i = 0; i < 4; i++) {
				sb.append(array[i]);
			}
			String str = sb.toString();
			// SHA1绛惧悕鐢熸垚
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			byte[] digest = md.digest();

			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}
}
