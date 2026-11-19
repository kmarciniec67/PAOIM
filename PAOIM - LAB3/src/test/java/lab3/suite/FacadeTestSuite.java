package lab3.suite;

import lab3.auth.AuthServiceTest;
import lab3.data.DataGeneratorTest;
import lab3.facade.StoreFacadeTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Fasada, dane i logowanie")
@SelectClasses({
        StoreFacadeTest.class,
        DataGeneratorTest.class,
        AuthServiceTest.class
})
public class FacadeTestSuite {
}
