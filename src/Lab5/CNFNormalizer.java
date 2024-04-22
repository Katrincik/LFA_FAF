package src.Lab5;

import java.util.*;

public class CNFNormalizer {
    private List<String> nonTerminals;
    private List<String> terminals;
    private Map<String, List<String>> productions;
    private String startSymbol;

    public CNFNormalizer(List<String> nonTerminals, List<String> terminals,
                         Map<String, List<String>> productions, String startSymbol) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.productions = productions;
        this.startSymbol = startSymbol;
    }

    public Map<String, List<String>> normalizeGrammar() {
        eliminateEpsilonProductions();
        eliminateRenaming();
        eliminateNonProductiveSymbols();
        eliminateInaccessibleSymbols();
        convertToCNF();

        return productions;
    }

    private void eliminateEpsilonProductions() {
        Set<String> nullable = new HashSet<>();
        // Find nullable non-terminals
        // A non-terminal is nullable if it directly produces an empty string (ε)
        for (String nonTerminal : productions.keySet()) {
            for (String production : productions.get(nonTerminal)) {
                if (production.equals("ε")) {
                    nullable.add(nonTerminal);
                    break;
                }
            }
        }

        // Loop to find indirectly nullable non-terminals.
        boolean changes;
        do {
            // Flag to check if the set of nullable non-terminals changed in this iteration
            changes = false;
            for (String nonTerminal : productions.keySet()) {
                for (String production : productions.get(nonTerminal)) {
                    if (production.chars().allMatch(c -> nullable.contains(String.valueOf((char) c)))) {
                        // If adding the non-terminal to the nullable set changes the set, then set changes to true
                        if (nullable.add(nonTerminal)) {
                            changes = true;
                        }
                    }
                }
            }
        } while (changes);

        // Add modified productions without nullable non-terminals
        for (String nonTerminal : new ArrayList<>(productions.keySet())) {
            List<String> newProductions = new ArrayList<>();
            for (String production : productions.get(nonTerminal)) {
                if (!production.equals("ε")) {
                    StringBuilder sb = new StringBuilder(production);
                    // Remove nullable symbols from the production
                    for (int i = 0; i < sb.length(); i++) {
                        String symbol = String.valueOf(sb.charAt(i));
                        if (nullable.contains(symbol)) {
                            sb.deleteCharAt(i);
                            // Adjust the index after removal
                            i--;
                            // Add the modified production if it's not empty
                            if (!sb.toString().isEmpty()) {
                                newProductions.add(sb.toString());
                            }
                        }
                    }
                    // Add the original production
                    newProductions.add(production);
                }
            }
            // Update the productions for the non-terminal
            productions.put(nonTerminal, newProductions);
        }
        // Remove ε-productions finally
        for (List<String> productionList : productions.values()) {
            productionList.removeIf("ε"::equals);
        }
    }

    private void eliminateRenaming() {
        boolean changesMade;
        do {
            changesMade = false;
            Map<String, List<String>> newProductions = new HashMap<>();

            for (String nonTerminal : productions.keySet()) {
                // Create a copy of the current productions for this non-terminal
                List<String> currentProductions = new ArrayList<>(productions.get(nonTerminal));
                // Initialize a list to store the new set of productions for this non-terminal, after eliminating renaming
                List<String> toAdd = new ArrayList<>();

                for (String production : currentProductions) {
                    if (production.length() == 1 && nonTerminals.contains(production)) {
                        // Add productions from the renaming non-terminal if not already present
                        for (String redirectedProduction : productions.get(production)) {
                            if (!currentProductions.contains(redirectedProduction) && !toAdd.contains(redirectedProduction)) {
                                toAdd.add(redirectedProduction);
                                changesMade = true; // A change was made, so we'll need to recheck
                            }
                        }
                    } else {
                        toAdd.add(production);
                    }
                }

                // Deduplicate and store in newProductions
                newProductions.put(nonTerminal, new ArrayList<>(new HashSet<>(toAdd)));
            }

            // Replace old productions with updated ones
            productions.clear();
            productions.putAll(newProductions);
        } while (changesMade);
    }

    private void eliminateNonProductiveSymbols() {
        Set<String> productive = new HashSet<>(terminals);
        boolean changes;
        do {
            changes = false;
            for (String nonTerminal : productions.keySet()) {
                for (String production : productions.get(nonTerminal)) {
                    // Check if all characters in the production are either productive or non-terminals
                    if (production.chars().allMatch(c -> productive.contains(String.valueOf((char) c)) || nonTerminals.contains(String.valueOf((char) c)))) {
                        // If the non-terminal leads to a production with all productive symbols, mark it as productive
                        if (productive.add(nonTerminal)) {
                            changes = true;
                        }
                    }
                }
            }
        } while (changes);

        // Create an iterator for all production rules
        Iterator<Map.Entry<String, List<String>>> iter = productions.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, List<String>> entry = iter.next();
            // If a non-terminal is not productive, remove its productions from the grammar
            if (!productive.contains(entry.getKey())) {
                iter.remove();
            } else {
                entry.getValue().removeIf(production -> !production.chars().allMatch(c -> productive.contains(String.valueOf((char) c))));
            }
        }
    }

    private void eliminateInaccessibleSymbols() {
        // Initialize a set to keep track of accessible symbols
        Set<String> accessible = new HashSet<>();
        // Initialize a set for symbols that need to be processed, starting with the start symbol
        Set<String> toProcess = new HashSet<>();
        toProcess.add(startSymbol);

        while (!toProcess.isEmpty()) {
            // Set for symbols found in the current round, to be processed in the next round
            Set<String> nextRound = new HashSet<>();
            for (String symbol : toProcess) {
                // Mark the symbol as accessible
                accessible.add(symbol);
                for (String production : productions.getOrDefault(symbol, Collections.emptyList())) {
                    for (char c : production.toCharArray()) {
                        String strC = String.valueOf(c);
                        // If the character is a non-terminal and not already marked as accessible, add it for the next round
                        if (nonTerminals.contains(strC) && !accessible.contains(strC)) {
                            nextRound.add(strC);
                        }
                    }
                }
            }
            // Update the set of symbols to process in the next round
            toProcess = nextRound;
        }

        productions.keySet().retainAll(accessible);
    }

    private void convertToCNF() {
        Map<String, String> terminalReplacements = new HashMap<>();
        // Track existing transformations
        Map<String, String> productionReplacements = new HashMap<>();
        // Prepare a list of new non-terminals to use for terminal replacements
        List<Character> newNonTerminals = Arrays.asList('Ш', 'Щ', 'Ч', 'Ц', 'Ж', 'Й',
                'Ъ', 'Э', 'Ю', 'Б', 'Ь', 'Г', 'Ы', 'П', 'Я', 'И');
        int newNonTerminalIndex = 0;

        // First pass: Replace terminal symbols in RHS of productions with new non-terminals
        Map<String, List<String>> newProductions = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            List<String> modifiedProductions = new ArrayList<>();
            for (String production : entry.getValue()) {
                // Check if production is a single terminal and should be kept as is
                if (production.length() == 1 && terminals.contains(production)) {
                    modifiedProductions.add(production);
                    continue;
                }

                StringBuilder newProduction = new StringBuilder();
                for (int i = 0; i < production.length(); i++) {
                    String symbol = String.valueOf(production.charAt(i));
                    if (terminals.contains(symbol)) {
                        terminalReplacements.putIfAbsent(symbol, String.valueOf(newNonTerminals.get(newNonTerminalIndex)));
                        newProduction.append(terminalReplacements.get(symbol));
                        if (terminalReplacements.size() > newNonTerminalIndex) {
                            newNonTerminalIndex = Math.min(newNonTerminalIndex + 1, newNonTerminals.size() - 1);
                        }
                    } else {
                        newProduction.append(symbol);
                    }
                }
                modifiedProductions.add(newProduction.toString());
            }
            newProductions.put(entry.getKey(), modifiedProductions);
        }

        // Add terminal replacements to productions
        Map<String, List<String>> finalNewProductions = newProductions;
        terminalReplacements.forEach((terminal, newNonTerminal) ->
                finalNewProductions.putIfAbsent(newNonTerminal, Collections.singletonList(terminal)));

        // Second pass: Ensure productions are binary or unary, without repeating transformations
        productions.clear();
        // Use the finalNewProductions from the first pass
        productions.putAll(finalNewProductions);
        newProductions = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            List<String> updatedProductions = new ArrayList<>();
            for (String production : entry.getValue()) {
                if (production.length() > 2) {
                    // Corrected handling for productions longer than two symbols
                    String remainingProduction = production;
                    while (remainingProduction.length() > 2) {
                        String firstTwoSymbols = remainingProduction.substring(0, 2);
                        remainingProduction = remainingProduction.substring(2);

                        // Check if we already have a replacement for the first two symbols
                        String newSymbol = productionReplacements.getOrDefault(firstTwoSymbols,
                                String.valueOf(newNonTerminals.get(newNonTerminalIndex)));
                        if (!productionReplacements.containsKey(firstTwoSymbols)) {
                            // If no existing replacement, update mappings and productions
                            productionReplacements.put(firstTwoSymbols, newSymbol);
                            newProductions.putIfAbsent(newSymbol, new ArrayList<>());
                            newProductions.get(newSymbol).add(firstTwoSymbols);

                            // Move to the next non-terminal if possible
                            if (newNonTerminalIndex < newNonTerminals.size() - 1) {
                                newNonTerminalIndex++;
                            }
                        }

                        // Update the remaining production to include the new non-terminal symbol
                        remainingProduction = newSymbol + remainingProduction;
                    }
                    updatedProductions.add(remainingProduction);
                } else {
                    // For unary or binary productions, just add them without modification
                    updatedProductions.add(production);
                }
            }
            newProductions.put(entry.getKey(), updatedProductions);
        }
        productions.clear();
        productions.putAll(newProductions);
    }

    public static void main(String[] args) {
        List<String> nonTerminals = Arrays.asList("S", "A", "B", "C", "D");
        List<String> terminals = Arrays.asList("a", "b");
        Map<String, List<String>> productions = new HashMap<>();
        productions.put("S", Arrays.asList("aB", "DA"));
        productions.put("A", Arrays.asList("a", "BD", "bDAB"));
        productions.put("B", Arrays.asList("b", "BA"));
        productions.put("D", Arrays.asList("ε", "BA"));
        productions.put("C", Arrays.asList("BA"));

        CNFNormalizer normalizer = new CNFNormalizer(nonTerminals, terminals, productions, "S");
        Map<String, List<String>> cnfProductions = normalizer.normalizeGrammar();

        // Output the CNF Productions for verification
        cnfProductions.forEach((key, value) -> System.out.println(key + " -> " + value));
    }
}

