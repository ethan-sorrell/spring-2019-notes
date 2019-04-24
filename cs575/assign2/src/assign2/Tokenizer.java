package assign2;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

public class Tokenizer {
    public static List<String> Tokenize(String input) {
        List<String> tokens = new ArrayList<String>();
        Stack<String> nameStack = new Stack<String>();
        input = input.trim();
        if (input.length() == 0) {
            return tokens;
        }
        boolean startTag = true;
        while (input.length() != 0) {
            if (input.startsWith("<") && !input.startsWith("<!--")) {
                // we have a tag
                input = input.substring(1, input.length()).trim();
                if (input.startsWith("/")) {
                    // we have an EndTag
                    tokens.add("</");
                    startTag = false;
                    input = input.substring(1, input.length()).trim();
                } else {
                    tokens.add("<");
                }
                int endToken = input.indexOf(">", 1);
                if (input.charAt(endToken - 1) == '/') {
                    // we have an EmptyTag
                    List<String> tagTokens = TagTokenize(input.substring(0, endToken-1));
                    CheckAttributes(tagTokens);
                    tagTokens.remove(0);
                    tokens.add("Name");
                    while (tagTokens.size() != 0) {
                        tagTokens.remove(0);
                        tokens.add("Name");
                        tagTokens.remove(0);
                        tokens.add("=");
                        tagTokens.remove(0);
                        tokens.add("String");
                    }
                    tokens.add("/>");
                } else {
                    // StartTag or EndTag
                    List<String> tagTokens = TagTokenize(input.substring(0, endToken));
                    if (startTag) {
                        CheckAttributes(tagTokens);
                        nameStack.push(tagTokens.get(0));
                    } else {
                        String matchName = nameStack.pop();
                        if (tagTokens.get(0).compareTo(matchName) != 0) {
                            // Error
                            System.out.println("EndTag name does not match StartTag");
                            System.out.println(matchName + " vs " + tagTokens.get(0));
                            System.exit(1);
                        }
                        startTag = true;
                    }
                    tagTokens.remove(0);
                    tokens.add("Name");
                    while (tagTokens.size() != 0) {
                        tagTokens.remove(0);
                        tokens.add("Name");
                        tagTokens.remove(0);
                        tokens.add("=");
                        tagTokens.remove(0);
                        tokens.add("String");
                    }
                    tokens.add(">");
                }
                input = input.substring(endToken+1, input.length()).trim();
            } else {
                // We have data
                if (input.indexOf("<!--") != -1 &&
                    input.indexOf("<") == input.indexOf("<!--")) {
                    // We have a comment
                    int startComment = input.indexOf("<!--") + 4;
                    String temp = input.substring(startComment, input.length());
                    int endComment = temp.indexOf("-->");
                    if (temp.substring(0, endComment).indexOf("--") != -1) {
                        // Error
                        System.out.println("Invalid Comment");
                        System.exit(1);
                    }
                    input = input.substring(0, input.indexOf("<!--")) +
                        temp.substring(endComment+4, temp.length());
                } else if (input.indexOf("<") != -1) {
                    String data = input.substring(0, input.indexOf("<"));
                    if (data.contains(">") || data.contains("<")) {
                        // error
                        System.out.println("Invalid Data: " + data);
                        System.out.println("Data cannot contain < or >");
                        System.out.println("Instead use &lt; or &gt;");
                        System.exit(1);
                    }
                    String temp = data;
                    while (temp.contains("&")) {
                        temp = temp.substring(temp.indexOf("&"), temp.length());
                        if (!temp.contains(";")) {
                            // Error
                            System.out.println("Error: Found & in data without corresponding ;");
                            System.exit(1);
                        }
                        String substr = temp.substring(0, temp.indexOf(";")+1);
                        if (!Pattern.matches("&((lt)|(gt)|(quot)|(apos)|(amp));", substr)) {
                            // Not a special
                            if (!Pattern.matches("&#[0-9]++;", substr) &&
                                !Pattern.matches("&#x[0-9a-fA-F]++;", substr)) {
                                // Not a Reference
                                System.out.println("Invalid Char: " + substr);
                                System.out.println("Appears as Malformed Special Character or Reference");
                                System.exit(1);
                            }
                        }
                        temp = temp.substring(temp.indexOf(";"), temp.length());
                    }
                    tokens.add("Data");
                    input = input.substring(input.indexOf("<"), input.length());
                } else {
                    System.out.println("Error: Last token is data (not tag)");
                    System.exit(1);
                    // tokens.add(input);
                    // input = "";
                }
            }
            // while (input.startsWith(" ") | input.startsWith("\t") |
            // input.startsWith("\n") | input.startsWith("\r"))
            // input = input.substring(1, input.length());
            input = input.trim();
        }
        return tokens;
    }

    public static int NextWhiteSpace(String input) {
        int output = input.length() + 1;
        if (input.contains(" ") && input.indexOf(" ") < output) {
            output = input.indexOf(" ");
        }
        if (input.contains("\n") && input.indexOf("\n") < output) {
            output = input.indexOf("\n");
        }
        if (input.contains("\t") && input.indexOf("\t") < output) {
            output = input.indexOf("\t");
        }
        if (input.contains("\r") && input.indexOf("\r") < output) {
            output = input.indexOf("\r");
        }
        if (output == input.length() + 1)
            return -1;
        else
            return output;
    }

    public static List<String> TagTokenize(String input) {
        List<String> tokens = new ArrayList<String>();
        // First add Names
        int endName;
        input = input.trim();
        if (NextWhiteSpace(input) != -1)
            endName = NextWhiteSpace(input);
        else
            endName = input.length();
        String name = input.substring(0, endName);
        if (Pattern.matches("[a-zA-Z_:][a-zA-Z0-9-_:.]*+", name)) {
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
        input = input.trim();
        while (input.length() > 0) {
            name = input.substring(0, input.indexOf("=")).trim();
            if (Pattern.matches("[a-zA-Z_:][a-zA-Z0-9-_:.]*+", name)) {
                tokens.add(name);
            } else {
                // error
                System.out.println("Invalid Name: " + name);
                System.exit(1);
            }
            tokens.add("=");
            // Add String
            input = input.substring(input.indexOf("=")+1, input.length()).trim();
            String str = "";
            String delim = "";
            if (input.startsWith("\"")) {
                delim = "\"";
            } else if (input.startsWith("'")) {
                delim = "'";
            } else {
                // error
                System.out.println("Error expected string, found: " +
                                   input.substring(0, input.length() > 10 ? 10 : input.length()));
                System.exit(1);
            }
            input = input.substring(1, input.length()).trim();
            if (!input.contains(delim)) {
                System.out.println("Error could not find matching string delimiter: " + delim);
                System.out.println(input.substring(0, input.length() > 10 ? 10 : input.length()));
                System.exit(1);
            }
            str = input.substring(0, input.indexOf(delim));
            if (str.contains(">") || str.contains("<")) {
                // error
                System.out.println("Invalid String: " + str);
                System.out.println("String cannot contain < or >");
                System.out.println("Instead use &lt; or &gt;");
                System.exit(1);
            }
            String temp = str;
            while (temp.contains("&")) {
                String substr = temp.substring(temp.indexOf("&"), temp.length());
                if (!temp.contains(";")) {
                    // Error
                    System.out.println("Error: Found & in string without corresponding ;");
                    System.exit(1);
                }
                substr = substr.substring(0, substr.indexOf(";")+1);
                if (!Pattern.matches("&((lt)|(gt)|(quot)|(apos)|(amp));", substr)) {
                    // Not a special
                    if (!Pattern.matches("&#[0-9]++;", substr) &&
                        !Pattern.matches("&#x[0-9a-fA-F]++;", substr)) {
                        // Not a Reference
                        System.out.println("Invalid Char: " + substr);
                        System.out.println("Appears as Malformed Special Character or Reference");
                        System.exit(1);
                    }
                }
                temp = temp.substring(temp.indexOf(";"), temp.length());
            }
            str = delim + str + delim;
            tokens.add(str);
            input = input.substring(input.indexOf(delim)+1, input.length()).trim();
            // input = input.substring(input.indexOf("\"")+1, input.length());
        }
        return tokens;
    }


    /**
     * Filters list of attribute names from output of TagTokenize
     * and checks that there are no duplicates
     */
    public static void CheckAttributes(List<String> input) {
        List<String> attrSet = new ArrayList<String>();
        for (int i=1; i < input.size(); i+=3) {
            if (attrSet.contains(input.get(i))) {
                // Error
                System.out.println("Tag contains duplicate attribute: " + input.get(i));
                System.exit(1);
            }
            attrSet.add(input.get(i));
        }
    }
}
