package cn.wemalls.view.web.action;

import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.security.support.SecurityUserHolder;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.Md5Encrypt;
import cn.wemalls.core.tools.WxAdvancedUtil;
import cn.wemalls.core.tools.WxCommonUtil;
import cn.wemalls.core.tools.bean.WxOauth2Token;
import cn.wemalls.core.tools.bean.WxUserInfo;
import cn.wemalls.foundation.domain.Album;
import cn.wemalls.foundation.domain.IntegralLog;
import cn.wemalls.foundation.domain.OrderForm;
import cn.wemalls.foundation.domain.Payment;
import cn.wemalls.foundation.domain.User;
import cn.wemalls.foundation.service.*;
import cn.wemalls.uc.api.UCClient;
import cn.wemalls.uc.api.UCTools;
import cn.wemalls.view.web.tools.CookitTools;
import cn.wemalls.view.web.tools.ImageViewTools;
import cn.wemalls.view.web.tools.WxAppletTools;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 买家登录控制器
 */
@Controller
public class LoginViewAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IIntegralLogService integralLogService;

    @Autowired
    private IAlbumService albumService;

    @Autowired
    private ImageViewTools imageViewTools;

    @Autowired
    private UCTools ucTools;
    
    @Autowired
    private IPaymentService paymentService;
    
    private static Logger logger = LoggerFactory.getLogger(LoginViewAction.class);
    


    /**
     * 用户登录跳转页面
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping({"/user/login.htm"})
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response, String url){
        ModelAndView mv = new JModelAndView("login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("/wap/login.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("/android/login.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("/ios/login.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }

        request.getSession(false).removeAttribute("verify_code");
        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
        if ((url != null) && (!url.equals(""))){
            request.getSession(false).setAttribute("refererUrl", url);
        }
        if (domain_error){
            mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            }
        }else{
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));
        
        //cookit登录
//        String auth_success = CookitTools.GetCookitByType(request,"auth_success");
//        String loginUserName = CookitTools.GetCookitByType(request,"login_user_name");
//        String loginPassword = CookitTools.GetCookitByType(request,"login_password");
//        if(auth_success!=null&&auth_success.length()>0&&loginUserName!=null&&loginUserName.length()>0){
//        	if(loginPassword!=null&&loginPassword.length()>0){
//        		mv.addObject("username",loginUserName);
//        		mv.addObject("password",loginPassword);
//        		mv.addObject("verify_code_flg","false");
//        		mv.addObject("login_type","wap_login");
//        		mv.setViewName("redirect:/wemalls_login.htm");
//        	}
//        	return mv;
//        }
        return mv;
    }
    
    
    /**
     * 用户登录跳转页面
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping({"/wxapplet/getsessionkey.htm"})
    public void wxGetSessionKey(HttpServletRequest request, HttpServletResponse response, String url,String code){
    	try {
    		String uri="https://api.weixin.qq.com/sns/jscode2session?appid="+WxAppletTools.WX_APPLET_AppID+"&secret="+WxAppletTools.WX_APPLET_AppSecret+"&js_code="+code+"&grant_type=authorization_code";
//    	String uri="https://api.weixin.qq.com/sns/jscode2session";
//    	JSONObject jsonObject = new JSONObject();
//		jsonObject.put("appid", WxAppletTools.WX_APPLET_AppID);
//		jsonObject.put("secret", WxAppletTools.WX_APPLET_AppSecret);
//		jsonObject.put("js_code",code);
//		jsonObject.put("grant_type","authorization_code");
    	String result=LoginViewAction.get(uri);    	
			PrintWriter writer = response.getWriter();
			writer.print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
//        WxCommonUtil.printObjJson(resMap, response);
    }
    
    
    public static String get(String url){
		String result = "";
		HttpGet get = new HttpGet(url);
		try{
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			HttpResponse response = httpClient.execute(get);
			result = getHttpEntityContent(response);
			
			if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				result = "服务器异常";
			}
		} catch (Exception e){
			System.out.println("请求异常");
			throw new RuntimeException(e);
		} finally{
			get.abort();
		}
		return result;
	}
    
    public static String getHttpEntityContent(HttpResponse response) throws UnsupportedOperationException, IOException{
		String result = "";
		HttpEntity entity = response.getEntity();
		if(entity != null){
			InputStream in = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			StringBuilder strber= new StringBuilder();
			String line = null;
			while((line = br.readLine())!=null){
				strber.append(line+'\n');
			}
			br.close();
			in.close();
			result = strber.toString();
		}
		
		return result;
		
	}
    
    public static String post(JSONObject json, String url){
		String result = "";
		HttpPost post = new HttpPost(url);
		try{
			CloseableHttpClient httpClient = HttpClients.createDefault();
        
			post.setHeader("Content-Type","application/json;charset=utf-8");
			post.addHeader("Authorization", "Basic YWRtaW46");
			StringEntity postingString = new StringEntity(json==null?"":json.toString(),"utf-8");
			post.setEntity(postingString);
			HttpResponse response = httpClient.execute(post);
			
			InputStream in = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			StringBuilder strber= new StringBuilder();
			String line = null;
			while((line = br.readLine())!=null){
				strber.append(line+'\n');
			}
			br.close();
			in.close();
			result = strber.toString();
			if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				result = "服务器异常";
			}
		} catch (Exception e){
			System.out.println("请求异常");
			throw new RuntimeException(e);
		} finally{
			post.abort();
		}
		return result;
	}
    
    
    /**
     * 用户登录跳转页面
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping({"/wx/login.htm"})
    public ModelAndView wxLogin(HttpServletRequest request, HttpServletResponse response, String url){
        ModelAndView mv = new JModelAndView("login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("/wap/login.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("/android/login.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("/ios/login.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }

        request.getSession(false).removeAttribute("verify_code");
        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
        if ((url != null) && (!url.equals(""))){
            request.getSession(false).setAttribute("refererUrl", url);
        }
        if (domain_error){
            mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            }
        }else{
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));
        
        //cookit登录
//        String auth_success = CookitTools.GetCookitByType(request,"auth_success");
//        String loginUserName = CookitTools.GetCookitByType(request,"login_user_name");
//        String loginPassword = CookitTools.GetCookitByType(request,"login_password");
//        if(auth_success!=null&&auth_success.length()>0&&loginUserName!=null&&loginUserName.length()>0){
//        	if(loginPassword!=null&&loginPassword.length()>0){
//        		mv.addObject("username",loginUserName);
//        		mv.addObject("password",loginPassword);
//        		mv.addObject("verify_code_flg","false");
//        		mv.addObject("login_type","wap_login");
//        		mv.setViewName("redirect:/wemalls_login.htm");
//        	}
//        	return mv;
//        }
        return mv;
    }
    
    /**
     * 微信登录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/wxlogin.htm"})
    public String wxLogin(HttpServletRequest request, HttpServletResponse response){
    	 Payment wxpayment=new Payment();
    	 Map params = new HashMap();
         params.put("mark", "weixin_wap");
//         params.put("store_id", of.getStore().getId());
         List payments = this.paymentService.query(
                             "select obj from Payment obj where obj.mark=:mark ", params, -1, -1);
         if (payments.size() > 0){
        	 wxpayment=(Payment) payments.get(0);
         }
         
        String APPID = wxpayment.getWeixin_appId();
        String siteURL = CommUtil.getURL(request);

        return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="
               + APPID
               + "&redirect_uri="
               + urlEncodeUTF8(siteURL+ "/wx_login_success.htm")
               + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    }
    
    /**
     * 微信CODE回调JSP并进行微信授权接口认证获取用户openid
     */
    @RequestMapping({"/wxapplet/login.htm"})
    public ModelAndView wxLogin(HttpServletRequest request, HttpServletResponse response,ModelAndView mv,String wxAppletOpenid,String wxAppletGender,String wxAppletNickName,String wxAppletAvatarUrl,String wxAppletSessionId){
        // 小程序用户同意授权，openId存在
        if (null != wxAppletOpenid && !"".equals(wxAppletOpenid)){
            Map params = new HashMap();
            User user = this.userService.getObjByProperty("wxAppletOpenid",wxAppletOpenid);
            // 用户不存在，添加新用户，用户名、密码
            if(null == user||null==user.getUserName()){
                user = new User();
                    user.setUserRole("BUYER");
                    user.setAddTime(new Date());
                    user.setEmail("");
                    user.setUserName(wxAppletOpenid);
                    user.setWxAppletOpenid(wxAppletOpenid);
                    user.setSex(CommUtil.null2Int(wxAppletGender));
                    user.setWxAppletNickname(wxAppletNickName);
                    user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
                    params.clear();
                    params.put("type", "BUYER");
                    List roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
                    user.getRoles().addAll(roles);
                    // 如果系统开启积分功能，则给会员新增积分
                    if (this.configService.getSysConfig().isIntegral()){
                        user.setIntegral(this.configService.getSysConfig().getMemberRegister());
                        this.userService.save(user);
                        IntegralLog log = new IntegralLog();
                        log.setAddTime(new Date());
                        log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister() + "分");
                        log.setIntegral(this.configService.getSysConfig().getMemberRegister());
                        log.setIntegral_user(user);
                        log.setType("reg");
                        this.integralLogService.save(log);
                    }else{
                        this.userService.save(user);
                    }
                    // 设置相册
                    Album album = new Album();
                    album.setAddTime(new Date());
                    album.setAlbum_default(true);
                    album.setAlbum_name("默认相册");
                    album.setAlbum_sequence(-10000);
                    album.setUser(user);
                    this.albumService.save(album);
                    request.getSession(false).removeAttribute("verify_code");
//                	}
            	mv.addObject("username",user.getUsername());
            	mv.addObject("password", "123456");
            	mv.addObject("verify_code_flg","false");
            	mv.addObject("ajax_login",true);
            	mv.addObject("login_type","wx_applet_login");
            	mv.addObject("login_auth","wx_applet_auth_login");
            	mv.setViewName("redirect:/wemalls_login.htm");
            	return mv;
                }
            
            //用户已存在，跳转登录
            //拿到openId，说明用户已授权，通过微信登录，则通过openId可查询用户信息，获取用户信息，如用户名，密码，提交登录页面，完成登录
            mv.addObject("username",user.getUsername());
        	mv.addObject("password","123456");
        	mv.addObject("verify_code_flg","false");
        	mv.addObject("ajax_login",true);
        	mv.addObject("login_type","wx_applet_login");
        	mv.addObject("login_auth","wx_applet_auth_login");
        	mv.setViewName("redirect:/wemalls_login.htm");
        	return mv;
         //openId 无效,授权失败，跳index页面，无法登录
        }else{
        	mv.setViewName("redirect:/login.htm");
        	return mv;
        }
    }
    
    /**
     * 微信CODE回调JSP并进行微信授权接口认证获取用户openid
     */
    @RequestMapping({"/wx_login_success.htm"})
    public ModelAndView wx_login_success(HttpServletRequest request, HttpServletResponse response,ModelAndView mv){
//        ModelAndView mv = new JModelAndView("wap/wxpay.html", this.configService.getSysConfig(),
//                                            this.userConfigService.getUserConfig(), 1, request, response);
        // 用户同意授权后，能获取到code
        String code = request.getParameter("code");
        HttpSession session = request.getSession();
        String scode = (String)session.getAttribute("wxcode");
        Payment wxpayment=new Payment();
        wxpayment=paymentService.getObjByProperty("mark", "weixin_wap");

        if(code != null && code.equalsIgnoreCase(scode)){
        }else{
            session.setAttribute("wxcode", code);
        }
        String openId = null;
        // 用户同意授权
        if (null != code && !"".equals(code) && !"authdeny".equals(code)){
            // 获取网页授权access_token
            WxOauth2Token wxOauth2Token = WxAdvancedUtil.getOauth2AccessToken(wxpayment.getWeixin_appId(),wxpayment.getWeixin_appSecret(), code);
            wxOauth2Token=WxAdvancedUtil.refreshOauth2AccessToken(wxpayment.getWeixin_appId(), wxOauth2Token.getRefreshToken());
            // 用户标识
            if(null != wxOauth2Token){
                openId = wxOauth2Token.getOpenId();
                WxUserInfo wxUser= WxAdvancedUtil.getUserInfo(wxOauth2Token.getAccessToken(), openId);
                //保存微信用户信息
                if(wxUser!=null&&wxUser.getOpenId()!=null){
                // 用户名不能重复
                Map params = new HashMap();
                params.put("wxOpenid", wxUser.getOpenId());
                List users = this.userService.query("select obj from User obj where obj.wxOpenid=:wxOpenid", params, -1, -1);
                User user = new User();
                boolean reg = true;
                if (users == null||users.size() == 0){
                    user.setUserName(wxUser.getOpenId().substring(0, 8));
                    user.setUserRole("BUYER");
                    user.setAddTime(new Date());
                    user.setEmail("");
                    user.setWxOpenid(wxUser.getOpenId());
                    user.setSex("男".equals(user.getSex())?1:0);
                    user.setWxNickname(wxUser.getNickname());
                    user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
                    params.clear();
                    params.put("type", "BUYER");
                    List roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
                    user.getRoles().addAll(roles);

                    // 如果系统开启积分功能，则给会员新增积分
                    if (this.configService.getSysConfig().isIntegral()){
                        user.setIntegral(this.configService.getSysConfig().getMemberRegister());
                        this.userService.save(user);
                        IntegralLog log = new IntegralLog();
                        log.setAddTime(new Date());
                        log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister() + "分");
                        log.setIntegral(this.configService.getSysConfig().getMemberRegister());
                        log.setIntegral_user(user);
                        log.setType("reg");
                        this.integralLogService.save(log);
                    }else{
                        this.userService.save(user);
                    }

                    // 设置相册
                    Album album = new Album();
                    album.setAddTime(new Date());
                    album.setAlbum_default(true);
                    album.setAlbum_name("默认相册");
                    album.setAlbum_sequence(-10000);
                    album.setUser(user);
                    this.albumService.save(album);
                    request.getSession(false).removeAttribute("verify_code");
                	}
            	mv.addObject("username",wxUser.getOpenId().substring(0, 8));
            	mv.addObject("password", "123456");
            	mv.addObject("verify_code_flg","false");
            	mv.addObject("login_type","wap_wx_login");
            	mv.setViewName("redirect:/wemalls_login.htm");
            	return mv;
                }
               }
        }else{
        	mv.setViewName("redirect:/login.htm");
        	return mv;
        }
        return mv;
    }
    
    
    public static String urlEncodeUTF8(String source){  
        String result = source;  
        try {  
                result = java.net.URLEncoder.encode(source,"utf-8");  
        } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
        }  
        return result;  
    }  
    

    /**
     * 注册跳转页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/register.htm"})
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("register.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);

        String wemalls_view_type = CookitTools.Get_View_Type(request);

        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/register.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/register.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/register.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        request.getSession(false).removeAttribute("verify_code");

        return mv;
    }

    /**
     * 注册保存
     * @param request
     * @param response
     * @param userName
     * @param password
     * @param email
     * @param code
     * @return
     * @throws HttpException
     * @throws IOException
     */
    @RequestMapping({"/register_finish.htm"})
    public ModelAndView register_finish(HttpServletRequest request, HttpServletResponse response, String userName, String password, String email, String code)
    throws HttpException, IOException {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String url = CommUtil.getURL(request) + "/user/login.htm";
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        //跳转到微信端
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("wap/success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
//            url = CommUtil.getURL(request) + "/wap/index.htm?store_id=" + store_id;
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("android/success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
//            url = CommUtil.getURL(request) + "/wap/index.htm?store_id=" + store_id;
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("ios/success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
//            url = CommUtil.getURL(request) + "/wap/index.htm?store_id=" + store_id;
        }

        boolean reg = true;

        // 判断验证码
        if ((code != null) && (!code.equals(""))){
            code = CommUtil.filterHTML(code);
        }
        if (this.configService.getSysConfig().isSecurityCodeRegister()){
            if (!request.getSession(false).getAttribute("verify_code").equals(code)){
                reg = false;
            }
        }

        // 用户名不能重复
        Map params = new HashMap();
        params.put("userName", userName);
        List users = this.userService.query("select obj from User obj where obj.userName=:userName", params, -1, -1);
        if ((users != null) && (users.size() > 0)){
            reg = false;
        }

        if (reg){
            User user = new User();
            user.setUserName(userName);
            user.setUserRole("BUYER");
            user.setAddTime(new Date());
            user.setEmail(email);
            user.setPassword(Md5Encrypt.md5(password).toLowerCase());
            params.clear();
            params.put("type", "BUYER");
            List roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
            user.getRoles().addAll(roles);

            // 如果系统开启积分功能，则给会员新增积分
            if (this.configService.getSysConfig().isIntegral()){
                user.setIntegral(this.configService.getSysConfig().getMemberRegister());
                this.userService.save(user);
                IntegralLog log = new IntegralLog();
                log.setAddTime(new Date());
                log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister() + "分");
                log.setIntegral(this.configService.getSysConfig().getMemberRegister());
                log.setIntegral_user(user);
                log.setType("reg");
                this.integralLogService.save(log);
            }else{
                this.userService.save(user);
            }

            // 设置相册
            Album album = new Album();
            album.setAddTime(new Date());
            album.setAlbum_default(true);
            album.setAlbum_name("默认相册");
            album.setAlbum_sequence(-10000);
            album.setUser(user);
            this.albumService.save(album);
            request.getSession(false).removeAttribute("verify_code");

            // UC会员同步
            if (this.configService.getSysConfig().isUc_bbs()){
                UCClient client = new UCClient();
                String ret = client.uc_user_register(userName, password, email);
                int uid = Integer.parseInt(ret);
                if (uid <= 0){
                    if (uid == -1)
                        System.out.print("用户名不合法");
                   else if (uid == -2)
                        System.out.print("包含要允许注册的词语");
                   else if (uid == -3)
                        System.out.print("用户名已经存在");
                   else if (uid == -4)
                        System.out.print("Email 格式有误");
                   else if (uid == -5)
                        System.out.print("Email 不允许注册");
                   else if (uid == -6)
                        System.out.print("该 Email 已经被注册");
                    else
                        System.out.print("未定义");
                }else{
                    this.ucTools.active_user(userName, user.getPassword(), email);
                }
            }
            mv.addObject("op_title", "用户注册成功，请登录");
            mv.addObject("url", url);
            return mv;
        }
        mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        url = CommUtil.getURL(request) + "/register.htm";
        //跳转到微信端
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("wap/error.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("android/error.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("ios/error.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        mv.addObject("op_title", "注册失败");
        mv.addObject("url", url);
        return mv;
    }

    /**
     * 登录操作成功之后跳转判断
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/user_login_success.htm"})
    public ModelAndView user_login_success(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String url = CommUtil.getURL(request) + "/index.htm";
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        //跳转到微信端
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("wap/success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/wap/index.htm?store_id=" + store_id;
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("android/success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/android/index.htm?store_id=" + store_id;
        }
        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("ios/success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/ios/index.htm?store_id=" + store_id;
        }
        HttpSession session = request.getSession(false);
        if ((session.getAttribute("refererUrl") != null) &&
                (!session.getAttribute("refererUrl").equals(""))){
            url = (String)session.getAttribute("refererUrl");
            session.removeAttribute("refererUrl");
        }
        if (this.configService.getSysConfig().isUc_bbs()){
            String uc_login_js = CommUtil.null2String(request.getSession(false).getAttribute("uc_login_js"));
            mv.addObject("uc_login_js", uc_login_js);
        }
        //第三方登录：QQ、新浪等
        String bind = CommUtil.null2String(request.getSession(false).getAttribute("bind"));
        if (!bind.equals("")){
            mv = new JModelAndView(bind + "_login_bind.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            User user = SecurityUserHolder.getCurrentUser();
            mv.addObject("user", user);
            request.getSession(false).removeAttribute("bind");
        }
        mv.addObject("op_title", "用户已登录");
        mv.addObject("url", url);

        return mv;
    }
    /**
     * 登录模态窗口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/user_dialog_login.htm"})
    public ModelAndView user_dialog_login(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user_dialog_login.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);

        return mv;
    }


    /** wap登录业务逻辑begin */

    @RequestMapping({ "/user/wap/login.htm" })
    public ModelAndView waplogin(HttpServletRequest request, HttpServletResponse response, String url){
        ModelAndView mv = new JModelAndView("wap/login.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        request.getSession(false).removeAttribute("verify_code");

        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
        if ((url != null) && (!url.equals(""))){
            request.getSession(false).setAttribute("refererUrl", url);
        }
        if (domain_error)
            mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       else{
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));

        /*String wemalls_view_type = CookitTools.Get_View_Type(request);

        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
        	//String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
        	mv = new JModelAndView("wap/success.html", this.configService.getSysConfig(),
        			this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "成功！");
        	mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
        }*/

        return mv;
    }
    
    /** wap登录业务逻辑begin */

    @RequestMapping({ "/user/android/login.htm" })
    public ModelAndView androidlogin(HttpServletRequest request, HttpServletResponse response, String url){
        ModelAndView mv = new JModelAndView("android/login.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        request.getSession(false).removeAttribute("verify_code");

        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
        if ((url != null) && (!url.equals(""))){
            request.getSession(false).setAttribute("refererUrl", url);
        }
        if (domain_error)
            mv = new JModelAndView("android/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       else{
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));

        /*String wemalls_view_type = CookitTools.Get_View_Type(request);

        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
        	//String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
        	mv = new JModelAndView("wap/success.html", this.configService.getSysConfig(),
        			this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "成功！");
        	mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
        }*/

        return mv;
    }
    
    /** wap登录业务逻辑begin */

    @RequestMapping({ "/user/ios/login.htm" })
    public ModelAndView ioslogin(HttpServletRequest request, HttpServletResponse response, String url){
        ModelAndView mv = new JModelAndView("ios/login.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        request.getSession(false).removeAttribute("verify_code");

        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
        if ((url != null) && (!url.equals(""))){
            request.getSession(false).setAttribute("refererUrl", url);
        }
        if (domain_error)
            mv = new JModelAndView("ios/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       else{
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));

        /*String wemalls_view_type = CookitTools.Get_View_Type(request);

        if ((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
        	//String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
        	mv = new JModelAndView("wap/success.html", this.configService.getSysConfig(),
        			this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "成功！");
        	mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
        }*/

        return mv;
    }


}




