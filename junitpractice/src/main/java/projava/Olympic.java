package projava;

import java.util.List;

public class Olympic {

    /**
     * 西暦年が夏季オリンピック開催年かどうかを判定する
     *
     * @param year 西暦年
     * @return 開催年であればtrue
     * @throws IllegalArgumentException まだ開催地が決定していない年が渡された場合
     */
    public boolean isSummerOlympicYear(int year) {

        var canceledYears = List.of(1916, 1940, 1944, 2020);
        var exceptionalHeldYears = List.of(2021);

        if (year < 1896) {
            return false;
        } else if (year > 2032) {
            throw new IllegalArgumentException("This program supports values up to 2032. Input year: " + year);
        } else if (canceledYears.contains(year)) {
            return false;
        } else if (exceptionalHeldYears.contains(year)) {
            return true;
        }
        return year % 4 == 0;
    }
}
