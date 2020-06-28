package com.androidapps.mynote.utilities;

public class Filter {


    String sortBy;
    String orderBy;

    static Filter filter;

    public Filter() {

    }

    public static Filter getFilterForCarInstance() {
        if (filter == null) {
            filter = new Filter();

        }
        return filter;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}
