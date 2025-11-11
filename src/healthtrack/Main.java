package healthtrack;

import healthtrack.model.Appointment;
import healthtrack.model.AppointmentType;
import healthtrack.model.ClinicSite;
import healthtrack.model.Doctor;
import healthtrack.model.DoctorAvailability;
import healthtrack.model.Patient;
import healthtrack.model.PatientCategory;
import healthtrack.service.HealthSystem;
import healthtrack.support.DemoData;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public final class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final HealthSystem SYSTEM = new HealthSystem();

    private Main() {
    }

    public static void main(String[] args) {
        String option;
        do {
            printMenu();
            option = SC.nextLine().trim();
            try {
                switch (option) {
                    case "1" -> loadDemoData();
                    case "2" -> registerClinic();
                    case "3" -> listClinics();
                    case "4" -> registerDoctor();
                    case "5" -> listDoctors();
                    case "6" -> registerAppointment();
                    case "7" -> showNextAppointment();
                    case "8" -> attendNextAppointment();
                    case "9" -> listUpcomingAppointments();
                    case "0" -> System.out.println("Saliendo...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (!option.equals("0"));
    }

    private static void printMenu() {
        System.out.println("\n=== HealthTrack ===");
        System.out.println("1. Cargar datos de ejemplo");
        System.out.println("2. Registrar nueva sede");
        System.out.println("3. Listar sedes");
        System.out.println("4. Registrar médico");
        System.out.println("5. Listar médicos");
        System.out.println("6. Registrar cita");
        System.out.println("7. Ver próxima cita");
        System.out.println("8. Atender próxima cita");
        System.out.println("9. Listar próximas 30 citas");
        System.out.println("0. Salir");
        System.out.print("Selecciona una opción: ");
    }

    private static void loadDemoData() {
        SYSTEM.clearAll();
        DemoData.load(SYSTEM);
        System.out.println("Se cargaron sedes, médicos y citas de ejemplo.");
    }

    private static void registerClinic() {
        System.out.print("Código de la sede: ");
        String code = SC.nextLine().trim();
        System.out.print("Nombre de la sede: ");
        String name = SC.nextLine().trim();
        System.out.print("Ciudad: ");
        String city = SC.nextLine().trim();

        ClinicSite site = new ClinicSite(code, name, city);
        if (SYSTEM.addClinic(site)) {
            System.out.println("Sede registrada correctamente.");
        } else {
            System.out.println("Ya existe una sede con ese código.");
        }
    }

    private static void listClinics() {
        List<ClinicSite> clinics = SYSTEM.listClinics();
        if (clinics.isEmpty()) {
            System.out.println("No hay sedes registradas.");
            return;
        }
        System.out.println("=== Sedes registradas ===");
        int index = 1;
        for (ClinicSite clinic : clinics) {
            System.out.printf("%d. %s (%s) - %s%n", index++, clinic.code(), clinic.name(), clinic.city());
        }
    }

    private static void registerDoctor() {
        if (SYSTEM.listClinics().isEmpty()) {
            System.out.println("Debes registrar al menos una sede primero.");
            return;
        }

        System.out.print("Identificación del médico: ");
        String id = SC.nextLine().trim();
        System.out.print("Nombre del médico: ");
        String name = SC.nextLine().trim();
        System.out.print("Especialidad: ");
        String specialty = SC.nextLine().trim();

        ClinicSite site = chooseClinic().orElse(null);
        if (site == null) {
            System.out.println("Operación cancelada.");
            return;
        }

        DoctorAvailability availability = configureDoctorAvailability();
        if (availability == null) {
            System.out.println("Operación cancelada.");
            return;
        }

        Doctor doctor = new Doctor(id, name, specialty, site.code(), availability);
        if (SYSTEM.addDoctor(doctor)) {
            System.out.println("Médico registrado correctamente.");
        } else {
            System.out.println("Ya existe un médico con esa identificación.");
        }
    }

    private static DoctorAvailability configureDoctorAvailability() {
        System.out.println("\n=== Configurar disponibilidad del médico ===");
        
        Set<DayOfWeek> days = chooseAvailableDays();
        if (days.isEmpty()) {
            return null;
        }

        System.out.print("Hora de inicio (HH:MM): ");
        LocalTime startTime = LocalTime.parse(SC.nextLine().trim());

        System.out.print("Hora de fin (HH:MM): ");
        LocalTime endTime = LocalTime.parse(SC.nextLine().trim());

        System.out.print("Duración de cada cita en minutos (ej: 30): ");
        int duration = Integer.parseInt(SC.nextLine().trim());

        try {
            return new DoctorAvailability(days, startTime, endTime, duration);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private static Set<DayOfWeek> chooseAvailableDays() {
        DayOfWeek[] allDays = DayOfWeek.values();
        System.out.println("Selecciona los días disponibles (ingresa números separados por comas, ej: 1,2,3):");
        System.out.println("1. Lunes");
        System.out.println("2. Martes");
        System.out.println("3. Miércoles");
        System.out.println("4. Jueves");
        System.out.println("5. Viernes");
        System.out.println("6. Sábado");
        System.out.println("7. Domingo");
        System.out.print("Días: ");
        
        String input = SC.nextLine().trim();
        if (input.isEmpty()) {
            return EnumSet.noneOf(DayOfWeek.class);
        }

        Set<DayOfWeek> selected = EnumSet.noneOf(DayOfWeek.class);
        String[] parts = input.split(",");
        for (String part : parts) {
            try {
                int dayNum = Integer.parseInt(part.trim());
                if (dayNum >= 1 && dayNum <= 7) {
                    selected.add(allDays[dayNum - 1]);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return selected;
    }

    private static void listDoctors() {
        List<Doctor> doctors = SYSTEM.listDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No hay médicos registrados.");
            return;
        }
        System.out.println("=== Médicos registrados ===");
        int index = 1;
        for (Doctor doctor : doctors) {
            ClinicSite site = SYSTEM.findClinic(doctor.clinicCode()).orElse(null);
            String siteLabel = site == null ? "Sin sede" : site.name();
            System.out.printf("%d. %s (%s) - %s%n", index++, doctor.name(), doctor.specialty(), siteLabel);
        }
    }

    private static void registerAppointment() {
        if (SYSTEM.listClinics().isEmpty()) {
            System.out.println("No hay sedes registradas.");
            return;
        }
        if (SYSTEM.listDoctors().isEmpty()) {
            System.out.println("No hay médicos registrados.");
            return;
        }

        System.out.print("Documento del paciente: ");
        String document = SC.nextLine().trim();

        System.out.print("Nombre completo: ");
        String name = SC.nextLine().trim();

        System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
        LocalDate birthDate = LocalDate.parse(SC.nextLine().trim());

        PatientCategory category = PatientCategory.fromBirthDate(birthDate);
        System.out.print("¿Es una urgencia? (s/n): ");
        if (SC.nextLine().trim().equalsIgnoreCase("s")) {
            category = PatientCategory.URGENCIA;
        }
        Patient patient = new Patient(document, name, birthDate, category);

        ClinicSite clinic = chooseClinic().orElse(null);
        if (clinic == null) {
            System.out.println("Operación cancelada.");
            return;
        }

        List<Doctor> doctors = SYSTEM.doctorsByClinic(clinic.code());
        if (doctors.isEmpty()) {
            System.out.println("No hay médicos registrados en la sede seleccionada.");
            return;
        }

        Doctor doctor = chooseDoctor(doctors).orElse(null);
        if (doctor == null) {
            System.out.println("Operación cancelada.");
            return;
        }

        AppointmentType type = chooseAppointmentType();

        LocalDate date = chooseAvailableDate(doctor);
        if (date == null) {
            System.out.println("Operación cancelada.");
            return;
        }

        LocalTime time = chooseAvailableTime(doctor, date);
        if (time == null) {
            System.out.println("Operación cancelada.");
            return;
        }

        LocalDateTime schedule = LocalDateTime.of(date, time);
        Appointment appointment = new Appointment(patient, doctor, clinic, type, schedule);

        if (SYSTEM.addAppointment(appointment)) {
            System.out.println("Cita registrada correctamente.");
        } else {
            System.out.println("No se pudo registrar la cita. Verifica que el horario esté disponible.");
        }
    }

    private static void showNextAppointment() {
        Appointment next = SYSTEM.peekAppointment();
        if (next == null) {
            System.out.println("No hay citas pendientes.");
        } else {
            System.out.println("Próxima cita: " + next.describe());
        }
    }

    private static void attendNextAppointment() {
        Appointment next = SYSTEM.pollAppointment();
        if (next == null) {
            System.out.println("No hay citas por atender.");
        } else {
            System.out.println("Atendiendo: " + next.describe());
        }
    }

    private static void listUpcomingAppointments() {
        List<Appointment> snapshot = SYSTEM.previewAppointments(30);
        if (snapshot.isEmpty()) {
            System.out.println("No hay citas registradas.");
            return;
        }
        System.out.println("=== Próximas 30 citas ===");
        for (int i = 0; i < snapshot.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, snapshot.get(i).describe());
        }
    }

    private static Optional<ClinicSite> chooseClinic() {
        List<ClinicSite> clinics = SYSTEM.listClinics();
        if (clinics.isEmpty()) {
            return Optional.empty();
        }
        System.out.println("Selecciona una sede:");
        for (int i = 0; i < clinics.size(); i++) {
            ClinicSite clinic = clinics.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, clinic.name(), clinic.city());
        }
        System.out.print("Opción (0 para cancelar): ");
        int option = Integer.parseInt(SC.nextLine().trim());
        if (option == 0) {
            return Optional.empty();
        }
        if (option < 1 || option > clinics.size()) {
            throw new IllegalArgumentException("Opción fuera de rango");
        }
        return Optional.of(clinics.get(option - 1));
    }

    private static Optional<Doctor> chooseDoctor(List<Doctor> doctors) {
        if (doctors.isEmpty()) {
            return Optional.empty();
        }
        System.out.println("Selecciona un médico:");
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, doctor.name(), doctor.specialty());
        }
        System.out.print("Opción (0 para cancelar): ");
        int option = Integer.parseInt(SC.nextLine().trim());
        if (option == 0) {
            return Optional.empty();
        }
        if (option < 1 || option > doctors.size()) {
            throw new IllegalArgumentException("Opción fuera de rango");
        }
        return Optional.of(doctors.get(option - 1));
    }

    private static AppointmentType chooseAppointmentType() {
        AppointmentType[] values = AppointmentType.values();
        System.out.println("Tipo de cita:");
        for (int i = 0; i < values.length; i++) {
            System.out.printf("%d. %s%n", i + 1, values[i]);
        }
        System.out.print("Selecciona una opción: ");
        int option = Integer.parseInt(SC.nextLine().trim());
        if (option < 1 || option > values.length) {
            throw new IllegalArgumentException("Opción fuera de rango");
        }
        return values[option - 1];
    }

    private static LocalDate chooseAvailableDate(Doctor doctor) {
        System.out.println("\n=== Seleccionar fecha ===");
        List<LocalDate> availableDates = SYSTEM.getAvailableDates(doctor, 21);
        if (availableDates.isEmpty()) {
            System.out.println("El médico no tiene fechas disponibles en las próximas semanas.");
            return null;
        }
        Locale locale = new Locale("es", "CO");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy", locale);
        for (int i = 0; i < availableDates.size(); i++) {
            LocalDate date = availableDates.get(i);
            String formatted = formatter.format(date);
            formatted = formatted.substring(0, 1).toUpperCase(locale) + formatted.substring(1);
            System.out.printf("%d. %s%n", i + 1, formatted);
        }
        System.out.print("Selecciona una fecha (0 para cancelar): ");
        int option = Integer.parseInt(SC.nextLine().trim());
        if (option == 0) {
            return null;
        }
        if (option < 1 || option > availableDates.size()) {
            System.out.println("Opción inválida.");
            return null;
        }
        return availableDates.get(option - 1);
    }

    private static LocalTime chooseAvailableTime(Doctor doctor, LocalDate date) {
        System.out.println("\n=== Seleccionar horario ===");
        List<LocalTime> availableSlots = SYSTEM.getAvailableTimeSlots(doctor, date);
        
        if (availableSlots.isEmpty()) {
            System.out.println("No hay horarios disponibles para este médico en esta fecha.");
            return null;
        }
        
        System.out.println("Horarios disponibles:");
        for (int i = 0; i < availableSlots.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, availableSlots.get(i));
        }
        
        System.out.print("Selecciona un horario (0 para cancelar): ");
        int option = Integer.parseInt(SC.nextLine().trim());
        
        if (option == 0) {
            return null;
        }
        
        if (option < 1 || option > availableSlots.size()) {
            System.out.println("Opción inválida.");
            return null;
        }
        
        return availableSlots.get(option - 1);
    }
}

