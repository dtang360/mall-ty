package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.GoodsClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGoodsClassService {
    public abstract boolean save(GoodsClass paramGoodsClass);

    public abstract GoodsClass getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(GoodsClass paramGoodsClass);

    public abstract List<GoodsClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract GoodsClass getObjByProperty(String paramString, Object paramObject);
}




