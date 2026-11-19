package lab3.facade;

/** Prośba klienta o kontakt w sprawie konkretnego egzemplarza. Widoczna w panelu administratora. */
public class ContactRequest {
    private final String clientName;
    private final String branchName;
    private final String laptopLabel;
    private final String message;

    public ContactRequest(String clientName, String branchName, String laptopLabel, String message) {
        this.clientName = clientName;
        this.branchName = branchName;
        this.laptopLabel = laptopLabel;
        this.message = message;
    }

    public String getClientName() {
        return clientName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getLaptopLabel() {
        return laptopLabel;
    }

    public String getMessage() {
        return message;
    }
}
