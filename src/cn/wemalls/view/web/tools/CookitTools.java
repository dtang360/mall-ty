package cn.wemalls.view.web.tools;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookit地址工具
 */
public class CookitTools {
	public static String Get_View_Type(HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		if(cookies==null) return"";
		for (int i = 0; i < cookies.length; i++) {
			if ("wemalls_view_type".equals(cookies[i].getName())) {
				return cookies[i].getValue();
			}
		}
		return "";
	}

	public static String GetCookitByType(HttpServletRequest request,String key){
		Cookie[] cookies = request.getCookies();
		if(cookies==null) return"";
		for (int i = 0; i < cookies.length; i++) {
			if (key.equals(cookies[i].getName())) {
				return cookies[i].getValue();
			}
		}
		return "";
	}
	public static void UpdateCookitByType(HttpServletRequest request,HttpServletResponse response,String type,String value,int maxAge){
		Cookie cookies[] = request.getCookies();
		if(cookies==null) return ;
		if (cookies != null)  
		{  
			for (int i = 0; i < cookies.length; i++)  
			{   
				if(type.equals(cookies[i].getName())){
					cookies[i].setValue(value);
					cookies[i].setPath("/");
					cookies[i].setMaxAge(maxAge);
					response.addCookie(cookies[i]);
				}  
			}  
		}
	}
}




