package healthtrack.model;

public enum PriorityLevel {
    URGENCIA(0),
    ADULTO_MAYOR(1),
    ADULTO(2),
    NINO(3);

    private final int weight;

    PriorityLevel(int weight) {
        this.weight = weight;
    }

    public int weight() {
        return weight;
    }

    public static PriorityLevel fromCategory(PatientCategory category) {
        return switch (category) {
            case URGENCIA -> URGENCIA;
            case ADULTO_MAYOR -> ADULTO_MAYOR;
            case ADULTO -> ADULTO;
            case NINO -> NINO;
        };
    }
}

