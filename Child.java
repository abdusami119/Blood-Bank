public class Child extends FamilyMember {
    public Child(String name, int age) {
        super(name, age);
    }

    @Override
    public String getRelation() {
        return "Child";
    }
}