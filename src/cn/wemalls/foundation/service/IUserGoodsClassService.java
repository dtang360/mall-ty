package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.UserGoodsClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IUserGoodsClassService {
    public abstract boolean save(UserGoodsClass paramUserGoodsClass);

    public abstract UserGoodsClass getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(UserGoodsClass paramUserGoodsClass);

    public abstract List<UserGoodsClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




