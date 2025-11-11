package healthtrack.support;

import healthtrack.model.Appointment;
import healthtrack.model.AppointmentType;
import healthtrack.model.ClinicSite;
import healthtrack.model.Doctor;
import healthtrack.model.DoctorAvailability;
import healthtrack.model.Patient;
import healthtrack.model.PatientCategory;
import healthtrack.service.HealthSystem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class DemoData {

    private static final String[] NAMES = {
            "Laura Gómez", "Miguel Rodríguez", "Sofía Herrera",
            "Camilo Fernández", "Andrés Páez", "Valentina Ruiz",
            "Juan Díaz", "Mariana López", "Sebastián Torres"
    };

    private DemoData() {
    }

    public static void load(HealthSystem system) {
        system.clearAll();

        ClinicSite bogota = new ClinicSite("BOG-01", "Clínica Central Bogotá", "Bogotá");
        ClinicSite medellin = new ClinicSite("MED-01", "Clínica Las Palmas", "Medellín");
        ClinicSite cali = new ClinicSite("CAL-01", "Clínica San Antonio", "Cali");

        system.addClinic(bogota);
        system.addClinic(medellin);
        system.addClinic(cali);

        Set<DayOfWeek> weekdays = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
                                             DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        Set<DayOfWeek> weekdaysPlusSaturday = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
                                                          DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, 
                                                          DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

        DoctorAvailability morningShift = new DoctorAvailability(
                weekdays, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        DoctorAvailability afternoonShift = new DoctorAvailability(
                weekdays, LocalTime.of(14, 0), LocalTime.of(18, 0), 30);
        DoctorAvailability fullDay = new DoctorAvailability(
                weekdaysPlusSaturday, LocalTime.of(8, 0), LocalTime.of(17, 0), 30);

        system.addDoctor(new Doctor("MED-100", "Ana Beltrán", "Medicina General", bogota.code(), morningShift));
        system.addDoctor(new Doctor("MED-101", "Carlos Henao", "Medicina General", bogota.code(), afternoonShift));
        system.addDoctor(new Doctor("MED-200", "Juliana Ortiz", "Cardiología", medellin.code(), fullDay));
        system.addDoctor(new Doctor("MED-201", "Santiago Ríos", "Pediatría", medellin.code(), morningShift));
        system.addDoctor(new Doctor("MED-300", "Laura Muñoz", "Medicina Interna", cali.code(), afternoonShift));
        system.addDoctor(new Doctor("MED-301", "Felipe Cárdenas", "Medicina General", cali.code(), fullDay));

        List<ClinicSite> clinics = system.listClinics();
        List<Doctor> doctors = system.listDoctors();

        Random random = new Random();
        int index = 0;
        while (index < 30) {
            ClinicSite clinic = clinics.get(random.nextInt(clinics.size()));
            List<Doctor> clinicDoctors = system.doctorsByClinic(clinic.code());
            if (clinicDoctors.isEmpty()) {
                continue;
            }
            Doctor doctor = clinicDoctors.get(random.nextInt(clinicDoctors.size()));

            List<LocalDate> availableDates = system.getAvailableDates(doctor, 30);
            if (availableDates.isEmpty()) {
                continue;
            }
            LocalDate date = availableDates.get(random.nextInt(availableDates.size()));
            List<LocalTime> availableSlots = system.getAvailableTimeSlots(doctor, date);
            if (availableSlots.isEmpty()) {
                continue;
            }
            LocalTime time = availableSlots.get(random.nextInt(availableSlots.size()));

            Patient patient = createPatient(index, random);
            AppointmentType type = AppointmentType.values()[random.nextInt(AppointmentType.values().length)];
            LocalDateTime schedule = LocalDateTime.of(date, time);

            if (system.addAppointment(new Appointment(patient, doctor, clinic, type, schedule))) {
                index++;
            }
        }
    }

    private static Patient createPatient(int index, Random random) {
        String document = "PT-" + String.format("%04d", index);
        String name = NAMES[random.nextInt(NAMES.length)];
        LocalDate birthDate = LocalDate.of(
                1960 + random.nextInt(40),
                1 + random.nextInt(12),
                1 + random.nextInt(28)
        );
        PatientCategory category = PatientCategory.fromBirthDate(birthDate);
        if (random.nextDouble() < 0.18) {
            category = PatientCategory.URGENCIA;
        }
        return new Patient(document, name, birthDate, category);
    }
}

