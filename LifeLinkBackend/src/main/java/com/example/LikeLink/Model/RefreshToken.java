package com.example.LikeLink.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;

@Data
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    @Indexed
    private String userId;
    
    private Instant expiryDate;
}
