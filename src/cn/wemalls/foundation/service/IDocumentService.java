package cn.wemalls.foundation.service;

import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.Document;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IDocumentService {
    public abstract boolean save(Document paramDocument);

    public abstract Document getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(Document paramDocument);

    public abstract List<Document> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract Document getObjByProperty(String paramString, Object paramObject);
}




