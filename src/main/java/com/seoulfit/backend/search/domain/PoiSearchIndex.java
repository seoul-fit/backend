package com.seoulfit.backend.search.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poi_search_index")
public class PoiSearchIndex {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    @Column(name = "aliases", columnDefinition = "TEXT")
    private String aliases;
    
    @Column(name = "ref_table", nullable = false, length = 50)
    private String refTable;
    
    @Column(name = "ref_id", nullable = false)
    private Long refId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    protected PoiSearchIndex() {}
    
    public PoiSearchIndex(String name, String address, String remark, String aliases, String refTable, Long refId) {
        this.name = name;
        this.address = address;
        this.remark = remark;
        this.aliases = aliases;
        this.refTable = refTable;
        this.refId = refId;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public String getAliases() {
        return aliases;
    }
    
    public String getRefTable() {
        return refTable;
    }
    
    public Long getRefId() {
        return refId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void updateName(String name) {
        this.name = name;
    }
    
    public void updateAddress(String address) {
        this.address = address;
    }
    
    public void updateRemark(String remark) {
        this.remark = remark;
    }
    
    public void updateAliases(String aliases) {
        this.aliases = aliases;
    }
}