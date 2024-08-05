package com.nowcoder.community.entity;

import org.springframework.core.SpringVersion;

//封装分页相关的组件
public class Page {
    //current page number - default is the first page
    private int current = 1;
    // limit
    private int limit = 10;
    //total rows - from server
    // used for calculate the total number of pages
    private int rows;
    // query path 查询路径 - 因为每一页都可以点，所以每一页都有一个路径 - 分页链接
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1 && limit<=100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    /*
     * 获取当前页的起始行
     */
    public int getOffset(){
        // current*limit - limit
        return (current-1)*limit;
    }
    // 获取总页数 -- 页面要显示页码
    public int getTotal(){
        // rows / limit
        if(rows%limit==0){
            return rows/limit;
        }else{
            return rows/limit+1;
        }
    }
// 从第几页到第几页
    // 获取起始页码
    public int getFrom(){

       int from = current-2;
       return from<1 ? 1: from;
    }
    //获取结束页码
    public int getTo(){
        int to  = current+2;
        int total = getTotal();
        return to>total? total:to;
    }
}
