package cn.wemalls.foundation.domain.query;

import org.springframework.web.servlet.ModelAndView;

import cn.wemalls.core.query.QueryObject;

public class BargainQueryObject extends QueryObject {
    public BargainQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType){
        super(currentPage, mv, orderBy, orderType);
    }

    public BargainQueryObject(){
    }
}




