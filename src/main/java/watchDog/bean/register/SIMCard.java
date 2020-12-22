
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardNumber == null) ? 0 : cardNumber.hashCode());
		result = prime * result + ((simCardType == null) ? 0 : simCardType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SIMCard other = (SIMCard) obj;
		if (cardNumber == null) {
			if (other.cardNumber != null)
				return false;
		} else if (!cardNumber.equals(other.cardNumber))
			return false;
		if (simCardType != other.simCardType)
			return false;
		return true;
	}
	
}
