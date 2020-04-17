package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Document;
import org.springframework.stereotype.Repository;

@Repository("documentDAO")
public class DocumentDAO extends GenericDAO<Document> {
}

