package com.example.compatibilityservice.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ScoredApplicantDTO {

    private String name;

    private Double score;
}
