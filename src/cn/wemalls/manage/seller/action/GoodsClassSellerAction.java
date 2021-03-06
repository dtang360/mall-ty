package cn.wemalls.manage.seller.action;

import cn.wemalls.core.annotation.SecurityMapping;
import cn.wemalls.core.domain.virtual.SysMap;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.security.support.SecurityUserHolder;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.WebForm;
import cn.wemalls.foundation.domain.UserGoodsClass;
import cn.wemalls.foundation.domain.query.UserGoodsClassQueryObject;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.foundation.service.IUserConfigService;
import cn.wemalls.foundation.service.IUserGoodsClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卖家商品分类控制器
 */
@Controller
public class GoodsClassSellerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IUserGoodsClassService usergoodsclassService;

    @SecurityMapping(display = false, rsequence = 0, title = "卖家商品分类列表", value = "/seller/usergoodsclass_list.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({"/seller/usergoodsclass_list.htm"})
    public ModelAndView usergoodsclass_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/usergoodsclass_list.html",
            this.configService.getSysConfig(), this.userConfigService
            .getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        UserGoodsClassQueryObject qo = new UserGoodsClassQueryObject(
            currentPage, mv, orderBy, orderType);
        qo.setPageSize(Integer.valueOf(20));
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, UserGoodsClass.class, mv);
        qo.addQuery("obj.parent.id is null", null);
        qo.addQuery("obj.user.id",
                    new SysMap("user_id",
                               SecurityUserHolder.getCurrentUser().getId()), "=");
        qo.setOrderBy("sequence");
        qo.setOrderType("asc");
        IPageList pList = this.usergoodsclassService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url +
                                            "/seller/usergoodsclass_list.htm", "", params, pList, mv);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家商品分类保存", value = "/seller/usergoodsclass_save.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({"/seller/usergoodsclass_save.htm"})
    public String usergoodsclass_save(HttpServletRequest request, HttpServletResponse response, String id, String pid){
        WebForm wf = new WebForm();
        UserGoodsClass usergoodsclass = null;
        if (id.equals("")){
            usergoodsclass = (UserGoodsClass)wf.toPo(request, UserGoodsClass.class);
            usergoodsclass.setAddTime(new Date());
        }else{
            UserGoodsClass obj = this.usergoodsclassService.getObjById(
                                     Long.valueOf(Long.parseLong(id)));
            usergoodsclass = (UserGoodsClass)wf.toPo(request, obj);
        }
        usergoodsclass.setUser(SecurityUserHolder.getCurrentUser());
        if (!pid.equals("")){
            UserGoodsClass parent = this.usergoodsclassService.getObjById(
                                        Long.valueOf(Long.parseLong(pid)));
            usergoodsclass.setParent(parent);
        }
        boolean ret = true;
        if (id.equals(""))
            ret = this.usergoodsclassService.save(usergoodsclass);
        else
            ret = this.usergoodsclassService.update(usergoodsclass);

        return "redirect:usergoodsclass_list.htm";
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家商品分类删除", value = "/seller/usergoodsclass_del.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({"/seller/usergoodsclass_del.htm"})
    public String usergoodsclass_del(HttpServletRequest request, String mulitId){
        String[] ids = mulitId.split(",");
        for (String id : ids){
            if (!id.equals("")){
                UserGoodsClass usergoodsclass = this.usergoodsclassService
                                                .getObjById(Long.valueOf(Long.parseLong(id)));
                this.usergoodsclassService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }

        return "redirect:usergoodsclass_list.htm";
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "卖家商品分类删除判断", value = "/seller/usergoodsclass_delable.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({"/seller/usergoodsclass_delable.htm"})
    public void usergoodsclass_delable(HttpServletRequest request,HttpServletResponse response, String mulitId){
        String[] ids = mulitId.split(",");
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
	        for (String id : ids){
	            if (!id.equals("")){
	                UserGoodsClass usergoodsclass = this.usergoodsclassService
	                                                .getObjById(Long.valueOf(Long.parseLong(id)));
	                if(usergoodsclass.getChilds()!=null&&usergoodsclass.getChilds().size()>0){
	                	writer.print("选择分类已关联子分类信息，无法删除！");
		            	return;
	                }
	                if(usergoodsclass.getGoods_list()!=null&&usergoodsclass.getGoods_list().size()>0){
	                	writer.print("选择分类已关联商品，无法删除！");
		            	return;
	                }
	            }
	        }
	        writer.print("success");
	    } catch (Exception e){
	        e.printStackTrace();
	    }
    }

    @SecurityMapping(display = false, rsequence = 0, title = "新增卖家商品分类", value = "/seller/address_add.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({"/seller/usergoodsclass_add.htm"})
    public ModelAndView usergoodsclass_add(HttpServletRequest request, HttpServletResponse response, String currentPage, String pid){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/usergoodsclass_add.html",
            this.configService.getSysConfig(), this.userConfigService
            .getUserConfig(), 0, request, response);
        Map map = new HashMap();
        map.put("uid", SecurityUserHolder.getCurrentUser().getId());
        List ugcs = this.usergoodsclassService
                    .query(
                        "select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id = :uid order by obj.sequence asc",
                        map, -1, -1);
        if (!CommUtil.null2String(pid).equals("")){
            UserGoodsClass parent = this.usergoodsclassService
                                    .getObjById(CommUtil.null2Long(pid));
            UserGoodsClass obj = new UserGoodsClass();
            obj.setParent(parent);
            mv.addObject("obj", obj);
        }
        mv.addObject("ugcs", ugcs);
        mv.addObject("currentPage", currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "编辑卖家商品分类", value = "/seller/usergoodsclass_edit.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({"/seller/usergoodsclass_edit.htm"})
    public ModelAndView usergoodsclass_edit(HttpServletRequest request, HttpServletResponse response, String currentPage, String id){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/usergoodsclass_add.html",
            this.configService.getSysConfig(), this.userConfigService
            .getUserConfig(), 0, request, response);
        List ugcs = this.usergoodsclassService
                    .query(
                        "select obj from UserGoodsClass obj where obj.parent.id is null order by obj.sequence asc",
                        null, -1, -1);
        UserGoodsClass obj = this.usergoodsclassService.getObjById(
                                 CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("ugcs", ugcs);
        mv.addObject("currentPage", currentPage);

        return mv;
    }
}




