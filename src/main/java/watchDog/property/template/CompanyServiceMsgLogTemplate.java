package watchDog.property.template;

public enum CompanyServiceMsgLogTemplate {

		// company service msg
		CSM_RENEW("csm_renew"),
		CSM_INQUIRY("csm_inquiry"),
		CSM_STOP("csm_stop");
		
		private String key;
		
		private CompanyServiceMsgLogTemplate(String key){
			this.key = key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
}
