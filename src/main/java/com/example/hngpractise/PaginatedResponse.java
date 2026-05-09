package com.example.hngpractise;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private String status;
    private int page;
    private int limit;
    private long total;
    private int total_pages;
    private Map<String, String> links;
    private List<T> data;
}
