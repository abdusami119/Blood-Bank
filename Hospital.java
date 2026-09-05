public class Hospital {
    private final String name;
    private final String phone;
    private final String location;
    private final String contactPerson;

    public Hospital(String name, String phone, String location, String contactPerson) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.contactPerson = contactPerson;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getContactPerson() {
        return contactPerson;
    }
}