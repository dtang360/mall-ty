package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.ActivityGoods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IActivityGoodsService {
    public abstract boolean save(ActivityGoods paramActivityGoods);

    public abstract ActivityGoods getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(ActivityGoods paramActivityGoods);

    public abstract List<ActivityGoods> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




