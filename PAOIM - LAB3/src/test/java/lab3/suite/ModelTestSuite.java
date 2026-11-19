package lab3.suite;

import lab3.exception.ExceptionTest;
import lab3.model.BranchTest;
import lab3.model.LaptopConditionTest;
import lab3.model.LaptopTest;
import lab3.model.StoreManagerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Model i wyjątki")
@SelectClasses({
        LaptopTest.class,
        LaptopConditionTest.class,
        BranchTest.class,
        StoreManagerTest.class,
        ExceptionTest.class
})
public class ModelTestSuite {
}
