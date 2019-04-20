package assign1;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Tokenizer {
    public static List<String> Tokenize(String input) {
        List<String> tokens = new ArrayList<String>();
        while (input.startsWith(" ") | input.startsWith("\t") |
               input.startsWith("\n") | input.startsWith("\r"))
            input = input.substring(1, input.length());
        if (input.length() == 0) {
            return tokens;
        }
        while (input.length() != 0) {
            if (input.startsWith("<") && !input.startsWith("<!--")) {
                // we have a tag
                input = input.substring(1, input.length());
                if (input.startsWith("/")) {
                    // we have an EndTag
                    tokens.add("</");
                    input = input.substring(1, input.length());
                } else {
                    tokens.add("<");
                }
                int endToken = input.indexOf(">", 1);
                if (input.charAt(endToken - 1) == '/') {
                    // we have an EmptyTag
                    endToken -= 1;
                    List<String> subProblem1 = TagTokenize(input.substring(0, endToken));
                    tokens.addAll(subProblem1);
                    tokens.add("/>");
                } else {
                    // StartTag or EndTag
                    List<String> subProblem1 = TagTokenize(input.substring(0, endToken));
                    tokens.addAll(subProblem1);
                    tokens.add(">");
                }
                input = input.substring(endToken+1, input.length());
            } else {
                // We have data
                if (input.indexOf("<!--") != -1) {
                    // We have a comment
                    int startComment = input.indexOf("<!--") + 4;
                    input = input.substring(startComment, input.length());
                    int endComment = input.indexOf("-->");
                    if (input.substring(0, endComment).indexOf("--") != -1) {
                        System.out.println("Invalid Comment");
                        System.exit(1);
                    }
                    input = input.substring(endComment+4, input.length());
                } else if (input.indexOf("<") != -1) {
                    String data = input.substring(0, input.indexOf("<"));
                    tokens.add(data);
                    input = input.substring(input.indexOf("<"), input.length());
                } else {
                    System.out.println("Warning: Last token is data (not tag)");
                    tokens.add(input);
                    input = "";
                }
            }
            while (input.startsWith(" ") | input.startsWith("\t") |
                   input.startsWith("\n") | input.startsWith("\r"))
                input = input.substring(1, input.length());
        }
        return tokens;
    }

    public static List<String> TagTokenize(String input) {
        List<String> tokens = new ArrayList<String>();
        // First add Names
        int endName;
        if (input.indexOf(" ") != -1)
            endName = input.indexOf(" ");
        else
            endName = input.length();
        String name = input.substring(0, endName);
        if (Pattern.matches("[a-zA-Z_:][a-zA-Z0-9-]*+", name)) {
            tokens.add(name);
            if(endName == input.length())
                input = "";
            else
                input = input.substring(endName+1, input.length());
        } else {
            // error
            System.out.println("Invalid Name: " + name);
            System.exit(1);
        }
        // Then add attributes
        while (input.length() > 0) {
            String n = input.substring(0, input.indexOf("="));
            tokens.add(n);
            tokens.add("=");
            input = input.substring(input.indexOf("=")+1, input.length());
            if (input.startsWith("\"")) {
                input = input.substring(1, input.length());
                n = "\"" + input.substring(0, input.indexOf("\"")+1);
                tokens.add(n);
                input = input.substring(input.indexOf("\"")+1, input.length());
            } else if (input.startsWith("'")) {
                input = input.substring(1, input.length());
                n = "'" + input.substring(0, input.indexOf("'")+1);
                tokens.add(n);
                input = input.substring(input.indexOf("'")+1, input.length());
            }
            while (input.startsWith(" ") | input.startsWith("\t") |
                   input.startsWith("\n") | input.startsWith("\r"))
                input = input.substring(1, input.length());
        }
        return tokens;
    }
}
