package io.vertx.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

// TODO: adjust this to handle health check
public class DataCenterInstance {
	public final String name;
	public final String url;

	public DataCenterInstance(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * Execute the request on the Data Center Instance
	 * @param path
	 * @return URLConnection
	 * @throws IOException
	 */
	public URLConnection executeRequest(String path) throws IOException {
		URLConnection conn = openConnection("http://"+path);
		return conn;
	}

	/**
	 * Open a connection with the Data Center Instance
	 * @param path
	 * @return URLConnection
	 * @throws IOException
	 */
	private URLConnection openConnection(String path) throws IOException {
		URL url = new URL(path);
		URLConnection conn = url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(false);
		conn.setConnectTimeout(5000);
		return conn;
	}
	
	public boolean isHealthy() {
		HttpURLConnection conn = null;
		boolean res = true;
		try {
			conn = (HttpURLConnection)executeRequest(getUrl());
			conn.setReadTimeout(4000);
			conn.connect();
			
			System.out.printf("%s. 1HEALTH[%s:%d]\n",
					TimeManager.PrintCurrentTime(), getUrl(), conn.getResponseCode());
			if(conn.getResponseCode() != 200){
				res = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = false;
		}finally{
			conn.disconnect();
		}
		
		return res;
	}
	
	
}
