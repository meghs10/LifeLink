package com.example.LikeLink.dto.response;

import com.example.LikeLink.Enum.BookingStatus;
import com.example.LikeLink.Model.Location;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
	private boolean success;
    private String message;
    private String bookingId;
    private BookingStatus status;
    private String driverId;
    private Location driverLocation;
    
    public BookingResponse(String message, String bookingId, Object data, String status, 
            String driverId) {
					this.success = true;
					this.message = message;
					this.bookingId = bookingId;
					this.status = BookingStatus.valueOf(status);
					this.driverId = driverId;
					}
					
					// Constructor for error response or simple messages
					public BookingResponse(String message, Object data, String status) {
					this.success = data != null;
					this.message = message;
					this.status = status != null ? BookingStatus.valueOf(status) : null;
					}
					
					// Additional helper methods
					public boolean isAssigned() {
					return BookingStatus.ASSIGNED.equals(this.status);
					}
					
					public boolean isCancelled() {
					return BookingStatus.CANCELLED.equals(this.status);
					}
					
					public boolean isCompleted() {
					return BookingStatus.COMPLETED.equals(this.status);
					}
					
					public boolean isSearching() {
					return BookingStatus.SEARCHING.equals(this.status);
					}
					    
    
}