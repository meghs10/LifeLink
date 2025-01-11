package com.example.LikeLink.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;
    private List<String> errors;
}
