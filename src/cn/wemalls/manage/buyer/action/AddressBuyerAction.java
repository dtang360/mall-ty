package cn.wemalls.manage.buyer.action;

import cn.wemalls.core.annotation.SecurityMapping;
import cn.wemalls.core.domain.virtual.SysMap;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.security.support.SecurityUserHolder;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.WebForm;
import cn.wemalls.core.tools.WxCommonUtil;
import cn.wemalls.core.tools.database.DatabaseTools;
import cn.wemalls.foundation.domain.Address;
import cn.wemalls.foundation.domain.Area;
import cn.wemalls.foundation.domain.Store;
import cn.wemalls.foundation.domain.query.AddressQueryObject;
import cn.wemalls.foundation.domain.query.StoreQueryObject;
import cn.wemalls.foundation.service.IAddressService;
import cn.wemalls.foundation.service.IAreaService;
import cn.wemalls.foundation.service.IStoreService;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.foundation.service.IUserConfigService;
import cn.wemalls.view.web.tools.CookitTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 买家收货地址控制器
 */
@Controller
public class AddressBuyerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IAreaService areaService;

    @Autowired
    private DatabaseTools databaseTools;
    
    @Autowired
    private IStoreService storeService;

    @SecurityMapping(display = false, rsequence = 0, title = "收货地址列表", value = "/buyer/address.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/address.htm"})
    public ModelAndView address(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("user/default/usercenter/address.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 0, request, response);
//        String wemalls_view_type = CookitTools.Get_View_Type(request);
        String wemalls_view_type =CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/address.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/address.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/address.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
        qo.addQuery("obj.id", new SysMap("id",Long.parseLong("0")),"<>");
        IPageList pList = this.addressService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/address.htm", "", params, pList, mv);
        List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);

        return mv;
    }
    
    
    
    @SecurityMapping(display = false, rsequence = 0, title = "收货地址列表", value = "/buyer/address.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/wxapplet/buyer/address.htm"})
    public void wxapplet_address(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
    	Map resMap=new HashMap();
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        AddressQueryObject qo = new AddressQueryObject(currentPage, resMap, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
        qo.addQuery("obj.id", new SysMap("id",Long.parseLong("0")),"<>");
        IPageList pList = this.addressService.list(qo);
//        CommUtil.saveIPageList2ModelAndView(url + "/buyer/address.htm", "", params, pList, mv);
        resMap.put("objsList", pList.getResult());
        List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
        resMap.put("areas", areas);
        
        String[] propertys={"objsList","Address","area","areas","Area"};
        WxCommonUtil.printObjJsonAll(resMap, response,propertys,false,false,false);
    }
    
    /**
     * 新增收货地址页面
     * @param request
     * @param response
     * @param currentPage
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title = "新增收货地址", value = "/buyer/address_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/address_add.htm"})
    public ModelAndView address_add(HttpServletRequest request, HttpServletResponse response, String currentPage){
        ModelAndView mv = new JModelAndView("user/default/usercenter/address_add.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 0, request, response);
//        String wemalls_view_type = CookitTools.Get_View_Type(request);
        String wemalls_view_type =CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/address_add.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/address_add.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/address_add.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        List areas = this.areaService.query("select obj from Area obj where obj.level=0 and obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        mv.addObject("currentPage", currentPage);
        //店铺名称
        StoreQueryObject sqo=new StoreQueryObject();
        sqo.setPageSize(99999);
        IPageList pList = this.storeService.list(sqo);
        mv.addObject("objs", pList.getResult());

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "新增收货地址", value = "/buyer/address_edit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/address_edit.htm"})
    public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView("user/default/usercenter/address_add.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 0, request, response);
//        String wemalls_view_type = CookitTools.Get_View_Type(request);
        String wemalls_view_type =CookitTools.Get_View_Type(request);
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("wap"))){
            mv = new JModelAndView("wap/address_add.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("android"))){
            mv = new JModelAndView("android/address_add.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        if((wemalls_view_type != null) && (!wemalls_view_type.equals("")) && (wemalls_view_type.equals("ios"))){
            mv = new JModelAndView("ios/address_add.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
        Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
        //店铺名称
        StoreQueryObject sqo=new StoreQueryObject();
        sqo.setPageSize(99999);
        IPageList pList = this.storeService.list(sqo);
        mv.addObject("objs", pList.getResult());
        
        mv.addObject("obj", obj);
        mv.addObject("areas", areas);
        mv.addObject("currentPage", currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "收货地址保存", value = "/buyer/address_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/address_save.htm"})
    public String address_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id,String def_store_id, String currentPage){
        WebForm wf = new WebForm();
        Address address = null;
        if (id.equals("")){
            address = (Address)wf.toPo(request, Address.class);
            address.setAddTime(new Date());
        }else{
            Address obj = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
            address = (Address)wf.toPo(request, obj);
        }
        address.setUser(SecurityUserHolder.getCurrentUser());
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        Store store = this.storeService.getObjById(CommUtil.null2Long(def_store_id));
        address.setArea(area);
        address.setDef_store(store);
        if (id.equals(""))
            this.addressService.save(address);
        else
            this.addressService.update(address);

        return "redirect:address.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "收货地址删除", value = "/buyer/address_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/address_del.htm"})
    public String address_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage){
        String[] ids = mulitId.split(",");
        for (String id : ids){
            if (!id.equals("")){
                Address address = this.addressService.getObjById(
                                      Long.valueOf(Long.parseLong(id)));
                this.addressService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }

        return "redirect:address.htm?currentPage=" + currentPage;
    }
}




