package com.example.LikeLink.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {
	Double latitude; 
	Double longitude; 
	String address; 

}
