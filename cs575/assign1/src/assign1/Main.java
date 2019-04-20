package assign1;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;


public class Main {
    public enum Sym {
        ELEMENT, STARTTAG, ENDTAG, EMPTYTAG, DATA, QENDTAG,
        EB, QATTR, ATTR, NAME, SB, STRING, EQ
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("/home/ethan/git/spring-2019-notes/cs575/assign1/test.txt");
        Scanner sc = new Scanner(file);
        sc.useDelimiter("\\Z");
        String input = sc.next();
        List<String> tokens = Tokenizer.Tokenize(input);
        Parser.Parse(tokens);
        /*
        Stack<Sym> grammarStack = new Stack<Sym>();
        grammarStack.add(Sym.ELEMENT);
        Stack<String> nameStack = new Stack<String>();
        List<Integer> rulesApplied = new ArrayList<Integer>();
        String token;
        Sym sym;
        while (tokens.size() > 0) {
            token = tokens.get(0);
            sym = grammarStack.pop();
            if (token == "<") {
                if (sym == Sym.ELEMENT) {
                    if (tokens.indexOf("/>") == -1 ||
                        tokens.indexOf(">") < tokens.indexOf("/>")) {
                        // RULE 1
                        rulesApplied.add(1);
                        grammarStack.push(Sym.QENDTAG);
                        grammarStack.push(Sym.STARTTAG);
                    } else {
                        // RULE 2
                        rulesApplied.add(2);
                        grammarStack.push(Sym.EMPTYTAG);
                    }
                } else {
                    if (sym == Sym.QENDTAG) {
                        // RULE 4
                        rulesApplied.add(4);
                        grammarStack.push(Sym.QENDTAG);
                        grammarStack.push(Sym.ELEMENT);
                    } else if (sym == Sym.STARTTAG) {
                        // RULE 3
                        rulesApplied.add(3);
                        grammarStack.push(Sym.QATTR);
                        grammarStack.push(Sym.NAME);
                        tokens.remove(0);
                    } else if (sym == Sym.EMPTYTAG) {
                        // RULE 8
                        rulesApplied.add(8);
                        grammarStack.push(Sym.QATTR);
                        grammarStack.push(Sym.NAME);
                        tokens.remove(0);
                    } else {
                        // ???
                        System.out.println(tokens);
                        System.out.println(grammarStack);
                        System.out.println("oops1");
                        System.out.println(rulesApplied);
                        System.out.println(sym);
                        System.exit(1);
                    }
                }
            } else if (token == "</") {
                if (sym == Sym.ENDTAG) {
                    // RULE 7
                    rulesApplied.add(7);
                    grammarStack.push(Sym.EB);
                    grammarStack.push(Sym.NAME);
                    tokens.remove(0);
                } else if (sym == Sym.QENDTAG) {
                    // RULE 6
                    rulesApplied.add(6);
                    grammarStack.push(Sym.ENDTAG);
                } else {
                    // ???
                    System.out.println(tokens);
                    System.out.println(grammarStack);
                    System.out.println("oops2");
                    System.out.println(rulesApplied);
                    System.out.println(sym);
                    System.exit(1);
                }
            } else if (token == ">") {
                if (sym == Sym.QATTR) {
                    // RULE 11
                    rulesApplied.add(11);
                    tokens.remove(0);
                } else if (sym == Sym.EB) {
                    // RULE 13
                    rulesApplied.add(13);
                    tokens.remove(0);
                } else {
                    // ???
                    System.out.println(tokens);
                    System.out.println(grammarStack);
                    System.out.println("oops3");
                    System.out.println(rulesApplied);
                    System.out.println(sym);
                    System.exit(1);
                }
            } else if (token == "/>") {
                if (sym == Sym.QATTR) {
                    // RULE 12
                    rulesApplied.add(12);
                    tokens.remove(0);
                } else {
                    // ???
                    System.out.println(tokens);
                    System.out.println(grammarStack);
                    System.out.println("oops4");
                    System.out.println(rulesApplied);
                    System.out.println(sym);
                    System.exit(1);
                }
            } else if (token == "=") {
                if (sym == Sym.EQ) {
                    rulesApplied.add(14);
                    tokens.remove(0);
                }
            } else if (token.startsWith("'") || token.startsWith("\"")) {
                if (sym == Sym.STRING) {
                    // TODO: parse string
                    rulesApplied.add(69);
                    tokens.remove(0);
                }
            } else {
                // Name, String, or Data
                if (sym == Sym.ATTR) {
                    // RULE 9
                    rulesApplied.add(9);
                    grammarStack.push(Sym.STRING);
                    grammarStack.push(Sym.EQ);
                    grammarStack.push(Sym.NAME);
                } else if (sym == Sym.QATTR) {
                    // RULE 10
                    rulesApplied.add(10);
                    grammarStack.push(Sym.QATTR);
                    grammarStack.push(Sym.ATTR);
                } else if (sym == Sym.NAME) {
                    // TODO: parse name
                    rulesApplied.add(69);
                    tokens.remove(0);
                } else if (sym == Sym.QENDTAG) {
                    //RULE 5
                    rulesApplied.add(5);
                    grammarStack.push(Sym.QENDTAG);
                    grammarStack.push(Sym.DATA);
                } else if (sym == Sym.DATA) {
                    // TODO: parse data
                    rulesApplied.add(70);
                    tokens.remove(0);
                } else {
                    // ???
                    System.out.println(tokens);
                    System.out.println(grammarStack);
                    System.out.println("oops5");
                    System.out.println(rulesApplied);
                    System.out.println(sym);
                    System.exit(1);
                }
            }
        }
        */
    }
}

