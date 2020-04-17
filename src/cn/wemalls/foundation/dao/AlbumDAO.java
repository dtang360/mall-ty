package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Album;
import org.springframework.stereotype.Repository;

@Repository("albumDAO")
public class AlbumDAO extends GenericDAO<Album> {
}

