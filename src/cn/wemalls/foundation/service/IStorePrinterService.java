package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.StoreClass;
import cn.wemalls.foundation.domain.StorePrinter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IStorePrinterService {
    public abstract boolean save(StorePrinter paramStoreClass);

    public abstract StorePrinter getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(StorePrinter paramStoreClass);

    public abstract List<StorePrinter> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public List executeNativeNamedQuery(final String nnq);
}




