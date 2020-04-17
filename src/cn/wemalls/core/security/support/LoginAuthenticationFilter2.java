package cn.wemalls.core.security.support;

import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.uc.api.UCClient;
import cn.wemalls.uc.api.XMLHelper;
import cn.wemalls.view.web.tools.CookitTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LoginAuthenticationFilter2 extends AuthenticationProcessingFilter {
    @Autowired
    private ISysConfigService configService;
    private Map aMap=new HashMap<>();
    String username = "";
    String password ="";

    /** 登录验证
     *
     * @param request
     * @return
     * @throws AuthenticationException
     */
    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        String login_role = request.getParameter("login_role");
        String verify_code_flg = request.getParameter("verify_code_flg");
        String login_type = request.getParameter("login_type");
        String wemalls_view_type =CookitTools.Get_View_Type(request);
        if ((login_role == null) || (login_role.equals("")))
            login_role = "user";
        HttpSession session = request.getSession();
        session.setAttribute("login_role", login_role);
        session.setAttribute("ajax_login", Boolean.valueOf(CommUtil.null2Boolean(request.getParameter("ajax_login"))));
        session.setAttribute("wemalls_view_type", wemalls_view_type);
        //登录类型
        if(login_type!=null&&!"".equals(login_type)){
        	 session.setAttribute("login_type",login_type);
        }else{
        	 session.setAttribute("login_type","system_login");
        }

        if(verify_code_flg==null){
            // 验证码校验
            boolean flag = true;
            if (session.getAttribute("verify_code") != null){
                String code = request.getParameter("code") != null ? request.getParameter("code").toUpperCase() : "";
                if (!session.getAttribute("verify_code").equals(code)){
                    flag = false;
                }
            }
            // 用户输入验证码错误
            if (!flag){
                String username = obtainUsername(request);
                String password = "";
                username = username.trim();
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username, password);
                if ((session != null) || (getAllowSessionCreation())){
                    request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME",
                                                      TextUtils.escapeEntities(username));
                }
                setDetails(request, authRequest);
                return getAuthenticationManager().authenticate(authRequest);
            }
        }

        
        if (CommUtil.null2Boolean(request.getParameter("encode")))
            username = CommUtil.decode(obtainUsername(request)) + "," +
                       login_role;
        else
            username = obtainUsername(request) + "," + login_role;
       password = obtainPassword(request);

        if (this.configService.getSysConfig().isUc_bbs()){
            String uc_login_js = uc_Login(
                                     CommUtil.decode(obtainUsername(request)), password);
            request.getSession(false).setAttribute("uc_login_js",
                                                   uc_login_js);
        }
        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password);
        if ((session != null) || (getAllowSessionCreation())){
            request.getSession().setAttribute(
                "SPRING_SECURITY_LAST_USERNAME",
                TextUtils.escapeEntities(username));
        }
        setDetails(request, authRequest);

        return getAuthenticationManager().authenticate(authRequest);
    }

    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        request.getSession(false).removeAttribute("verify_code");
    	Cookie cookauth = new Cookie("auth_success", "success");
    	cookauth.setMaxAge(24 * 60 * 60 * 30);
    	cookauth.setPath("/");
    	response.addCookie(cookauth);
    	Cookie cookuname = new Cookie("login_user_name", username);
    	cookuname.setMaxAge(24 * 60 * 60 * 30);
    	cookuname.setPath("/");
    	response.addCookie(cookuname);
    	Cookie cookpword = new Cookie("login_password", password);
    	cookpword.setMaxAge(24 * 60 * 60 * 30);
    	cookpword.setPath("/");
    	response.addCookie(cookpword);
        super.onSuccessfulAuthentication(request, response, authResult);
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        String uri = request.getRequestURI();
        super.onUnsuccessfulAuthentication(request, response, failed);
    }

    private static String uc_Login(String username, String pws){
        String ucsynlogin = "";
        UCClient e = new UCClient();
        String result = e.uc_user_login(username, pws);
        LinkedList rs = XMLHelper.uc_unserialize(result);
        if (rs.size() > 0){
            int uid = Integer.parseInt((String)rs.get(0));
            String uname = (String)rs.get(1);
            String password = (String)rs.get(2);
            String email = (String)rs.get(3);
            if (uid > 0){
                ucsynlogin = e.uc_user_synlogin(uid);
            }else if (uid == -1){
                System.out.println("用户不存在,或者被删除");
            }
        }

        return ucsynlogin;
    }
}
