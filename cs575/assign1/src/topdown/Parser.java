package topdown;
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
        } else if (token.compareTo("String") == 0) {
            return "STR_" + sym.name();
        } else if (token.compareTo("Data") == 0){
            return "DATA_" + sym.name();
        } else if (token.compareTo("Name") == 0){
            return "NAME_" + sym.name();
        } else {
            System.out.println("ERROR invalid token: " + token);
            System.exit(1);
            return "";
        }
    }

    public static void Parse(List<String> tokens) {
        Stack<Sym> grammarStack = new Stack<Sym>();
        grammarStack.add(Sym.ELEMENT);
        Stack<String> nameStack = new Stack<String>();
        List<Integer> rulesApplied = new ArrayList<Integer>();
        String token;
        Sym sym;
        System.out.println("Document -> Element");
        while (tokens.size() > 0) {
            token = tokens.get(0);
            sym = grammarStack.pop();
            switch (Pair(token, sym)) {
            case "<_ELEMENT":
                if (tokens.indexOf("/>") == -1 ||
                    tokens.indexOf(">") < tokens.indexOf("/>")) {
                    // RULE 1
                    System.out.println("Element -> StartTag QEndTag");
                    rulesApplied.add(1);
                    grammarStack.push(Sym.QENDTAG);
                    grammarStack.push(Sym.STARTTAG);
                } else {
                    // RULE 2
                    System.out.println("Element -> EmptyTag");
                    rulesApplied.add(2);
                    grammarStack.push(Sym.EMPTYTAG);
                }
                break;
            case "<_QENDTAG":
                // RULE 4
                System.out.println("QEndTag -> Element QEndTag");
                rulesApplied.add(4);
                grammarStack.push(Sym.QENDTAG);
                grammarStack.push(Sym.ELEMENT);
                break;
            case "<_STARTTAG":
                // RULE 3
                System.out.println("StartTag -> < Name QAttribute");
                rulesApplied.add(3);
                grammarStack.push(Sym.QATTR);
                grammarStack.push(Sym.NAME);
                tokens.remove(0);
                break;
            case "<_EMPTYTAG":
                // RULE 8
                System.out.println("EmptyTag -> < Name QAttribute");
                rulesApplied.add(8);
                grammarStack.push(Sym.QATTR);
                grammarStack.push(Sym.NAME);
                tokens.remove(0);
                break;
            case "</_ENDTAG":
                // RULE 7
                System.out.println("EndTag -> </ Name EB");
                rulesApplied.add(7);
                grammarStack.push(Sym.EB);
                grammarStack.push(Sym.NAME);
                tokens.remove(0);
                break;
            case "</_QENDTAG":
                // RULE 6
                System.out.println("QEndTag -> EndTag");
                rulesApplied.add(6);
                grammarStack.push(Sym.ENDTAG);
                break;
            case ">_QATTR":
                // RULE 11
                System.out.println("Qattribute -> >");
                rulesApplied.add(11);
                tokens.remove(0);
                break;
            case ">_EB":
                // RULE 13
                System.out.println("EB -> >");
                rulesApplied.add(13);
                tokens.remove(0);
                break;
            case "/>_QATTR":
                // RULE 12
                System.out.println("QAttribute -> />");
                rulesApplied.add(12);
                tokens.remove(0);
                break;
            case "=_EQ":
                // Rule 14
                System.out.println("EQ -> =");
                rulesApplied.add(14);
                tokens.remove(0);
                break;
            case "STR_STRING":
                // rulesApplied.add(69);
                tokens.remove(0);
                break;
            case "NAME_ATTR":
                // RULE 9
                System.out.println("Attribute -> Name EQ String");
                rulesApplied.add(9);
                grammarStack.push(Sym.STRING);
                grammarStack.push(Sym.EQ);
                grammarStack.push(Sym.NAME);
                break;
            case "NAME_QATTR":
                // RULE 10
                System.out.println("QAttribute -> Attribute QAttribute");
                rulesApplied.add(10);
                grammarStack.push(Sym.QATTR);
                grammarStack.push(Sym.ATTR);
                break;
            case "NAME_NAME":
                // rulesApplied.add(69);
                tokens.remove(0);
                break;
            case "DATA_QENDTAG":
                //RULE 5
                System.out.println("QEndTag -> Data QEndTag");
                rulesApplied.add(5);
                grammarStack.push(Sym.QENDTAG);
                grammarStack.push(Sym.DATA);
                break;
            case "DATA_DATA":
                // rulesApplied.add(70);
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
        // System.out.println(rulesApplied);
    }
}
