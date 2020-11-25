
package watchDog.wechat.bean;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 25, 2020
 */
public class WechatResult {

	private int errcode;
	
	private String errmsg;
	
	private String id;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isOK(){
		return this.errcode == 0;
	}
}
