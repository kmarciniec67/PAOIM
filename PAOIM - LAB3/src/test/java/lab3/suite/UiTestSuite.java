package lab3.suite;

import lab3.ui.AdminPanelTest;
import lab3.ui.ApplicationFrameTest;
import lab3.ui.ClientPanelTest;
import lab3.ui.GenericTableModelTest;
import lab3.ui.ParsersTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Interfejs")
@SelectClasses({
        GenericTableModelTest.class,
        ParsersTest.class,
        AdminPanelTest.class,
        ClientPanelTest.class,
        ApplicationFrameTest.class
})
public class UiTestSuite {
}
