package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.ChattingLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IChattingLogService {
    public abstract boolean save(ChattingLog paramChattingLog);

    public abstract ChattingLog getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(ChattingLog paramChattingLog);

    public abstract List<ChattingLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




