package cn.wemalls.manage.admin.action;

import cn.wemalls.core.annotation.SecurityMapping;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.WebForm;
import cn.wemalls.core.tools.database.DatabaseTools;
import cn.wemalls.foundation.domain.*;
import cn.wemalls.foundation.domain.query.StoreQueryObject;
import cn.wemalls.foundation.service.*;
import cn.wemalls.manage.admin.tools.AreaManageTools;
import cn.wemalls.manage.admin.tools.StoreTools;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 店铺管理控制器
 */
@Controller
public class StorePrinterAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private IStoreGradeService storeGradeService;
    
    @Autowired
    private IStorePrinterService storePrinterService;

    @Autowired
    private IAreaService areaService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IConsultService consultService;

    @Autowired
    private AreaManageTools areaManageTools;

    @Autowired
    private StoreTools storeTools;

    @Autowired
    private DatabaseTools databaseTools;

    @Autowired
    private ITemplateService templateService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IStoreGradeLogService storeGradeLogService;

    @Autowired
    private IEvaluateService evaluateService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private IOrderFormLogService orderFormLogService;

    @Autowired
    private IAccessoryService accessoryService;

    @Autowired
    private IAlbumService albumService;

    @SecurityMapping(display = false, rsequence = 0, title = "店铺列表", value = "/admin/store_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_printer_list.htm"})
    public ModelAndView store_printer_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
    	ModelAndView mv = new JModelAndView("admin/blue/store_printer_list.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy,
                orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Store.class, mv);
        IPageList pList = this.storeService.list(qo);
        List rlist=pList.getResult();
        StorePrinter sp=new StorePrinter();
        if(rlist!=null&&rlist.size()>0){
        	for(int i=0;i<rlist.size();i++){
        		sp=new StorePrinter();
//        		sp=storePrinterService.getObjById(((Store)rlist.get(i)).getId());
        		Map pas = new HashMap();
        		pas.put("storeId",((Store)rlist.get(i)).getId());
                List scs = storePrinterService.query("select obj from StorePrinter obj where obj.store.id=:storeId ",pas, -1, -1);
//             List areas = this.areaService.query(
//                              "select obj from Area obj where obj.parent.id is null",
//                              null, -1, -1);
             if(scs!=null&&scs.size()>0){
             	((Store)rlist.get(i)).setStorePrinter((StorePrinter)scs.get(0));
             }
             
//        		if(sp!=null){
//        			
//        		}
        	}
        	pList.setResult(rlist);
        }
        CommUtil.saveIPageList2ModelAndView(url + "/admin/store_printer_list.htm", "",
                                            params, pList, mv);
        List grades = this.storePrinterService.query(
                          "select obj from StorePrinter obj order by obj.id asc",
                          null, -1, -1);
        mv.addObject("grades", grades);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺添加1", value = "/admin/store_add.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_printer_add.htm"})
    public ModelAndView store_printer_add(HttpServletRequest request, HttpServletResponse response, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/store_add.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        mv.addObject("currentPage", currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺编辑", value = "/admin/store_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_printer_edit.htm"})
    public ModelAndView store_printer_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/store_printer_edit.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        if ((id != null) && (!id.equals(""))){
            Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
            Map params = new HashMap();
            params.put("storeId",Long.valueOf(Long.parseLong(id)));
            List scs = this.storePrinterService
                       .query(
                           "select obj from StorePrinter obj where obj.store.id=:storeId ",
                           params, -1, -1);
//            List areas = this.areaService.query(
//                             "select obj from Area obj where obj.parent.id is null",
//                             null, -1, -1);
            mv.addObject("storeId", id);
            if(scs!=null&&scs.size()>0){
            	mv.addObject("obj", scs.get(0));
            }
            mv.addObject("objs", store);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
//            if (store.getArea() != null){
//                mv.addObject("area_info", this.areaManageTools
//                             .generic_area_info(store.getArea()));
//            }
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺保存", value = "/admin/store_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_printer_save.htm"})
    public ModelAndView store_printer_save(HttpServletRequest request, HttpServletResponse response,String store_id, String id, String area_id, String sc_id, String grade_id, String user_id, String store_status, String currentPage, String cmd, String list_url, String add_url)
    throws Exception {
        WebForm wf = new WebForm();
        StorePrinter st = null;
       Store store=storeService.getObjById(Long.valueOf(Long.parseLong(store_id)));
        if (id.equals("")){
        	st = (StorePrinter)wf.toPo(request, StorePrinter.class);
        	st.setAddTime(new Date());
        }else{
        	StorePrinter obj = this.storePrinterService.getObjById(Long.valueOf(Long.parseLong(id)));
        	st = (StorePrinter)wf.toPo(request, obj);
        }
        st.setStore(store);
//        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
//        store.setArea(area);
//        StoreClass sc = this.storeClassService
//                        .getObjById(Long.valueOf(Long.parseLong(sc_id)));
//        st.setSc(sc);
//        st.setTemplate("default");
//        if ((grade_id != null) && (!grade_id.equals(""))){
//            store.setGrade(this.storeGradeService.getObjById(
//                               Long.valueOf(Long.parseLong(grade_id))));
//        }
//        if ((store_status != null) && (!store_status.equals("")))
//        	st.setStore_status(CommUtil.null2Int(store_status));
//        else
//        	st.setStore_status(2);
//        if (st.isStore_recommend())
//        	st.setStore_recommend_time(new Date());
//        else
//        	st.setStore_recommend_time(null);
        if (id.equals(""))
            this.storePrinterService.save(st);
        else
            this.storePrinterService.update(st);
        if ((user_id != null) && (!user_id.equals(""))){
            User user = this.userService.getObjById(Long.valueOf(Long.parseLong(user_id)));
//            user.setStore(st);
            user.setUserRole("BUYER_SELLER");
            Map params = new HashMap();
            params.put("type", "SELLER");
            List roles = this.roleService.query(
                             "select obj from Role obj where obj.type=:type", params,
                             -1, -1);
            user.getRoles().addAll(roles);
            this.userService.update(user);
        }
//        if ((!id.equals("")) && (store.getStore_status() == 3)){
//            send_site_msg(request, "msg_toseller_store_closed_notify",
//                          store);
//        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存信息成功");
        if (add_url != null){
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
        }
        return mv;
    }

    private void printer_send_msg(HttpServletRequest request, String mark, Store store) throws Exception {
        cn.wemalls.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
        if (template.isOpen()){
            String path = request.getRealPath("/") + "/vm/";
            PrintWriter pwrite = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", request
                          .getRealPath("/") +
                          "vm" + File.separator);
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            Velocity.init(p);
            org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
                                                 "UTF-8");
            VelocityContext context = new VelocityContext();
            context.put("reason", store.getViolation_reseaon());
            context.put("user", store.getUser());
            context.put("config", this.configService.getSysConfig());
            context.put("send_time", CommUtil.formatLongDate(new Date()));
            StringWriter writer = new StringWriter();
            blank.merge(context, writer);
            String content = writer.toString();
            User fromUser = this.userService.getObjByProperty("userName",
                            "admin");
            Message msg = new Message();
            msg.setAddTime(new Date());
            msg.setContent(content);
            msg.setFromUser(fromUser);
            msg.setTitle(template.getTitle());
            msg.setToUser(store.getUser());
            msg.setType(0);
            this.messageService.save(msg);
            CommUtil.deleteFile(path + "msg.vm");
            writer.flush();
            writer.close();
        }
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺删除", value = "/admin/store_del.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_printer_del.htm"})
    public String store_printer_del(HttpServletRequest request, String mulitId) throws Exception {
        String[] ids = mulitId.split(",");
        for (String id : ids){
            if (!id.equals("")){
                Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
                if (store.getUser() != null)
                    store.getUser().setStore(null);
                List<GoodsCart> goodCarts;
                for (Goods goods : store.getGoods_list()){
                    Map map = new HashMap();
                    map.put("gid", goods.getId());
                    goodCarts = this.goodsCartService
                                .query(
                                    "select obj from GoodsCart obj where obj.goods.id = :gid",
                                    map, -1, -1);
                    Long ofid = null;
                    Map map2;
                    List goodCarts2;
                    for (GoodsCart gc : goodCarts){
                        gc.getGsps().clear();
                        this.goodsCartService.delete(gc.getId());
                        if (gc.getOf() != null){
                            map2 = new HashMap();
                            ofid = gc.getOf().getId();
                            map2.put("ofid", ofid);
                            goodCarts2 = this.goodsCartService
                                         .query(
                                             "select obj from GoodsCart obj where obj.of.id = :ofid",
                                             map2, -1, -1);
                            if (goodCarts2.size() == 0){
                                this.orderFormService.delete(ofid);
                            }
                        }
                    }

                    List<Evaluate> evaluates = goods.getEvaluates();
                    for (Evaluate e : evaluates){
                        this.evaluateService.delete(e.getId());
                    }
                    goods.getGoods_ugcs().clear();
                    Accessory acc = goods.getGoods_main_photo();
                    if (acc != null){
                        acc.setAlbum(null);
                        Album album = acc.getCover_album();
                        if (album != null){
                            album.setAlbum_cover(null);
                            this.albumService.update(album);
                        }
                        this.accessoryService.update(acc);
                    }
                    for (Accessory acc1 : goods.getGoods_photos()){
                        if (acc1 != null){
                            acc1.setAlbum(null);
                            Album album = acc1.getCover_album();
                            if (album != null){
                                album.setAlbum_cover(null);
                                this.albumService.update(album);
                            }
                            acc1.setCover_album(null);
                            this.accessoryService.update(acc1);
                        }
                    }
                    goods.getCombin_goods().clear();
                    this.goodsService.delete(goods.getId());
                }
                for (OrderForm of : store.getOfs()){
                    for (GoodsCart gc : of.getGcs()){
                        gc.getGsps().clear();
                        this.goodsCartService.delete(gc.getId());
                    }
                    this.orderFormService.delete(of.getId());
                }
                this.storeService.delete(CommUtil.null2Long(id));
//                send_site_msg(request,
//                              "msg_toseller_goods_delete_by_admin_notify", store);
            }
        }
        return "redirect:store_list.htm";
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "店铺列表", value = "/admin/store_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_printer_log.htm"})
    public ModelAndView store_printer_log(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("admin/blue/store_printer_log.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy,
                orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Store.class, mv);
        IPageList pList = this.storeService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/store_printer_log.htm", "",
                                            params, pList, mv);
        ArrayList arrayList=new ArrayList<>();
        if(pList.getResult()!=null){
        	for(int i=0;i<pList.getResult().size();i++){
        		arrayList.add(((Store)(pList.getResult().get(i))).getId());
        	}
        }
        Map pars = new HashMap();
       
        String wheresql=" where 1=1 ";
        if(arrayList.size()>0){
        	wheresql+=" and obj.store.id in (:ids) ";
        	pars.put("ids", arrayList);
        }
        List grades = this.storeGradeService.query(
                          " select obj from StorePrinterLog obj "+wheresql+" order by obj.id desc ",
                          pars, -1, -1);
        mv.addObject("logs", grades);
        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "店铺列表", value = "/admin/store_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_printer_report.htm"})
    public ModelAndView store_printer_report(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("admin/blue/store_printer_report.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy,
                orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Store.class, mv);
        List rList = this.storePrinterService.executeNativeNamedQuery("select ws.store_name, w.store_id,max(w.printerSN)  as printerSN  ,count(w.store_id) as cou ,(SELECT count(*) from wemalls_store_printer_log s1 where s1.store_id=w.store_id and DATE_FORMAT(s1.ret_time,'%Y%m%d')=DATE_FORMAT(SYSDATE(),'%Y%m%d') ) as days,(SELECT count(*) from wemalls_store_printer_log s1 where s1.store_id=w.store_id and DATE_FORMAT(s1.ret_time,'%Y%u')=DATE_FORMAT(SYSDATE(),'%Y%u') ) as weeks,(SELECT count(*) from wemalls_store_printer_log s1 where s1.store_id=w.store_id and DATE_FORMAT(s1.ret_time,'%Y%m')=DATE_FORMAT(SYSDATE(),'%Y%m') ) as months from wemalls_store_printer_log w left join wemalls_store ws on ws.id=w.store_id GROUP BY w.store_id");
//        CommUtil.saveIPageList2ModelAndView(url + "/admin/store_printer_report.htm", "",
//                                            params, pList, mv);
//        List logs = this.storeGradeService.query(
//                          "select obj from StorePrinterLog obj order by obj.id desc ",
//                          null, -1, -1);
        List retList=new ArrayList<>();
        Map m=new HashMap<>();
//        if(rList.next()) { 
//        	   rowCount=rset .getInt("totalCount "); 
//        	}
//        for (Object object : rList) {
//        	 Field[] fields = object.getClass().getDeclaredFields();   
//        	m.put("store_name",object.ge)
//		}
        mv.addObject("objs", rList);

        return mv;
    }

}




