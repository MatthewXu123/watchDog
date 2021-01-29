
package watchDog.bean.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * @author Matthew Xu
 * @date Jan 27, 2021
 */
public class SpecialAlarmDTO {

	private String code;
	
	private String hint;
	
	private List<SpecialAlarmAdviceDTO> advices = new ArrayList<>();

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public List<SpecialAlarmAdviceDTO> getAdvices() {
		return advices;
	}

	public void setAdvices(List<SpecialAlarmAdviceDTO> advices) {
		this.advices = advices;
	}

	@Override
	public String toString() {
		return "SpecialAlarmDTO [code=" + code + ", hint=" + hint + ", advices=" + advices + "]";
	}
	
}
