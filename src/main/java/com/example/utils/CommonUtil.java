package com.example.utils;

import java.io.Serializable;

public class CommonUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static int normalizePageNumber(Integer pageNumber) {
        if (pageNumber == null) {
            return 0;
        }
        return pageNumber <= 0 ? 0 : pageNumber;
    }

    public static int normalizePageSize(Integer pageSize) {
        if (pageSize == null) {
            return 10;
        }
        return pageSize <= 0 ? 10 : pageSize;
    }
}