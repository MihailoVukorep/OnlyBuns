package com.onlybuns.OnlyBuns.util;

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
}
