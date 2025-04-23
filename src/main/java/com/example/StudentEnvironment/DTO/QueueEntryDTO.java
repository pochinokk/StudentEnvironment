package com.example.StudentEnvironment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueEntryDTO {
    private Long placeId;
    private String fullName;
    private LocalDateTime time;
    private Long userId;
    public QueueEntryDTO(String fullName, LocalDateTime time) {
        this.fullName = fullName;
        this.time = time;
    }
}