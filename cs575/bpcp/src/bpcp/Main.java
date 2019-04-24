package bpcp;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            System.out.println("Error: Expected 1 argument got " + args.length);
            System.exit(1);
        }
        File file = new File(args[0]);
        // File file = new File("/home/ethan/git/spring-2019-notes/cs575/input2.txt");
        Scanner sc = new Scanner(file);
        sc.useDelimiter("\\n");
        int bound = Integer.parseInt(sc.next()) + 1;
        // System.out.println(bound);

        String first;
        String second;
        List<Rule> ruleList = new ArrayList<Rule>();
        while (sc.hasNext("\\[\\w+?\\,\\w+?\\]")) {
            Rule r = new Rule(sc.next("\\[\\w+?\\,\\w+?\\]"));
            // r.print();
            ruleList.add(r);
        }

        // perform search
        List<PartialSolution> parts = findStart(ruleList);
        if (parts.size() == 0) {
            System.out.println("No Solution");
            System.exit(0);
        }
        for (int i = 2; i < bound; i++) {
            parts = searchDepth(ruleList, parts);
        }
        System.out.println("No Solution");
    }

    public static List<PartialSolution> findStart(List<Rule> rules) {
        List<PartialSolution> startRules = new ArrayList<PartialSolution>();
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            if (rule.a.compareTo(rule.b) == 0) {
                System.out.println(i);
                System.exit(0);
            }
            else if (rule.a.startsWith(rule.b)) {
                List<Integer> oneRule = new ArrayList<Integer>();
                oneRule.add(i);
                String diff = rule.a.substring(rule.b.length(), rule.a.length());
                PartialSolution newPart = new PartialSolution(oneRule, diff, true);
                startRules.add(newPart);
            } else if (rule.b.startsWith(rule.a)) {
                List<Integer> oneRule = new ArrayList<Integer>();
                oneRule.add(i);
                String diff = rule.b.substring(rule.a.length(), rule.b.length());
                PartialSolution newPart = new PartialSolution(oneRule, diff, false);
                startRules.add(newPart);
            }
        }
        return startRules;
    }

    public static List<PartialSolution> searchDepth(List<Rule> rules,
                                                    List<PartialSolution> parts) {
        List<PartialSolution> newParts = new ArrayList<PartialSolution>();
        for (PartialSolution part: parts) {
            for (int i = 0; i < rules.size(); i++) {
                Rule rule = rules.get(i);
                if ((part.fromTop && rule.b.compareTo(part.diff + rule.a) == 0) ||
                    (!part.fromTop && rule.a.compareTo(part.diff + rule.b) == 0)) {
                    // found solution
                    part.rules.add(i);
                    System.out.println(part.rules);
                    System.exit(0);
                } else if (!part.fromTop && (rule.a.startsWith(part.diff) ||
                                             part.diff.startsWith(rule.a))) {
                    List<Integer> newRules = new ArrayList<>(part.rules);
                    newRules.add(i);
                    if (part.diff.length() + rule.b.length() < rule.a.length()) {
                        // fromTop is true
                        String diff = rule.a.substring(part.diff.length() +
                                                       rule.b.length(),
                                                       rule.a.length());
                        newParts.add(new PartialSolution(newRules, diff, true));
                    } else {
                        // fromTop is false
                        String diff = (part.diff + rule.b).substring(rule.a.length(),
                                                                     part.diff.length() +
                                                                     rule.b.length());
                        newParts.add(new PartialSolution(newRules, diff, false));
                    }
                } else if (part.fromTop && (rule.b.startsWith(part.diff) ||
                                            part.diff.startsWith(rule.b))) {
                    List<Integer> newRules = new ArrayList<>(part.rules);
                    newRules.add(i);
                    if (part.diff.length() + rule.a.length() < rule.b.length()) {
                        // fromTop is false
                        String diff = rule.b.substring(part.diff.length() + 
                                                       rule.a.length(),
                                                       rule.b.length());
                        newParts.add(new PartialSolution(newRules, diff, false));
                    } else {
                        // fromTop is true
                        String diff = (part.diff + rule.a).substring(rule.b.length(),
                                                                     part.diff.length() +
                                                                     rule.a.length());
                        newParts.add(new PartialSolution(newRules, diff, true));
                    }
                }
            }
        }
        return newParts;
    }

    public static void printState(List<PartialSolution> parts) {
        for (PartialSolution part: parts) {
            if (part.fromTop)
                System.out.println("remaining top: " + part.diff);
            else
                System.out.println("remaining bottom: " + part.diff);
            System.out.println("from rules: ");
            System.out.println(part.rules);
        }
    }
}
