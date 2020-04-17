package cn.wemalls.foundation.domain.query;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import cn.wemalls.core.query.QueryObject;

public class FavoriteQueryObject extends QueryObject {
    public FavoriteQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType){
        super(currentPage, mv, orderBy, orderType);
    }
    
    public FavoriteQueryObject(String currentPage, Map map, String orderBy, String orderType){
        super(currentPage, map, orderBy, orderType);
    }


    public FavoriteQueryObject(){
    }
}




