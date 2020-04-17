package cn.wemalls.manage.buyer.action;

import cn.wemalls.core.annotation.SecurityMapping;
import cn.wemalls.core.domain.virtual.SysMap;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.security.support.SecurityUserHolder;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.WxCommonUtil;
import cn.wemalls.foundation.domain.Favorite;
import cn.wemalls.foundation.domain.query.FavoriteQueryObject;
import cn.wemalls.foundation.service.IFavoriteService;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.foundation.service.IUserConfigService;
import cn.wemalls.view.web.tools.CookitTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 买家收藏控制器
 */
@Controller
public class FavoriteBuyerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IFavoriteService favoriteService;

    @SecurityMapping(display = false, rsequence = 0, title = "用户商品收藏", value = "/buyer/favorite_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/favorite_goods.htm"})
    public ModelAndView favorite_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/favorite_goods.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/favorite_goods.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/favorite_goods.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/favorite_goods.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
                orderBy, orderType);
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(0)), "=");
        qo.addQuery("obj.user.id",
                    new SysMap("user_id",
                               SecurityUserHolder.getCurrentUser().getId()), "=");
        IPageList pList = this.favoriteService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_goods.htm",
                                            "", params, pList, mv);

        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "用户商品收藏", value = "/buyer/favorite_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/wxapplet/buyer/favorite_goods.htm"})
    public void wxapplet_favorite_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
    	Map resMap=new HashMap();
    	//        ModelAndView mv = new JModelAndView(
//            "user/default/usercenter/favorite_goods.html", this.configService
//            .getSysConfig(),
//            this.userConfigService.getUserConfig(), 0, request, response);
//        String wemalls_view_type = CookitTools.Get_View_Type(request);
//        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
//            mv = new JModelAndView("wap/favorite_goods.html", this.configService.getSysConfig(),
//                                   this.userConfigService.getUserConfig(), 1, request, response);
//        }
//        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
//            mv = new JModelAndView("android/favorite_goods.html", this.configService.getSysConfig(),
//                                   this.userConfigService.getUserConfig(), 1, request, response);
//        }
//        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
//            mv = new JModelAndView("ios/favorite_goods.html", this.configService.getSysConfig(),
//                                   this.userConfigService.getUserConfig(), 1, request, response);
//        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, resMap,
                orderBy, orderType);
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(0)), "=");
        qo.addQuery("obj.user.id",
                    new SysMap("user_id",
                               SecurityUserHolder.getCurrentUser().getId()), "=");
        IPageList pList = this.favoriteService.list(qo);
//        CommUtil.saveIPageList2Map2(url + "/buyer/favorite_goods.htm",
//                                            "", params, pList, resMap);
        resMap.put("objsList", pList.getResult());
//      CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
      String[] propertys={"objsList","Favorite","goods","goods_main_photo"};
      WxCommonUtil.printObjJsonAll(resMap, response,propertys,false,false,false);
//        return mv;
    }
    
    

    @SecurityMapping(display = false, rsequence = 0, title = "用户店铺收藏", value = "/buyer/favorite_store.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/favorite_store.htm"})
    public ModelAndView favorite_store(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/favorite_store.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/favorite_store.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/favorite_store.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/favorite_store.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
                orderBy, orderType);
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(1)), "=");
        qo.addQuery("obj.user.id",
                    new SysMap("user_id",
                               SecurityUserHolder.getCurrentUser().getId()), "=");
        IPageList pList = this.favoriteService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store.htm",
                                            "", params, pList, mv);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "用户收藏删除", value = "/buyer/favorite_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/favorite_del.htm"})
    public String favorite_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage, int type){
        String[] ids = mulitId.split(",");
        for (String id : ids){
            if (!id.equals("")){
                Favorite favorite = this.favoriteService.getObjById(
                                        Long.valueOf(Long.parseLong(id)));
                this.favoriteService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        if (type == 0){
            return "redirect:favorite_goods.htm?currentPage=" + currentPage;
        }

        return "redirect:favorite_store.htm?currentPage=" + currentPage;
    }
}




