package com.example.compatibilityservice.integration;

import com.example.compatibilityservice.CompatibilityServiceApplication;
import com.example.compatibilityservice.dto.*;

import com.example.compatibilityservice.exception.InvalidScoreException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CompatibilityServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:priorityValues.properties")
public class CompatibilityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${firstPriority}")
    private Double firstPriority;

    @Value("${secondPriority}")
    private Double secondPriority;

    @Value("${thirdPriority}")
    private Double thirdPriority;

    @Value("${fourthPriority}")
    private Double fourthPriority;


    @Test
    public void WhenPostTestMemberApplicant_GivenNormalData_ShouldReturnNormal() throws Exception {

        AttributesDTO teamMember1AttributesDTO = new AttributesDTO(1, 10, 1, 1);
        AttributesDTO teamMember2AttributesDTO = new AttributesDTO(10, 1, 1, 1);
        TeamMemberDTO teamMember1DTO = new TeamMemberDTO("Bob", teamMember1AttributesDTO);
        TeamMemberDTO teamMember2DTO = new TeamMemberDTO("Susan", teamMember2AttributesDTO);
        List<TeamMemberDTO> teamList = new ArrayList<TeamMemberDTO>();
        teamList.add(teamMember1DTO);
        teamList.add(teamMember2DTO);

        AttributesDTO applicant1AttributesDTO = new AttributesDTO(10, 10, 1, 1);
        AttributesDTO applicant2AttributesDTO = new AttributesDTO(1, 1, 10, 10);
        ApplicantDTO applicant1DTO = new ApplicantDTO("John", applicant1AttributesDTO);
        ApplicantDTO applicant2DTO = new ApplicantDTO("Chloe", applicant2AttributesDTO);
        List<ApplicantDTO> applicantList = new ArrayList<ApplicantDTO>();
        applicantList.add(applicant1DTO);
        applicantList.add(applicant2DTO);

        TeamMemberApplicantDTO expected = new TeamMemberApplicantDTO(teamList, applicantList);

        HashMap<String, Double> priorityMap = new LinkedHashMap<>();
        priorityMap.put("Intelligence", 0.0);
        priorityMap.put("Endurance", 0.0);
        priorityMap.put("Strength", 0.0);
        priorityMap.put("SpicyFoodTolerance", 0.0);

        priorityMap.put("Intelligence", priorityMap.get("Intelligence") +
                teamMember1DTO.getAttributes().getIntelligence() + teamMember2DTO.getAttributes().getIntelligence());
        priorityMap.put("Strength", priorityMap.get("Strength") +
                teamMember1DTO.getAttributes().getStrength() + teamMember2DTO.getAttributes().getStrength());
        priorityMap.put("Endurance", priorityMap.get("Endurance") +
                teamMember1DTO.getAttributes().getEndurance() + teamMember2DTO.getAttributes().getEndurance());
        priorityMap.put("SpicyFoodTolerance", priorityMap.get("SpicyFoodTolerance") +
                teamMember1DTO.getAttributes().getSpicyFoodTolerance() + teamMember2DTO.getAttributes().getSpicyFoodTolerance());

        Double priorityArray[] = new Double[]{firstPriority, secondPriority, thirdPriority, fourthPriority};

        PriorityQueue<Double> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
        for (Double value : priorityMap.values()) {
            priorityQueue.add(value);
        }

        //Sets Max attribute to first priority, second Max attribute to second priority, etc....
        for (int i = 0; i < 4; ++i) {
            Double value = priorityQueue.poll();
            for (String key : priorityMap.keySet()) {
                if (priorityMap.get(key).equals(value)) {
                    priorityMap.put(key, priorityArray[i]);
                    //Prevents attributes with the same total score to get the same priority value
                    //In case of tie, prioritizes left to right: Intelligence, Endurance, Strength, SpicyFoodTolerance
                    break;
                }
            }
        }


        //Finds average of four digits
        Double expectedScore1 = (applicant1DTO.getAttributes().getIntelligence() * priorityMap.get("Intelligence") +
                applicant1DTO.getAttributes().getStrength() * priorityMap.get("Strength") +
                applicant1DTO.getAttributes().getEndurance() * priorityMap.get("Endurance") +
                applicant1DTO.getAttributes().getSpicyFoodTolerance() * priorityMap.get("SpicyFoodTolerance")) / 40.0;

        Double expectedScore2 = (applicant2DTO.getAttributes().getIntelligence() * priorityMap.get("Intelligence") +
                applicant2DTO.getAttributes().getStrength() * priorityMap.get("Strength") +
                applicant2DTO.getAttributes().getEndurance() * priorityMap.get("Endurance") +
                applicant2DTO.getAttributes().getSpicyFoodTolerance() * priorityMap.get("SpicyFoodTolerance")) / 40.0;

        mockMvc.perform(post("/api/v1/compatibility/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scoredApplicants.[0].name", Matchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.scoredApplicants.[1].name", Matchers.is("Chloe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.scoredApplicants.[0].score", Matchers.is(expectedScore1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.scoredApplicants.[1].score", Matchers.is(expectedScore2)));


    }

    @Test
    public void WhenPostTestMemberApplicant_GivenDataWithBadScores_ShouldThrowInvalidScoreException() throws Exception {

        AttributesDTO teamMember1AttributesDTO = new AttributesDTO(1, 15, 1, 1);
        AttributesDTO teamMember2AttributesDTO = new AttributesDTO(10, 1, 1, 1);
        TeamMemberDTO teamMember1DTO = new TeamMemberDTO("Bob", teamMember1AttributesDTO);
        TeamMemberDTO teamMember2DTO = new TeamMemberDTO("Susan", teamMember2AttributesDTO);
        List<TeamMemberDTO> teamList = new ArrayList<TeamMemberDTO>();
        teamList.add(teamMember1DTO);
        teamList.add(teamMember2DTO);

        AttributesDTO applicant1AttributesDTO = new AttributesDTO(10, 10, 1, 1);
        AttributesDTO applicant2AttributesDTO = new AttributesDTO(1, 1, 10, 10);
        ApplicantDTO applicant1DTO = new ApplicantDTO("John", applicant1AttributesDTO);
        ApplicantDTO applicant2DTO = new ApplicantDTO("Chloe", applicant2AttributesDTO);
        List<ApplicantDTO> applicantList = new ArrayList<ApplicantDTO>();
        applicantList.add(applicant1DTO);
        applicantList.add(applicant2DTO);

        TeamMemberApplicantDTO expected = new TeamMemberApplicantDTO(teamList, applicantList);

        mockMvc.perform(post("/api/v1/compatibility/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidScoreException))
                .andExpect(content().string("Invalid score(s). Please enter a score between 0 - 10"));
    }

}
