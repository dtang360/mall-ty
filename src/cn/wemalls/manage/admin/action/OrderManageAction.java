package cn.wemalls.manage.admin.action;

import cn.wemalls.core.annotation.SecurityMapping;
import cn.wemalls.core.domain.virtual.SysMap;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.core.tools.WebForm;
import cn.wemalls.foundation.domain.Coupon;
import cn.wemalls.foundation.domain.CouponInfo;
import cn.wemalls.foundation.domain.Evaluate;
import cn.wemalls.foundation.domain.Goods;
import cn.wemalls.foundation.domain.GoodsCart;
import cn.wemalls.foundation.domain.OrderForm;
import cn.wemalls.foundation.domain.Store;
import cn.wemalls.foundation.domain.SysConfig;
import cn.wemalls.foundation.domain.User;
import cn.wemalls.foundation.domain.query.OrderFormQueryObject;
import cn.wemalls.foundation.domain.virtual.TransInfo;
import cn.wemalls.foundation.service.IOrderFormService;
import cn.wemalls.foundation.service.IStoreService;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.foundation.service.IUserConfigService;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * 订单管理控制器
 */
@Controller
public class OrderManageAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IOrderFormService orderFormService;
    
    @Autowired
    private IStoreService storeService;

    @SecurityMapping(display = false, rsequence = 0, title = "订单设置", value = "/admin/set_order_confirm.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
    @RequestMapping({"/admin/set_order_confirm.htm"})
    public ModelAndView set_order_confirm(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "admin/blue/set_order_confirm.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "订单设置保存", value = "/admin/set_order_confirm_save.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
    @RequestMapping({"/admin/set_order_confirm_save.htm"})
    public ModelAndView set_order_confirm_save(HttpServletRequest request, HttpServletResponse response, String id, String auto_order_confirm, String auto_order_notice, String auto_order_return, String auto_order_evaluate){
        SysConfig obj = this.configService.getSysConfig();
        WebForm wf = new WebForm();
        SysConfig config = null;
        if (id.equals("")){
            config = (SysConfig)wf.toPo(request, SysConfig.class);
            config.setAddTime(new Date());
        }else{
            config = (SysConfig)wf.toPo(request, obj);
        }
        config.setAuto_order_confirm(CommUtil.null2Int(auto_order_confirm));
        config.setAuto_order_notice(CommUtil.null2Int(auto_order_notice));
        config.setAuto_order_return(CommUtil.null2Int(auto_order_return));
        config.setAuto_order_evaluate(CommUtil.null2Int(auto_order_evaluate));
        if (id.equals(""))
            this.configService.save(config);
       else{
            this.configService.update(config);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        mv.addObject("op_title", "订单设置成功");
        mv.addObject("list_url", CommUtil.getURL(request) +
                     "/admin/set_order_confirm.htm");

        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "订单列表", value = "/admin/order_list.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
    @RequestMapping({"/admin/order_list.htm"})
    public ModelAndView order_list(HttpServletRequest request, HttpServletResponse response, String order_status,String pay_status, String type, String type_data, String payment, String beginTime, String endTime, String begin_price, String end_price, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/order_list.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        
        OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
                "addTime", "desc");
        if (!CommUtil.null2String(order_status).equals("")){
            ofqo.addQuery("obj.order_status",
                          new SysMap("order_status",
                                     Integer.valueOf(CommUtil.null2Int(order_status))), "=");
        }
        if (!CommUtil.null2String(type_data).equals("")){
            if (type.equals("store")){
                ofqo.addQuery("obj.store.store_name",
                              new SysMap("store_name",
                                         "%"+type_data+"%"), "like");
            }
            if (type.equals("buyer")){
                ofqo.addQuery("obj.user.userName",
                              new SysMap("userName",
                            		  "%"+type_data+"%"), "like");
            }
            if (type.equals("order")){
                ofqo.addQuery("obj.order_id",
                              new SysMap("order_id", "%"+type_data+"%"), "like");
            }
        }
        if (!CommUtil.null2String(payment).equals("")){
            ofqo.addQuery("obj.payment.mark", new SysMap("mark", payment), "=");
        }
        if (!CommUtil.null2String(beginTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("beginTime",
                                     CommUtil.formatDate(beginTime+" 00:00:00","yyyy-MM-dd hh:mm:ss")), ">=");
        }
        if (!CommUtil.null2String(endTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("endTime",
                                     CommUtil.formatDate(endTime+" 23:59:59","yyyy-MM-dd hh:mm:ss")), "<=");
        }
        if (!CommUtil.null2String(begin_price).equals("")){
            ofqo.addQuery("obj.totalPrice",
                          new SysMap("begin_price",
                                     BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
                          ">=");
        }
        if (!CommUtil.null2String(end_price).equals("")){
            ofqo.addQuery("obj.totalPrice",
                          new SysMap("end_price",
                                     BigDecimal.valueOf(CommUtil.null2Double(end_price))), "<=");
        }
        IPageList pList = this.orderFormService.list(ofqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("order_status", order_status);
        mv.addObject("type", type);
        mv.addObject("type_data", type_data);
        mv.addObject("payment", payment);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("begin_price", begin_price);
        mv.addObject("end_price", end_price);
        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "订单列表", value = "/admin/store_order_list.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
    @RequestMapping({"/admin/store_order_list.htm"})
    public ModelAndView store_order_list(HttpServletRequest request, HttpServletResponse response, String order_status, String pay_status,String type, String type_data, String payment, String beginTime, String endTime, String begin_price, String end_price, String currentPage){
    	ModelAndView mv = new JModelAndView("admin/blue/store_order_list.html",
    			this.configService.getSysConfig(), this.userConfigService
    			.getUserConfig(), 0, request, response);

    	//店铺查询
    	List<Store> store_list = new ArrayList();
    	StringBuffer hqls= new StringBuffer(" from Store obj where 1=1 ");
    	Map pars = new HashMap();
    	List stores=new ArrayList<>();
    	if (type_data!=null&&!CommUtil.null2String(type_data).equals("")){
    		hqls.append(" and store_name like :type_data ");
    		pars.put("type_data","%"+type_data+"%");

    		hqls.append("  and store_status =2 ");
    		store_list = this.storeService
    				.query(hqls.toString(),pars, -1, -1);
    		StringBuffer storeStr=new StringBuffer("(");
    		if(store_list!=null&&store_list.size()>0){
    			for (Store sto : store_list) {
    				stores.add(sto.getId().toString());
    			}
    		}
    	}
    	
    	List order_list = new ArrayList();
    	List result_list = new ArrayList();
    	Map params = new HashMap();
    	Set store_ids = new TreeSet();
    	StringBuffer hql= new StringBuffer("select max(obj.pick_store) as store_id,count(store_id) as count_num,sum(obj.totalPrice) as sum_totalPrice from OrderForm obj where 1=1 ");
    	if (order_status!=null&&!CommUtil.null2String(order_status).equals("")){
    		hql.append(" and order_status=:order_status ");
    		params.put("order_status",order_status);
    	}else{
    		hql.append(" and order_status=:order_status ");
    		params.put("order_status",40);
    	}
    	if (beginTime!=null&&!CommUtil.null2String(beginTime).equals("")){
    		hql.append(" and obj.addTime>=:beginTime ");
			try {
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				Date bdate = sdf.parse(beginTime);
				params.put("beginTime",bdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}  
    	}
    	if (endTime!=null&&!CommUtil.null2String(endTime).equals("")){
    		hql.append(" and obj.addTime<=:endTime ");
			try {
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				Date edate = sdf.parse(endTime);
				params.put("endTime",edate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	if (type_data!=null&&!CommUtil.null2String(type_data).equals("")){
    		if(stores!=null&&stores.size()>0){
    		hql.append(" and obj.pick_store in (:pick_stores) ");
    		params.put("pick_stores", stores);
    		}
    	}
    	if (pay_status!=null&&!CommUtil.null2String(pay_status).equals("")){
    		if("0".equals(pay_status)){
    			hql.append(" and obj.pay_status is null ");
    		}else{
    			hql.append(" and obj.pay_status=:paystatus ");
    			params.put("paystatus", String.valueOf(pay_status));
    		}
    	}else{
    		hql.append(" and obj.pay_status is null ");
    	}
    	hql.append(" group by obj.pick_store ");
    	order_list = this.orderFormService
    			.query(hql.toString(),params, -1, -1);
    	Map resultMap=new HashMap<>();
        for (int i = 0; i < order_list.size(); i++){
            Object[] obj = (Object[])order_list.get(i);
            resultMap=new HashMap<>();
            if(obj!=null){
            	resultMap.put("store_id",CommUtil.null2String(obj[0]));
            	resultMap.put("store_name",storeService.getObjById(CommUtil.null2Long(obj[0])).getStore_name());
            	resultMap.put("count_num",CommUtil.null2Int(obj[1]));
            	resultMap.put("sum_totalPrice",CommUtil.null2Double(obj[2]));
            	result_list.add(resultMap);
            }
        }
        mv.addObject("order_status",order_status); 
        mv.addObject("pay_status",pay_status);
        mv.addObject("type_data",type_data);
        mv.addObject("payment",payment);
        mv.addObject("beginTime",beginTime);
        mv.addObject("endTime",endTime);
    	mv.addObject("objs",result_list);
    	return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "订单列表", value = "/admin/store_order_list.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
    @RequestMapping({"/admin/store_order_list2.htm"})
    public ModelAndView store_order_lis2(HttpServletRequest request, HttpServletResponse response, String order_status, String type, String type_data, String payment, String beginTime, String endTime, String begin_price, String end_price, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/store_order_list.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
                "addTime", "desc");
        if (!CommUtil.null2String(order_status).equals("")){
            ofqo.addQuery("obj.order_status",
                          new SysMap("order_status",
                                     Integer.valueOf(CommUtil.null2Int(order_status))), "=");
        }
        if (!CommUtil.null2String(type_data).equals("")){
            if (type.equals("store")){
                ofqo.addQuery("obj.store.store_name",
                              new SysMap("store_name",
                                         "%"+type_data+"%"), "like");
            }
            if (type.equals("buyer")){
                ofqo.addQuery("obj.user.userName",
                              new SysMap("userName",
                            		  "%"+type_data+"%"), "like");
            }
            if (type.equals("order")){
                ofqo.addQuery("obj.order_id",
                              new SysMap("order_id", "%"+type_data+"%"), "like");
            }
        }
        if (!CommUtil.null2String(payment).equals("")){
            ofqo.addQuery("obj.payment.mark", new SysMap("mark", payment), "=");
        }
        if (!CommUtil.null2String(beginTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("beginTime",
                                     CommUtil.formatDate(beginTime)), ">=");
        }
        if (!CommUtil.null2String(endTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("endTime",
                                     CommUtil.formatDate(endTime)), "<=");
        }
        if (!CommUtil.null2String(begin_price).equals("")){
            ofqo.addQuery("obj.totalPrice",
                          new SysMap("begin_price",
                                     BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
                          ">=");
        }
        if (!CommUtil.null2String(end_price).equals("")){
            ofqo.addQuery("obj.totalPrice",
                          new SysMap("end_price",
                                     BigDecimal.valueOf(CommUtil.null2Double(end_price))), "<=");
        }
        IPageList pList = this.orderFormService.list(ofqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("order_status", order_status);
        mv.addObject("type", type);
        mv.addObject("type_data", type_data);
        mv.addObject("payment", payment);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("begin_price", begin_price);
        mv.addObject("end_price", end_price);
        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "订单列表", value = "/admin/store_pay_list.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
    @RequestMapping({"/admin/store_pay_list.htm"})
    public ModelAndView store_pay_list(HttpServletRequest request, HttpServletResponse response,String pick_id,String order_status,String pay_status,String type_data,String pick_store, String payment, String beginTime, String endTime, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/store_pay_list.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        List order_list = new ArrayList();
    	List result_list = new ArrayList();
    	Map params = new HashMap();
    	Set store_ids = new TreeSet();
    	StringBuffer hql= new StringBuffer("select max(obj.pick_store) as store_id,count(*) as count_num,sum(obj.totalPrice) as sum_totalPrice,obj.payment.name as pay_name,obj.payment.id as pay_id from OrderForm obj where 1=1 ");
    	if (order_status!=null&&!CommUtil.null2String(order_status).equals("")){
    		hql.append(" and order_status=:order_status ");
    		params.put("order_status",order_status);
    	}else{
    		hql.append(" and order_status=:order_status ");
    		params.put("order_status",40);
    	}
    	if (beginTime!=null&&!CommUtil.null2String(beginTime).equals("")){
    		hql.append(" and obj.addTime>=:beginTime ");
			try {
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				Date bdate = sdf.parse(beginTime);
				params.put("beginTime",bdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}  
    	}
    	if (endTime!=null&&!CommUtil.null2String(endTime).equals("")){
    		hql.append(" and obj.addTime<=:endTime ");
			try {
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				Date edate = sdf.parse(endTime);
				params.put("endTime",edate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	if (pay_status!=null&&!CommUtil.null2String(pay_status).equals("")){
    		if("0".equals(pay_status)){
    			hql.append(" and obj.pay_status is null ");
    		}else{
    			hql.append(" and obj.pay_status=:paystatus ");
    			params.put("paystatus", String.valueOf(pay_status));
    		}
    	}else{
    		hql.append(" and obj.pay_status is null ");
    	}
    	
    	if(pick_id!=null&&!CommUtil.null2String(pick_id).equals("")){
    		hql.append(" and obj.pick_store =:pick_store ");
    		params.put("pick_store", pick_id);
    	}
    	
    	hql.append(" group by obj.payment.id ");
    	order_list = this.orderFormService
    			.query(hql.toString(),params, -1, -1);
    	Map resultMap=new HashMap<>();
        for (int i = 0; i < order_list.size(); i++){
            Object[] obj = (Object[])order_list.get(i);
            resultMap=new HashMap<>();
            if(obj!=null){
            	resultMap.put("store_id",CommUtil.null2String(obj[0]));
            	resultMap.put("store_name",storeService.getObjById(CommUtil.null2Long(obj[0])).getStore_name());
            	resultMap.put("count_num",CommUtil.null2Int(obj[1]));
            	resultMap.put("sum_totalPrice",CommUtil.null2Double(obj[2]));
            	resultMap.put("pay_name",CommUtil.null2String(obj[3]));
            	resultMap.put("pay_id",CommUtil.null2String(obj[4]));
            	result_list.add(resultMap);
            }
        }
        mv.addObject("pick_id",pick_id);
        mv.addObject("order_status",order_status); 
        mv.addObject("pay_status",pay_status);
        mv.addObject("type_data",type_data);
        mv.addObject("payment",payment);
        mv.addObject("beginTime",beginTime);
        mv.addObject("endTime",endTime);
    	mv.addObject("objs",result_list);
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "订单列表", value = "/admin/store_pay_detail.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
    @RequestMapping({"/admin/store_pay_detail.htm"})
    public ModelAndView store_pay_detail(HttpServletRequest request, HttpServletResponse response,String pick_id,String order_status,String pay_status,String type_data,String pick_store, String payment,  String payment_id, String beginTime, String endTime, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/store_pay_detail.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        
        OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
                "addTime", "desc");
    	if (order_status!=null&&!CommUtil.null2String(order_status).equals("")){
    		ofqo.addQuery("obj.order_status",
                    new SysMap("order_status",
                  		  Integer.valueOf(order_status)), "=");
    	}else{
    		ofqo.addQuery("obj.order_status",
                    new SysMap("order_status",
                  		  Integer.valueOf(40)), "=");
    	}
        if (CommUtil.null2String(pay_status).equals("1")){
            ofqo.addQuery("obj.pay_status",
                          new SysMap("pay_status",pay_status), "=");
        }else{
//        	ofqo.addQuery("obj.pay_status",
//                    new SysMap("pay_status","null"), "is");
        	ofqo.addStringQuery(" and obj.pay_status is null ");
        }
        if (!CommUtil.null2String(pick_id).equals("")){
            ofqo.addQuery("obj.pick_store",new SysMap("pick_store",pick_id), "=");
        }
        
        if (!CommUtil.null2String(payment_id).equals("")){
            ofqo.addQuery("obj.payment.id", new SysMap("mark",CommUtil.null2Long(payment_id)), "=");
        }
        
        if (!CommUtil.null2String(beginTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("beginTime",
                                     CommUtil.formatDate(beginTime)), ">=");
        }
        if (!CommUtil.null2String(endTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("endTime",
                                     CommUtil.formatDate(endTime)), "<=");
        }
        IPageList pList = this.orderFormService.list(ofqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("pick_id",pick_id);
        mv.addObject("order_status",order_status); 
        mv.addObject("pay_status",pay_status);
        mv.addObject("type_data",type_data);
        mv.addObject("payment",payment);
        mv.addObject("beginTime",beginTime);
        mv.addObject("endTime",endTime);
        mv.addObject("payment_id",payment_id);
        mv.addObject("pick_store",pick_store);
        mv.addObject("order_status", "40");
//        mv.addObject("mark", CommUtil.null2Long(payment_id));
        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "订单拨付", value = "/admin/admin/orderform_bofu.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "拨付")
    @RequestMapping({"/admin/orderform_bofu.htm"})
    public ModelAndView orderform_bofu(HttpServletRequest request, HttpServletResponse response, String mulitId,String currentPage,String pick_id,String order_status,String pay_status,String type_data,String pick_store, String payment,  String payment_id, String beginTime, String endTime){
    	String[] ids = mulitId.split(",");
    	ModelAndView mv = new ModelAndView();
        for (String id : ids){
    	 if (!CommUtil.null2String(id).equals("")){
    		OrderForm orderForm= orderFormService.getObjById(CommUtil.null2Long(id));
    		if(orderForm!=null){
    			if(null!=orderForm&&!"1".equals(orderForm.getPay_status())){
    				orderForm.setPay_status("1");
    				orderFormService.update(orderForm);
    			}
    		}
		        mv = new JModelAndView("admin/blue/success.html",
		                                            this.configService.getSysConfig(), this.userConfigService
		                                            .getUserConfig(), 0, request, response);
		        mv.addObject("op_title", "拨付成功");
            }else{
            	mv= new JModelAndView("admin/blue/success.html",
                        this.configService.getSysConfig(), this.userConfigService
                        .getUserConfig(), 0, request, response);
            	mv.addObject("op_title", "拨付失败");
            }
        }
        mv.addObject("list_url", CommUtil.getURL(request) +
                     "/admin/store_pay_detail.htm?currentPage="+currentPage+"&pick_id="+pick_id+"&pay_status="+pay_status+"&order_status="+order_status+"&beginTime="+beginTime+"&endTime="+endTime+"&payment_id="+payment_id);
        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "订单拨付", value = "/admin/admin/orderform_bofu.htm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "拨付")
    @RequestMapping({"/admin/orderform_bofu_piliang.htm"})
    public ModelAndView orderform_bofu_piliang(HttpServletRequest request, HttpServletResponse response, String mulitId,String currentPage,String pick_id,String order_status,String pay_status,String type_data,String pick_store, String payment,  String payment_id, String beginTime, String endTime){
    	
    	String[] ids = mulitId.split(",");
    	Long[] los = new Long[ids.length];
    	List<OrderForm> order_lists = new ArrayList();
    	for(int i=0;i<ids.length;i++){
    		los[i] = Long.parseLong(ids[i]);
    		List<OrderForm> order_list = new ArrayList();
    		Map params = new HashMap();
    		Set store_ids = new TreeSet();
    		StringBuffer hql= new StringBuffer(" from OrderForm obj where 1=1 ");
    		if(los!=null&&los.length>0){
    			hql.append(" and obj.payment.id =:ids ");
    			params.put("ids",los[i]);
    		}
    		if (order_status!=null&&!CommUtil.null2String(order_status).equals("")){
    			hql.append(" and order_status=:order_status ");
    			params.put("order_status",order_status);
    		}else{
    			hql.append(" and order_status=:order_status ");
    			params.put("order_status",40);
    		}
    		if (beginTime!=null&&!CommUtil.null2String(beginTime).equals("")){
    			hql.append(" and obj.addTime>=:beginTime ");
    			try {
    				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
    				Date bdate = sdf.parse(beginTime);
    				params.put("beginTime",bdate);
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}  
    		}
    		if (endTime!=null&&!CommUtil.null2String(endTime).equals("")){
    			hql.append(" and obj.addTime<=:endTime ");
    			try {
    				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
    				Date edate = sdf.parse(endTime);
    				params.put("endTime",edate);
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}
    		}
    		if (pay_status!=null&&!CommUtil.null2String(pay_status).equals("")){
    			if("0".equals(pay_status)){
    				hql.append(" and obj.pay_status is null ");
    			}else{
    				hql.append(" and obj.pay_status=:paystatus ");
    				params.put("paystatus", String.valueOf(pay_status));
    			}
    		}else{
    			hql.append(" and obj.pay_status is null ");
    		}

    		if(pick_id!=null&&!CommUtil.null2String(pick_id).equals("")){
    			hql.append(" and obj.pick_store =:pick_store ");
    			params.put("pick_store", pick_id);
    		}
    		order_list = this.orderFormService
    				.query(hql.toString(),params, -1, -1);
    		order_lists.addAll(order_list);
    	}
    	
    	ModelAndView mv = new ModelAndView();
    	if(order_lists!=null&&order_lists.size()>0){
        for (OrderForm  obj:order_lists){
    	 if (!CommUtil.null2String(obj.getId()).equals("")){
    		OrderForm orderForm= orderFormService.getObjById(CommUtil.null2Long(obj.getId()));
    		if(orderForm!=null){
    			if(null!=orderForm&&!"1".equals(orderForm.getPay_status())){
    				orderForm.setPay_status("1");
    				orderFormService.update(orderForm);
    			}
    		}
		        mv = new JModelAndView("admin/blue/success.html",
		                                            this.configService.getSysConfig(), this.userConfigService
		                                            .getUserConfig(), 0, request, response);
		        mv.addObject("op_title", "拨付成功");
            }else{
            	mv= new JModelAndView("admin/blue/success.html",
                        this.configService.getSysConfig(), this.userConfigService
                        .getUserConfig(), 0, request, response);
            	mv.addObject("op_title", "拨付失败");
            }
        }
        }
    	
        mv.addObject("pick_id",pick_id);
        mv.addObject("order_status",order_status); 
        mv.addObject("pay_status",pay_status);
        mv.addObject("type_data",type_data);
        mv.addObject("payment",payment);
        mv.addObject("beginTime",beginTime);
        mv.addObject("endTime",endTime);
        mv.addObject("payment_id",payment_id);
        mv.addObject("pick_store",pick_store);
        mv.addObject("order_status", "40");
        mv.addObject("list_url", CommUtil.getURL(request) +
                     "/admin/store_pay_list.htm?currentPage="+currentPage+"&pick_id="+pick_id+"&pay_status="+pay_status+"&order_status="+order_status+"&beginTime="+beginTime+"&endTime="+endTime+"&payment_id="+payment_id);
        return mv;
    }
    
//    @SecurityMapping(display = false, rsequence = 0, title = "会员删除", value = "/admin/user_del.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
//    @RequestMapping({"/admin/user_del.htm"})
//    public String user_del(HttpServletRequest request, String mulitId, String currentPage){
//        String[] ids = mulitId.split(",");
//        for (String id : ids){
//            if (!id.equals("")){
//                User parent = this.userService.getObjById(Long.valueOf(Long.parseLong(id)));
//                if (!parent.getUsername().equals("admin")){
//                    Long ofid;
//                    for (User user : parent.getChilds()){
//                        user.getRoles().clear();
//                        if (user.getStore() != null){
//                            for (Goods goods : user.getStore().getGoods_list()){
//                                Map map = new HashMap();
//                                map.put("gid", goods.getId());
//                                List<GoodsCart> goodCarts = this.goodsCartService
//                                                            .query(
//                                                                "select obj from GoodsCart obj where obj.goods.id = :gid",
//                                                                map, -1, -1);
//                                ofid = null;
//                                Map map2;
//                                for (GoodsCart gc : goodCarts){
//                                    ofid = gc.getOf().getId();
//                                    this.goodsCartService.delete(gc.getId());
//                                    map2 = new HashMap();
//                                    map2.put("ofid", ofid);
//                                    List goodCarts2 = this.goodsCartService
//                                                      .query(
//                                                          "select obj from GoodsCart obj where obj.of.id = :ofid",
//                                                          map2, -1, -1);
//                                    if (goodCarts2.size() == 0){
//                                        this.orderFormService.delete(ofid);
//                                    }
//                                }
//
//                                List<Evaluate> evaluates = goods.getEvaluates();
//                                for (Evaluate e : evaluates){
//                                    this.evaluateService.delete(e.getId());
//                                }
//                                goods.getGoods_ugcs().clear();
//                                this.goodsService.delete(goods.getId());
//                            }
//                        }
//                        this.userService.delete(user.getId());
//                    }
//                    parent.getRoles().clear();
//                    if (parent.getStore() != null){
//                        for (Goods goods : parent.getStore().getGoods_list()){
//                            Map map = new HashMap();
//                            map.put("gid", goods.getId());
//                            List<GoodsCart> goodCarts = this.goodsCartService
//                                                        .query(
//                                                            "select obj from GoodsCart obj where obj.goods.id = :gid",
//                                                            map, -1, -1);
//                            Long ofid1 = null;
//                            Map map2;
//                            for (GoodsCart gc : goodCarts){
//                                ofid1 = gc.getOf().getId();
//                                this.goodsCartService.delete(gc.getId());
//                                map2 = new HashMap();
//                                map2.put("ofid", ofid1);
//                                List goodCarts2 = this.goodsCartService
//                                                  .query(
//                                                      "select obj from GoodsCart obj where obj.of.id = :ofid",
//                                                      map2, -1, -1);
//                                if (goodCarts2.size() == 0){
//                                    this.orderFormService.delete(ofid1);
//                                }
//                            }
//
//                            List<Evaluate> evaluates = goods.getEvaluates();
//                            for (Evaluate e : evaluates){
//                                this.evaluateService.delete(e.getId());
//                            }
//                            goods.getGoods_ugcs().clear();
//                            this.goodsService.delete(goods.getId());
//                        }
//                    }
//                    this.userService.delete(parent.getId());
//                }
//            }
//        }
//
//        return "redirect:user_list.htm?currentPage=" + currentPage;
//    }
    

    @SecurityMapping(display = false, rsequence = 0, title = "订单详情", value = "/admin/order_view.htm*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
    @RequestMapping({"/admin/order_view.htm"})
    public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView("admin/blue/order_view.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        TransInfo transInfo = query_ship_getData(id);
        mv.addObject("transInfo", transInfo);
        mv.addObject("obj", obj);

        return mv;
    }

    public TransInfo query_ship_getData(String id){
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        try {
            URL url = new URL(
                "http://api.kuaidi100.com/api?id=" +
                this.configService.getSysConfig().getKuaidi_id() +
                "&com=" + (
                    obj.getEc() != null ? obj.getEc()
                    .getCompany_mark() : "") + "&nu=" +
                obj.getShipCode() + "&show=0&muti=1&order=asc");
            URLConnection con = url.openConnection();
            con.setAllowUserInteraction(false);
            InputStream urlStream = url.openStream();
            String type = URLConnection.guessContentTypeFromStream(urlStream);
            String charSet = null;
            if (type == null)
                type = con.getContentType();
            if ((type == null) || (type.trim().length() == 0) ||
                    (type.trim().indexOf("text/html") < 0))
                return info;
            if (type.indexOf("charset=") > 0)
                charSet = type.substring(type.indexOf("charset=") + 8);
            byte[] b = new byte[10000];
            int numRead = urlStream.read(b);
            String content = new String(b, 0, numRead, charSet);
            while (numRead != -1){
                numRead = urlStream.read(b);
                if (numRead == -1)
                    continue;
                String newContent = new String(b, 0, numRead, charSet);
                content = content + newContent;
            }

            info = (TransInfo)Json.fromJson(TransInfo.class, content);
            urlStream.close();
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return info;
    }
}




