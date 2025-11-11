package healthtrack.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class DoctorAvailability {

    private final Set<DayOfWeek> availableDays;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int slotDurationMinutes;

    public DoctorAvailability(Set<DayOfWeek> availableDays, LocalTime startTime, LocalTime endTime, int slotDurationMinutes) {
        Objects.requireNonNull(availableDays, "availableDays");
        if (availableDays.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un día disponible");
        }
        this.availableDays = EnumSet.copyOf(availableDays);
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        this.endTime = Objects.requireNonNull(endTime, "endTime");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (slotDurationMinutes <= 0) {
            throw new IllegalArgumentException("La duración del slot debe ser mayor a cero");
        }
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public Set<DayOfWeek> availableDays() {
        return EnumSet.copyOf(availableDays);
    }

    public LocalTime startTime() {
        return startTime;
    }

    public LocalTime endTime() {
        return endTime;
    }

    public int slotDurationMinutes() {
        return slotDurationMinutes;
    }

    public boolean isAvailableOn(DayOfWeek day) {
        return availableDays.contains(day);
    }

    public List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = startTime;
        while (!current.isAfter(endTime.minusMinutes(slotDurationMinutes))) {
            slots.add(current);
            current = current.plusMinutes(slotDurationMinutes);
        }
        return slots;
    }
}

