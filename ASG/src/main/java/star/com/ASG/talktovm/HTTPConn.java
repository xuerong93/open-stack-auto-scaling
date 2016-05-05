package star.com.ASG.talktovm;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class HTTPConn {
	public static final int READ_CONN_TIMEOUT_MILLI = 4000;
	public static final int PORTONLB = 80;

	public boolean URLConnect(String ip,int port, String path)  {
		URL url;
		HttpURLConnection conn = null;
		boolean res = false;
		try {
			url = new URL("http://"+ip+":"+port+path);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
			conn.setConnectTimeout(READ_CONN_TIMEOUT_MILLI);
			conn.setReadTimeout(READ_CONN_TIMEOUT_MILLI);
			if(conn.getResponseCode() == 200){
				res = true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}catch(Exception e){
			//e.printStackTrace();
		}finally{
			if(conn != null){
				conn.disconnect();
			}
		}
		
		return res;
	}
	public boolean recursiveVisit(String ip, int port, String path, int times){
		for(int i=0; i<times; i++){
			if(URLConnect(ip,port,path)){
				return true;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
