package cn.wemalls.core.security.support;

import cn.wemalls.core.security.SecurityManager;
import cn.wemalls.foundation.domain.Res;
import cn.wemalls.foundation.domain.Role;
import cn.wemalls.foundation.domain.User;
import cn.wemalls.foundation.service.IResService;
import cn.wemalls.foundation.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import java.util.*;

public class SecurityManagerSupport implements UserDetailsService, SecurityManager {
    @Autowired
    private IUserService userService;

    @Autowired
    private IResService resService;

    public UserDetails loadUserByUsername(String data) throws UsernameNotFoundException, DataAccessException {
        String[] list = data.split(",");
        String userName = list[0];
        String loginRole = "user";
        if (list.length == 2){
            loginRole = list[1];
        }
        Map params = new HashMap();
        params.put("userName", userName);
        List users = this.userService.query(
                         "select obj from User obj where obj.userName =:userName ",
                         params, -1, -1);
        if (users.isEmpty()){
            throw new UsernameNotFoundException("User " + userName +
                                                " has no GrantedAuthority");
        }
        User user = (User)users.get(0);
        Set authorities = new HashSet();
        if ((!user.getRoles().isEmpty()) && (user.getRoles() != null)){
            Iterator roleIterator = user.getRoles().iterator();
            while (roleIterator.hasNext()){
                Role role = (Role)roleIterator.next();
                if (loginRole.equalsIgnoreCase("ADMIN")){
                    GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(
                        role.getRoleCode().toUpperCase());
                    authorities.add(grantedAuthority);
                }else if (!role.getType().equals("ADMIN")){
                    GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(
                        role.getRoleCode().toUpperCase());
                    authorities.add(grantedAuthority);
                }
            }
        }

        GrantedAuthority[] auths = new GrantedAuthority[authorities.size()];
        user.setAuthorities((GrantedAuthority[])authorities.toArray(auths));

        return user;
    }

    public Map<String, String> loadUrlAuthorities(){
        Map<String, String> urlAuthorities = new HashMap<String, String>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "URL");
        List<Res> urlResources = this.resService.query(
                                     "select obj from Res obj where obj.type = :type", params, -1,
                                     -1);
        for (Res res : urlResources){
            urlAuthorities.put(res.getValue(), res.getRoleAuthorities());
        }

        return urlAuthorities;
    }
}
