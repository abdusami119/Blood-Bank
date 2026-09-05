public abstract class Person {
    protected String name;
    protected String phone;
    protected String bloodGroup;
    protected String location;

    public Person(String name, String phone, String bloodGroup, String location) {
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getLocation() {
        return location;
    }

    public abstract String getRole();
}
