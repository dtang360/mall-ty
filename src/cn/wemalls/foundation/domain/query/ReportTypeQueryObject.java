package cn.wemalls.foundation.domain.query;

import org.springframework.web.servlet.ModelAndView;

import cn.wemalls.core.query.QueryObject;

public class ReportTypeQueryObject extends QueryObject {
    public ReportTypeQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType){
        super(currentPage, mv, orderBy, orderType);
    }

    public ReportTypeQueryObject(){
    }
}




