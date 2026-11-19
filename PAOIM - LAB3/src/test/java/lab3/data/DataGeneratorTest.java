package lab3.data;

import lab3.model.Branch;
import lab3.model.LaptopCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DataGenerator")
public class DataGeneratorTest {
    @Test
    void sampleHasBranchesLaptopsConditionsAndAnEmptyBranch() throws Exception {
        var manager = DataGenerator.createSample();
        assertEquals(5, manager.getBranches().size());
        int laptops = 0;
        Set<String> brands = new HashSet<>();
        EnumSet<LaptopCondition> conditions = EnumSet.noneOf(LaptopCondition.class);
        boolean empty = false;
        boolean full = false;
        for (Branch branch : manager.getBranches()) {
            laptops += branch.size();
            if (branch.isEmpty()) {
                empty = true;
                assertEquals("Wrocław Rynek", branch.getBranchName());
            }
            if (branch.isFull()) {
                full = true;
            }
            for (var laptop : branch.getLaptops()) {
                brands.add(laptop.getBrand());
                conditions.add(laptop.getCondition());
            }
        }
        assertEquals(15, laptops);
        assertTrue(empty);
        assertTrue(full);
        assertEquals(EnumSet.allOf(LaptopCondition.class), conditions);
        assertTrue(brands.contains("Lenovo"));
        assertTrue(brands.contains("Dell"));
        assertTrue(brands.contains("Apple"));
        assertTrue(brands.contains("HP"));
        assertTrue(brands.contains("Asus"));
        assertTrue(brands.contains("Acer"));
        assertTrue(brands.contains("MSI"));
    }

    @Test
    void eachSampleIsIndependent() throws Exception {
        var first = DataGenerator.createSample();
        var second = DataGenerator.createSample();
        assertNotSame(first, second);
        first.removeBranch("Wrocław Rynek");
        assertEquals(5, second.getBranches().size());
    }

    @Test
    void singletonReturnsTheSameManager() throws Exception {
        DataGenerator first = DataGenerator.getInstance();
        DataGenerator second = DataGenerator.getInstance();
        assertSame(first, second);
        assertSame(first.getManager(), second.getManager());
        assertEquals(5, first.getManager().getBranches().size());
    }
}
