package assign3;

import java.util.List;
import java.util.ArrayList;

public class PartialSolution {
    List<Integer> rules;
    String diff;
    Boolean fromTop;

    public PartialSolution(List<Integer> rules, String difference, Boolean fromTop) {
        this.rules= rules;
        this.diff = difference;
        this.fromTop = fromTop;
    }
}
