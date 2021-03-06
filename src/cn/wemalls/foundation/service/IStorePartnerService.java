package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.StorePartner;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IStorePartnerService {
    public abstract boolean save(StorePartner paramStorePartner);

    public abstract StorePartner getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(StorePartner paramStorePartner);

    public abstract List<StorePartner> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




