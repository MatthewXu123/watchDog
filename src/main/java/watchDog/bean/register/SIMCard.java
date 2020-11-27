
package watchDog.bean.register;

import java.util.Date;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 26, 2020
 */
public class SIMCard {

	private int id;
	
	private String cardNumber;
	
	private SIMCardType simCardType;
	
	private SIMCardStatus simCardStatus;
	
	private Date insertTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public SIMCardType getSimCardType() {
		return simCardType;
	}

	public void setSimCardType(SIMCardType simCardType) {
		this.simCardType = simCardType;
	}

	public SIMCardStatus getSimCardStatus() {
		return simCardStatus;
	}

	public void setSimCardStatus(SIMCardStatus simCardStatus) {
		this.simCardStatus = simCardStatus;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	@Override
	public String toString() {
		return "SIMCard [id=" + id + ", cardNumber=" + cardNumber + ", simCardType=" + simCardType + ", simCardStatus="
				+ simCardStatus + "]";
	}
	
}
