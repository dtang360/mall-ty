package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.SpareGoodsClass;
import cn.wemalls.foundation.service.ISpareGoodsClassService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpareGoodsClassServiceImpl
    implements ISpareGoodsClassService {
    @Resource(name = "spareGoodsClassDAO")
    private IGenericDAO<SpareGoodsClass> spareGoodsClassDao;

    public boolean save(SpareGoodsClass spareGoodsClass){
        try {
            this.spareGoodsClassDao.save(spareGoodsClass);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public SpareGoodsClass getObjById(Long id){
        SpareGoodsClass spareGoodsClass = (SpareGoodsClass)this.spareGoodsClassDao.get(id);
        if (spareGoodsClass != null){
            return spareGoodsClass;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.spareGoodsClassDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> spareGoodsClassIds){
        for (Serializable id : spareGoodsClassIds){
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
        GenericPageList pList = new GenericPageList(SpareGoodsClass.class, query,
                params, this.spareGoodsClassDao);
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

    public boolean update(SpareGoodsClass spareGoodsClass){
        try {
            this.spareGoodsClassDao.update(spareGoodsClass);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<SpareGoodsClass> query(String query, Map params, int begin, int max){
        return this.spareGoodsClassDao.query(query, params, begin, max);
    }
}




