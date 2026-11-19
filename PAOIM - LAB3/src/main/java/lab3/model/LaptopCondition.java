package lab3.model;

/**
 * Stan laptopa przyjętego do sieci sklepów.
 * {@code toString()} zwraca polską nazwę używaną w tabeli i komunikatach.
 */
public enum LaptopCondition {
    WORKING {
        @Override
        public String toString() {
            return "Sprawny";
        }
    },
    DAMAGED {
        @Override
        public String toString() {
            return "Uszkodzony";
        }
    },
    IN_REPAIR {
        @Override
        public String toString() {
            return "W naprawie";
        }
    },
    IN_TESTING {
        @Override
        public String toString() {
            return "W trakcie testów";
        }
    },
    RESERVED {
        @Override
        public String toString() {
            return "Zarezerwowany";
        }
    }
}
