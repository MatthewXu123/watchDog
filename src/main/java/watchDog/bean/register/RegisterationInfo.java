
package watchDog.bean.register;

import java.util.Date;

/**
 * Description:
 * @author Matthew Xu
 * @date Nov 26, 2020
 */
public class RegisterationInfo {

	private int id;
	
	// VPN IP
	private String vpnAddress;
	
	// 日期
	private Date registerationDate;
	
	// 采购方
	private String purchaser;
	
	// 终端用户
	private String endUser;
	
	//服务年限
	private int servicePeriod;
	
	//型号
	private String productCode;
	
	// BOSS Mac
	private String productMac;
	
	private String routerMac;
	
	// 出厂地址
	private String originalVersion;
	
	// 是否升级
	private boolean isUpdated;
	
	//是否是4G链接
	private boolean is4GConnection;
	
	private SIMCard simCard;

	private Date insertTime;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVpnAddress() {
		return vpnAddress;
	}

	public void setVpnAddress(String vpnAddress) {
		this.vpnAddress = vpnAddress;
	}

	public Date getRegisterationDate() {
		return registerationDate;
	}

	public void setRegisterationDate(Date registerationDate) {
		this.registerationDate = registerationDate;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public String getEndUser() {
		return endUser;
	}

	public void setEndUser(String endUser) {
		this.endUser = endUser;
	}

	public int getServicePeriod() {
		return servicePeriod;
	}

	public void setServicePeriod(int servicePeriod) {
		this.servicePeriod = servicePeriod;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductMac() {
		return productMac;
	}

	public void setProductMac(String productMac) {
		this.productMac = productMac;
	}

	public String getRouterMac() {
		return routerMac;
	}

	public void setRouterMac(String routerMac) {
		this.routerMac = routerMac;
	}

	public String getOriginalVersion() {
		return originalVersion;
	}

	public void setOriginalVersion(String originalVersion) {
		this.originalVersion = originalVersion;
	}

	public SIMCard getSimCard() {
		return simCard;
	}

	public void setSimCard(SIMCard simCard) {
		this.simCard = simCard;
	}

	public boolean getIsUpdated() {
		return isUpdated;
	}

	public void setIsUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	public boolean getIs4GConnection() {
		return is4GConnection;
	}

	public void setIs4GConnection(boolean is4gConnection) {
		is4GConnection = is4gConnection;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	@Override
	public String toString() {
		return "RegisterationInfo [id=" + id + ", vpnAddress=" + vpnAddress + ", registerationDate=" + registerationDate
				+ ", purchaser=" + purchaser + ", endUser=" + endUser + ", servicePeriod=" + servicePeriod
				+ ", productCode=" + productCode + ", productMac=" + productMac + ", routerMac=" + routerMac
				+ ", originalVersion=" + originalVersion + ", isUpdated=" + isUpdated + ", is4GConnection="
				+ is4GConnection + ", simCard=" + simCard + "]";
	}
	
}
