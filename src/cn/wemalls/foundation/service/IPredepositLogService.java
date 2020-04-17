package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.PredepositLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IPredepositLogService {
    public abstract boolean save(PredepositLog paramPredepositLog);

    public abstract PredepositLog getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(PredepositLog paramPredepositLog);

    public abstract List<PredepositLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




