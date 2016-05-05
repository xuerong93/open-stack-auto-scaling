package star.com.ASG.talktovm;

public class ConnToDC extends HTTPConn{
	String ip;
	String id;
	public ConnToDC(String ip, String id){
		this.ip = ip;
		this.id = id;
	}
	public  boolean checkHealth(){
		return recursiveVisit(ip,80,"", 200);
	}
	public String getIp(){
		return ip;
	}
	public String getId(){
		return id;
	}
}
