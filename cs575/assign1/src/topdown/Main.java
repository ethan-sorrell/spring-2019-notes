package topdown;
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
        if (args.length != 1) {
            System.out.println("Expected 1 argument got " + args.length);
            System.exit(1);
        }
        File file = new File(args[0]);
        // File file = new File("/home/ethan/git/spring-2019-notes/cs575/assign1/test.txt");
        Scanner sc = new Scanner(file);
        sc.useDelimiter("\\Z");
        String input = sc.next();
        List<String> tokens = Tokenizer.Tokenize(input);
        Parser.Parse(tokens);
    }
}

