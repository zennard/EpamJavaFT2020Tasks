package ua.testing.demo_jpa.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PageDTO {
    Integer limit;
    Integer prevPage;
    Integer nextPage;
    Integer currentPage;
    Integer totalPages;
    boolean hasNext;
    boolean hasPrev;
    String url;
}
