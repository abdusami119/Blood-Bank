public class Father extends FamilyMember {
    public Father(String name, int age) {
        super(name, age);
    }

    @Override
    public String getRelation() {
        return "Father";
    }
}