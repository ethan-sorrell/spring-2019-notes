package assign1;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;


public class Parser {
    public enum Sym {
        ELEMENT, STARTTAG, ENDTAG, EMPTYTAG, DATA, QENDTAG,
        EB, QATTR, ATTR, NAME, SB, STRING, EQ
    }

    public static String Pair(String token, Sym sym) {
        if (Arrays.asList("<", "=", ">", "/>", "</").contains(token)) {
            return token + "_" + sym.name();
        } else if (token.startsWith("'") || token.startsWith("\"")) {
            return "QSTR_" + sym.name();
        } else {
            return "STR_" + sym.name();
        }
    }

    public static void Parse(List<String> tokens) {
        Stack<Sym> grammarStack = new Stack<Sym>();
        grammarStack.add(Sym.ELEMENT);
        Stack<String> nameStack = new Stack<String>();
        List<Integer> rulesApplied = new ArrayList<Integer>();
        String token;
        Sym sym;
        while (tokens.size() > 0) {
            token = tokens.get(0);
            sym = grammarStack.pop();
            switch (Pair(token, sym)) {
            case "<_ELEMENT":
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
                break;
            case "<_QENDTAG":
                // RULE 4
                rulesApplied.add(4);
                grammarStack.push(Sym.QENDTAG);
                grammarStack.push(Sym.ELEMENT);
                break;
            case "<_STARTTAG":
                // RULE 3
                rulesApplied.add(3);
                grammarStack.push(Sym.QATTR);
                grammarStack.push(Sym.NAME);
                tokens.remove(0);
                break;
            case "<_EMPTYTAG":
                // RULE 8
                rulesApplied.add(8);
                grammarStack.push(Sym.QATTR);
                grammarStack.push(Sym.NAME);
                tokens.remove(0);
                break;
            case "</_ENDTAG":
                // RULE 7
                rulesApplied.add(7);
                grammarStack.push(Sym.EB);
                grammarStack.push(Sym.NAME);
                tokens.remove(0);
                break;
            case "</_QENDTAG":
                // RULE 6
                rulesApplied.add(6);
                grammarStack.push(Sym.ENDTAG);
                break;
            case ">_QATTR":
                // RULE 11
                rulesApplied.add(11);
                tokens.remove(0);
                break;
            case ">_EB":
                // RULE 13
                rulesApplied.add(13);
                tokens.remove(0);
                break;
            case "/>_QATTR":
                // RULE 12
                rulesApplied.add(12);
                tokens.remove(0);
                break;
            case "=_EQ":
                rulesApplied.add(14);
                tokens.remove(0);
                break;
            case "QSTR_STRING":
                // TODO: parse string
                rulesApplied.add(69);
                tokens.remove(0);
                break;
            case "STR_ATTR":
                // RULE 9
                rulesApplied.add(9);
                grammarStack.push(Sym.STRING);
                grammarStack.push(Sym.EQ);
                grammarStack.push(Sym.NAME);
                break;
            case "STR_QATTR":
                // RULE 10
                rulesApplied.add(10);
                grammarStack.push(Sym.QATTR);
                grammarStack.push(Sym.ATTR);
                break;
            case "STR_NAME":
                // TODO: parse name
                rulesApplied.add(69);
                tokens.remove(0);
                break;
            case "STR_QENDTAG":
                //RULE 5
                rulesApplied.add(5);
                grammarStack.push(Sym.QENDTAG);
                grammarStack.push(Sym.DATA);
                break;
            case "STR_DATA":
                // TODO: parse data
                rulesApplied.add(70);
                tokens.remove(0);
                break;
            default:
                System.out.println("ERROR:");
                System.out.println(Pair(token, sym));
                System.out.println(tokens);
                System.out.println(grammarStack);
                System.out.println(rulesApplied);
                System.exit(1);
            }
        }
        System.out.println("Done");
        System.out.println(rulesApplied);
    }
}
