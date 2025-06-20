package org.example.knowmateadmin.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class NutritionSummaryDTO {
    private List<String> labels;
    private List<Double> values;
}