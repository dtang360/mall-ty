package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.Activity;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IActivityService {
    public abstract boolean save(Activity paramActivity);

    public abstract Activity getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(Activity paramActivity);

    public abstract List<Activity> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




