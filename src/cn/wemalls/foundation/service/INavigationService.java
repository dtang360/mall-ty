package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.Navigation;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface INavigationService {
    public abstract boolean save(Navigation paramNavigation);

    public abstract Navigation getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(Navigation paramNavigation);

    public abstract List<Navigation> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




