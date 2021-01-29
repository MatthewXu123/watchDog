
package watchDog.bean.config;

/**
 * Description:
 * @author Matthew Xu
 * @date Jan 27, 2021
 */
public class SpecialAlarmAdviceDTO {

	private int id;
	
	private String reason;
	
	private String advice;
	
	private String videoUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	@Override
	public String toString() {
		return "SpecialAlarmAdviceDTO [id=" + id + ", reason=" + reason + ", advice=" + advice + ", videoUrl="
				+ videoUrl + "]";
	}
	
}
