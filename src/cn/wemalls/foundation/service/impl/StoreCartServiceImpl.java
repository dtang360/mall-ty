package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.core.security.support.SecurityUserHolder;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.foundation.dao.IntegralGoodsCartDAO;
import cn.wemalls.foundation.domain.GoodsCart;
import cn.wemalls.foundation.domain.StoreCart;
import cn.wemalls.foundation.domain.User;
import cn.wemalls.foundation.service.IGoodsCartService;
import cn.wemalls.foundation.service.IStoreCartService;
import cn.wemalls.foundation.service.IUserService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoreCartServiceImpl
    implements IStoreCartService {
    @Resource(name = "storeCartDAO")
    private IGenericDAO<StoreCart> storeCartDao;
    @Autowired
    private IntegralGoodsCartDAO goodsCartDao;
    @Autowired
    private IUserService userService;

    public boolean save(StoreCart storeCart){
        try {
            this.storeCartDao.save(storeCart);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public StoreCart getObjById(Long id){
        StoreCart storeCart = (StoreCart)this.storeCartDao.get(id);
        if (storeCart != null){
            return storeCart;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.storeCartDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> storeCartIds){
        for (Serializable id : storeCartIds){
            delete((Long)id);
        }

        return true;
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(StoreCart.class, query,
                params, this.storeCartDao);
        if (properties != null){
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null)
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
                             .getCurrentPage().intValue(), pageObj.getPageSize() == null ? 0 :
                             pageObj.getPageSize().intValue());
        }else{
            pList.doList(0, -1);
        }

        return pList;
    }

    public boolean update(StoreCart storeCart){
        try {
            this.storeCartDao.update(storeCart);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<StoreCart> query(String query, Map params, int begin, int max){
        return this.storeCartDao.query(query, params, begin, max);
    }
    
    public int executeNativeSQL(String nnq){
        return this.storeCartDao.executeNativeSQL(nnq);
    }
    
  //购物车数据
    public List<StoreCart> cart_calc_wxapplet(HttpServletRequest request,boolean remove){
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if (SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        Map params = new HashMap();
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("cart_session_id")){
                    cart_session_id = CommUtil.null2String(cookie.getValue());
                }
            }
        }
        if (user != null){
            if (!cart_session_id.equals("")){
                if (user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartDao.query(
                                                            "select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status  and obj.store.id=:store_id ",
                                                            params, -1, -1);
                    for (StoreCart sc : store_cookie_cart){
                        // sc = (StoreCart)localIterator1.next();
                        for (GoodsCart gc : ((StoreCart) sc).getGcs()){
                            gc.getGsps().clear();
                            this.executeNativeSQL(" delete from wemalls_goodscart  where id = "+CommUtil.null2Long(gc.getId()));
//                            this.goodsCartService.delete(gc.getId());
                        }
                        sc.getGcs().clear();
                        this.delete(((StoreCart) sc).getId());
                    }
                }

                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.query(
                                  "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                                  params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.query(
                                "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
                                -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.query(
                                "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
                                -1, -1);
            }

        }else if (!cart_session_id.equals("")){
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.query(
                              "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                              params, -1, -1);
        }

        for (StoreCart sc : user_cart){
            boolean sc_add = true;
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                }
            }
            if (sc_add){
                cart.add(sc);
            }
        }
        for (StoreCart sc : cookie_cart){
            boolean sc_add = true;
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                    for (GoodsCart gc : sc.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartDao.update(gc);
                    }
                    this.delete(sc.getId());
                }
            }
            if (sc_add){
                cart.add(sc);
            }
        }
        
        
   	 //删除已提交订的购物车商品
        if (cart != null){
        	for (int i=0;i<cart.size();i++){
        		boolean tf=false;
        		List gcs =((StoreCart)cart.get(i)).getGcs();
        		for (int j=0;j<gcs.size();j++){
        			if(null!=((GoodsCart)gcs.get(j)).getOf()) {
        				cart.get(i).getGcs().remove(j);
        				j-=1;
        			}else{
        				if(((GoodsCart)gcs.get(j)).isChecked()) {
        					tf=true;
        				}
        			}			
        		}
        		if(tf) {
        			((StoreCart)cart.get(i)).setChecked(true);
        		}else {
        			((StoreCart)cart.get(i)).setChecked(false);
        		}        		
        		if(gcs.size()==0||!tf&&remove) {
        			cart.remove(i);
        			i-=1;
        		}
        	}
        }

        return (List<StoreCart>) cart;
    }

    
    
}




