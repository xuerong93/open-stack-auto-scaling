package talktoopenstack;

import java.util.List;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.telemetry.Statistics;

import star.com.ASG.TimeManager;

public class TalkWithCeilometer {
	public static final int PENDING_UP = 1;
	public static final int PENDING_DOWN = -1;
	public static final int PENDING_PENDING = 0;
	
	private int pendingScaleAction;

	private OSClient os;
	private TimeManager timer;
	private int cur_count;
	public TalkWithCeilometer(OSClient os, int interval){
		this.os = os;
		timer = new TimeManager(interval);
		cur_count = 0;
		pendingScaleAction = PENDING_PENDING;
	}
	
	private double getAvgCpu(){

		List<? extends Statistics> stats = os.telemetry().
				meters().statistics("cpu_util", 10);
		double cpuUtil = stats.get(stats.size() - 1).getAvg();
		TimeManager.PrintCurrentTime("Current cpu util %f\n", cpuUtil);
		return cpuUtil;
	}
	public int evaluate(int eval_count, double cpu_up_thr, double cpu_low_thr){
		timer.update();
		double avg = getAvgCpu();
		if(avg > cpu_up_thr){
			if(pendingScaleAction == PENDING_UP){
				cur_count ++;
				if(cur_count >= eval_count){
					cur_count = 0;
					return PENDING_UP;
				}
			}else{
				pendingScaleAction = PENDING_UP;
				cur_count = 1;
			}
		}else if(avg < cpu_low_thr){
			if(pendingScaleAction == PENDING_DOWN){
				cur_count ++;
				if(cur_count >= eval_count){
					cur_count = 0;
					return PENDING_DOWN;
				}
			}else{
				pendingScaleAction = PENDING_DOWN;
				cur_count = 1;
			}
		}else{
			pendingScaleAction = PENDING_PENDING;
			cur_count = 0;
		}
		return PENDING_PENDING;
	}
	public boolean isItTime(){
		return timer.isItTime();
	}
}
