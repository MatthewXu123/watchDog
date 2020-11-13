
package watchDog.property.template;

/**
 * Description:
 * @author Matthew Xu
 * @date May 28, 2020
 */
public enum FaxMsgLogTemplate {

	// fax msg
	FM_SEND("fm_send"),
	FM_SIMPLE_CALLING_NEW("fm_simple_calling_new"),
	FM_SIMPLE_CALLING_REPEAT("fm_simple_calling_repeat"),
	// fax log 
	FL_SEND_FAILED("fl_send_failed"),
	FL_SEND_SUCCESS("fl_send_success"),
	FL_NO_MOBILE("fl_no_mobile"),
	FL_COUNT_NOT_ENOUGH("fl_count_not_enough"),
	FL_DELAY_NOT_ENOUGH("fl_delay_not_enough"),
	FL_CALL_TIMES_ENOUGH("fl_call_times_enough");
    
	
	private String key;
	
	private FaxMsgLogTemplate(String key){
		this.key = key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
}
