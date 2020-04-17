package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Complaint;
import org.springframework.stereotype.Repository;

@Repository("complaintDAO")
public class ComplaintDAO extends GenericDAO<Complaint> {
}

