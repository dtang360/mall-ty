package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.Res;
import cn.wemalls.foundation.service.IResService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResService
    implements IResService {
    @Resource(name = "resDAO")
    private IGenericDAO<Res> resDAO;

    public boolean delete(Long id){
        try {
            this.resDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean save(Res res){
        try {
            this.resDAO.save(res);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Res res){
        try {
            this.resDAO.update(res);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public Res getObjById(Long id){
        return (Res)this.resDAO.get(id);
    }

    public List<Res> query(String query, Map params, int begin, int max){
        return this.resDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Res.class, query, params,
                this.resDAO);
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
}




