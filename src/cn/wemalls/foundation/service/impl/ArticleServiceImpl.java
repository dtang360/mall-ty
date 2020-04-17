package cn.wemalls.foundation.service.impl;

import cn.wemalls.core.dao.IGenericDAO;
import cn.wemalls.core.query.GenericPageList;
import cn.wemalls.core.query.PageObject;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.query.support.IQueryObject;
import cn.wemalls.foundation.domain.Article;
import cn.wemalls.foundation.service.IArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ArticleServiceImpl
    implements IArticleService {
    @Resource(name = "articleDAO")
    private IGenericDAO<Article> articleDao;

    public boolean save(Article article){
        try {
            this.articleDao.save(article);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public Article getObjById(Long id){
        Article article = (Article) this.articleDao.get(id);
        if (article != null){
            return article;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.articleDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> articleIds){
        for (Serializable id : articleIds){
            delete((Long) id);
        }

        return true;
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Article.class, query,
                params, this.articleDao);
        if (properties != null){
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null)
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
                             .getCurrentPage().intValue(), pageObj.getPageSize() == null ? 0 :
                             pageObj.getPageSize().intValue());
        }else{
            pList.doList(0, -1);
        }
        
        List<Article> rslist=pList.getResult();
        if(rslist!=null&&rslist.size()>0){
        	for(int i=0;i<rslist.size();i++){
        		if(rslist.get(i).getContent()!=null){
        			String actString=rslist.get(i).getContent().toString();
        			int fint =actString.indexOf("img");
        			int furl =actString.indexOf("src=\"",fint);
        			furl+=5;
        			int eurl =actString.indexOf("\"",furl);
        			//        	int indexOf(String str, int startIndex)
        			if(actString!=null&&actString.length()>0&&fint>0){
        				String firstImgUrl=actString.substring(furl, eurl);
        				if(firstImgUrl!=null&&firstImgUrl.length()>0){
        					rslist.get(i).setFirstImgUrl(firstImgUrl);
        				}
        			}
        		}
        	}
        	pList.setResult(rslist);
        }
        return pList;
    }

    public boolean update(Article article){
        try {
            this.articleDao.update(article);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<Article> query(String query, Map params, int begin, int max){
    	List<Article> rslist=this.articleDao.query(query, params, begin, max);
        if(rslist!=null&&rslist.size()>0){
        	for(int i=0;i<rslist.size();i++){
        		if(rslist.get(i).getContent()!=null){
        			String actString=rslist.get(i).getContent().toString();
        			int fint =actString.indexOf("img");
        			int furl =actString.indexOf("src=\"",fint);
        			furl+=5;
        			int eurl =actString.indexOf("\"",furl);
        			if(actString!=null&&actString.length()>0&&fint>0){
        				String firstImgUrl=actString.substring(furl, eurl);
        				if(firstImgUrl!=null&&firstImgUrl.length()>0){
        					rslist.get(i).setFirstImgUrl(firstImgUrl);
        				}
        			}
        		}
        	}
        	return rslist;
        }
        return null;
    }

    public Article getObjByProperty(String propertyName, Object value){
        Article obj;
        try {
            return (Article) this.articleDao.getBy(propertyName, value);
        } catch (Exception e){
            obj = new Article();
            obj.setTitle("文章错误");
        }

        return obj;
    }
}




