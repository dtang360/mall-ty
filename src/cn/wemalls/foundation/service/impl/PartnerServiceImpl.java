package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.Partner;
import cn.wemalls.foundation.service.IPartnerService;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PartnerServiceImpl
    implements IPartnerService {
    @Resource(name = "partnerDAO")
    private IGenericDAO<Partner> partnerDao;

    public boolean save(Partner partner){
        try {
            this.partnerDao.save(partner);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public Partner getObjById(Long id){
        Partner partner = (Partner)this.partnerDao.get(id);
        if (partner != null){
            return partner;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.partnerDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> partnerIds){
        for (Serializable id : partnerIds){
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
        GenericPageList pList = new GenericPageList(Partner.class, query,
                params, this.partnerDao);
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

    public boolean update(Partner partner){
        try {
            this.partnerDao.update(partner);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<Partner> query(String query, Map params, int begin, int max){
        return this.partnerDao.query(query, params, begin, max);
    }
}




