package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.PredepositCash;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IPredepositCashService {
    public abstract boolean save(PredepositCash paramPredepositCash);

    public abstract PredepositCash getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(PredepositCash paramPredepositCash);

    public abstract List<PredepositCash> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




