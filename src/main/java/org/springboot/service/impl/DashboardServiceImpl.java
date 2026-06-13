package org.springboot.service.impl;

import org.springboot.entity.dto.DashboardTrendsDTO;
import org.springboot.entity.dto.TrendPointDTO;
import org.springboot.mapper.DashboardMapper;
import org.springboot.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final int TREND_DAYS = 10;

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public DashboardTrendsDTO getDashboardTrends() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(TREND_DAYS - 1L);

        List<TrendPointDTO> bookAdds = fillDailyCounts(
                dashboardMapper.countBookAddsByDate(startDate, endDate),
                startDate
        );
        List<TrendPointDTO> userTotals = buildUserTotals(startDate, endDate);

        return new DashboardTrendsDTO(bookAdds, userTotals);
    }

    private List<TrendPointDTO> fillDailyCounts(List<TrendPointDTO> source, LocalDate startDate) {
        Map<LocalDate, Long> countMap = source.stream()
                .collect(Collectors.toMap(TrendPointDTO::getDate, TrendPointDTO::getCount));

        return IntStream.range(0, TREND_DAYS)
                .mapToObj(offset -> {
                    LocalDate date = startDate.plusDays(offset);
                    return new TrendPointDTO(date, countMap.getOrDefault(date, 0L));
                })
                .toList();
    }

    private List<TrendPointDTO> buildUserTotals(LocalDate startDate, LocalDate endDate) {
        List<TrendPointDTO> userAdds = dashboardMapper.countUserAddsByDate(startDate, endDate);
        Map<LocalDate, Long> userAddMap = new HashMap<>();
        for (TrendPointDTO point : userAdds) {
            userAddMap.put(point.getDate(), point.getCount());
        }

        long total = dashboardMapper.countUsersBefore(startDate);
        List<TrendPointDTO> result = new ArrayList<>();
        for (int offset = 0; offset < TREND_DAYS; offset++) {
            LocalDate date = startDate.plusDays(offset);
            total += userAddMap.getOrDefault(date, 0L);
            result.add(new TrendPointDTO(date, total));
        }
        return result;
    }
}
