package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Coupon;
import org.springframework.stereotype.Repository;

@Repository("couponDAO")
public class CouponDAO extends GenericDAO<Coupon> {
}
