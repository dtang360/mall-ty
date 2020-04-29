package cn.wemalls.view.web.action;

import cn.wemalls.core.filter.HibernatePropertyFilter;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.security.support.SecurityUserHolder;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.Md5Encrypt;
import cn.wemalls.core.tools.WxCommonUtil;
import cn.wemalls.foundation.domain.*;
import cn.wemalls.foundation.service.*;
import cn.wemalls.manage.admin.tools.MsgTools;
import cn.wemalls.view.web.tools.CookitTools;
import cn.wemalls.view.web.tools.GoodsFloorViewTools;
import cn.wemalls.view.web.tools.GoodsViewTools;
import cn.wemalls.view.web.tools.NavViewTools;
import cn.wemalls.view.web.tools.StoreViewTools;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 商城首页控制器
 */
@Controller
public class IndexViewAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IGoodsClassService goodsClassService;

    @Autowired
    private IGoodsBrandService goodsBrandService;

    @Autowired
    private IPartnerService partnerService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IArticleClassService articleClassService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IAccessoryService accessoryService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private INavigationService navigationService;

    @Autowired
    private IGroupGoodsService groupGoodsService;

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IGoodsFloorService goodsFloorService;

    @Autowired
    private IBargainGoodsService bargainGoodsService;

    @Autowired
    private IDeliveryGoodsService deliveryGoodsService;

    @Autowired
    private IStoreCartService storeCartService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private NavViewTools navTools;

    @Autowired
    private GoodsViewTools goodsViewTools;

    @Autowired
    private StoreViewTools storeViewTools;

    @Autowired
    private MsgTools msgTools;

    @Autowired
    private GoodsFloorViewTools gf_tools;
    
    @Autowired
    private IAdvertPositionService advertPositionService;
    /**
     * 页面最上面部分
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/top.htm" })
    public ModelAndView top(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("top.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        List msgs = new ArrayList();
        if(SecurityUserHolder.getCurrentUser() != null){
            Map params = new HashMap();
            params.put("status", Integer.valueOf(0));
            params.put("reply_status", Integer.valueOf(1));
            params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
            params.put("to_user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService.query("select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ", params, -1, -1);
        }
        Store store = null;
        if(SecurityUserHolder.getCurrentUser() != null)
            store = this.storeService.getObjByProperty("user.id", SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgs", msgs);
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if(SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        Map params = new HashMap();
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("cart_session_id")){
                    cart_session_id = CommUtil.null2String(cookie.getValue());
                }
            }
        }
        if(user != null){
            if(!cart_session_id.equals("")){
                if(user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                                        params, -1, -1);
                    for(StoreCart sc : store_cookie_cart){
                        for(GoodsCart gc : ((StoreCart)sc).getGcs()){
                            gc.getGsps().clear();
//                            goodsCartService.update(gc);
//                            goodsCartService.delete(gc.getId());
                            goodsCartService.executeNativeSQL("delete from wemalls_cart_gsp where cart_id="+gc.getId());
                            goodsCartService.executeNativeSQL("delete from wemalls_goodscart where id="+gc.getId());
                        }
                        storeCartService.executeNativeSQL("delete from wemalls_storecart where id="+sc.getId());
                    }
                }

                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }

        }else if(!cart_session_id.equals("")){
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);
        }

        for(StoreCart sc : user_cart){
            boolean sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                }
            }
            if(sc_add)
                cart.add(sc);
        }
        boolean sc_add;
        for(StoreCart sc : cookie_cart){
            sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                    for(GoodsCart gc : sc.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc.getId());
                }
            }
            if(sc_add){
                cart.add(sc);
            }
        }
        if(cart != null){
            for(StoreCart sc : cart){
                if(sc != null){
                    list.addAll(sc.getGcs());
                }
            }
        }
        float total_price = 0.0F;
        for(GoodsCart gc : list){
            Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if(CommUtil.null2String(gc.getCart_type()).equals("combin"))
                total_price = CommUtil.null2Float(goods.getCombin_price());
           else{
                total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(Float.valueOf(gc.getCount()), goods.getGoods_current_price()))) + total_price;
            }
        }
        mv.addObject("total_price", Float.valueOf(total_price));
        mv.addObject("cart", list);

        return (ModelAndView)mv;
    }
    /**
     * 横着的导航栏
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/nav.htm" })
    public ModelAndView nav(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("nav.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 14);
        if(gcs!=null&&gcs.size()>0){
        	List childs = ((GoodsClass)gcs.get(0)).getChilds();
        }
        mv.addObject("gcs", gcs);
        return mv;
    }

    @RequestMapping({ "/nav1.htm" })
    public ModelAndView nav1(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("nav1.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 14);
        mv.addObject("gcs", gcs);
        mv.addObject("navTools", this.navTools);

        return mv;
    }

    @RequestMapping({ "/head.htm" })
    public ModelAndView head(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("head.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String type = CommUtil.null2String(request.getAttribute("type"));
        mv.addObject("type", type.equals("") ? "goods" : type);

        return mv;
    }

    @RequestMapping({ "/login_head.htm" })
    public ModelAndView login_head(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("login_head.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        return mv;
    }
    
    @RequestMapping({ "/floor_mobile.htm" })
    public ModelAndView floor_mobile(HttpServletRequest request, HttpServletResponse response){
    	ModelAndView mv = new JModelAndView("floor.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
    	String wemalls_view_type = CookitTools.Get_View_Type(request);
        String type = String.valueOf(request.getAttribute("type")!=null?request.getAttribute("type"):"");
    	if(type!=null&&type.length()>0) {
    		wemalls_view_type=type;
    	}
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/floor.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/floor.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/floor.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        Map params = new HashMap();
        params.put("gf_display", Boolean.valueOf(true));
        List floors = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc", params, -1, -1);
        mv.addObject("floors", floors);
        mv.addObject("gf_tools", this.gf_tools);
        mv.addObject("url", CommUtil.getURL(request));

        params.put("gf_display", Boolean.valueOf(true));
//        List lists = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null and obj.id=:id order by obj.gf_sequence asc", params, -1, -1);
        Map<String, Object> map = new HashMap<String, Object>();
        CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);
        								//List lists, int i, String url, Map<String, Object> map
//        String ret = showLoadFloorAjaxHtml(lists, i, CommUtil.getURL(request), map);
//        response.setContentType("text/plain");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setCharacterEncoding("UTF-8");
        
        String loadimg = map.get("imageWebServer") + "/resources/style/common/images/loader.gif";
        String errorimg = map.get("webPath") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");
        String goods_url = null;
        String goods_class_url = null;
        String child_goods_class_url = null;
//        GoodsFloor floor = (GoodsFloor)lists.get(0);
//        List<GoodsClass> gcs = this.gf_tools.generic_gf_gc(floor.getGf_gc_list());
//        Map<String, Object> mmap = gf_tools.generic_goods_list(floor.getGf_list_goods());
//        Map leftadv=gf_tools.generic_advm(CommUtil.getURL(request), floor.getGf_left_adv());
//        Map rightadv=gf_tools.generic_advm(CommUtil.getURL(request), floor.getGf_right_adv());
        
//        mv.addObject("lists", lists);
        mv.addObject("url", CommUtil.getURL(request));
        mv.addObject("map", map);
        mv.addObject("loadimg", loadimg);
        mv.addObject("errorimg", errorimg);
//        mv.addObject("mmap",mmap);
        mv.addObject("gf_tools",gf_tools);
//        mv.addObject("leftadv",leftadv);
//        mv.addObject("rightadv",rightadv);
//        mv.addObject("floor", floor);
//        mv.addObject("gcs", gcs);
        return mv;
    }

    @RequestMapping({ "/floor.htm" })
    public ModelAndView floor(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("floor.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        Map params = new HashMap();
        params.put("gf_display", Boolean.valueOf(true));
        List floors = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc", params, -1, -1);
        mv.addObject("floors", floors);
        mv.addObject("gf_tools", this.gf_tools);
        mv.addObject("url", CommUtil.getURL(request));

        return mv;
    }
    
    @RequestMapping({ "/wxapplet/floor.htm" })
    public void wxappletFloor(HttpServletRequest request, HttpServletResponse response){
    		Map resMap=new HashMap();
    		String wemalls_view_type = CookitTools.Get_View_Type(request);
            String type = String.valueOf(request.getAttribute("type")!=null?request.getAttribute("type"):"");
        	if(type!=null&&type.length()>0) {
        		wemalls_view_type=type;
        	}
            Map params = new HashMap();
            params.put("gf_display", Boolean.valueOf(true));
            List floors = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc", params, -1, -1);
            resMap.put("floors", floors);
            
//            if(floors!=null&&floors.size()>0) {
//            	GoodsFloor goodsFloor=null;
//            for(int i=0;i<floors.size();i++) {
//            	goodsFloor=(GoodsFloor)floors.get(i);
//            	if(goodsFloor!=null) {
//            		GoodsFloor floor=goodsFloor.getChilds().get(0);
//            		((GoodsFloor)((GoodsFloor)floors.get(i)).getChilds().get(0)).setGf_gc_goods(gf_gc_goods);
//            		((List<GoodsFloor>)(goodsFloor.getChilds()).get(0
//            		gf_tools.generic_goods(floor.getGf_gc_goods());
//            		
//            		((GoodsFloor)floors.get(i))floor.getChilds().set(0, gf_tools.generic_goods(floor.getGf_gc_goods());)
//            	}
//            }     
//            }       
            
            resMap.put("gf_tools", this.gf_tools);
            resMap.put("url", CommUtil.getURL(request));

            params.put("gf_display", Boolean.valueOf(true));
            Map<String, Object> map = new HashMap<String, Object>();
            CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);
            String loadimg = map.get("imageWebServer") + "/resources/style/common/images/loader.gif";
            String errorimg = map.get("webPath") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");
            String goods_url = null;
            String goods_class_url = null;
            String child_goods_class_url = null;
            
            resMap.put("url", CommUtil.getURL(request));
            resMap.put("map", map);
            resMap.put("loadimg", loadimg);
            resMap.put("errorimg", errorimg);
            resMap.put("gf_tools",gf_tools);

        WxCommonUtil.printObjJson(resMap, response);
    }

    @RequestMapping({"/floor_ajax.htm"})
    public ModelAndView floorAjax(HttpServletRequest request, HttpServletResponse response, Long id, Integer count){
    	ModelAndView mv = new JModelAndView("floor_ajax.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        Map params = new HashMap();
        params.put("gf_display", Boolean.valueOf(true));
        params.put("id", id);
        List lists = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null and obj.id=:id order by obj.gf_sequence asc", params, -1, -1);
        Map<String, Object> map = new HashMap<String, Object>();
        CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);
        								//List lists, int i, String url, Map<String, Object> map
//        String ret = showLoadFloorAjaxHtml(lists, i, CommUtil.getURL(request), map);
//        response.setContentType("text/plain");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setCharacterEncoding("UTF-8");
        
        String loadimg = map.get("imageWebServer") + "/resources/style/common/images/loader.gif";
        String errorimg = map.get("webPath") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");
        String goods_url = null;
        String goods_class_url = null;
        String child_goods_class_url = null;
        GoodsFloor floor = (GoodsFloor)lists.get(0);
        List<GoodsClass> gcs = this.gf_tools.generic_gf_gc(floor.getGf_gc_list());
        Map<String, Object> mmap = gf_tools.generic_goods_list(floor.getGf_list_goods());
        Map leftadv=gf_tools.generic_advm(CommUtil.getURL(request), floor.getGf_left_adv());
        Map rightadv=gf_tools.generic_advm(CommUtil.getURL(request), floor.getGf_right_adv());
        
        mv.addObject("lists", lists);
        mv.addObject("count", count.toString());
        mv.addObject("url", CommUtil.getURL(request));
        mv.addObject("map", map);
        mv.addObject("loadimg", loadimg);
        mv.addObject("errorimg", errorimg);
        mv.addObject("mmap",mmap);
        mv.addObject("gf_tools",gf_tools);
        mv.addObject("leftadv",leftadv);
        mv.addObject("rightadv",rightadv);
        mv.addObject("floor", floor);
        mv.addObject("gcs", gcs);
        return mv;
    }

    @RequestMapping({ "/footer.htm" })
    public ModelAndView footer(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("footer.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        String type = String.valueOf(request.getAttribute("type")!=null?request.getAttribute("type"):"");
    	if(type!=null&&type.length()>0) {
    		wemalls_view_type=type;
    	}
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/footer.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/footer.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/footer.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        Map params = new HashMap();
        params.put("class_mark", "about");
        params.put("display", Boolean.valueOf(true));
        List articles_about = this.articleService.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 6);
        mv.addObject("articles_about", articles_about);
        params.clear();
        params.put("class_mark", "helpcenter");
        params.put("display", Boolean.valueOf(true));
        List articles_helpcenter = this.articleService.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 6);
        mv.addObject("articles_helpcenter", articles_helpcenter);
        mv.addObject("navTools", this.navTools);

        return mv;
    }

    /**
     * PC首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/index.htm" })
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
//        //销毁cookit
//        Cookie cookies[] = request.getCookies();  
//        if (cookies != null)  
//        {  
//            for (int i = 0; i < cookies.length; i++)  
//            {   
//            	if("wemalls_view_type".equals(cookies[i].getName())){
//	                cookies[i].setMaxAge(0);
//	                response.addCookie(cookies[i]);
//                }  
//            }  
//        }
//        //设置为PC访问
//        Cookie cookie = new Cookie("wemalls_view_type", "pc");
//        cookie.setMaxAge(24 * 60 * 60 * 30);
//        response.addCookie(cookie);
        CookitTools.UpdateCookitByType(request, response, "wemalls_view_type", "pc", 24 * 60 * 60 * 30);
        if(!"pc".equals(CookitTools.GetCookitByType(request, "wemalls_view_type"))){
        	Cookie cooki = new Cookie("wemalls_view_type", "pc");
        	cooki.setMaxAge(24 * 60 * 60 * 30);
        	cooki.setPath("/");
        	response.addCookie(cooki);
        }

        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gcs", gcs);
        params.clear();
        // 推荐品牌
        params.put("audit", Integer.valueOf(1));
        params.put("recommend", Boolean.valueOf(true));
        List gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence", params, 0, 4);
        mv.addObject("gbs", gbs);
        // 底部显示的合作伙伴
        params.clear();
        List img_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1);
        mv.addObject("img_partners", img_partners);
        List text_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1);
        mv.addObject("text_partners", text_partners);
        // 底部新闻分类显示
        params.clear();
        params.put("mark", "news");
        List acs = this.articleClassService.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark order by obj.sequence asc", params, 0, 9);
        mv.addObject("acs", acs);
        // 商城新闻
        params.clear();
        params.put("class_mark", "news");
        params.put("display", Boolean.valueOf(true));
        List articles = this.articleService.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5);
        mv.addObject("articles", articles);
        // 查询推荐店铺商品
        params.clear();
        params.put("store_recommend", Boolean.valueOf(true));
        params.put("goods_status", Integer.valueOf(0));
        List store_reommend_goods_list = this.goodsService.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc", params, -1, -1);
        List store_reommend_goods = new ArrayList();
        int max = store_reommend_goods_list.size() >= 5 ? 4 : store_reommend_goods_list.size() - 1;
        for(int i = 0; i <= max; i++){
            store_reommend_goods.add((Goods)store_reommend_goods_list.get(i));
        }
        // 1、推荐商品可在后台编辑
        mv.addObject("store_reommend_goods", store_reommend_goods);
        mv.addObject("store_reommend_goods_count", Double.valueOf(Math.ceil(CommUtil.div(Integer.valueOf(store_reommend_goods_list.size()), Integer.valueOf(5)))));
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
        if(SecurityUserHolder.getCurrentUser() != null){
            mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        }
        // 团购查询
        params.clear();
        params.put("beginTime", new Date());
        params.put("endTime", new Date());
        List groups = this.groupService.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
        if(groups.size() > 0){
            // 2、团购商品
            params.clear();
            params.put("gg_status", Integer.valueOf(1));
            params.put("gg_recommend", Integer.valueOf(1));
            params.put("group_id", ((Group)groups.get(0)).getId());
            List ggs = this.groupGoodsService.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.gg_recommend=:gg_recommend and obj.group.id=:group_id order by obj.gg_recommend_time desc", params, 0, 5);
            //if(ggs.size() > 0)
            mv.addObject("ggs", ggs);
        }
        // 3、天天特价
        params.clear();
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
        params.put("bg_status", Integer.valueOf(1));
        List bgs = this.bargainGoodsService.query("select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", params, 0, 5);
        mv.addObject("bgs", bgs);
        // 4、热销商品倒序-疯狂抢购
        params.clear();
        params.put("goods_status", Integer.valueOf(0));
        List list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.goods_salenum desc", params, 0, 5);
        mv.addObject("fengKuangs", list);

        // 5、随机生成推荐商品 猜您喜欢
        List store_guess_goods = new ArrayList();
        Random rand = new Random();
        int recommend_goods_random = rand.nextInt(5);
        int begin = recommend_goods_random * 5;
        if(begin > store_reommend_goods_list.size() - 1){
            begin = 0;
        }
        int maxsize = begin + 5;
        if(maxsize > store_reommend_goods_list.size()){
            begin -= maxsize - store_reommend_goods_list.size();
            maxsize--;
        }
        for(int i = 0; i < store_reommend_goods_list.size(); i++){
            if((i >= begin) && (i < maxsize)){
                store_guess_goods.add((Goods)store_reommend_goods_list.get(i));
            }
        }
        mv.addObject("cais", store_guess_goods);
        // 6、满送商品
        params.clear();
        params.put("d_status", Integer.valueOf(1));
        params.put("d_begin_time", new Date());
        params.put("d_end_time", new Date());
        List dgs = this.deliveryGoodsService.query("select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc", params, 0, 5);
        mv.addObject("dgs", dgs);

        // 7、新品上架
        params.clear();
        params.put("goods_status", Integer.valueOf(0));
        List new_goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 999999999);
        mv.addObject("xinShangs", new_goods_list);

        // 8、点击数最多:人气商品
        params.clear();
        params.put("goods_status", Integer.valueOf(0));
        List hot_goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.goods_click desc", params, 0, 5);
        mv.addObject("hots", hot_goods_list);
        
//        params.clear();
//        params.put("goods_status", Integer.valueOf(0));
//        params.put("gc_ids",getGoodsClass(gcs,"50"));
//        List goodVRs = this.goodsService.query("select obj from Goods obj where obj.gc.id in (:gc_ids) and obj.goods_status=:goods_status order by obj.goods_click desc", params, 0, 4);
//        mv.addObject("goodVRs", goodVRs);
//        params.clear();
//        params.put("goods_status", Integer.valueOf(0));
//        params.put("gc_ids",getGoodsClass(gcs,"65689"));
//        List goodFoods = this.goodsService.query("select obj from Goods obj where obj.gc.id in (:gc_ids) and  obj.goods_status=:goods_status order by obj.goods_click desc", params, 0, 4);
//        mv.addObject("goodFoods", goodFoods);
//        params.clear();
//        params.put("goods_status", Integer.valueOf(0));
//        params.put("gc_ids",getGoodsClass(gcs,"69"));
//        List goodTours = this.goodsService.query("select obj from Goods obj where obj.gc.id in (:gc_ids) and  obj.goods_status=:goods_status order by obj.goods_click desc", params, 0, 4);
//        mv.addObject("goodTours", goodTours);
//        params.clear();
//        params.put("goods_status", Integer.valueOf(0));
//        params.put("gc_ids",getGoodsClass(gcs,"25"));
//        List goodHotels = this.goodsService.query("select obj from Goods obj where obj.gc.id in (:gc_ids) and  obj.goods_status=:goods_status order by obj.goods_click desc", params, 0,4);
//        mv.addObject("goodHotels", goodHotels);
        
        
        //文章 巴楚
        params.clear();
        params.put("class_mark", "bachu");
        params.put("display", Boolean.valueOf(true));
        List articles1 = this.articleService.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5);
        mv.addObject("articles1", articles1);
        //文章 规划
        params.clear();
        params.put("class_mark", "plan");
        params.put("display", Boolean.valueOf(true));
        List articles2 = this.articleService.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5);
        mv.addObject("articles2", articles2);
        //文章 信息
        params.clear();
        params.put("class_mark", "info");
        params.put("display", Boolean.valueOf(true));
        List articles3 = this.articleService.query("select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5);
        mv.addObject("articles3", articles3);
        
        
        
        mv.addObject("login_type",request.getSession(false).getAttribute("login_type"));
        return mv;
    }
    
    private List getGoodsClass(List gcs,String gc_id){
        List goodsClasss=new ArrayList<>();
        List resList=new ArrayList<>();
        GoodsClass goodsClass=new GoodsClass();
        for(int i=0;i<gcs.size();i++){
        	if(gc_id.equals(((GoodsClass)gcs.get(i)).getId().toString())){
        		goodsClass=(GoodsClass)gcs.get(i);
        		//一级分类
        		goodsClasss.add(goodsClass);
        		if(((List<GoodsClass>)((GoodsClass)gcs.get(i)).getChilds())!=null){
        			//二级分类
        			goodsClasss.addAll((List<GoodsClass>)((GoodsClass)gcs.get(i)).getChilds());
        			for(int j=0;j<((List<GoodsClass>)((GoodsClass)gcs.get(i)).getChilds()).size();j++){
        				if((((List<GoodsClass>)((GoodsClass)gcs.get(i)).getChilds()).get(j).getChilds())!=null){
        					//三级
        					goodsClasss.addAll(((List<GoodsClass>)((GoodsClass)gcs.get(i)).getChilds()).get(j).getChilds());
        				}
        			}
        		}
        	}
        }
        if(goodsClasss!=null){
        	for(int f=0;f<goodsClasss.size();f++){
        		resList.add(((GoodsClass)goodsClasss.get(f)).getId());
        	}
        }
        return resList;
    }

    @RequestMapping({ "/close.htm" })
    public ModelAndView wapclose(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("close.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/close.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/close.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/close.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }

        return mv;
    }

    @RequestMapping({ "/404.htm" })
    public ModelAndView error404(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("404.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/404.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/404.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/android/index.htm");
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/404.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/ios/index.htm");
        }

        return mv;
    }

    @RequestMapping({ "/500.htm" })
    public ModelAndView error500(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("500.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("wap/500.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm?store_id=" + store_id);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("android/500.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/android/index.htm?store_id=" + store_id);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("ios/500.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/ios/index.htm?store_id=" + store_id);
        }

        return mv;
    }

    @RequestMapping({ "/goods_class.htm" })
    public ModelAndView goods_class(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("goods_class.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/goods_class.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/goods_class.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/goods_class.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, -1, -1);
        mv.addObject("gcs", gcs);

        return mv;
    }

    /**
     * 商品分类
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/goodsclass.htm" })
    public ModelAndView goodsclass(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("goodsclass.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/goodsclass.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/goodsclass.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/goodsclass.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        Map params = new HashMap();
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null  order by obj.sequence asc", params, -1, -1);
        mv.addObject("gcs", gcs);

        return mv;
    }
    
    /**
     * 商品分类
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/wxapplet/goodsclass.htm" })
    public void wxappletGgoodsclass(HttpServletRequest request, HttpServletResponse response){
    	Map resMap=new HashMap();
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        Map params = new HashMap();
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null  order by obj.sequence asc", params, -1, -1);
        resMap.put("gcs", gcs);
        WxCommonUtil.printObjJson(resMap, response);
    }
    
    /**
     * 商品分类
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/wxapplet/goodsclassdetail.htm" })
    public void wxappletGoodsclassDetail(HttpServletRequest request, HttpServletResponse response,String id){
    	Map resMap=new HashMap();
        Map params = new HashMap();
        params.put("id", CommUtil.null2Long(id));        
        List gcds = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id=:id   order by obj.sequence asc", params, -1, -1);
        resMap.put("gcds", gcds);
        String[] propertys={"gcds","GoodsClass","Accessory","icon_acc"};
        WxCommonUtil.printObjJsonAll(resMap, response,propertys,false,false,false);
    }

    @RequestMapping({ "/forget.htm" })
    public ModelAndView forget(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("forget.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        SysConfig config = this.configService.getSysConfig();
        if(!config.isEmailEnable()){
            mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统关闭邮件功能，不能找回密码");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }

        return mv;
    }

    @RequestMapping({ "/find_pws.htm" })
    public ModelAndView find_pws(HttpServletRequest request, HttpServletResponse response, String userName, String email, String code){
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        HttpSession session = request.getSession(false);
        String verify_code = (String)session.getAttribute("verify_code");
        if(code.toUpperCase().equals(verify_code)){
            User user = this.userService.getObjByProperty("userName", userName);
            if(user.getEmail().equals(email.trim())){
                String pws = CommUtil.randomString(6).toLowerCase();
                String subject = this.configService.getSysConfig().getTitle() + "密码找回邮件";
                String content = user.getUsername() + ",您好！您通过密码找回功能重置密码，新密码为：" + pws;
                boolean ret = this.msgTools.sendEmail(email, subject, content);
                if(ret){
                    user.setPassword(Md5Encrypt.md5(pws));
                    this.userService.update(user);
                    mv.addObject("op_title", "新密码已经发送到邮箱:<font color=red>" + email + "</font>，请查收后重新登录");
                    mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
                }else{
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
                    mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
                }
            }else{
                mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "用户名、邮箱不匹配");
                mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
            }
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "验证码不正确");
            mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
        }

        return mv;
    }

    @RequestMapping({ "/switch_recommend_goods.htm" })
    public ModelAndView switch_recommend_goods(HttpServletRequest request, HttpServletResponse response, int recommend_goods_random){
        ModelAndView mv = new JModelAndView("switch_recommend_goods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        Map params = new HashMap();
        params.put("store_recommend", Boolean.valueOf(true));
        params.put("goods_status", Integer.valueOf(0));
        List store_reommend_goods_list = this.goodsService.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc", params, -1, -1);
        List store_reommend_goods = new ArrayList();
        int begin = recommend_goods_random * 5;
        if(begin > store_reommend_goods_list.size() - 1){
            begin = 0;
        }
        int max = begin + 5;
        if(max > store_reommend_goods_list.size()){
            begin -= max - store_reommend_goods_list.size();
            max--;
        }
        for(int i = 0; i < store_reommend_goods_list.size(); i++){
            if((i >= begin) && (i < max)){
                store_reommend_goods.add((Goods)store_reommend_goods_list.get(i));
            }
        }
        mv.addObject("store_reommend_goods", store_reommend_goods);

        return mv;
    }

    @RequestMapping({ "/outline.htm" })
    public ModelAndView outline(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "该用户在其他地点登录，您被迫下线！");
        mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        String wemalls_view_type = CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/android/index.htm");
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/ios/index.htm");
        }

        return mv;
    }
    
    /**
     * wap首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/wxapplet/index.htm" })
    public void wxappletIndex(HttpServletRequest request, HttpServletResponse response){
        Map resMap = new HashMap();
        Map params = new HashMap();
//        params.put("display", Boolean.valueOf(true));
//        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 15);
//        resMap.put("gcs", gcs);
//        params.clear();
//        params.put("audit", Integer.valueOf(1));
//        params.put("recommend", Boolean.valueOf(true));
//        List gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence", params, -1, -1);
////        resMap.put("gbs", gbs);
//        params.clear();
//        List img_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1);
////        resMap.put("img_partners", img_partners);
//        List text_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1);
////        resMap.put("text_partners", text_partners);
//        params.clear();
//        params.put("mark", "news");
//        List acs = this.articleClassService.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark order by obj.sequence asc", params, 0, 9);
////        resMap.put("acs", acs);
//        params.clear();
////        params.put("store_recommend", Boolean.valueOf(true));
//        params.put("goods_status", Integer.valueOf(0));
//        List store_reommend_goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 999999);
//        List store_reommend_goods = new ArrayList();
//        int max = store_reommend_goods_list.size() >= 21 ? 20 : store_reommend_goods_list.size() - 1;
//        for(int i = 0; i <= max; i++){
//            store_reommend_goods.add((Goods)store_reommend_goods_list.get(i));
//        }
//        resMap.put("store_reommend_goods", store_reommend_goods_list);

//        resMap.put("store_reommend_goods_count", Double.valueOf(Math.ceil(CommUtil.div(Integer.valueOf(store_reommend_goods_list.size()), Integer.valueOf(5)))));
//        resMap.put("goodsViewTools", this.goodsViewTools);
//        resMap.put("storeViewTools", this.storeViewTools);
//        if(SecurityUserHolder.getCurrentUser() != null){
//        	User cuser=this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
//        	resMap.put("user", cuser);
//            String logintype=String.valueOf(request.getSession(false).getAttribute("login_type"));
//            if("wap_wx_login".equals(logintype)){
//            	resMap.put("login_name",cuser.getWxNickname());
//            }else{
//            	resMap.put("login_name",cuser.getUsername());
//            }
//            
//        }
//        params.clear();
//        params.put("beginTime", new Date());
//        params.put("endTime", new Date());
//        List groups = this.groupService.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
//        if(groups.size() > 0){
//            params.clear();
//            params.put("gg_status", Integer.valueOf(1));
//            params.put("gg_recommend", Integer.valueOf(1));
//            params.put("group_id", ((Group)groups.get(0)).getId());
//            List ggs = this.groupGoodsService.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.gg_recommend=:gg_recommend and obj.group.id=:group_id order by obj.gg_recommend_time desc", params, 0, 1);
//            if(ggs.size() > 0)
//            	resMap.put("group", ggs.get(0));
//        }
//        params.clear();
//        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
//        params.put("bg_status", Integer.valueOf(1));
//        List bgs = this.bargainGoodsService.query("select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", params, 0, 5);
        //特价商品买就送商品
//        resMap.put("bgs", bgs);
        params.clear();
        params.put("d_status", Integer.valueOf(1));
        params.put("d_begin_time", new Date());
        params.put("d_end_time", new Date());
        List dgs = this.deliveryGoodsService.query("select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc", params, 0, 3);
        //买就送商品
//        resMap.put("dgs", dgs);
        List msgs = new ArrayList();
        if(SecurityUserHolder.getCurrentUser() != null){
            params.clear();
            params.put("status", Integer.valueOf(0));
            params.put("reply_status", Integer.valueOf(1));
            params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
            params.put("to_user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService.query("select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ", params, -1, -1);
        }
        Store store = null;
        if(SecurityUserHolder.getCurrentUser() != null)
            store = this.storeService.getObjByProperty("user.id", SecurityUserHolder.getCurrentUser().getId());
//        resMap.put("store", store);
//        resMap.put("navTools", this.navTools);
//        resMap.put("msgs", msgs);
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if(SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        params.clear();
//        if(cookies != null){
//            for(Cookie cookie : cookies){
//                if(cookie.getName().equals("cart_session_id")){
//                    cart_session_id = CommUtil.null2String(CookitTools.GetCookitByType(request, "cart_session_id"));
//                }
//            }
//        }
        if(user != null){
            if(!cart_session_id.equals("")){
                if(user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                                        params, -1, -1);
                    for(StoreCart sc : store_cookie_cart){
                        for(GoodsCart gc : ((StoreCart)sc).getGcs()){
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(((StoreCart)sc).getId());
                    }
                }
                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }
        }else if(!cart_session_id.equals("")){
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);
        }
        for(StoreCart sc : user_cart){
            boolean sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                }
            }
            if(sc_add)
                cart.add(sc);
        }
        boolean sc_add;
        for(StoreCart sc : cookie_cart){
            sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                    for(GoodsCart gc : sc.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc.getId());
                }
            }
            if(sc_add){
                cart.add(sc);
            }
        }
        if(cart != null){
            for(StoreCart sc : cart){
                if(sc != null){
                    list.addAll(sc.getGcs());
                }
            }
        }
        float total_price = 0.0F;
        for(GoodsCart gc : list){
            Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if(CommUtil.null2String(gc.getCart_type()).equals("combin"))
                total_price = CommUtil.null2Float(goods.getCombin_price());
           else{
                total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(Float.valueOf(gc.getCount()), goods.getGoods_current_price()))) + total_price;
            }
        }        
//        resMap.put("total_price", Float.valueOf(total_price));
//        resMap.put("cart", list);    
        
        //首页楼层数据
        params.put("gf_display", Boolean.valueOf(true));
        List floors = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc", params, -1, -1);
//        resMap.put("floors", floors);
        List floorsList=new ArrayList();
        List floorsChilds=new ArrayList();   
        if(floors!=null&&floors.size()>0) {
        	GoodsFloor goodsFloor=null;
        	for(int i=0;i<floors.size();i++) {
        		goodsFloor=(GoodsFloor)floors.get(i);
        		if(goodsFloor!=null) {
        			floorsChilds=new ArrayList();
        			GoodsFloor floor=goodsFloor.getChilds().get(0);
        			if(((GoodsFloor)floors.get(i)).getChilds()!=null&&null!=((GoodsFloor)floors.get(i)).getChilds().get(0)) {
        				String gf_gc_goods=((GoodsFloor)((GoodsFloor)floors.get(i)).getChilds().get(0)).getGf_gc_goods();
        				if(gf_gc_goods!=null) {
        					floorsChilds=gf_tools.generic_goods(gf_gc_goods);
        				}
        				if(floorsChilds!=null&&floorsChilds.size()>0) {
        					floorsList.add(floorsChilds);
        				}
        			}
        		}
        	}     
        }       
        //首页幻灯图片
        List accList=new ArrayList();
        AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(262157));
        Map accMap=new HashMap();
        for(int i=0;i<ap.getAdvs().size();i++){
        	accMap=new HashMap();
        	 accMap.put("path", ap.getAdvs().get(i).getAd_acc().getPath());
        	 accMap.put("name", ap.getAdvs().get(i).getAd_acc().getName());
        	 accList.add(accMap);        	 
        }
        resMap.put("accList", accList);
        //首页楼层数据结束
//        resMap.put("url", CommUtil.getURL(request));
//        params.put("gf_display", Boolean.valueOf(true));
        Map<String, Object> map = new HashMap<String, Object>();
        CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);
        String loadimg = map.get("imageWebServer") + "/resources/style/common/images/loader.gif";
        String errorimg = map.get("webPath") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");
        resMap.put("url", CommUtil.getURL(request));
        resMap.put("map", map);
        resMap.put("loadimg", loadimg);
        resMap.put("errorimg", errorimg);       
        resMap.put("floorsList",floorsList);
        String[] propertys={"group","accList","map","loadimg","errorimg","floorsList","goods_main_photo"};
        WxCommonUtil.printObjJsonAll(resMap, response,propertys,false,false,false);
        
//        WxCommonUtil.printObjJson(resMap, response);
    }
    

    /**
     * wap首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/wap/index.htm" })
    public ModelAndView wapindex(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("wap/index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        //销毁cookit
        CookitTools.UpdateCookitByType(request, response, "wemalls_view_type", "wap", 24 * 60 * 60 * 30);
        if(!"wap".equals(CookitTools.GetCookitByType(request, "wemalls_view_type"))){
        	Cookie cooki = new Cookie("wemalls_view_type", "wap");
        	cooki.setMaxAge(24 * 60 * 60 * 30);
        	cooki.setPath("/");
        	response.addCookie(cooki);
        }
        //设置为wap访问
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gcs", gcs);
        params.clear();
        params.put("audit", Integer.valueOf(1));
        params.put("recommend", Boolean.valueOf(true));
        List gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence", params, -1, -1);
        mv.addObject("gbs", gbs);
        params.clear();
        List img_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1);
        mv.addObject("img_partners", img_partners);
        List text_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1);
        mv.addObject("text_partners", text_partners);
        params.clear();
        params.put("mark", "news");
        List acs = this.articleClassService.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark order by obj.sequence asc", params, 0, 9);
        mv.addObject("acs", acs);
        params.clear();
//        params.put("store_recommend", Boolean.valueOf(true));
        params.put("goods_status", Integer.valueOf(0));
        List store_reommend_goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 999999);
//        List store_reommend_goods = new ArrayList();
//        int max = store_reommend_goods_list.size() >= 21 ? 20 : store_reommend_goods_list.size() - 1;
//        for(int i = 0; i <= max; i++){
//            store_reommend_goods.add((Goods)store_reommend_goods_list.get(i));
//        }
        mv.addObject("store_reommend_goods", store_reommend_goods_list);

        mv.addObject("store_reommend_goods_count", Double.valueOf(Math.ceil(CommUtil.div(Integer.valueOf(store_reommend_goods_list.size()), Integer.valueOf(5)))));
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
        if(SecurityUserHolder.getCurrentUser() != null){
        	User cuser=this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            mv.addObject("user", cuser);
            String logintype=String.valueOf(request.getSession(false).getAttribute("login_type"));
            if("wap_wx_login".equals(logintype)){
            	mv.addObject("login_name",cuser.getWxNickname());
            }else{
            	mv.addObject("login_name",cuser.getUsername());
            }
            
        }
        params.clear();
        params.put("beginTime", new Date());
        params.put("endTime", new Date());
        List groups = this.groupService.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
        if(groups.size() > 0){
            params.clear();
            params.put("gg_status", Integer.valueOf(1));
            params.put("gg_recommend", Integer.valueOf(1));
            params.put("group_id", ((Group)groups.get(0)).getId());
            List ggs = this.groupGoodsService.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.gg_recommend=:gg_recommend and obj.group.id=:group_id order by obj.gg_recommend_time desc", params, 0, 1);
            if(ggs.size() > 0)
                mv.addObject("group", ggs.get(0));
        }
        params.clear();
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
        params.put("bg_status", Integer.valueOf(1));
        List bgs = this.bargainGoodsService.query("select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", params, 0, 5);
        mv.addObject("bgs", bgs);
        params.clear();
        params.put("d_status", Integer.valueOf(1));
        params.put("d_begin_time", new Date());
        params.put("d_end_time", new Date());
        List dgs = this.deliveryGoodsService.query("select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc", params, 0, 3);
        mv.addObject("dgs", dgs);

        List msgs = new ArrayList();
        if(SecurityUserHolder.getCurrentUser() != null){
            params.clear();
            params.put("status", Integer.valueOf(0));
            params.put("reply_status", Integer.valueOf(1));
            params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
            params.put("to_user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService.query("select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ", params, -1, -1);
        }
        Store store = null;
        if(SecurityUserHolder.getCurrentUser() != null)
            store = this.storeService.getObjByProperty("user.id", SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgs", msgs);
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if(SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        params.clear();
//        if(cookies != null){
//            for(Cookie cookie : cookies){
//                if(cookie.getName().equals("cart_session_id")){
                    cart_session_id = CommUtil.null2String(CookitTools.GetCookitByType(request, "cart_session_id"));
//                }
//            }
//        }
        if(user != null){
            if(!cart_session_id.equals("")){
                if(user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                                        params, -1, -1);
                    for(StoreCart sc : store_cookie_cart){
                        for(GoodsCart gc : ((StoreCart)sc).getGcs()){
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(((StoreCart)sc).getId());
                    }
                }

                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }

        }else if(!cart_session_id.equals("")){
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);
        }

        for(StoreCart sc : user_cart){
            boolean sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                }
            }
            if(sc_add)
                cart.add(sc);
        }
        boolean sc_add;
        for(StoreCart sc : cookie_cart){
            sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                    for(GoodsCart gc : sc.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc.getId());
                }
            }
            if(sc_add){
                cart.add(sc);
            }
        }
        if(cart != null){
            for(StoreCart sc : cart){
                if(sc != null){
                    list.addAll(sc.getGcs());
                }
            }
        }
        float total_price = 0.0F;
        for(GoodsCart gc : list){
            Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if(CommUtil.null2String(gc.getCart_type()).equals("combin"))
                total_price = CommUtil.null2Float(goods.getCombin_price());
           else{
                total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(Float.valueOf(gc.getCount()), goods.getGoods_current_price()))) + total_price;
            }
        }
        mv.addObject("total_price", Float.valueOf(total_price));
        mv.addObject("cart", list);
        return mv;
    }
    
    /**
     * wap首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/android/index.htm" })
    public ModelAndView androidindex(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("android/index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        //销毁cookit
        CookitTools.UpdateCookitByType(request, response, "wemalls_view_type", "android", 24 * 60 * 60 * 30);
        if(!"android".equals(CookitTools.GetCookitByType(request, "wemalls_view_type"))){
        	Cookie cooki = new Cookie("wemalls_view_type", "android");
        	cooki.setMaxAge(24 * 60 * 60 * 30);
        	cooki.setPath("/");
        	response.addCookie(cooki);
        }
        //设置为wap访问
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gcs", gcs);
        params.clear();
        params.put("audit", Integer.valueOf(1));
        params.put("recommend", Boolean.valueOf(true));
        List gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence", params, -1, -1);
        mv.addObject("gbs", gbs);
        params.clear();
        List img_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1);
        mv.addObject("img_partners", img_partners);
        List text_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1);
        mv.addObject("text_partners", text_partners);
        params.clear();
        params.put("mark", "news");
        List acs = this.articleClassService.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark order by obj.sequence asc", params, 0, 9);
        mv.addObject("acs", acs);
        params.clear();
//        params.put("store_recommend", Boolean.valueOf(true));
        params.put("goods_status", Integer.valueOf(0));
        List store_reommend_goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 999999);
//        List store_reommend_goods = new ArrayList();
//        int max = store_reommend_goods_list.size() >= 21 ? 20 : store_reommend_goods_list.size() - 1;
//        for(int i = 0; i <= max; i++){
//            store_reommend_goods.add((Goods)store_reommend_goods_list.get(i));
//        }
        mv.addObject("store_reommend_goods", store_reommend_goods_list);

        mv.addObject("store_reommend_goods_count", Double.valueOf(Math.ceil(CommUtil.div(Integer.valueOf(store_reommend_goods_list.size()), Integer.valueOf(5)))));
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
        if(SecurityUserHolder.getCurrentUser() != null){
        	User cuser=this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            mv.addObject("user", cuser);
            String logintype=String.valueOf(request.getSession(false).getAttribute("login_type"));
            if("wap_wx_login".equals(logintype)){
            	mv.addObject("login_name",cuser.getWxNickname());
            }else{
            	mv.addObject("login_name",cuser.getUsername());
            }
            
        }
        params.clear();
        params.put("beginTime", new Date());
        params.put("endTime", new Date());
        List groups = this.groupService.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
        if(groups.size() > 0){
            params.clear();
            params.put("gg_status", Integer.valueOf(1));
            params.put("gg_recommend", Integer.valueOf(1));
            params.put("group_id", ((Group)groups.get(0)).getId());
            List ggs = this.groupGoodsService.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.gg_recommend=:gg_recommend and obj.group.id=:group_id order by obj.gg_recommend_time desc", params, 0, 1);
            if(ggs.size() > 0)
                mv.addObject("group", ggs.get(0));
        }
        params.clear();
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
        params.put("bg_status", Integer.valueOf(1));
        List bgs = this.bargainGoodsService.query("select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", params, 0, 5);
        mv.addObject("bgs", bgs);
        params.clear();
        params.put("d_status", Integer.valueOf(1));
        params.put("d_begin_time", new Date());
        params.put("d_end_time", new Date());
        List dgs = this.deliveryGoodsService.query("select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc", params, 0, 3);
        mv.addObject("dgs", dgs);

        List msgs = new ArrayList();
        if(SecurityUserHolder.getCurrentUser() != null){
            params.clear();
            params.put("status", Integer.valueOf(0));
            params.put("reply_status", Integer.valueOf(1));
            params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
            params.put("to_user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService.query("select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ", params, -1, -1);
        }
        Store store = null;
        if(SecurityUserHolder.getCurrentUser() != null)
            store = this.storeService.getObjByProperty("user.id", SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgs", msgs);
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if(SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        params.clear();
//        if(cookies != null){
//            for(Cookie cookie : cookies){
//                if(cookie.getName().equals("cart_session_id")){
                    cart_session_id = CommUtil.null2String(CookitTools.GetCookitByType(request, "cart_session_id"));
//                }
//            }
//        }
        if(user != null){
            if(!cart_session_id.equals("")){
                if(user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                                        params, -1, -1);
                    for(StoreCart sc : store_cookie_cart){
                        for(GoodsCart gc : ((StoreCart)sc).getGcs()){
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(((StoreCart)sc).getId());
                    }
                }

                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }

        }else if(!cart_session_id.equals("")){
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);
        }

        for(StoreCart sc : user_cart){
            boolean sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                }
            }
            if(sc_add)
                cart.add(sc);
        }
        boolean sc_add;
        for(StoreCart sc : cookie_cart){
            sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                    for(GoodsCart gc : sc.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc.getId());
                }
            }
            if(sc_add){
                cart.add(sc);
            }
        }
        if(cart != null){
            for(StoreCart sc : cart){
                if(sc != null){
                    list.addAll(sc.getGcs());
                }
            }
        }
        float total_price = 0.0F;
        for(GoodsCart gc : list){
            Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if(CommUtil.null2String(gc.getCart_type()).equals("combin"))
                total_price = CommUtil.null2Float(goods.getCombin_price());
           else{
                total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(Float.valueOf(gc.getCount()), goods.getGoods_current_price()))) + total_price;
            }
        }
        mv.addObject("total_price", Float.valueOf(total_price));
        mv.addObject("cart", list);
        return mv;
    }
    
    /**
     * wap首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/ios/index.htm" })
    public ModelAndView iosindex(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("ios/index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        //销毁cookit
        CookitTools.UpdateCookitByType(request, response, "wemalls_view_type", "ios", 24 * 60 * 60 * 30);
        if(!"ios".equals(CookitTools.GetCookitByType(request, "wemalls_view_type"))){
        	Cookie cooki = new Cookie("wemalls_view_type", "ios");
        	cooki.setMaxAge(24 * 60 * 60 * 30);
        	cooki.setPath("/");
        	response.addCookie(cooki);
        }
        //设置为wap访问
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gcs", gcs);
        params.clear();
        params.put("audit", Integer.valueOf(1));
        params.put("recommend", Boolean.valueOf(true));
        List gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence", params, -1, -1);
        mv.addObject("gbs", gbs);
        params.clear();
        List img_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1);
        mv.addObject("img_partners", img_partners);
        List text_partners = this.partnerService.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1);
        mv.addObject("text_partners", text_partners);
        params.clear();
        params.put("mark", "news");
        List acs = this.articleClassService.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark order by obj.sequence asc", params, 0, 9);
        mv.addObject("acs", acs);
        params.clear();
//        params.put("store_recommend", Boolean.valueOf(true));
        params.put("goods_status", Integer.valueOf(0));
        List store_reommend_goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 999999);
//        List store_reommend_goods = new ArrayList();
//        int max = store_reommend_goods_list.size() >= 21 ? 20 : store_reommend_goods_list.size() - 1;
//        for(int i = 0; i <= max; i++){
//            store_reommend_goods.add((Goods)store_reommend_goods_list.get(i));
//        }
        mv.addObject("store_reommend_goods", store_reommend_goods_list);

        mv.addObject("store_reommend_goods_count", Double.valueOf(Math.ceil(CommUtil.div(Integer.valueOf(store_reommend_goods_list.size()), Integer.valueOf(5)))));
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
        if(SecurityUserHolder.getCurrentUser() != null){
        	User cuser=this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            mv.addObject("user", cuser);
            String logintype=String.valueOf(request.getSession(false).getAttribute("login_type"));
            if("wap_wx_login".equals(logintype)){
            	mv.addObject("login_name",cuser.getWxNickname());
            }else{
            	mv.addObject("login_name",cuser.getUsername());
            }
            
        }
        params.clear();
        params.put("beginTime", new Date());
        params.put("endTime", new Date());
        List groups = this.groupService.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
        if(groups.size() > 0){
            params.clear();
            params.put("gg_status", Integer.valueOf(1));
            params.put("gg_recommend", Integer.valueOf(1));
            params.put("group_id", ((Group)groups.get(0)).getId());
            List ggs = this.groupGoodsService.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.gg_recommend=:gg_recommend and obj.group.id=:group_id order by obj.gg_recommend_time desc", params, 0, 1);
            if(ggs.size() > 0)
                mv.addObject("group", ggs.get(0));
        }
        params.clear();
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
        params.put("bg_status", Integer.valueOf(1));
        List bgs = this.bargainGoodsService.query("select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", params, 0, 5);
        mv.addObject("bgs", bgs);
        params.clear();
        params.put("d_status", Integer.valueOf(1));
        params.put("d_begin_time", new Date());
        params.put("d_end_time", new Date());
        List dgs = this.deliveryGoodsService.query("select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc", params, 0, 3);
        mv.addObject("dgs", dgs);

        List msgs = new ArrayList();
        if(SecurityUserHolder.getCurrentUser() != null){
            params.clear();
            params.put("status", Integer.valueOf(0));
            params.put("reply_status", Integer.valueOf(1));
            params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
            params.put("to_user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService.query("select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ", params, -1, -1);
        }
        Store store = null;
        if(SecurityUserHolder.getCurrentUser() != null)
            store = this.storeService.getObjByProperty("user.id", SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgs", msgs);
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if(SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        params.clear();
//        if(cookies != null){
//            for(Cookie cookie : cookies){
//                if(cookie.getName().equals("cart_session_id")){
                    cart_session_id = CommUtil.null2String(CookitTools.GetCookitByType(request, "cart_session_id"));
//                }
//            }
//        }
        if(user != null){
            if(!cart_session_id.equals("")){
                if(user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                                        params, -1, -1);
                    for(StoreCart sc : store_cookie_cart){
                        for(GoodsCart gc : ((StoreCart)sc).getGcs()){
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(((StoreCart)sc).getId());
                    }
                }

                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1);
            }

        }else if(!cart_session_id.equals("")){
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1);
        }

        for(StoreCart sc : user_cart){
            boolean sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                }
            }
            if(sc_add)
                cart.add(sc);
        }
        boolean sc_add;
        for(StoreCart sc : cookie_cart){
            sc_add = true;
            for(StoreCart sc1 : cart){
                if(sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                    for(GoodsCart gc : sc.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc.getId());
                }
            }
            if(sc_add){
                cart.add(sc);
            }
        }
        if(cart != null){
            for(StoreCart sc : cart){
                if(sc != null){
                    list.addAll(sc.getGcs());
                }
            }
        }
        float total_price = 0.0F;
        for(GoodsCart gc : list){
            Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if(CommUtil.null2String(gc.getCart_type()).equals("combin"))
                total_price = CommUtil.null2Float(goods.getCombin_price());
           else{
                total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(Float.valueOf(gc.getCount()), goods.getGoods_current_price()))) + total_price;
            }
        }
        mv.addObject("total_price", Float.valueOf(total_price));
        mv.addObject("cart", list);
        return mv;
    }

    public String showLoadFloorAjaxHtml(List lists, int i, String url, Map<String, Object> map){
        String img = null;
//        String loadimg = map.get("imageWebServer") + "/resources/style/common/images/loader.gif";
        String loadimg = map.get("imageWebServer") + "/resources/style/common/images/loader.gif";
        String errorimg = map.get("webPath") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");
        String goods_url = null;
        String goods_class_url = null;
        String child_goods_class_url = null;

        GoodsFloor floor = (GoodsFloor)lists.get(0);

        img = null;

        StringBuffer sb = new StringBuffer(1000);
        sb.append("<div class='floor " + floor.getGf_css() + "'>").append("<div class='floor_box' id='floor_" + i + "'>");
        sb.append("<div class='floor_menu'>").append("<div class='title'>").append("<div class='txt-type'>").append("<span>").append(i).append("</span>");
        sb.append("<h2 title='").append(floor.getGf_name()).append("'>").append(floor.getGf_name()).append("</h2></div></div><div ><ul class='flr_m_du'>");
        List<GoodsClass> gcs = this.gf_tools.generic_gf_gc(floor.getGf_gc_list());
        for(GoodsClass gc : gcs){
            goods_class_url = map.get("webPath") + "/store_goods_list_" + gc.getId() + ".htm";
            sb.append("<li>"
//            		+ "<h4><a href='").append(goods_class_url).append("'>").append(gc.getClassName()).append("</a></h4>"
            		+ "<p>");
            for(GoodsClass c_gc : gc.getChilds()){
                child_goods_class_url = map.get("webPath") + "/store_goods_list_" + c_gc.getId() + ".htm";
                sb.append("<span><a href='").append(child_goods_class_url).append("' target='_blank'>").append(c_gc.getClassName()).append("</a></span>");
            }
            sb.append("</p></li>");
        }
        sb.append("</ul><div class='flr_advertisment'>");
        //拼接左侧广告
        sb.append(gf_tools.generic_adv(url, floor.getGf_left_adv()));

        sb.append("</div></div></div><div class='floorclass'><ul class='floorul'>");

        int num = 0;
        for(GoodsFloor info : floor.getChilds()){
            num++;
            sb.append("<li ");
            if(num == 1){
                sb.append("class='this'");
            }
            sb.append("style='cursor:pointer;' id='").append(info.getId()).append("' store_gc='").append(floor.getId()).append("' >");
            sb.append(info.getGf_name()).append("<s></s></li>");
        }
        sb.append("</ul>");

        int count = 0;

        for(GoodsFloor info : floor.getChilds()){
            count++;
            sb.append("<div id='").append(info.getId()).append("' store_gc='").append(floor.getId()).append("' class='ftab'");
            if(count > 1){
                sb.append("style='display:none;'");
            }
            sb.append("><div class='ftabone'><div class='classpro'>");
            for(Goods goods : this.gf_tools.generic_goods(info.getGf_gc_goods())){
                if(goods != null){
                    if(goods.getGoods_main_photo() != null)
                        img = map.get("imageWebServer") + "/" + goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt();
                    else
                        img = map.get("imageWebServer") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");

                    goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";

                    if((Boolean)map.get("IsSecondDomainOpen")){
                        goods_url = "http://" + goods.getGoods_store().getStore_second_domain() + "." + map.get("domainPath") + "/goods_" + goods.getId() + ".htm";
                    }
                    sb.append("<div class='productone'><ul class='this'><li><span class='center_span'>");
                    sb.append("<p><a href='").append(goods_url).append("' target='_blank' ><img src='").append(img).append("' original='");
                    sb.append(img).append("' onerror=\"this.src=").append(errorimg).append(";\" /></a></p></span></li>");
                    sb.append("<li class='pronames'><a href='").append(goods_url).append("' target='_blank'>").append(goods.getGoods_name()).append("</a></li>");
                    sb.append("<li><span class=\"hui2\">市场价：</span><span class=\"through hui\">￥").append(goods.getGoods_price());
                    sb.append("</span></li><li><span class=\"hui2\">商城价：</span><strong class=\"red\">￥").append(goods.getGoods_current_price());
                    sb.append("</strong></li></ul></div>");
                }
            }
            sb.append("</div></div></div>");
        }
        sb.append("</div><div class='ranking'>");
        Map<String, Object> mmap = gf_tools.generic_goods_list(floor.getGf_list_goods());
        sb.append("<h1>").append(mmap.get("list_title")).append("</h1>");

        if(mmap.get("goods1") != null){
            Goods goods = (Goods)mmap.get("goods1");
            if(goods.getGoods_main_photo() != null)
                img = map.get("imageWebServer") + "/" + goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt();
            else
                img = map.get("imageWebServer") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");

            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";

            sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
            sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append(CommUtil.substring(goods.getGoods_name(), 12)).append("</a></strong></li><li class=\"rankmoney\">￥").append(goods.getGoods_current_price());
            sb.append("</li></ul>");
        }

        if(mmap.get("goods2") != null){
            Goods goods = (Goods)mmap.get("goods2");
            if(goods.getGoods_main_photo() != null)
                img = map.get("imageWebServer") + "/" + goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt();
            else
                img = map.get("imageWebServer") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");

            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";

            sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
            sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append(CommUtil.substring(goods.getGoods_name(), 12)).append("</a></strong></li><li class=\"rankmoney\">￥").append(goods.getGoods_current_price());
            sb.append("</li></ul>");
        }

        if(mmap.get("goods3") != null){
            Goods goods = (Goods)mmap.get("goods3");
            if(goods.getGoods_main_photo() != null)
                img = map.get("imageWebServer") + "/" + goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt();
            else
                img = map.get("imageWebServer") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");

            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";

            sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
            sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append(CommUtil.substring(goods.getGoods_name(), 12)).append("</a></strong></li><li class=\"rankmoney\">￥").append(goods.getGoods_current_price());
            sb.append("</li></ul>");
        }
        if(mmap.get("goods4") != null){
            Goods goods = (Goods)mmap.get("goods4");
            if(goods.getGoods_main_photo() != null)
                img = map.get("imageWebServer") + "/" + goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt();
            else
                img = map.get("imageWebServer") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");

            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";

            sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
            sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append(CommUtil.substring(goods.getGoods_name(), 12)).append("</a></strong></li><li class=\"rankmoney\">￥").append(goods.getGoods_current_price());
            sb.append("</li></ul>");
        }
        if(mmap.get("goods5") != null){
            Goods goods = (Goods)mmap.get("goods5");
            if(goods.getGoods_main_photo() != null)
                img = map.get("imageWebServer") + "/" + goods.getGoods_main_photo().getPath() + "/" + goods.getGoods_main_photo().getName() + "_small." + goods.getGoods_main_photo().getExt();
            else
                img = map.get("imageWebServer") + "/" + map.get("goodsImagePath") + "/" + map.get("goodsImageName");

            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";

            sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
            sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
            sb.append(CommUtil.substring(goods.getGoods_name(), 12)).append("</a></strong></li><li class=\"rankmoney\">￥").append(goods.getGoods_current_price());
            sb.append("</li></ul>");
        }

        sb.append(""
//        + "<ul class=\"rankul2\">");
//        if(mmap.get("goods4") != null){
//            Goods goods = (Goods)mmap.get("goods4");
//            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";
//            sb.append("<li><a href='").append(goods_url).append("' target='_blank'>").append(CommUtil.substring(goods.getGoods_name(), 14)).append("</a></li>");
//        }
//        if(mmap.get("goods5") != null){
//            Goods goods = (Goods)mmap.get("goods5");
//            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";
//            sb.append("<li><a href='").append(goods_url).append("' target='_blank'>").append(CommUtil.substring(goods.getGoods_name(), 14)).append("</a></li>");
//        }
//        if(mmap.get("goods6") != null){
//            Goods goods = (Goods)mmap.get("goods6");
//            goods_url = map.get("webPath") + "/goods_" + goods.getId() + ".htm";
//            sb.append("<li><a href='").append(goods_url).append("' target='_blank'>").append(CommUtil.substring(goods.getGoods_name(), 14)).append("</a></li>");
//        }
//        sb.append("</ul>"
//        		+ "<div class=\"rank_advertisment\">");
//        //拼接右侧广告
//        sb.append(this.gf_tools.generic_adv(url, floor.getGf_right_adv()));
//        sb.append("</div>"
        		+ "</div></div><div ><span class=\"fl_brand_sp\"></span><span class=\"flr_sp_brand\">");

        for(GoodsBrand brand : this.gf_tools.generic_brand(floor.getGf_brand_list())){
            String brand_url = map.get("webPath") + "/brand_goods_" + brand.getId() + ".htm";
            String brand_img = map.get("imageWebServer") + "/" + brand.getBrandLogo().getPath() + "/" + brand.getBrandLogo().getName();
            sb.append("<a href='").append(brand_url).append("' target='_blank'><img src='").append(brand_img).append("' width='98' height='35' /></a>");
        }
        sb.append("</span></div></div>");

        return sb.toString();
    }
}
