package ua.testing.demo_jpa.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PageDTO {
    private Integer limit;
    private Integer prevPage;
    private Integer nextPage;
    private Integer currentPage;
    private Integer totalPages;
    private boolean hasNext;
    private boolean hasPrev;
    private String url;
}
