package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.foundation.dao.StoreDAO;
import cn.wemalls.foundation.domain.OrderForm;
import cn.wemalls.foundation.service.IOrderFormService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderFormServiceImpl
    implements IOrderFormService {
    @Resource(name = "orderFormDAO")
    private IGenericDAO<OrderForm> orderFormDao;
    @Resource(name = "storeDAO")
    private StoreDAO storeDAO;

    public boolean save(OrderForm orderForm){
        try {
            this.orderFormDao.save(orderForm);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public OrderForm getObjById(Long id){
        OrderForm orderForm = (OrderForm)this.orderFormDao.get(id);
        if (orderForm != null){
            return orderForm;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.orderFormDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> orderFormIds){
        for (Serializable id : orderFormIds){
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
        GenericPageList pList = new GenericPageList(OrderForm.class, query,
                params, this.orderFormDao);
        if (properties != null){
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null)
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
                             .getCurrentPage().intValue(), pageObj.getPageSize() == null ? 0 :
                             pageObj.getPageSize().intValue());
        }else{
            pList.doList(0, -1);
        }
        //获取提货店铺
        OrderForm orderForm=new OrderForm();
        List<OrderForm> list=pList.getResult();
        if(list!=null&&list.size()>0){
        	for(int i=0;i<list.size();i++){
        		orderForm=new OrderForm();
        		orderForm=list.get(i);
        		list.get(i).setPickStore(storeDAO.get(CommUtil.null2Long(orderForm.getPick_store())));
        	}
        	pList.setResult(list);
        }
        return pList;
    }

    public boolean update(OrderForm orderForm){
        try {
            this.orderFormDao.update(orderForm);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<OrderForm> query(String query, Map params, int begin, int max){
        return this.orderFormDao.query(query, params, begin, max);
    }
}




