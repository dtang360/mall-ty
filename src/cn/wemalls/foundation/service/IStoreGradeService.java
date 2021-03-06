package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.StoreGrade;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IStoreGradeService {
    public abstract boolean save(StoreGrade paramStoreGrade);

    public abstract StoreGrade getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(StoreGrade paramStoreGrade);

    public abstract List<StoreGrade> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




