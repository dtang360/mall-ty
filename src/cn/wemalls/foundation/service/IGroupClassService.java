package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.GroupClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGroupClassService {
    public abstract boolean save(GroupClass paramGroupClass);

    public abstract GroupClass getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(GroupClass paramGroupClass);

    public abstract List<GroupClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




