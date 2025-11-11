package healthtrack.model;

import java.util.Objects;

public final class Doctor {

    private final String id;
    private final String name;
    private final String specialty;
    private final String clinicCode;
    private final DoctorAvailability availability;

    public Doctor(String id, String name, String specialty, String clinicCode, DoctorAvailability availability) {
        this.id = requireNonEmpty(id, "id");
        this.name = requireNonEmpty(name, "name");
        this.specialty = requireNonEmpty(specialty, "specialty");
        this.clinicCode = requireNonEmpty(clinicCode, "clinicCode");
        this.availability = Objects.requireNonNull(availability, "availability");
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String specialty() {
        return specialty;
    }

    public String clinicCode() {
        return clinicCode;
    }

    public DoctorAvailability availability() {
        return availability;
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

