public class Donor extends Person {
	public Donor(String name, String phone, String bloodGroup, String location) {
		super(name, phone, bloodGroup, location);
	}

	@Override
	public String getRole() {
		return "Sudanir Donor";
	}
}
