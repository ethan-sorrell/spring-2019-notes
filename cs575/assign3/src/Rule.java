package assign3;
public class Rule {
    public String a;
    public String b;

    public Rule(String token) {
        int split = 0;
        while (token.charAt(split) != ',') {
            split += 1;
        }
        this.a = token.substring(1, split);
        this.b = token.substring(split+1, token.length()-1);
    }

    public Rule(String first, String second) {
        this.a = first;
        this.b = second;
    }

    public void print() {
        System.out.println("First: " + this.a);
        System.out.println("Second: " + this.b);
    }

}
