package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.Goods;
import org.springframework.stereotype.Repository;

@Repository("goodsDAO")
public class GoodsDAO extends GenericDAO<Goods> {
}

