package cn.wemalls.foundation.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import cn.wemalls.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemalls_store_printer_log")
public class StorePrinterLog extends IdEntity {
	
    //店铺
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;
    
    //打印机
    @ManyToOne(fetch = FetchType.LAZY)
    private StorePrinter storePrinter;
    
    //打印状态
    private int print_status;
    
    //打印类型
    private int print_type;
    
    //打印内容
    private String print_content;
    
    private int print_count;
    
    private String printerSN;
    
//    private String print_api_user;
    
    private String print_api_name;
    
    private String ret_code;
    
    private String ret_content;
    
    private Date ret_time;

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public StorePrinter getStorePrinter() {
		return storePrinter;
	}

	public void setStorePrinter(StorePrinter storePrinter) {
		this.storePrinter = storePrinter;
	}

	public int getPrint_status() {
		return print_status;
	}

	public void setPrint_status(int print_status) {
		this.print_status = print_status;
	}

	public int getPrint_type() {
		return print_type;
	}

	public void setPrint_type(int print_type) {
		this.print_type = print_type;
	}

	public String getPrint_content() {
		return print_content;
	}

	public void setPrint_content(String print_content) {
		this.print_content = print_content;
	}

	public int getPrint_count() {
		return print_count;
	}

	public void setPrint_count(int print_count) {
		this.print_count = print_count;
	}

	public String getPrinterSN() {
		return printerSN;
	}

	public void setPrinterSN(String printerSN) {
		this.printerSN = printerSN;
	}

	public String getPrint_api_name() {
		return print_api_name;
	}

	public void setPrint_api_name(String print_api_name) {
		this.print_api_name = print_api_name;
	}

	public String getRet_code() {
		return ret_code;
	}

	public void setRet_code(String ret_code) {
		this.ret_code = ret_code;
	}

	public String getRet_content() {
		return ret_content;
	}

	public void setRet_content(String ret_content) {
		this.ret_content = ret_content;
	}

	public Date getRet_time() {
		return ret_time;
	}

	public void setRet_time(Date ret_time) {
		this.ret_time = ret_time;
	}

}




