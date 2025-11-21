package br.com.datasinapse.ecommerce.dto;

import java.util.List;

public class PaginatedResponse<T>{
    private List<T> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public PaginatedResponse(List<T> data, long totalElements, PaginationRequest request) {
        this.data = data;
        this.totalElements = totalElements;
        this.currentPage = request.getPage();
        this.pageSize = request.getSize();
        this.totalPages = (int) Math.ceil((double) totalElements / request.getSize());
    }

    public List<T> getData() { return data; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
}
