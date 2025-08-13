package com.seoulfit.backend.search.adapter.in.web.dto;

import com.seoulfit.backend.search.domain.PoiSearchIndex;

public class SearchIndexResponse {
    
    private Long id;
    private String name;
    private String address;
    private String remark;
    private String aliases;
    private String refTable;
    private Long refId;
    
    public SearchIndexResponse() {}
    
    public SearchIndexResponse(PoiSearchIndex index) {
        this.id = index.getId();
        this.name = index.getName();
        this.address = index.getAddress();
        this.remark = index.getRemark();
        this.aliases = index.getAliases();
        this.refTable = index.getRefTable();
        this.refId = index.getRefId();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getAliases() {
        return aliases;
    }
    
    public void setAliases(String aliases) {
        this.aliases = aliases;
    }
    
    public String getRefTable() {
        return refTable;
    }
    
    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }
    
    public Long getRefId() {
        return refId;
    }
    
    public void setRefId(Long refId) {
        this.refId = refId;
    }
}