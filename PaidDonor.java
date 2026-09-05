public class PaidDonor extends Donor {
    public PaidDonor(String name, String phone, String bloodGroup, String location) {
        super(name, phone, bloodGroup, location);
    }

    @Override
    public String getRole() {
        return "Paid Donor";
    }
}