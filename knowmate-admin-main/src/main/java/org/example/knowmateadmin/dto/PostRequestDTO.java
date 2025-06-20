package org.example.knowmateadmin.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
    private List<String> mediaUrls;
}