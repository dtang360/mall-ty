package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.IntegralGoods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IIntegralGoodsService {
    public abstract boolean save(IntegralGoods paramIntegralGoods);

    public abstract IntegralGoods getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(IntegralGoods paramIntegralGoods);

    public abstract List<IntegralGoods> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




