package healthtrack.model;

import java.time.LocalDate;
import java.time.Period;

public enum PatientCategory {
    URGENCIA,
    ADULTO_MAYOR,
    ADULTO,
    NINO;

    public static PatientCategory fromBirthDate(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 15) {
            return NINO;
        } else if (age < 60) {
            return ADULTO;
        } else {
            return ADULTO_MAYOR;
        }
    }
}

