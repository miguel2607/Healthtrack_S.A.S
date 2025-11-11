package healthtrack.model;

import java.time.LocalDate;
import java.util.Objects;

public final class Patient {

    private final String document;
    private final String name;
    private final LocalDate birthDate;
    private final PatientCategory category;

    public Patient(String document, String name, LocalDate birthDate, PatientCategory category) {
        this.document = requireNonEmpty(document, "document");
        this.name = requireNonEmpty(name, "name");
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate");
        this.category = Objects.requireNonNull(category, "category");
    }

    public String document() {
        return document;
    }

    public String name() {
        return name;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public PatientCategory category() {
        return category;
    }

    private static String requireNonEmpty(String value, String field) {
        Objects.requireNonNull(value, field);
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("El campo " + field + " es obligatorio");
        }
        return trimmed;
    }
}

