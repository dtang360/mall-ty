package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Address;
import org.springframework.stereotype.Repository;

@Repository("addressDAO")
public class AddressDAO extends GenericDAO<Address> {
}

