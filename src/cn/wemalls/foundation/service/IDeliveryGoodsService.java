package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.DeliveryGoods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IDeliveryGoodsService {
    public abstract boolean save(DeliveryGoods paramDeliveryGoods);

    public abstract DeliveryGoods getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(DeliveryGoods paramDeliveryGoods);

    public abstract List<DeliveryGoods> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




