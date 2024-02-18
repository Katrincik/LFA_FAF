import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Grammar {
    private final Map<Character, List<String>> rules;
    private final Random random;

    public Grammar() {
        this.rules = new HashMap<>();
        this.random = new Random();
        initializeRules();
    }

    private void initializeRules() {
        // Each non-terminal symbol ('S', 'B', 'D') is associated with its production
        // rules.
        rules.put('S', List.of("aB"));
        rules.put('B', List.of("aD", "bB", "cS"));
        rules.put('D', List.of("aD", "bS", "c"));
    }

    public String generateString() {
        // Start symbol - S
        return expand('S');
    }

    private String expand(char symbol) {
        StringBuilder result = new StringBuilder();
        // Check if the symbol is a non-terminal symbol
        if (rules.containsKey(symbol)) {
            // Randomly select one of the production rules for the symbol
            List<String> possibleProductions = rules.get(symbol);
            String production = possibleProductions.get(random.nextInt(possibleProductions.size()));
            // Recursively expand each symbol in the selected production
            for (int i = 0; i < production.length(); i++) {
                char nextSymbol = production.charAt(i);
                result.append(expand(nextSymbol));
            }
        } else {
            // If it's a terminal symbol, simply append it to the result
            result.append(symbol);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        // Generate 5 valid strings
        for (int i = 0; i < 5; i++) {
            System.out.println(grammar.generateString());
        }
    }
}
