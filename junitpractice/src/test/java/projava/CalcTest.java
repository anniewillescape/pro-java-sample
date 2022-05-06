package projava;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


class CalcTest {

    @Test
    void Addition_of_positive_numbers() {
        assertAll(
                () -> assertEquals(4, new Calc().add(2, 2), "2 + 2 = 4"),
                () -> assertEquals(6, new Calc().add(2, 4), "2 + 4 = 6")
        );
    }

    @Test
    void Addition_of_negative_numbers() {
        assertAll(
                () -> assertEquals(-4, new Calc().add(-2, -2), "-2 + -2 = -4"),
                () -> assertEquals(-6, new Calc().add(-2, -4), "-2 + -4 = -6")
        );
    }
}
