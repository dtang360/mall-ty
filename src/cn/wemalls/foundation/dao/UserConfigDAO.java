package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.UserConfig;
import org.springframework.stereotype.Repository;

@Repository("userConfigDAO")
public class UserConfigDAO extends GenericDAO<UserConfig> {
}

