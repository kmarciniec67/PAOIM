package lab3.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LaptopCondition")
public class LaptopConditionTest {
    @Test
    void polishNamesCoverEveryStateIncludingReserved() {
        assertEquals(5, LaptopCondition.values().length);
        assertEquals("Sprawny", LaptopCondition.WORKING.toString());
        assertEquals("Uszkodzony", LaptopCondition.DAMAGED.toString());
        assertEquals("W naprawie", LaptopCondition.IN_REPAIR.toString());
        assertEquals("W trakcie testów", LaptopCondition.IN_TESTING.toString());
        assertEquals("Zarezerwowany", LaptopCondition.RESERVED.toString());
        assertEquals(LaptopCondition.RESERVED, LaptopCondition.valueOf("RESERVED"));
    }
}
