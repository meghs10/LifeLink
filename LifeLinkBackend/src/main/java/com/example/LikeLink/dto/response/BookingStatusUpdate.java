package com.example.LikeLink.dto.response;

import com.example.LikeLink.Enum.BookingStatus;

import lombok.Data;

@Data
public class BookingStatusUpdate {
    private BookingStatus status;
}
