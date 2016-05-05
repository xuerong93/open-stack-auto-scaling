package star.com.ASG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable{
	
	String ASG_ID ;
	private static  final int PORT = 8888 ;
	private  BufferedReader incomingRequestReader;
	
	public Listener(String id){
		ASG_ID = id;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		  try {
	            serverSocket = new ServerSocket(PORT);
	      } catch (IOException e) {
	            System.err.println("ERROR: Could not listen on port: " + PORT);
	            e.printStackTrace();
	            System.exit(-1);
	        }
		  
		  while(true){
			  try {
				Socket sock = serverSocket.accept();
				this.incomingRequestReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				
				String line = incomingRequestReader.readLine();
				
				Configure conf = Configure.getConf();
				
				if(line.equals(ASG_ID)){
					conf.setMaxNum(0);
					conf.setMinNum(0);
					incomingRequestReader.close();
					sock.close();
					break;
				}
				incomingRequestReader.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			  
		  }
		  
		  try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
