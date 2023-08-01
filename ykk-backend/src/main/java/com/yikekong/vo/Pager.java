package com.yikekong.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Pager
 * @param <T> The type of the object
 */
@Data
public class Pager<T> implements Serializable{
    // Total number of records
    private long counts;
     // Number of records per page
    private long pageSize;
     // Total number of pages
    private long pages;
     // Current page number
    private long page;
     // The data of the current page
    private List<T> items;

    public Pager(IPage page) {
        this.pageSize = page.getSize();
        this.counts = page.getTotal();
        this.page = page.getCurrent();
        this.pages = page.getPages();
        this.items = page.getRecords();
    }

    public Pager(Long counts,Long pageSize){
        this.counts = counts;
        this.pageSize = pageSize;
        if(pageSize <= 0){
            pages = 0;
        }else {
            pages = counts%pageSize ==0? (counts/pageSize) : (counts/pageSize) +1;
        }
    }
}
