package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.IntegralLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IIntegralLogService {
    public abstract boolean save(IntegralLog paramIntegralLog);

    public abstract IntegralLog getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(IntegralLog paramIntegralLog);

    public abstract List<IntegralLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




