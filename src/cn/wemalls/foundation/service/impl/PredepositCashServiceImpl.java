package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.PredepositCash;
import cn.wemalls.foundation.service.IPredepositCashService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PredepositCashServiceImpl
    implements IPredepositCashService {
    @Resource(name = "predepositCashDAO")
    private IGenericDAO<PredepositCash> predepositCashDao;

    public boolean save(PredepositCash predepositCash){
        try {
            this.predepositCashDao.save(predepositCash);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public PredepositCash getObjById(Long id){
        PredepositCash predepositCash = (PredepositCash)this.predepositCashDao.get(id);
        if (predepositCash != null){
            return predepositCash;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.predepositCashDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> predepositCashIds){
        for (Serializable id : predepositCashIds){
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
        GenericPageList pList = new GenericPageList(PredepositCash.class, query,
                params, this.predepositCashDao);
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

    public boolean update(PredepositCash predepositCash){
        try {
            this.predepositCashDao.update(predepositCash);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<PredepositCash> query(String query, Map params, int begin, int max){
        return this.predepositCashDao.query(query, params, begin, max);
    }
}




