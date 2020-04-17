package cn.wemalls.view.web.tools;

import java.io.InputStream;
import java.util.Properties;

import cn.wemalls.uc.api.UCClient;

public class WxAppletTools {
	
	public static String WX_APPLET_AppID = "";
	public static String WX_APPLET_AppSecret = "";
	public static String WX_APPLET_mchid = "";
	public static String WX_APPLET_key = "";
	public static String WX_APPLET_notify_url = "";
	static {
		InputStream in = UCClient.class.getClassLoader().getResourceAsStream(
				"config.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
			WX_APPLET_AppID = properties.getProperty("WX_APPLET_AppID");
			WX_APPLET_AppSecret = properties.getProperty("WX_APPLET_AppSecret");
			WX_APPLET_mchid = properties.getProperty("WX_APPLET_mchid");
			WX_APPLET_key = properties.getProperty("WX_APPLET_key");
			WX_APPLET_notify_url = properties.getProperty("WX_APPLET_notify_url");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
