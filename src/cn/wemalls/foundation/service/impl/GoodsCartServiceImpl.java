package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.GoodsCart;
import cn.wemalls.foundation.service.IGoodsCartService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GoodsCartServiceImpl
    implements IGoodsCartService {
    @Resource(name = "goodsCartDAO")
    private IGenericDAO<GoodsCart> goodsCartDao;

    public boolean save(GoodsCart goodsCart){
        try {
            this.goodsCartDao.save(goodsCart);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public GoodsCart getObjById(Long id){
        GoodsCart goodsCart = (GoodsCart)this.goodsCartDao.get(id);
        if (goodsCart != null){
            return goodsCart;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.goodsCartDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
    
    public int executeNativeSQL(String nnq){
        return this.goodsCartDao.executeNativeSQL(nnq);
    }

    public boolean batchDelete(List<Serializable> goodsCartIds){
        for (Serializable id : goodsCartIds){
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
        GenericPageList pList = new GenericPageList(GoodsCart.class, query,
                params, this.goodsCartDao);
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

    public boolean update(GoodsCart goodsCart){
        try {
            this.goodsCartDao.update(goodsCart);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<GoodsCart> query(String query, Map params, int begin, int max){
        return this.goodsCartDao.query(query, params, begin, max);
    }
}




