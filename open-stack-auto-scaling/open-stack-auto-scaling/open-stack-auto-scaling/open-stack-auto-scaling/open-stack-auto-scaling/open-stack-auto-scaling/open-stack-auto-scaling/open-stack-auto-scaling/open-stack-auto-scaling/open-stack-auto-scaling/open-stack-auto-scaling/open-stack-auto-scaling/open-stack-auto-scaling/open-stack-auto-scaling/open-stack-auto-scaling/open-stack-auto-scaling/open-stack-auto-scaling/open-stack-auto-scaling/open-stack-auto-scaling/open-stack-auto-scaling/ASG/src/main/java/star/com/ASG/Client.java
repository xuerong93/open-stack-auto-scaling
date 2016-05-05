package star.com.ASG;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;

public class Client {
	
	public static void main(String[] argv){
		
		if(argv.length < 2){
			System.out.println(argv[0]);
			System.out.println("Pl. give the path of configure file and ASGID");
			return;
		}
		String confUri = argv[0];
		String ASGID = argv[1];
		
		Configure conf = Configure.getConf(confUri, "54.174.213.100");
		String ip = conf.getASGAddr();
		String user = conf.getUsername();
		String tenant = conf.getProjName();
		String passwd = conf.getPassword();
		conf.setAsgId(ASGID);
		
		String endPoint = new StringBuilder("http://").append(ip+":5000").append("/v2.0").toString();
		OSClient os = OSFactory.builder()
                .endpoint(endPoint)
                .credentials(user,passwd)
                .tenantName(tenant)
                .authenticate();
		
		AsgRunner asg = new AsgRunner(os);
		

		Thread listener = new Thread(new Listener(ASGID));
		listener.start();
		
		asg.run();
	}
}
