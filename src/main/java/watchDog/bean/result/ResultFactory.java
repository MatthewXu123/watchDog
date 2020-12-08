package watchDog.bean.result;

/**
* Description: The factory to create the com.rc.entity.result.RestResult.
* @author MatthewXu
* @date Nov 26, 2019
*/
public class ResultFactory extends RestResult {

	private static final long serialVersionUID = -1469899904991156521L;

	private static final String SUCCESS = "success";
	
	private static final String FAILED = "failed";

	/**
	 * 
	 * Description: Get the default successful result.
	 * @return
	 * @author Matthew Xu
	 * @date 11 Jan 2020
	 */
    public static RestResult getSuccessResult() {
    	return getFreeResult(ResultCode.SUCCESS, SUCCESS, null);
    }
    
    /**
     * 
     * Description: Get the successful result with data.
     * @param data
     * @return
     * @author Matthew Xu
     * @date 11 Jan 2020
     */
    public  RestResult getSuccessResult(Object data) {
    	return getFreeResult(ResultCode.SUCCESS, SUCCESS, data);
    }
    
    /**
     * 
     * Description: Get the successful result with data and customized message.
     * @param msg
     * @param data
     * @return
     * @author Matthew Xu
     * @date 11 Jan 2020
     */
    public  RestResult getSuccessResult(String msg,Object data) {
    	return getFreeResult(ResultCode.SUCCESS, msg, data);
    }
   
    /**
     * 
     * Description: Get the default failed result.
     * @return
     * @author Matthew Xu
     * @date 11 Jan 2020
     */
    public  RestResult getFailResult() {
    	return getFreeResult(ResultCode.FAIL, FAILED, null);
    }
    
    /**
     * 
     * Description: Get the failed result with customized message.
     * @param msg
     * @return
     * @author Matthew Xu
     * @date 11 Jan 2020
     */
    public  RestResult getFailResult(String msg) {
    	return getFreeResult(ResultCode.FAIL, msg, null);
    }
    
    /**
     * 
     * Description: Get the failed result with data and customized message.
     * @param msg
     * @param data
     * @return
     * @author Matthew Xu
     * @date 11 Jan 2020
     */
    public static RestResult getFailResult(String msg, Object data) {
    	return getFreeResult(ResultCode.FAIL, msg, data);
    }
    
    /**
     * 
     * Description: Get the customized result with the code, the message and the data.
     * @param resultCode
     * @param msg
     * @param data
     * @return
     * @author Matthew Xu
     * @date 11 Jan 2020
     */
    public static RestResult getFreeResult(ResultCode resultCode, String msg, Object data) {
    	RestResult restResult = new RestResult();
    	restResult.setStatus(resultCode.getStatus());
    	restResult.setMsg(msg);
    	restResult.setData(data);
        return restResult;
    }
}
