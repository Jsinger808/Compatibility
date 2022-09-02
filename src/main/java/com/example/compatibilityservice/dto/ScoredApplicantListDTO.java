package com.example.compatibilityservice.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class ScoredApplicantListDTO {

    private List<ScoredApplicantDTO> scoredApplicants;
}
