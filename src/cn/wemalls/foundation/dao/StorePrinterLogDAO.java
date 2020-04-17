package cn.wemalls.foundation.dao;

import cn.wemalls.core.base.GenericDAO;
import cn.wemalls.foundation.domain.StoreClass;
import cn.wemalls.foundation.domain.StorePrinter;
import cn.wemalls.foundation.domain.StorePrinterLog;

import org.springframework.stereotype.Repository;

@Repository("storePrinterLogDAO")
public class StorePrinterLogDAO extends GenericDAO<StorePrinterLog> {
}

