package com.example.compatibilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributesDTO {

    private Integer intelligence;

    private Integer strength;

    private Integer endurance;

    private Integer spicyFoodTolerance;
}
