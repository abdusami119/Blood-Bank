public class Patient extends Person {
    public Patient(String name, String phone, String bloodGroup, String hospital) {
        super(name, phone, bloodGroup, hospital);
    }

    @Override
    public String getRole() {
        return "Patient";
    }

    public String getHospital() {
        return location;
    }
}