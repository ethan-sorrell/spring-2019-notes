package assign2;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;


public class Parser {
    public enum Sym {
        // non-terminals
        ELEMENT, STARTTAG, ENDTAG, EMPTYTAG,
        ATTR, QATTR, DOCUMENT,
        // tokens aka terminals
        DATA, NAME, STRING, EQ, SB, EB, SSB, SEB,
        // because Java
        ERROR
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
        List<Integer> rulesApplied = new ArrayList<Integer>();
        Stack<Sym> parseStack = new Stack<Sym>();
        String token;
        Sym sym;
        while (tokens.size() > 0) {
            token = tokens.get(0);
            // Token to Sym
            switch (token) {
            case ">":
                sym = Sym.EB;
                break;
            case "<":
                sym = Sym.SB;
                break;
            case "=":
                sym = Sym.EQ;
                break;
            case "/>":
                sym = Sym.SEB;
                break;
            case "</":
                sym = Sym.SSB;
                break;
            case "Data":
                sym = Sym.DATA;
                break;
            case "Name":
                sym = Sym.NAME;
                break;
            case "String":
                sym = Sym.STRING;
                break;
            default:
                // Error
                System.out.println("Did not recognize token: " + token);
                System.exit(1);
                sym = Sym.ERROR;
            }
            // Reduce
            while (GetReduction(parseStack) != 0) {
                int rule = GetReduction(parseStack);
                parseStack = PerformReduction(parseStack, rule);
            }
            // System.out.println(parseStack);
            // Shift
            parseStack.push(sym);
            tokens.remove(0);
        }
        while (GetReduction(parseStack) != 0) {
            int rule = GetReduction(parseStack);
            parseStack = PerformReduction(parseStack, rule);
        }
        System.out.println(parseStack);
        if (parseStack.peek() == Sym.ELEMENT && parseStack.size() == 1) {
            System.out.println("Reducing Element to Document");
            parseStack.pop();
            parseStack.push(Sym.DOCUMENT);
        }
    }

    public static Stack<Sym> PerformReduction(Stack<Sym> p, int rule) {
        switch (rule) {
        case 1:
            System.out.println("Reducing Name = String to Attribute");
            p.pop();
            p.pop();
            p.pop();
            p.push(Sym.ATTR);
            break;
        case 2:
            System.out.println("Reducing </ Name > to EndTag");
            p.pop();
            p.pop();
            p.pop();
            p.push(Sym.ENDTAG);
            break;
        case 3:
            System.out.println("Reducing < Name /> to EmptyTag");
            p.pop();
            p.pop();
            p.pop();
            p.push(Sym.EMPTYTAG);
            break;
        case 4:
            System.out.println("Reducing < Name Attribute /> to EmptyTag");
            p.pop();
            p.pop();
            p.pop();
            p.pop();
            p.push(Sym.EMPTYTAG);
            break;
        case 5:
            System.out.println("Reducing < Name > to StartTag");
            p.pop();
            p.pop();
            p.pop();
            p.push(Sym.STARTTAG);
            break;
        case 6:
            System.out.println("Reducing Attribute Attribute to Attribute");
            p.pop();
            break;
        case 7:
            System.out.println("Reducing < Name Attribute > to StartTag");
            p.pop();
            p.pop();
            p.pop();
            p.pop();
            p.push(Sym.STARTTAG);
            break;
        case 8:
            System.out.println("Reducing EmptyTag to Element");
            p.pop();
            p.push(Sym.ELEMENT);
            break;
        case 9:
            System.out.println("Reducing Data EndTag to EndTag");
            p.pop();
            p.pop();
            p.push(Sym.ENDTAG);
            break;
        case 10:
            System.out.println("Reducing Element EndTag to EndTag");
            p.pop();
            p.pop();
            p.push(Sym.ENDTAG);
            break;
        case 11:
            System.out.println("Reducing StartTag EndTag to Element");
            p.pop();
            p.pop();
            p.push(Sym.ELEMENT);
            break;
        }
        return p;
    }

    public static int GetReduction(Stack<Sym> parseStack) {
        if (Match(parseStack, "NAME_EQ_STRING")) {
            // RULE 1
            return 1;
        } else if (Match(parseStack, "SSB_NAME_EB")) {
            // RULE 2
            return 2;
        } else if (Match(parseStack, "SB_NAME_SEB")) {
            // RULE 3
            return 3;
        } else if (Match(parseStack, "SB_NAME_ATTR_SEB")) {
            // RULE 4
            return 4;
        } else if (Match(parseStack, "SB_NAME_EB")) {
            // RULE 5
            return 5;
        } else if (Match(parseStack, "ATTR_ATTR")) {
            // RULE 6
            return 6;
        } else if (Match(parseStack, "SB_NAME_ATTR_EB")) {
            // RULE 7
            return 7;
        } else if (Match(parseStack, "EMPTYTAG")) {
            // RULE 8
            return 8;
        } else if (Match(parseStack, "DATA_ENDTAG")) {
            // RULE 9
            return 9;
        } else if (Match(parseStack, "ELEMENT_ENDTAG")) {
            // RULE 10
            return 10;
        } else if (Match(parseStack, "STARTTAG_ENDTAG")) {
            // RULE 11
            return 11;
        } else {
            return 0;
        }
    }

    /**
     * @params p parseStack
     * @params b String representation of RH of rule
     */
    public static boolean Match(Stack<Sym> p, String b) {
        int n = b.length() - b.replaceAll("_", "").length() + 1;
        if (StringRepr(p, n).compareTo(b) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String StringRepr(Stack<Sym> parseStack, int n) {
        String output = "";
        if (parseStack.size() == 0) {
            return "";
        }

        if (n > parseStack.size()) {
            n = parseStack.size();
        }
        for (int i = n; i > 1; i--) {
            output = output + parseStack.get(parseStack.size() - i) + "_";
        }
        output = output + parseStack.get(parseStack.size() - 1);
        return output;
    }
}
