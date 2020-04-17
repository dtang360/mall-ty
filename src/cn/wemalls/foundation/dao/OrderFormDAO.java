package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.OrderForm;
import org.springframework.stereotype.Repository;

@Repository("orderFormDAO")
public class OrderFormDAO extends GenericDAO<OrderForm> {
}

