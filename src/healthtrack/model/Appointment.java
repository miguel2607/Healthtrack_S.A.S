package healthtrack.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Appointment implements Comparable<Appointment> {

    private final Patient patient;
    private final Doctor doctor;
    private final ClinicSite clinic;
    private final AppointmentType type;
    private final LocalDateTime schedule;
    private final PriorityLevel priority;

    public Appointment(Patient patient, Doctor doctor, ClinicSite clinic, AppointmentType type, LocalDateTime schedule) {
        this.patient = Objects.requireNonNull(patient, "patient");
        this.doctor = Objects.requireNonNull(doctor, "doctor");
        this.clinic = Objects.requireNonNull(clinic, "clinic");
        this.type = Objects.requireNonNull(type, "type");
        this.schedule = Objects.requireNonNull(schedule, "schedule");
        this.priority = PriorityLevel.fromCategory(patient.category());
    }

    public Patient patient() {
        return patient;
    }

    public Doctor doctor() {
        return doctor;
    }

    public ClinicSite clinic() {
        return clinic;
    }

    public AppointmentType type() {
        return type;
    }

    public LocalDateTime schedule() {
        return schedule;
    }

    public PriorityLevel priority() {
        return priority;
    }

    public String describe() {
        return patient.name() + " (" + patient.document() + ") | " +
                "Prioridad: " + priority + " | " +
                "Médico: " + doctor.name() + " | " +
                "Tipo: " + type + " | " +
                "Sede: " + clinic.name() + " (" + clinic.city() + ") | " +
                "Fecha: " + schedule;
    }

    @Override
    public int compareTo(Appointment other) {
        int diff = Integer.compare(this.priority.weight(), other.priority.weight());
        if (diff != 0) {
            return diff;
        }
        diff = this.schedule.compareTo(other.schedule);
        if (diff != 0) {
            return diff;
        }
        return this.patient.document().compareTo(other.patient.document());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Appointment other)) {
            return false;
        }
        return patient.document().equals(other.patient.document()) &&
                schedule.equals(other.schedule);
    }

    @Override
    public int hashCode() {
        return patient.document().hashCode() * 31 + schedule.hashCode();
    }
}

