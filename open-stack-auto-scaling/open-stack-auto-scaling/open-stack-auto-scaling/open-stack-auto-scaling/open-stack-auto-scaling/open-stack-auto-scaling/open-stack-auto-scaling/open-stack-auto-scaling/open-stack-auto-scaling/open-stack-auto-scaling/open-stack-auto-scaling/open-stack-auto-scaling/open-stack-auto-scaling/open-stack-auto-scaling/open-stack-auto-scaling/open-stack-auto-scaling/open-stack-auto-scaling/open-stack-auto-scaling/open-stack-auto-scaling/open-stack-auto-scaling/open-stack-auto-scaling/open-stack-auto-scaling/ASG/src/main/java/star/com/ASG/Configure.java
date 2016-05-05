package star.com.ASG;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import java.util.Properties;


public class Configure {
	private Properties prop;
	private static Configure conf;
	private Configure(String uri, String asgIp) throws FileNotFoundException, IOException{
		prop = new Properties();
		prop.load(new FileInputStream(uri));
		prop.setProperty("ASG_ADDR", asgIp);
	}
	public static Configure getConf(String...args){
		if(conf == null){
			if(args.length < 2){
				TimeManager.PrintCurrentTime("ERROR: Configure not initialized yet!\n");
				return null;
			}
			try {
				conf = new Configure(args[0],args[1]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return conf;
		}
		return conf;
	}
	public int getCoolDownInterval(){
		String cooldown = prop.getProperty("COOLDOWN");
		return Integer.parseInt(cooldown);
	}
	public String getPassword(){
		return prop.getProperty("PASSWORD").replace("\"", "");
	}
	public String getUsername(){
		return prop.getProperty("USER_NAME").replace("\"", "");
	}
	public String getProjName(){
		return prop.getProperty("PROJECT_NAME").replace("\"", "");
	}
	public double getCpuLowThr(){
		return Double.parseDouble(prop.getProperty("CPU_LOWER_TRES"));
	}
	public double getCpuUpThr(){
		return Double.parseDouble(prop.getProperty("CPU_UPPER_TRES"));
	}
	public String getInatanceName(){
		return prop.getProperty("ASG_NAME").replace("\"", "");
	}
	public String getImageName(){
		return prop.getProperty("ASG_IMAGE").replace("\"", "");
	}
	public String getFlavorName(){
		return prop.getProperty("ASG_FLAVOR").replace("\"", "");
	}
	public int getEvalInterval(){
		return Integer.parseInt(prop.getProperty("EVAL_PERIOD"));
	}
	public int getEvalCount(){
		return Integer.parseInt(prop.getProperty("EVAL_COUNT"));
	}
	public int getDelta(){
		return Integer.parseInt(prop.getProperty("DELTA"));
	}
	public int getMaxNum(){
		return Integer.parseInt(prop.getProperty("MAX_INSTANCE"));
	}
	public void setMaxNum(int val){
		prop.setProperty("MAX_INSTANCE", new Integer(val).toString());
	}
	public int getMinNum(){
		return Integer.parseInt(prop.getProperty("MIN_INSTANCE"));
	}
	public void setMinNum(int val){
		prop.setProperty("MIN_INSTANCE", new Integer(val).toString());
	}
	public String getLBAddr(){
		return prop.getProperty("LB_IPADDR");
	}
	public String getASGAddr(){
		return prop.getProperty("ASG_ADDR");
	}
	
	public void setAsgId(String id){
		prop.setProperty("ASG_ID", id);
	}
	public String getAsgId(){
		return prop.getProperty("ASG_ID");
	}
}
