package healthtrack.model;

import java.util.Objects;

public final class ClinicSite {

    private final String code;
    private final String name;
    private final String city;

    public ClinicSite(String code, String name, String city) {
        this.code = Objects.requireNonNull(code, "code").trim();
        this.name = Objects.requireNonNull(name, "name").trim();
        this.city = Objects.requireNonNull(city, "city").trim();
        if (this.code.isEmpty() || this.name.isEmpty() || this.city.isEmpty()) {
            throw new IllegalArgumentException("Código, nombre y ciudad son obligatorios");
        }
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public String city() {
        return city;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClinicSite other)) {
            return false;
        }
        return code.equalsIgnoreCase(other.code);
    }

    @Override
    public int hashCode() {
        return code.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + city + ")";
    }
}

