package cn.wemalls.core.domain;

import cn.wemalls.core.annotation.Lock;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class IdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;
    private Date addTime;

    @Lock
    private boolean deleteStatus;

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Date getAddTime(){
        return this.addTime;
    }

    public void setAddTime(Date addTime){
        this.addTime = addTime;
    }

    public boolean isDeleteStatus(){
        return this.deleteStatus;
    }

    public void setDeleteStatus(boolean deleteStatus){
        this.deleteStatus = deleteStatus;
    }
}