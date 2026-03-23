package com.asset.integration.model;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

/**
 * 交易日历
 * 支持判断各市场是否为交易日
 */
public class TradingCalendar {

    /**
     * 市场类型
     */
    public enum Market {
        A_SHARE("A股", "cn"),
        HK("港股", "hk"),
        US("美股", "us"),
        FUND("基金", "cn");  // 基金跟随A股

        @Getter
        private final String name;
        @Getter
        private final String code;

        Market(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }

    /**
     * 2024-2026年A股休市日期（含港股通休市）
     * 数据来源：上交所/深交所公告
     */
    private static final Set<LocalDate> CN_HOLIDAYS_2024 = Set.of(
            // 元旦
            LocalDate.of(2024, 1, 1),
            // 春节
            LocalDate.of(2024, 2, 9),
            LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 12),
            LocalDate.of(2024, 2, 13), LocalDate.of(2024, 2, 14), LocalDate.of(2024, 2, 15),
            LocalDate.of(2024, 2, 16), LocalDate.of(2024, 2, 17),
            // 清明节
            LocalDate.of(2024, 4, 4), LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 6),
            // 劳动节
            LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 2), LocalDate.of(2024, 5, 3),
            LocalDate.of(2024, 5, 4), LocalDate.of(2024, 5, 5),
            // 端午节
            LocalDate.of(2024, 6, 10),
            // 中秋节
            LocalDate.of(2024, 9, 15), LocalDate.of(2024, 9, 16), LocalDate.of(2024, 9, 17),
            // 国庆节
            LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 2), LocalDate.of(2024, 10, 3),
            LocalDate.of(2024, 10, 4), LocalDate.of(2024, 10, 5), LocalDate.of(2024, 10, 6),
            LocalDate.of(2024, 10, 7)
    );

    private static final Set<LocalDate> CN_HOLIDAYS_2025 = Set.of(
            // 元旦
            LocalDate.of(2025, 1, 1),
            // 春节
            LocalDate.of(2025, 1, 28), LocalDate.of(2025, 1, 29), LocalDate.of(2025, 1, 30),
            LocalDate.of(2025, 1, 31), LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2),
            LocalDate.of(2025, 2, 3), LocalDate.of(2025, 2, 4),
            // 清明节
            LocalDate.of(2025, 4, 4), LocalDate.of(2025, 4, 5), LocalDate.of(2025, 4, 6),
            // 劳动节
            LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 2), LocalDate.of(2025, 5, 3),
            LocalDate.of(2025, 5, 4), LocalDate.of(2025, 5, 5),
            // 端午节
            LocalDate.of(2025, 5, 31), LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 2),
            // 中秋节+国庆节
            LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 2), LocalDate.of(2025, 10, 3),
            LocalDate.of(2025, 10, 4), LocalDate.of(2025, 10, 5), LocalDate.of(2025, 10, 6),
            LocalDate.of(2025, 10, 7), LocalDate.of(2025, 10, 8)
    );

    private static final Set<LocalDate> CN_HOLIDAYS_2026 = Set.of(
            // 元旦
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 3),
            // 春节（预估）
            LocalDate.of(2026, 2, 16), LocalDate.of(2026, 2, 17), LocalDate.of(2026, 2, 18),
            LocalDate.of(2026, 2, 19), LocalDate.of(2026, 2, 20), LocalDate.of(2026, 2, 21),
            LocalDate.of(2026, 2, 22), LocalDate.of(2026, 2, 23),
            // 清明节（预估）
            LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 6), LocalDate.of(2026, 4, 7),
            // 劳动节（预估）
            LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 2), LocalDate.of(2026, 5, 3),
            LocalDate.of(2026, 5, 4), LocalDate.of(2026, 5, 5),
            // 端午节（预估）
            LocalDate.of(2026, 6, 19), LocalDate.of(2026, 6, 20), LocalDate.of(2026, 6, 21),
            // 中秋节+国庆节（预估，10/3中秋与国庆重叠）
            LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 3),
            LocalDate.of(2026, 10, 4), LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 6),
            LocalDate.of(2026, 10, 7), LocalDate.of(2026, 10, 8)
    );

    /**
     * A股周末补班日（调休上班）
     */
    private static final Set<LocalDate> CN_WORKDAYS_ON_WEEKEND = Set.of(
            // 2024年调休
            LocalDate.of(2024, 2, 4),   // 春节调休
            LocalDate.of(2024, 2, 18),  // 春节调休
            LocalDate.of(2024, 4, 7),   // 清明调休
            LocalDate.of(2024, 4, 28),  // 劳动节调休
            LocalDate.of(2024, 5, 11),  // 劳动节调休
            LocalDate.of(2024, 9, 14),  // 中秋调休
            LocalDate.of(2024, 9, 29),  // 国庆调休
            LocalDate.of(2024, 10, 12), // 国庆调休
            // 2025年调休
            LocalDate.of(2025, 1, 26),  // 春节调休
            LocalDate.of(2025, 2, 8),   // 春节调休
            LocalDate.of(2025, 4, 27),  // 劳动节调休
            LocalDate.of(2025, 9, 28),  // 国庆调休
            LocalDate.of(2025, 10, 11), // 国庆调休
            // 2026年调休（预估）
            LocalDate.of(2026, 2, 7),   // 春节调休
            LocalDate.of(2026, 2, 14),  // 春节调休
            LocalDate.of(2026, 9, 27),  // 国庆调休
            LocalDate.of(2026, 10, 10)  // 国庆调休
    );

    /**
     * 香港公众假期 2024-2026
     */
    private static final Set<LocalDate> HK_HOLIDAYS_2024 = Set.of(
            LocalDate.of(2024, 1, 1),   // 元旦
            LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 12),
            LocalDate.of(2024, 2, 13),  // 春节
            LocalDate.of(2024, 3, 29), LocalDate.of(2024, 3, 30), // 复活节
            LocalDate.of(2024, 4, 4),   // 清明节
            LocalDate.of(2024, 5, 1),   // 劳动节
            LocalDate.of(2024, 5, 15),  // 佛诞
            LocalDate.of(2024, 6, 10),  // 端午节
            LocalDate.of(2024, 7, 1),   // 香港回归纪念日
            LocalDate.of(2024, 9, 18),  // 中秋节翌日
            LocalDate.of(2024, 10, 1),  // 国庆节
            LocalDate.of(2024, 10, 11), // 重阳节
            LocalDate.of(2024, 12, 25), LocalDate.of(2024, 12, 26) // 圣诞节
    );

    private static final Set<LocalDate> HK_HOLIDAYS_2025 = Set.of(
            LocalDate.of(2025, 1, 1),   // 元旦
            LocalDate.of(2025, 1, 29), LocalDate.of(2025, 1, 30), LocalDate.of(2025, 1, 31), // 春节
            LocalDate.of(2025, 4, 4),   // 清明节
            LocalDate.of(2025, 4, 18), LocalDate.of(2025, 4, 19), LocalDate.of(2025, 4, 21), // 复活节
            LocalDate.of(2025, 5, 1),   // 劳动节
            LocalDate.of(2025, 5, 5),   // 佛诞
            LocalDate.of(2025, 6, 2),   // 端午节
            LocalDate.of(2025, 7, 1),   // 香港回归纪念日
            LocalDate.of(2025, 10, 1),  // 国庆节
            LocalDate.of(2025, 10, 7),  // 中秋节翌日
            LocalDate.of(2025, 10, 29), // 重阳节
            LocalDate.of(2025, 12, 25), LocalDate.of(2025, 12, 26) // 圣诞节
    );

    private static final Set<LocalDate> HK_HOLIDAYS_2026 = Set.of(
            LocalDate.of(2026, 1, 1),   // 元旦
            LocalDate.of(2026, 2, 17), LocalDate.of(2026, 2, 18), LocalDate.of(2026, 2, 19),
            LocalDate.of(2026, 2, 20),  // 春节（预估）
            LocalDate.of(2026, 4, 3), LocalDate.of(2026, 4, 6), LocalDate.of(2026, 4, 7), // 清明+复活节
            LocalDate.of(2026, 5, 1),   // 劳动节
            LocalDate.of(2026, 5, 25),  // 佛诞（预估）
            LocalDate.of(2026, 6, 19),  // 端午节
            LocalDate.of(2026, 7, 1),   // 香港回归纪念日
            LocalDate.of(2026, 10, 1),  // 国庆节
            LocalDate.of(2026, 10, 3),  // 中秋节翌日
            LocalDate.of(2026, 10, 19), // 重阳节
            LocalDate.of(2026, 12, 25), LocalDate.of(2026, 12, 26) // 圣诞节
    );

    /**
     * 美国市场假期 2024-2026
     */
    private static final Set<LocalDate> US_HOLIDAYS_2024 = Set.of(
            LocalDate.of(2024, 1, 1),   // New Year's Day
            LocalDate.of(2024, 1, 15),  // Martin Luther King Jr. Day
            LocalDate.of(2024, 2, 19),  // Presidents' Day
            LocalDate.of(2024, 3, 29),  // Good Friday
            LocalDate.of(2024, 5, 27),  // Memorial Day
            LocalDate.of(2024, 6, 19),  // Juneteenth
            LocalDate.of(2024, 7, 4),   // Independence Day
            LocalDate.of(2024, 9, 2),   // Labor Day
            LocalDate.of(2024, 11, 28), // Thanksgiving
            LocalDate.of(2024, 12, 25)  // Christmas
    );

    private static final Set<LocalDate> US_HOLIDAYS_2025 = Set.of(
            LocalDate.of(2025, 1, 1),   // New Year's Day
            LocalDate.of(2025, 1, 20),  // Martin Luther King Jr. Day
            LocalDate.of(2025, 2, 17),  // Presidents' Day
            LocalDate.of(2025, 4, 18),  // Good Friday
            LocalDate.of(2025, 5, 26),  // Memorial Day
            LocalDate.of(2025, 6, 19),  // Juneteenth
            LocalDate.of(2025, 7, 4),   // Independence Day
            LocalDate.of(2025, 9, 1),   // Labor Day
            LocalDate.of(2025, 11, 27), // Thanksgiving
            LocalDate.of(2025, 12, 25)  // Christmas
    );

    private static final Set<LocalDate> US_HOLIDAYS_2026 = Set.of(
            LocalDate.of(2026, 1, 1),   // New Year's Day
            LocalDate.of(2026, 1, 19),  // Martin Luther King Jr. Day
            LocalDate.of(2026, 2, 16),  // Presidents' Day
            LocalDate.of(2026, 4, 3),   // Good Friday
            LocalDate.of(2026, 5, 25),  // Memorial Day
            LocalDate.of(2026, 6, 19),  // Juneteenth
            LocalDate.of(2026, 7, 3),   // Independence Day (observed)
            LocalDate.of(2026, 9, 7),   // Labor Day
            LocalDate.of(2026, 11, 26), // Thanksgiving
            LocalDate.of(2026, 12, 25)  // Christmas
    );

    // 合并所有年份的假期
    private static final Set<LocalDate> CN_HOLIDAYS = new HashSet<>();
    private static final Set<LocalDate> HK_HOLIDAYS = new HashSet<>();
    private static final Set<LocalDate> US_HOLIDAYS = new HashSet<>();

    static {
        CN_HOLIDAYS.addAll(CN_HOLIDAYS_2024);
        CN_HOLIDAYS.addAll(CN_HOLIDAYS_2025);
        CN_HOLIDAYS.addAll(CN_HOLIDAYS_2026);

        HK_HOLIDAYS.addAll(HK_HOLIDAYS_2024);
        HK_HOLIDAYS.addAll(HK_HOLIDAYS_2025);
        HK_HOLIDAYS.addAll(HK_HOLIDAYS_2026);

        US_HOLIDAYS.addAll(US_HOLIDAYS_2024);
        US_HOLIDAYS.addAll(US_HOLIDAYS_2025);
        US_HOLIDAYS.addAll(US_HOLIDAYS_2026);
    }

    /**
     * 判断是否为交易日
     *
     * @param date   日期
     * @param market 市场类型
     * @return 是否为交易日
     */
    public static boolean isTradingDay(LocalDate date, Market market) {
        return switch (market) {
            case A_SHARE, FUND -> isChinaTradingDay(date);
            case HK -> isHongKongTradingDay(date);
            case US -> isUSTradingDay(date);
        };
    }

    /**
     * 判断A股是否为交易日
     */
    private static boolean isChinaTradingDay(LocalDate date) {
        // 检查是否为周末补班日
        if (CN_WORKDAYS_ON_WEEKEND.contains(date)) {
            return true;
        }

        // 检查是否为周末
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // 检查是否为节假日
        return !CN_HOLIDAYS.contains(date);
    }

    /**
     * 判断港股是否为交易日
     */
    private static boolean isHongKongTradingDay(LocalDate date) {
        // 检查是否为周末
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // 检查是否为公众假期
        return !HK_HOLIDAYS.contains(date);
    }

    /**
     * 判断美股是否为交易日
     */
    private static boolean isUSTradingDay(LocalDate date) {
        // 检查是否为周末
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // 检查是否为节假日
        return !US_HOLIDAYS.contains(date);
    }

    /**
     * 获取指定市场的下一个交易日
     */
    public static LocalDate getNextTradingDay(LocalDate date, Market market) {
        LocalDate nextDay = date.plusDays(1);
        while (!isTradingDay(nextDay, market)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    /**
     * 获取指定市场的上一个交易日
     */
    public static LocalDate getPreviousTradingDay(LocalDate date, Market market) {
        LocalDate prevDay = date.minusDays(1);
        while (!isTradingDay(prevDay, market)) {
            prevDay = prevDay.minusDays(1);
        }
        return prevDay;
    }
}
