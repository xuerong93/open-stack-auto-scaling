package io.vertx.example;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManager {
	public static String PrintCurrentTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date); //2014/08/06 15:59:48
	}
}
