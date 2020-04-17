package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Article;
import org.springframework.stereotype.Repository;

@Repository("articleDAO")
public class ArticleDAO extends GenericDAO<Article> {
}
