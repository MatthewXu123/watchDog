package watchDog.bean;

public class PageVPNInfo {
    private String ip = null;
    private String pid = null;
    private String connectTime = null;
    private String publicIP = null;
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getConnectTime() {
        return connectTime;
    }
    public void setConnectTime(String connectTime) {
        this.connectTime = connectTime;
    }
    public String getPublicIP() {
        return publicIP;
    }
    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }
    
    
}
