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
        //convertToCNF();

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
        // Initialize a new map to store the updated set of productions without renaming rules
        Map<String, List<String>> newProductions = new HashMap<>();

        for (String nonTerminal : productions.keySet()) {
            // Create a copy of the current productions for this non-terminal
            List<String> currentProductions = new ArrayList<>(productions.get(nonTerminal));
            // Initialize a list to store the new set of productions for this non-terminal, after eliminating renaming
            List<String> toAdd = new ArrayList<>();

            for (String production : currentProductions) {
                if (production.length() == 1 && nonTerminals.contains(production)) {
                    // Instead of modifying currentProductions, store changes to apply later
                    toAdd.addAll(productions.get(production));
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

