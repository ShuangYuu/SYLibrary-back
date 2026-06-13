package org.springboot.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTrendsDTO {
    private List<TrendPointDTO> recentBookAdds;
    private List<TrendPointDTO> userTotals;
}
