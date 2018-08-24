import java.io.IOException;

public class ProgramTester {

    public static void main (String[] args) {
        Parser fileParser = new Parser();
        Grammar parsedGrammar = null;

        try {
            parsedGrammar = fileParser.parseGrammarFromFile("CFG.txt");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        if (parsedGrammar != null) {
            System.out.println(parsedGrammar.toString());
            Simplifier simplifier = new Simplifier();
            Grammar simplifiedGrammar = simplifier.simplify(parsedGrammar);
            System.out.println(simplifiedGrammar.toString());
        }
        else {
            System.out.println("The parsed Grammar was null! :|");
        }
    }
}
