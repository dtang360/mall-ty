package cn.wemalls.foundation.domain.query;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import cn.wemalls.core.query.QueryObject;

public class AddressQueryObject extends QueryObject {
    public AddressQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType){
        super(currentPage, mv, orderBy, orderType);
    }
    
    public AddressQueryObject(String currentPage, Map mv, String orderBy, String orderType){
        super(currentPage, mv, orderBy, orderType);
    }

    public AddressQueryObject(){
    }
}




