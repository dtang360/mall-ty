package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.SpareGoodsClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface ISpareGoodsClassService {
    public abstract boolean save(SpareGoodsClass paramSpareGoodsClass);

    public abstract SpareGoodsClass getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(SpareGoodsClass paramSpareGoodsClass);

    public abstract List<SpareGoodsClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




