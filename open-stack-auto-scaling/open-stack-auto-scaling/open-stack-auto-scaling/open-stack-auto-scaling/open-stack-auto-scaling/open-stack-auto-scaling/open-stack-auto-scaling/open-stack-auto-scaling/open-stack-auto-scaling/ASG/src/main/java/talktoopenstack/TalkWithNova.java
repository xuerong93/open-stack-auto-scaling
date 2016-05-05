package talktoopenstack;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;

import star.com.ASG.Configure;
import star.com.ASG.TimeManager;
import star.com.ASG.talktovm.ConnToDC;

public class TalkWithNova {
	
	public String imgId = null;
	public String flavorId = null;
	private TimeManager timer;
	private int interval;
	private OSClient os;
	
	public TalkWithNova(OSClient os, int interval){
		this.os = os;
		this.interval = interval;
		Configure conf = Configure.getConf();
		imgId = getImgId(conf.getImageName());
		flavorId = getFlavorId(conf.getFlavorName());
		timer = new TimeManager(interval);
		
	}
	public ConnToDC launchOne(String name){
		//update timestamp
		timer.update();
		
	

		// Create a Server Model Object
		ServerCreate sc = Builders.server().name(name).flavor(flavorId)
				.image(imgId).build();

		// Boot the Server,wait until active, max 10min wait time
		Server server = os.compute().servers().bootAndWaitActive(sc, 600000);
		String id = server.getId();
		TimeManager.PrintCurrentTime("Try launching[id:%s] \n",server.getId());

		//keep refreshing server object to get private ip
		int try_cnt = 0;
		String ip = null;
		while(try_cnt < 100){
			server = os.compute().servers().get(id);
			String addrInfo = server.getAddresses().toString();
			if(addrInfo != null && addrInfo.length() >= 50){
				int start = addrInfo.lastIndexOf("address=") + "address=".length();
				int end = addrInfo.indexOf(",", start);
				ip = addrInfo.substring(start,end);
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try_cnt ++;
		}
		
		ConnToDC newDC = new ConnToDC(ip, id);
		
		//I will not return until I am sure about this dc's http health state
		if(newDC.checkHealth()){
			
			TimeManager.PrintCurrentTime("[%s:healthy]\n", ip);
			return newDC;
		}else{
			TimeManager.PrintCurrentTime("[%s:httpfail]\n", ip);
			return null;
		}
	}
	public boolean removeDC(String dcid){
		//update timestamp
		timer.update();
		TimeManager.PrintCurrentTime("[deleting:%s]\n", dcid);

		ActionResponse result = os.compute().servers().delete(dcid);
		return result.isSuccess();
	}
	public boolean isItTime(){
		return timer.isItTime();
	}
	private String getImgId(String name){
		for(Image img : os.compute().images().list()){
			if(img.getName().equals(name)){
				return img.getId();
			}
		}
		return null;
	}
	private String getFlavorId(String name){
		for(Flavor fl : os.compute().flavors().list()){
			if(fl.getName().equals(name)){
				return fl.getId();
			}
		}
		return null;
	}
	
}
