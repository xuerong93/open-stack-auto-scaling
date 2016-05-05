package star.com.ASG;

import java.util.LinkedList;
import java.util.Queue;

import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;

import star.com.ASG.talktovm.ConnToDC;
import star.com.ASG.talktovm.ConnToLB;
import talktoopenstack.TalkWithCeilometer;
import talktoopenstack.TalkWithNova;

public class AsgRunner {
	private OSClient os;
	private TalkWithCeilometer meter;
	private TalkWithNova nova;
	private ConnToLB lb;
	
	Queue<ConnToDC> dcList;
	
	public AsgRunner(OSClient os){
		Configure conf = Configure.getConf();
		
		this.os = os;
		
		meter = new TalkWithCeilometer(os, conf.getEvalInterval());
		nova = new TalkWithNova(os, conf.getCoolDownInterval());
		
		dcList = new LinkedList<ConnToDC>();
		lb = new ConnToLB(conf.getLBAddr());
	}
	
	public void initAsg(){
		
		//Check min number, init cluster
		Configure conf = Configure.getConf();
		int min = conf.getMaxNum();
		
		//launch min number of instances
		TimeManager.PrintCurrentTime("Init Cluster, launching %d DC\n", min);
		for(int i=0; i < min; i++){
			ConnToDC dc = nova.launchOne(conf.getInatanceName());
			if(dc != null){
				dcList.offer(dc);
				lb.addDC(dc.getIp());
				TimeManager.PrintCurrentTime("Successfully launch 1 DC [%s:%s]\n", 
						dc.getIp(), dc.getId());
			}
		}
		
		return ;
	}

	public void run(){
		
		initAsg();
		
		Configure conf = Configure.getConf();

		while(true){
		
			while(dcList.size() > conf.getMaxNum()){
				ConnToDC dc = dcList.poll();
				if(nova.removeDC(dc.getId())){
					lb.delDC(dc.getIp());
					TimeManager.PrintCurrentTime("DC number EXCEED: Successfully remove 1 DC [%s:%s]\n", 
							dc.getIp(), dc.getId());
				}
			}
			
			if(!meter.isItTime()){
				continue;
			}
			int evalResult = meter.evaluate(conf.getEvalCount(), conf.getCpuUpThr(), conf.getCpuLowThr());
			
			if(evalResult == TalkWithCeilometer.PENDING_DOWN && dcList.size() > conf.getMinNum()){
				
				if(!nova.isItTime()){
					continue;
				}
				
				TimeManager.PrintCurrentTime("Need to remove one more DC\n");
				
				ConnToDC dc = dcList.poll();
				if(nova.removeDC(dc.getId())){
					lb.delDC(dc.getIp());
					TimeManager.PrintCurrentTime("Successfully remove 1 DC [%s:%s]\n", 
							dc.getIp(), dc.getId());
				}

			}else if(evalResult == TalkWithCeilometer.PENDING_UP && dcList.size() < conf.getMaxNum()){
				
				if(!nova.isItTime()){
					continue;
				}
				
				TimeManager.PrintCurrentTime("Need to add one more DC\n");

				ConnToDC dc = nova.launchOne(conf.getInatanceName());
				
				if(dc != null){
					dcList.offer(dc);
					lb.addDC(dc.getIp());
					TimeManager.PrintCurrentTime("Successfully add 1 DC [%s:%s]\n", 
							dc.getIp(), dc.getId());
				}
			}
		}
	}
	public void deleteAll(){
		while(!dcList.isEmpty()){
			ConnToDC dc = dcList.poll();
			if(nova.removeDC(dc.getId())){
				lb.delDC(dc.getIp());
				TimeManager.PrintCurrentTime("RESETTING: Successfully remove 1 DC [%s:%s]\n", 
						dc.getIp(), dc.getId());
			}
		}
	}
}
