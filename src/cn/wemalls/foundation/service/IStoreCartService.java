package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.StoreCart;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract interface IStoreCartService {
    public abstract boolean save(StoreCart paramStoreCart);

    public abstract StoreCart getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(StoreCart paramStoreCart);

    public abstract List<StoreCart> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public int executeNativeSQL(String nnq);
    
    public List<StoreCart> cart_calc_wxapplet(HttpServletRequest request,boolean remove);
}




