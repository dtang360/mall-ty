package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.dao.StorePrinterDAO;
import cn.wemalls.foundation.domain.StorePrinter;
import cn.wemalls.foundation.service.IStorePrinterService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StorePrinterServiceImpl
    implements IStorePrinterService {
    @Resource(name = "storePrinterDAO")
    private IGenericDAO<StorePrinter> storePrinterDao;

    public boolean save(StorePrinter storePrinter){
        try {
            this.storePrinterDao.save(storePrinter);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public StorePrinter getObjById(Long id){
        StorePrinter storePrinter = (StorePrinter)this.storePrinterDao.get(id);
        if (storePrinter != null){
            return storePrinter;
        }
        return null;
    }

    public boolean delete(Long id){
        try {
            this.storePrinterDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> storePrinterIds){
        for (Serializable id : storePrinterIds){
            delete((Long)id);
        }

        return true;
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(StorePrinter.class, query,
                params, this.storePrinterDao);
        if (properties != null){
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null)
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
                             .getCurrentPage().intValue(), pageObj.getPageSize() == null ? 0 :
                             pageObj.getPageSize().intValue());
        }else{
            pList.doList(0, -1);
        }

        return pList;
    }

    public boolean update(StorePrinter storePrinter){
        try {
            this.storePrinterDao.update(storePrinter);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<StorePrinter> query(String query, Map params, int begin, int max){
        return this.storePrinterDao.query(query, params, begin, max);
    }
    
    public List executeNativeNamedQuery(final String nnq){
    	return storePrinterDao.executeNativeNamedQuery(nnq);
    }
}




