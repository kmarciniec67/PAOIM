package lab3.ui;

import lab3.data.DataGenerator;
import lab3.facade.StoreFacade;
import lab3.model.LaptopCondition;
import lab3.support.Edt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Panel klienta")
public class ClientPanelTest {
    private StoreFacade facade;
    private ClientPanel panel;

    @BeforeEach
    void setUp() throws Exception {
        facade = new StoreFacade(DataGenerator.createSample());
        Edt.run(() -> panel = new ClientPanel(facade, new Navigator() {
            @Override
            public void showAdmin() {
            }

            @Override
            public void showClient() {
            }

            @Override
            public void showLogin() {
            }
        }));
    }

    @Test
    void reservesWorkingLaptopSortsAndSavesContactRequest() throws Exception {
        Edt.run(() -> {
            panel.catalog.laptops.selectRow(0);
            assertEquals(LaptopCondition.WORKING, panel.catalog.getSelectedLaptop().getCondition());
            panel.reserveButton.doClick();
            assertEquals(LaptopCondition.RESERVED, panel.catalog.getSelectedBranch().getLaptops().get(0).getCondition());

            panel.sortButton.doClick();
            assertEquals("Kraków Kazimierz", facade.getBranches().get(0).getBranchName());

            panel.catalog.selectBranch("Gdańsk Wrzeszcz");
            panel.catalog.laptops.selectRow(1);
            panel.requestContact("Anna Nowak", "Proszę o kontakt w sprawie tego egzemplarza.");
            assertEquals(1, facade.getContactRequests().size());
            assertEquals("Anna Nowak", facade.getContactRequests().get(0).getClientName());

            AdminPanel admin = new AdminPanel(facade, new Navigator() {
                @Override
                public void showAdmin() {
                }

                @Override
                public void showClient() {
                }

                @Override
                public void showLogin() {
                }
            });
            assertEquals(1, admin.requests.getRowCount());
            assertEquals("Anna Nowak", admin.requests.getModel().getValueAt(0, 0));
        });
    }
}
