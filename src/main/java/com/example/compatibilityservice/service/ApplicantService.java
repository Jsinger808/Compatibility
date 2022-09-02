package com.example.compatibilityservice.service;


import com.example.compatibilityservice.dto.*;
import com.example.compatibilityservice.exception.InvalidScoreException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.zip.CheckedOutputStream;

@Service
@PropertySource(ignoreResourceNotFound = true, value = "classpath:priorityValues.properties")
public class ApplicantService {

    @Value("${firstPriority}")
    private Double firstPriority;

    @Value("${secondPriority}")
    private Double secondPriority;

    @Value("${thirdPriority}")
    private Double thirdPriority;

    @Value("${fourthPriority}")
    private Double fourthPriority;


    public ScoredApplicantListDTO evaluateCandidates(TeamMemberApplicantDTO teamMemberApplicantDTO) {
        ScoredApplicantListDTO scoredApplicantListDTO = new ScoredApplicantListDTO();
        List<ScoredApplicantDTO> list = new ArrayList<>();

        HashMap<String, Double>priorityMap = new LinkedHashMap<>();
        priorityMap.put("Intelligence", 0.0);
        priorityMap.put("Endurance", 0.0);
        priorityMap.put("Strength", 0.0);
        priorityMap.put("SpicyFoodTolerance", 0.0);

        Double priorityArray[] = new Double[] {firstPriority, secondPriority, thirdPriority, fourthPriority};

        for (TeamMemberDTO teamMemberDTO : teamMemberApplicantDTO.getTeam()) {

            Integer teamIntelligence = teamMemberDTO.getAttributes().getIntelligence();
            Integer teamStrength = teamMemberDTO.getAttributes().getStrength();
            Integer teamEndurance = teamMemberDTO.getAttributes().getEndurance();
            Integer teamSpicyFoodTolerance = teamMemberDTO.getAttributes().getSpicyFoodTolerance();

            //Throws invalid scores
            checkForInvalidScores(teamIntelligence, teamStrength, teamEndurance, teamSpicyFoodTolerance);

            priorityMap.put("Intelligence", priorityMap.get("Intelligence") + teamIntelligence);
            priorityMap.put("Strength", priorityMap.get("Strength") + teamStrength);
            priorityMap.put("Endurance", priorityMap.get("Endurance") + teamEndurance);
            priorityMap.put("SpicyFoodTolerance", priorityMap.get("SpicyFoodTolerance") + teamSpicyFoodTolerance);
        }

        //Creates a reverse priority queue to sort team's total attributes to pop off Max attribute first
        PriorityQueue<Double> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
        for (Double value : priorityMap.values()) {
            priorityQueue.add(value);
        }

        //Sets Max attribute to first priority, second Max attribute to second priority, etc....
        for (int i = 0; i < 4; ++i) {
            Double value = priorityQueue.poll();
            for(String key: priorityMap.keySet()) {
                if(priorityMap.get(key).equals(value)) {
                    priorityMap.put(key, priorityArray[i]);
                    //Prevents attributes with the same total score to get the same priority value
                    //In case of tie, prioritizes left to right: Intelligence, Endurance, Strength, SpicyFoodTolerance
                    break;
                }
            }
        }

        for (ApplicantDTO applicantDTO : teamMemberApplicantDTO.getApplicants()) {
            ScoredApplicantDTO scoredApplicantDTO = new ScoredApplicantDTO();
            scoredApplicantDTO.setName(applicantDTO.getName());

            Integer applicantIntelligence = applicantDTO.getAttributes().getIntelligence();
            Integer applicantStrength = applicantDTO.getAttributes().getStrength();
            Integer applicantEndurance = applicantDTO.getAttributes().getEndurance();
            Integer applicantSpicyFoodTolerance = applicantDTO.getAttributes().getSpicyFoodTolerance();

            //Throws invalid scores
            checkForInvalidScores(applicantIntelligence, applicantStrength, applicantEndurance, applicantSpicyFoodTolerance);

            //Finds average of four digits
            Double totalScore = applicantIntelligence * priorityMap.get("Intelligence") +
                    applicantStrength * priorityMap.get("Strength") +
                    applicantEndurance * priorityMap.get("Endurance") +
                    applicantSpicyFoodTolerance * priorityMap.get("SpicyFoodTolerance");
            scoredApplicantDTO.setScore(totalScore / 40.0);
            list.add(scoredApplicantDTO);
        }
        scoredApplicantListDTO.setScoredApplicants(list);
        return scoredApplicantListDTO;
    }

    public void checkForInvalidScores (Integer intelligence, Integer strength, Integer endurance, Integer spicyFoodTolerance) throws InvalidScoreException {
        if ((intelligence == null || intelligence > 10 || intelligence < 0) ||
                (strength == null || strength > 10 || strength < 0) ||
                (endurance == null || endurance > 10 || endurance < 0) ||
                (spicyFoodTolerance == null || spicyFoodTolerance > 10 || spicyFoodTolerance < 0)
        ) {
            throw new InvalidScoreException();
        }
    }
}
