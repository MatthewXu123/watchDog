
package watchDog.bean.result;

import java.io.Serializable;

/**
 * Description:
 * @author Matthew Xu
 * @date Sep 17, 2020
 */
public class RestResult implements Serializable{

	private static final long serialVersionUID = -8389242246278878830L;

	private int status;
	
	private String msg;
	
	private Object data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
