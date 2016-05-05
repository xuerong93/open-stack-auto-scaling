package star.com.ASG;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManager {
	private long lastScale;
	private long interval;
	public TimeManager(int interval){
		lastScale = new Date().getTime();
		this.interval = interval*1000;
	}
	public long getInterval(){
		return interval;
	}
	public void update(){
		lastScale = new Date().getTime();
	}
	
	public boolean isItTime(){
		long compare = new Date().getTime() - lastScale;
		return compare >= interval;
	}
	public static void PrintCurrentTime(String format,Object ...args){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		Date date = new Date();
		System.out.printf(dateFormat.format(date) + ". "+format, args); //2014/08/06 15:59:48
	}
}
