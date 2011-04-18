package com.taobao.top.pageapi.domain;

import java.util.Date;


public class WhiteListDomain {
    // 主键
    private Long id;

    // 域名
    private String domain;

    private Date gmtCreate;
    private Date gmtModified;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getDomain() {
        return domain;
    }


    public void setDomain(String domain) {
        this.domain = domain;
    }


    public Date getGmtCreate() {
        return gmtCreate;
    }


    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }


    public Date getGmtModified() {
        return gmtModified;
    }


    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

}
