package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.Evaluate;
import cn.wemalls.foundation.domain.Goods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IEvaluateService {
    public abstract boolean save(Evaluate paramEvaluate);

    public abstract Evaluate getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(Evaluate paramEvaluate);

    public abstract List<Evaluate> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract List<Goods> query_goods(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




