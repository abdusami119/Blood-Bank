public abstract class FamilyMember {
    protected final String name;
    protected final int age;

    protected FamilyMember(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public abstract String getRelation();
}