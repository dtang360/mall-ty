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
@Table(name = "wemalls_store_printer")
public class StorePrinter extends IdEntity {
	
    //类型名称
    private String printer_sn;
    
    private String printer_key;
    
    private String printer_name;
    
    private String printer_sim_num;
    //等待打印数
    private int wait_print_count;
    
    private Date last_print_time;
    
    private Date last_online_time;

    //店铺父类型
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

	public String getPrinter_sn() {
		return printer_sn;
	}

	public void setPrinter_sn(String printer_sn) {
		this.printer_sn = printer_sn;
	}

	public String getPrinter_key() {
		return printer_key;
	}

	public void setPrinter_key(String printer_key) {
		this.printer_key = printer_key;
	}

	public String getPrinter_name() {
		return printer_name;
	}

	public void setPrinter_name(String printer_name) {
		this.printer_name = printer_name;
	}

	public String getPrinter_sim_num() {
		return printer_sim_num;
	}

	public void setPrinter_sim_num(String printer_sim_num) {
		this.printer_sim_num = printer_sim_num;
	}

	public int getWait_print_count() {
		return wait_print_count;
	}

	public void setWait_print_count(int wait_print_count) {
		this.wait_print_count = wait_print_count;
	}

	public Date getLast_print_time() {
		return last_print_time;
	}

	public void setLast_print_time(Date last_print_time) {
		this.last_print_time = last_print_time;
	}

	public Date getLast_online_time() {
		return last_online_time;
	}

	public void setLast_online_time(Date last_online_time) {
		this.last_online_time = last_online_time;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

    
}




