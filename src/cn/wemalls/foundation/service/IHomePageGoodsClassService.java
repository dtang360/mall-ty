package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.HomePageGoodsClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IHomePageGoodsClassService {
    public abstract boolean save(HomePageGoodsClass paramHomePageGoodsClass);

    public abstract HomePageGoodsClass getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(HomePageGoodsClass paramHomePageGoodsClass);

    public abstract List<HomePageGoodsClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




