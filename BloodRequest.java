public class BloodRequest {
    private final Patient patient;
    private final String bloodGroup;
    private final int units;
    private final boolean accepted;

    public BloodRequest(Patient patient, String bloodGroup, int units, boolean accepted) {
        this.patient = patient;
        this.bloodGroup = bloodGroup;
        this.units = units;
        this.accepted = accepted;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public int getUnits() {
        return units;
    }

    public String getStatus() {
        return accepted ? "Accepted" : "Blood Not Available";
    }
}