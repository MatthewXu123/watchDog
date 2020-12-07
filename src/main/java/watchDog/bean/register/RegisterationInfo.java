
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
	private String project;
	
	// 服务年限
	private int servicePeriod;
	
	// 型号
	private String productCode;
	
	// BOSS Mac
	private String productMac;
	
	// Router Mac
	private String routerMac;
	
	// 路由器厂商
	private String routerManufacturer;
	
	// 出厂版本
	private String originalVersion;
	
	// 是否升级
	private boolean isUpdated;
	
	//是否是4G链接
	private boolean isConnected;
	
	private SIMCard simCard;
	
	private String comment;
	
	//删除标志位
	private boolean isDeleted;

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

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
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
	
	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public String getRouterManufacturer() {
		return routerManufacturer;
	}

	public void setRouterManufacturer(String routerManufacturer) {
		this.routerManufacturer = routerManufacturer;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	public boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	@Override
	public String toString() {
		return "RegisterationInfo [id=" + id + ", vpnAddress=" + vpnAddress + ", registerationDate=" + registerationDate
				+ ", purchaser=" + purchaser + ", project=" + project + ", servicePeriod=" + servicePeriod
				+ ", productCode=" + productCode + ", productMac=" + productMac + ", routerMac=" + routerMac
				+ ", routerManufacturer=" + routerManufacturer + ", originalVersion=" + originalVersion + ", isUpdated="
				+ isUpdated + ", isConnected=" + isConnected + ", simCard=" + simCard + ", comment=" + comment
				+ ", isDeleted=" + isDeleted + ", insertTime=" + insertTime + "]";
	}
	
}
