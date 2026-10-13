package com.jr.todo.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jr.todo.dto.PagedResponse;

public class PaginationUtil {

    private static final int PAGE_SIZE = 5;

    private PaginationUtil() {
    }

    public static Pageable of(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("Pagina no valida");
        }
        return PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "dateCreation"));
    }

    public static <T> PagedResponse<T> toResponse(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious());
    }

}
