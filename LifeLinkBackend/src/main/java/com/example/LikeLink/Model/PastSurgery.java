package com.example.LikeLink.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PastSurgery {
    private String surgeryType;
    private String approximateDate; 
}
