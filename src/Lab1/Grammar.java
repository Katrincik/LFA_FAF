package src.Lab1;

import java.util.*;

class Grammar {
    // Rules that associate non-terminal symbol keys with production rules values
    private final Map<Character, List<String>> rules;
    private final Random random;

    public Grammar() {
        this.rules = new HashMap<>();
        this.random = new Random();
        initializeRules();
    }

    private void initializeRules() {
        // Populate rules with the grammar production rules
        // Each non-terminal symbol ('S', 'B', 'D') is mapped to its production rules
        rules.put('S', List.of("aB"));
        rules.put('B', List.of("aD", "bB", "cS"));
        rules.put('D', List.of("aD", "bS", "c"));
    }

    public String generateString() {
        return expand('S');
    }

    private String expand(char symbol) {
        // Stringbuilder used for better appending characters in a loop
        StringBuilder result = new StringBuilder();
        // Check if the symbol is a key ('S', 'B', 'D')
        if (rules.containsKey(symbol)) {
            // Take the possible rules and store them in a list in order to retrieve one randomly
            List<String> possibleProductions = rules.get(symbol);
            String production = possibleProductions.get(random.nextInt(possibleProductions.size()));
            for (int i = 0; i < production.length(); i++) {
                // If non-terminal will further expend it
                // If terminal not found in rules, it appends it to the result
                char nextSymbol = production.charAt(i);
                result.append(expand(nextSymbol));
            }
        } else {
            result.append(symbol);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        for (int i = 0; i < 5; i++) {
            System.out.println(grammar.generateString());
        }
    }
}
