package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Favorite;
import org.springframework.stereotype.Repository;

@Repository("favoriteDAO")
public class FavoriteDAO extends GenericDAO<Favorite> {
}

