package com.example.jwt_token.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginatedResult<T> {
    private List<T> data;
    private int page;
    private int limit;
    private int offset;
    private int pages;
    private long total;

    public PaginatedResult(Page<T> pageData) {
        this.data = pageData.getContent();
        this.page = pageData.getNumber() + 1; // ubah ke 1-based
        this.limit = pageData.getSize();
        this.offset = pageData.getNumber() * pageData.getSize();
        this.pages = pageData.getTotalPages();
        this.total = pageData.getTotalElements();
    }
}
