/**
 * 瀵瑰叕浼楀钩鍙板彂閫佺粰鍏紬璐﹀彿鐨勬秷鎭姞瑙ｅ瘑绀轰緥浠ｇ爜.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package watchDog.wechat.aes;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 鎻愪緵鍩轰簬PKCS7绠楁硶鐨勫姞瑙ｅ瘑鎺ュ彛.
 */
class PKCS7Encoder {
	static Charset CHARSET = Charset.forName("utf-8");
	static int BLOCK_SIZE = 32;

	/**
	 * 鑾峰緱瀵规槑鏂囪繘琛岃ˉ浣嶅～鍏呯殑瀛楄妭.
	 * 
	 * @param count 闇�杩涜濉厖琛ヤ綅鎿嶄綔鐨勬槑鏂囧瓧鑺備釜鏁�
	 * @return 琛ラ綈鐢ㄧ殑瀛楄妭鏁扮粍
	 */
	static byte[] encode(int count) {
		// 璁＄畻闇�濉厖鐨勪綅鏁�
		int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
		if (amountToPad == 0) {
			amountToPad = BLOCK_SIZE;
		}
		// 鑾峰緱琛ヤ綅鎵�敤鐨勫瓧绗�
		char padChr = chr(amountToPad);
		String tmp = new String();
		for (int index = 0; index < amountToPad; index++) {
			tmp += padChr;
		}
		return tmp.getBytes(CHARSET);
	}

	/**
	 * 鍒犻櫎瑙ｅ瘑鍚庢槑鏂囩殑琛ヤ綅瀛楃
	 * 
	 * @param decrypted 瑙ｅ瘑鍚庣殑鏄庢枃
	 * @return 鍒犻櫎琛ヤ綅瀛楃鍚庣殑鏄庢枃
	 */
	static byte[] decode(byte[] decrypted) {
		int pad = (int) decrypted[decrypted.length - 1];
		if (pad < 1 || pad > 32) {
			pad = 0;
		}
		return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
	}

	/**
	 * 灏嗘暟瀛楄浆鍖栨垚ASCII鐮佸搴旂殑瀛楃锛岀敤浜庡鏄庢枃杩涜琛ョ爜
	 * 
	 * @param a 闇�杞寲鐨勬暟瀛�
	 * @return 杞寲寰楀埌鐨勫瓧绗�
	 */
	static char chr(int a) {
		byte target = (byte) (a & 0xFF);
		return (char) target;
	}

}
