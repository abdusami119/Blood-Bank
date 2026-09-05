public class UnpaidDonor extends Donor {
    public UnpaidDonor(String name, String phone, String bloodGroup, String location) {
        super(name, phone, bloodGroup, location);
    }

    @Override
    public String getRole() {
        return "Unpaid Donor";
    }
}