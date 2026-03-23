package com.asset.integration.config;

import com.asset.integration.model.TradingCalendar;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 交易日历配置加载器
 * 在应用启动时自动加载配置文件中的交易日历数据
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "trading-calendar")
public class TradingCalendarConfigLoader {

    /**
     * A股休市日期
     */
    private List<String> cnHolidays2024;
    private List<String> cnHolidays2025;
    private List<String> cnHolidays2026;

    /**
     * A股调休补班日
     */
    private List<String> cnWorkdays2024;
    private List<String> cnWorkdays2025;
    private List<String> cnWorkdays2026;

    /**
     * 港股休市日期
     */
    private List<String> hkHolidays2024;
    private List<String> hkHolidays2025;
    private List<String> hkHolidays2026;

    /**
     * 美股休市日期
     */
    private List<String> usHolidays2024;
    private List<String> usHolidays2025;
    private List<String> usHolidays2026;

    /**
     * 应用启动后自动加载配置
     */
    @PostConstruct
    public void loadTradingCalendar() {
        log.info("开始加载交易日历配置...");

        int loadedCount = 0;

        // 加载A股休市日期
        loadedCount += loadHolidays("CN", cnHolidays2024, "A股2024");
        loadedCount += loadHolidays("CN", cnHolidays2025, "A股2025");
        loadedCount += loadHolidays("CN", cnHolidays2026, "A股2026");

        // 加载A股调休补班日
        loadedCount += loadWorkdays(cnWorkdays2024, "2024");
        loadedCount += loadWorkdays(cnWorkdays2025, "2025");
        loadedCount += loadWorkdays(cnWorkdays2026, "2026");

        // 加载港股休市日期
        loadedCount += loadHolidays("HK", hkHolidays2024, "港股2024");
        loadedCount += loadHolidays("HK", hkHolidays2025, "港股2025");
        loadedCount += loadHolidays("HK", hkHolidays2026, "港股2026");

        // 加载美股休市日期
        loadedCount += loadHolidays("US", usHolidays2024, "美股2024");
        loadedCount += loadHolidays("US", usHolidays2025, "美股2025");
        loadedCount += loadHolidays("US", usHolidays2026, "美股2026");

        log.info("交易日历配置加载完成，共加载 {} 条动态数据", loadedCount);
    }

    private int loadHolidays(String market, List<String> dates, String desc) {
        if (dates == null || dates.isEmpty()) {
            return 0;
        }
        TradingCalendar.addHolidays(market, dates);
        log.debug("加载 {} 休市日期: {} 条", desc, dates.size());
        return dates.size();
    }

    private int loadWorkdays(List<String> dates, String year) {
        if (dates == null || dates.isEmpty()) {
            return 0;
        }
        TradingCalendar.addWorkdays(dates);
        log.debug("加载 {} 调休补班日: {} 条", year, dates.size());
        return dates.size();
    }
}
