public class Grandfather extends FamilyMember {
    public Grandfather(String name, int age) {
        super(name, age);
    }

    @Override
    public String getRelation() {
        return "Grandfather";
    }
}