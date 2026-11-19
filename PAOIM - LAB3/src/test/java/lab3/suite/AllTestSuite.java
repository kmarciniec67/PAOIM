package lab3.suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Wszystkie testy Lab 3")
@SelectClasses({
        ModelTestSuite.class,
        FacadeTestSuite.class,
        UiTestSuite.class
})
public class AllTestSuite {
}
