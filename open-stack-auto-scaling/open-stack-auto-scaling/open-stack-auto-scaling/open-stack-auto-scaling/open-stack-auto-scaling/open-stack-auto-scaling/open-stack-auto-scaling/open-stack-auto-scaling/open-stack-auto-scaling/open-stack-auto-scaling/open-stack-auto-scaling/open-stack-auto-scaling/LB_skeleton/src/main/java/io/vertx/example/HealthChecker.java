package io.vertx.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

public class HealthChecker implements Runnable {
	private int cooldown = 5;
	private Object lock;
	private List<DataCenterInstance> instances;

	public HealthChecker(List<DataCenterInstance> instances) {
		this.instances = instances;
		lock = new Object();
	}

	public void setCoolDown(int val){
		synchronized(lock){
			cooldown = val;
		}
	}
	public int getCoolDown(){
		synchronized(lock){
			return cooldown;
		}
	}
	public void run() {
		while(true){
			try {
				Iterator<DataCenterInstance> it = instances.iterator();
				while(it.hasNext()){
					DataCenterInstance machine = it.next();
					
					if(!machine.isHealthy()){
				        System.out.printf("%s. Found instance [IP: %s] dead. Remove\n", 
				        		TimeManager.PrintCurrentTime(), machine.getUrl());
						it.remove();
					}
				}
				Thread.sleep(getCoolDown() * 1000);
	//			Request request = new Request(socket, instance);
	//			request.execute();
	//			Response response = request.getResponse();
	//			sendToClient(response);
	//			request.close();
			} catch (Exception ex) {
				System.out.println("Exception occured when running RequestHandler: " + ex);
				ex.printStackTrace();
			}
		}
	}

	
}
