package projava;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings("NonAsciiCharacters")
class OlympicTest {

    private Olympic olympic;

    @BeforeEach
    public void init() {
        olympic = new Olympic();
    }

    @Test
    void 近代オリンピック開催以前() {
        assertAll(
                () -> assertFalse(olympic.isSummerOlympicYear(1888), "1888年"),
                () -> assertFalse(olympic.isSummerOlympicYear(1892), "1892年"),
                () -> assertFalse(olympic.isSummerOlympicYear(1895), "1895年"),
                // 初回開催年
                () -> assertTrue(olympic.isSummerOlympicYear(1896), "1896年")
        );
    }

    @Test
    void 四年周期の一般的な開催年() {
        int[] years = {1900, 1920, 1936, 1964, 2000};
        for (int year : years) {
            assertTrue(olympic.isSummerOlympicYear(year), year + "");
        }
    }

    @Test
    void 四年周期から外れる一般的な非開催年() {
        int[] years = {1905, 1907, 1925, 1967, 2001};
        for (int year : years) {
            assertFalse(olympic.isSummerOlympicYear(year), year + "");
        }
    }

    /**
     * 1916年 ベルリン大会: 第一次世界大戦により中止
     * 1940年 東京大会→ヘルシンキ大会: 日中戦争が影響し開催地変更 そののち第二次世界大戦により中止
     * 1944年 ロンドン大会: 第二次世界大戦により中止
     * 2020年 東京大会: 新型コロナウイルスにより2021年に延期
     */
    @Test
    void 戦争またはパンデミックで中止となった年() {
        int[] years = {1916, 1940, 1944, 2020};
        for (int year : years) {
            assertFalse(olympic.isSummerOlympicYear(year), year + "");
        }
    }

    @Test
    void 四年間隔ではない例外的な開催年() {
        // 新型コロナウイルスにより延期開催
        assertTrue(olympic.isSummerOlympicYear(2021), "2021年");
    }

    @Test
    void 境界値上限() {
        // 2022-05-06時点 2032年のオーストラリア大会まで開催地確定
        assertDoesNotThrow(() -> olympic.isSummerOlympicYear(2032));
        assertThrows(IllegalArgumentException.class,
                () -> olympic.isSummerOlympicYear(2033));
    }
}
