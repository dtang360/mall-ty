package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.GoodsReturnItem;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGoodsReturnItemService {
    public abstract boolean save(GoodsReturnItem paramGoodsReturnItem);

    public abstract GoodsReturnItem getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(GoodsReturnItem paramGoodsReturnItem);

    public abstract List<GoodsReturnItem> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




