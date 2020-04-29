package cn.wemalls.view.web.action;

import cn.wemalls.core.domain.virtual.SysMap;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.WxCommonUtil;
import cn.wemalls.foundation.domain.Article;
import cn.wemalls.foundation.domain.ArticleClass;
import cn.wemalls.foundation.domain.query.ArticleQueryObject;
import cn.wemalls.foundation.service.IArticleClassService;
import cn.wemalls.foundation.service.IArticleService;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.foundation.service.IUserConfigService;
import cn.wemalls.view.web.tools.ArticleViewTools;
import cn.wemalls.view.web.tools.CookitTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ���¿�����
 */
@Controller
public class ArticleViewAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IArticleClassService articleClassService;

    @Autowired
    private ArticleViewTools articleTools;

    @RequestMapping({"/articlelist.htm"})
    public ModelAndView articlelist(HttpServletRequest request, HttpServletResponse response, String param, String currentPage){
        ModelAndView mv = new JModelAndView("articlelist.html", this.configService
                                            .getSysConfig(), this.userConfigService.getUserConfig(), 1,
                                            request, response);
//        ModelAndView mv = new JModelAndView("goods_cart1.html", this.configService.getSysConfig(),
//                this.userConfigService.getUserConfig(), 1, request, response);
		String wemalls_view_type = CookitTools.Get_View_Type(request);
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
		mv = new JModelAndView("wap/articlelist.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
		mv = new JModelAndView("android/articlelist.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
		mv = new JModelAndView("ios/articlelist.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
        ArticleClass ac = null;
        ArticleQueryObject aqo = new ArticleQueryObject();
        aqo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
        Long id = CommUtil.null2Long(param);
        String mark = "";
        if (id.longValue() == -1L){
            mark = param;
        }
        if (!mark.equals("")){
            aqo
            .addQuery("obj.articleClass.mark",
                      new SysMap("mark", mark), "=");
            ac = this.articleClassService.getObjByPropertyName("mark", mark);
        }
        if (id.longValue() != -1L){
            aqo.addQuery("obj.articleClass.id", new SysMap("id", id), "=");
            ac = this.articleClassService.getObjById(id);
        }
        aqo.addQuery("obj.display", new SysMap("display", Boolean.valueOf(true)), "=");
        aqo.setOrderBy("addTime");
        aqo.setOrderType("desc");
        IPageList pList = this.articleService.list(aqo);
        String url = CommUtil.getURL(request) + "/articlelist_" + ac.getId();
        CommUtil.saveIPageList2ModelAndView("", url, "", pList, mv);
        List acs = this.articleClassService
                   .query(
                       "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
                       null, -1, -1);
        List articles = this.articleService.query(
                            "select obj from Article obj order by obj.addTime desc", null,
                            0, 6);
        mv.addObject("ac", ac);
        mv.addObject("articles", articles);
        mv.addObject("acs", acs);

        return mv;
    }

    @RequestMapping({"/article.htm"})
    public ModelAndView article(HttpServletRequest request, HttpServletResponse response, String param){
        ModelAndView mv = new JModelAndView("article.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
		String wemalls_view_type = CookitTools.Get_View_Type(request);
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
		mv = new JModelAndView("wap/article.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
		mv = new JModelAndView("android/article.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
		if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
		mv = new JModelAndView("ios/article.html", this.configService.getSysConfig(),
		       this.userConfigService.getUserConfig(), 1, request, response);
		}
        Article obj = null;
        Long id = CommUtil.null2Long(param);
        String mark = "";
        if (id.longValue() == -1L){
            mark = param;
        }
        if (id.longValue() != -1L){
            obj = this.articleService.getObjById(id);
        }
        if (!mark.equals("")){
            obj = this.articleService.getObjByProperty("mark", mark);
        }
        List acs = this.articleClassService.query(
                       "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
        List articles = this.articleService.query(
                            "select obj from Article obj order by obj.addTime desc", null, 0, 6);
        mv.addObject("articles", articles);
        mv.addObject("acs", acs);
        mv.addObject("obj", obj);
        mv.addObject("articleTools", this.articleTools);

        return mv;
    }
    
    
    @RequestMapping({"/wxapplet/article.htm"})
    public void wxappletArticle(HttpServletRequest request, HttpServletResponse response, String mark,String id){
        Map resMap = new HashMap();
        Article obj = null;
//        Long id = CommUtil.null2Long(param);
//        Long id = CommUtil.null2Long(id);
//        String mark = "";
//        if (id.longValue() == -1L){
//            mark = param;
//        }
        if (id!=null){
            obj = this.articleService.getObjById(CommUtil.null2Long(id));
        }
        if (mark!=null&&!mark.equals("")){
            obj = this.articleService.getObjByProperty("mark", mark);
        }
        List acs = this.articleClassService.query(
                       "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
        List articles = this.articleService.query(
                            "select obj from Article obj order by obj.addTime desc", null, 0, 6);
        resMap.put("articles", articles);
        resMap.put("acs", acs);
        resMap.put("obj", obj);
        resMap.put("articleTools", this.articleTools);

        WxCommonUtil.printObjJson(resMap, response);
//        return mv;
    }

}




