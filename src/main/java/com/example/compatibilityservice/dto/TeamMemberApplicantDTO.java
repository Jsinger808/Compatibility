package com.example.compatibilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberApplicantDTO {

    private List<TeamMemberDTO> team;

    private List<ApplicantDTO> applicants;

}
