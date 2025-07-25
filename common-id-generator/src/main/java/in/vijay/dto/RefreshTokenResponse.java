package in.vijay.dto;

import lombok.*;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshTokenResponse {
    private int id;
    private String token;
    private Instant expiryDate;
    private String username; // Added Username as Identifier
    private String email;


    // Method to check if token is expired
    /*public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }*/
}