package com.seoulfit.backend.search.adapter.in.web.dto;

public class SearchRequest {
    
    private String keyword;
    private Integer page = 0;
    private Integer size = 10;
    
    public SearchRequest() {}
    
    public SearchRequest(String keyword, Integer page, Integer size) {
        this.keyword = keyword;
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public Integer getPage() {
        return page != null ? page : 0;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size != null ? size : 10;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
}