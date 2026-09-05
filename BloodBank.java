import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodBank {
    private final List<Donor> donors = new ArrayList<>();
    private final List<Hospital> hospitals = new ArrayList<>();
    private final List<BloodRequest> bloodRequests = new ArrayList<>();
    private final Map<String, Integer> bloodStock = new HashMap<>();

    public void addDonor(Donor donor) {
        donors.add(donor);
    }

    public void donateBlood(Donor donor, int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Donation units must be greater than zero.");
        }

        addBlood(donor.getBloodGroup(), units);
    }

    public List<Donor> getDonors() {
        return Collections.unmodifiableList(donors);
    }

    public void addHospital(Hospital hospital) {
        hospitals.add(hospital);
    }

    public List<Hospital> getHospitals() {
        return Collections.unmodifiableList(hospitals);
    }

    public List<Hospital> findHospitals(String searchText) {
        String normalizedSearch = searchText == null ? "" : searchText.trim().toLowerCase();
        List<Hospital> matches = new ArrayList<>();

        for (Hospital hospital : hospitals) {
            if (hospital.getName().toLowerCase().contains(normalizedSearch)
                    || hospital.getLocation().toLowerCase().contains(normalizedSearch)) {
                matches.add(hospital);
            }
        }

        return matches;
    }

    public List<Donor> findDonorsByBloodGroup(String bloodGroup) {
        String normalizedGroup = normalizeBloodGroup(bloodGroup);
        List<Donor> matches = new ArrayList<>();

        for (Donor donor : donors) {
            if (donor.getBloodGroup().equalsIgnoreCase(normalizedGroup)) {
                matches.add(donor);
            }
        }

        return matches;
    }

    public void addBlood(String bloodGroup, int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Units must be greater than zero.");
        }

        String normalizedGroup = normalizeBloodGroup(bloodGroup);
        bloodStock.put(normalizedGroup, getBloodStock(normalizedGroup) + units);
    }

    public boolean requestBlood(String bloodGroup, int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Units must be greater than zero.");
        }

        String normalizedGroup = normalizeBloodGroup(bloodGroup);
        int availableUnits = getBloodStock(normalizedGroup);
        if (availableUnits < units) {
            return false;
        }

        bloodStock.put(normalizedGroup, availableUnits - units);
        return true;
    }

    public BloodRequest requestBlood(Patient patient, String bloodGroup, int units) {
        String normalizedGroup = normalizeBloodGroup(bloodGroup);
        boolean accepted = requestBlood(normalizedGroup, units);
        BloodRequest request = new BloodRequest(patient, normalizedGroup, units, accepted);
        bloodRequests.add(request);
        return request;
    }

    public List<BloodRequest> getBloodRequests() {
        return Collections.unmodifiableList(bloodRequests);
    }

    public int getBloodStock(String bloodGroup) {
        return bloodStock.getOrDefault(normalizeBloodGroup(bloodGroup), 0);
    }

    private String normalizeBloodGroup(String bloodGroup) {
        if (bloodGroup == null || bloodGroup.isBlank()) {
            throw new IllegalArgumentException("Blood group cannot be empty.");
        }

        return bloodGroup.trim().toUpperCase();
    }
}