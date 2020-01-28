package plagiarismdetector;

public class Person {
    String person1, person2;
    double matching;

    Person(String person1, String person2, double matching) {
        this.person1 = person1;
        this.person2 = person2;
        this.matching = matching;
    }

    public String getPerson1() {
        return person1;
    }

    public String getPerson2() {
        return person2;
    }

    public double getMatching() {
        return matching;
    }
    
}
