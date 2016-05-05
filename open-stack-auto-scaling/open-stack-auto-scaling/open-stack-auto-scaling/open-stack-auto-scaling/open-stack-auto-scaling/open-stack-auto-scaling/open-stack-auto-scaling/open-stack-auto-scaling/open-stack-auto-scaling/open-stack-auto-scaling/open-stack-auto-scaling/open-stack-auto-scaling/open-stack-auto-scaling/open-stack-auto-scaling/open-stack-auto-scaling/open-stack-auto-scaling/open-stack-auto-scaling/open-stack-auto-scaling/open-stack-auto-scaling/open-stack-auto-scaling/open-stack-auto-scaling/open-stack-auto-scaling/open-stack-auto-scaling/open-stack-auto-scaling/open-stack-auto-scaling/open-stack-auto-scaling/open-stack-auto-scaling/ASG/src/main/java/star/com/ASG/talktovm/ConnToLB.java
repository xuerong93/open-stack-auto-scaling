package star.com.ASG.talktovm;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public  class ConnToLB extends HTTPConn{
	private String ip;
	public ConnToLB(String ip){
		this.ip = ip;
	}
	public boolean addDC(String dcip){
		return URLConnect(ip,8080,"/add?ip="+dcip);
	}
	public boolean delDC(String dcip){
		return URLConnect(ip,8080,"/remove?ip="+dcip);
	}
}

