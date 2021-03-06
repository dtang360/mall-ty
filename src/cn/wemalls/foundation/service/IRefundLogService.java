package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.RefundLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IRefundLogService {
    public abstract boolean save(RefundLog paramRefundLog);

    public abstract RefundLog getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(RefundLog paramRefundLog);

    public abstract List<RefundLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




