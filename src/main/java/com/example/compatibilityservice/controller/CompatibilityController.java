package com.example.compatibilityservice.controller;


import com.example.compatibilityservice.dto.ScoredApplicantListDTO;
import com.example.compatibilityservice.dto.TeamMemberApplicantDTO;
import com.example.compatibilityservice.service.ApplicantService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/compatibility/")
public class CompatibilityController {

    private final ApplicantService applicantService;

    public CompatibilityController(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

//USER Table

    @PostMapping("/candidate")
    public ScoredApplicantListDTO evaluateCandidates(@RequestBody TeamMemberApplicantDTO teamMemberApplicantDTO) {
        return applicantService.evaluateCandidates(teamMemberApplicantDTO);
    }

}