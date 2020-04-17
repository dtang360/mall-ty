package cn.wemalls.view.web.action;

import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.foundation.domain.Document;
import cn.wemalls.foundation.service.IDocumentService;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.foundation.service.IUserConfigService;
import cn.wemalls.view.web.tools.CookitTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ϵͳ���¿�����
 */
@Controller
public class DocumentViewAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IDocumentService documentService;

    @RequestMapping({"/doc.htm"})
    public ModelAndView doc(HttpServletRequest request, HttpServletResponse response, String mark){
        ModelAndView mv = new JModelAndView("doc.html", this.configService
                                            .getSysConfig(), this.userConfigService.getUserConfig(), 1,
                                            request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
		mv = new JModelAndView("wap/doc.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
		mv = new JModelAndView("android/doc.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
		mv = new JModelAndView("ios/doc.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
        Document obj = this.documentService.getObjByProperty("mark", mark);
        mv.addObject("obj", obj);

        return mv;
    }
}




