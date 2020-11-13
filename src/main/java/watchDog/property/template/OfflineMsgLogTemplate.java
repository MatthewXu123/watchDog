package watchDog.property.template;

public enum OfflineMsgLogTemplate {

	// offline msg
	OM_ROUTER("om_router"),
	OM_SITE("om_site");
	
	private String key;
	
	private OfflineMsgLogTemplate(String key){
		this.key = key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
