package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.SnsFriend;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface ISnsFriendService {
    public abstract boolean save(SnsFriend paramSnsFriend);

    public abstract SnsFriend getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(SnsFriend paramSnsFriend);

    public abstract List<SnsFriend> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




