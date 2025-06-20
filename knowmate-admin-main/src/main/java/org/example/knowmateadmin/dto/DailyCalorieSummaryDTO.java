package org.example.knowmateadmin.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DailyCalorieSummaryDTO {
    private List<String> dates;
    private List<Double> consumedCalories;
    private List<Double> burnedCalories;
}