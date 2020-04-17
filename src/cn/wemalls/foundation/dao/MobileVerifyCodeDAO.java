package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.MobileVerifyCode;
import org.springframework.stereotype.Repository;

@Repository("mobileVerifyCodeDAO")
public class MobileVerifyCodeDAO extends GenericDAO<MobileVerifyCode> {
}
