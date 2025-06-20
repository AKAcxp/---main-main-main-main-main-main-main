package org.example.knowmateadmin.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BmiTrendDTO {
    private List<String> dates;
    private List<Double> bmiValues;
}