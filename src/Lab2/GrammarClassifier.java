package src.Lab2;

public class GrammarClassifier {

    public static void main(String[] args) {
        String[] productions = {
                "S -> aB",
                "B -> aD",
                "B -> bB",
                "D -> aD",
                "D -> bS",
                "B -> cS",
                "D -> c"
        };

        int grammarType = classifyGrammar(productions);
        System.out.println("The grammar is of type " + grammarType);
    }

    public static int classifyGrammar(String[] productions) {
        boolean isType3 = true;
        boolean isType2 = true;
        boolean isType1 = true;

        for (String production : productions) {
            // Split the production rule into left and right parts based on "->"
            String[] parts = production.split("->");
            String leftSide = parts[0].trim();
            String rightSide = parts[1].trim();

            // Check for Type 3: Regular Grammar
            if (!rightSide.matches("^[a-c]*[SBD]?$")) {
                isType3 = false;
            }

            // Check for Type 2: Context-Free Grammar
            if (leftSide.length() != 1 || !leftSide.matches("[SBD]")) {
                isType2 = false;
            }

            // Check for Type 1: Context-Sensitive Grammar
            if (rightSide.length() < leftSide.length()) {
                isType1 = false;
                break;
            }
        }

        // Determine grammar type
        if (isType3) {
            return 3;
        } else if (isType2) {
            return 2;
        } else if (isType1) {
            return 1;
        } else {
            // If none of the above, it's Type 0.
            return 0;
        }
    }
}

