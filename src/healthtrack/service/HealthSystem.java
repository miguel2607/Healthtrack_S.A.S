package healthtrack.service;

import healthtrack.model.Appointment;
import healthtrack.model.ClinicSite;
import healthtrack.model.Doctor;
import healthtrack.model.DoctorAvailability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public final class HealthSystem {

    private final List<ClinicSite> clinics = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();
    private final PriorityQueue<Appointment> appointments =
            new PriorityQueue<>(Comparator.naturalOrder());
    private final List<Appointment> registeredAppointments = new ArrayList<>();

    public boolean addClinic(ClinicSite clinic) {
        for (ClinicSite existing : clinics) {
            if (existing.code().equalsIgnoreCase(clinic.code())) {
                return false;
            }
        }
        clinics.add(clinic);
        return true;
    }

    public List<ClinicSite> listClinics() {
        return new ArrayList<>(clinics);
    }

    public Optional<ClinicSite> findClinic(String code) {
        return clinics.stream()
                .filter(clinic -> clinic.code().equalsIgnoreCase(code))
                .findFirst();
    }

    public boolean addDoctor(Doctor doctor) {
        if (findClinic(doctor.clinicCode()).isEmpty()) {
            throw new IllegalArgumentException("La sede asociada al médico no existe.");
        }
        for (Doctor existing : doctors) {
            if (existing.id().equalsIgnoreCase(doctor.id())) {
                return false;
            }
        }
        doctors.add(doctor);
        return true;
    }

    public List<Doctor> listDoctors() {
        return new ArrayList<>(doctors);
    }

    public List<Doctor> doctorsByClinic(String clinicCode) {
        List<Doctor> result = new ArrayList<>();
        for (Doctor doctor : doctors) {
            if (doctor.clinicCode().equalsIgnoreCase(clinicCode)) {
                result.add(doctor);
            }
        }
        return result;
    }

    public boolean addAppointment(Appointment appointment) {
        if (registeredAppointments.contains(appointment)) {
            return false;
        }
        if (!isDoctorAvailableAt(appointment.doctor(), appointment.schedule())) {
            return false;
        }
        registeredAppointments.add(appointment);
        appointments.add(appointment);
        return true;
    }

    private boolean isDoctorAvailableAt(Doctor doctor, LocalDateTime schedule) {
        DoctorAvailability availability = doctor.availability();

        if (!availability.isAvailableOn(schedule.getDayOfWeek())) {
            return false;
        }

        LocalTime time = schedule.toLocalTime();
        if (time.isBefore(availability.startTime()) || !time.isBefore(availability.endTime())) {
            return false;
        }

        boolean matchesSlot = availability.generateTimeSlots().stream()
                .anyMatch(slot -> slot.equals(time));
        if (!matchesSlot) {
            return false;
        }

        return isSlotFree(doctor, schedule);
    }

    private boolean isSlotFree(Doctor doctor, LocalDateTime schedule) {
        for (Appointment existing : registeredAppointments) {
            if (existing.doctor().id().equals(doctor.id())
                    && existing.schedule().equals(schedule)) {
                return false;
            }
        }
        return true;
    }

    public List<LocalDate> getAvailableDates(Doctor doctor, int daysAhead) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i <= daysAhead; i++) {
            LocalDate date = today.plusDays(i);
            if (!doctor.availability().isAvailableOn(date.getDayOfWeek())) {
                continue;
            }
            if (!getAvailableTimeSlots(doctor, date).isEmpty()) {
                dates.add(date);
            }
        }
        return dates;
    }

    public List<LocalTime> getAvailableTimeSlots(Doctor doctor, LocalDate date) {
        List<LocalTime> available = new ArrayList<>();
        DoctorAvailability availability = doctor.availability();
        if (!availability.isAvailableOn(date.getDayOfWeek())) {
            return available;
        }
        for (LocalTime slot : availability.generateTimeSlots()) {
            LocalDateTime schedule = LocalDateTime.of(date, slot);
            if (isSlotFree(doctor, schedule)) {
                available.add(slot);
            }
        }
        return available;
    }

    public Appointment peekAppointment() {
        return appointments.peek();
    }

    public Appointment pollAppointment() {
        Appointment appointment = appointments.poll();
        if (appointment != null) {
            registeredAppointments.remove(appointment);
        }
        return appointment;
    }

    public List<Appointment> previewAppointments(int limit) {
        List<Appointment> snapshot = new ArrayList<>(appointments);
        snapshot.sort(Comparator.naturalOrder());
        return snapshot.subList(0, Math.min(limit, snapshot.size()));
    }

    public void clearAll() {
        clinics.clear();
        doctors.clear();
        appointments.clear();
        registeredAppointments.clear();
    }
}


