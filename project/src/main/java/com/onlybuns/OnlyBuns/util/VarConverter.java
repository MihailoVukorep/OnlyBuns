package com.onlybuns.OnlyBuns.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class VarConverter {
    public Sort parseSort(String sort) {
        Sort sortOrder = Sort.unsorted();

        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1].toUpperCase();

                Sort.Direction dir = Sort.Direction.fromString(direction);
                sortOrder = Sort.by(dir, field);
            }
        }
        return sortOrder;
    }

    public Pageable pageable(Integer page, Integer size, String sort) {
        int pageNumber = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 12 : size; // Default size is 12
        if (pageNumber < 0) { pageNumber = 0; } // Page num can't be negative
        if (pageSize < 1) { pageSize = 1; } // Page size must not be less than one
        return PageRequest.of(pageNumber, pageSize, parseSort(sort));
    }
}
