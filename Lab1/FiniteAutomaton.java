import java.util.*;

public class FiniteAutomaton {
    private Set<String> states;
    private Set<Character> alphabet;
    // Pair state with input symbol and present a new string as a new state after transition
    private Map<Map.Entry<String, Character>, String> transitions;
    private String current_state;
    // Allow last state/character
    private Set<String> accepting_states;

    public FiniteAutomaton() {
        states = new HashSet<>();
        states.add("S");
        states.add("B");
        states.add("D");

        alphabet = new HashSet<>();
        alphabet.add('a');
        alphabet.add('b');
        alphabet.add('c');

        transitions = new HashMap<>();
        // Define transitions: state + input symbol -> new state
        // For ex. when state S and input a transition to B
        transitions.put(new AbstractMap.SimpleEntry<>("S", 'a'), "B");
        transitions.put(new AbstractMap.SimpleEntry<>("B", 'a'), "D");
        transitions.put(new AbstractMap.SimpleEntry<>("B", 'b'), "B");
        transitions.put(new AbstractMap.SimpleEntry<>("B", 'c'), "S");
        transitions.put(new AbstractMap.SimpleEntry<>("D", 'a'), "D");
        transitions.put(new AbstractMap.SimpleEntry<>("D", 'b'), "S");
        transitions.put(new AbstractMap.SimpleEntry<>("D", 'c'), "c");

        // Start at S
        current_state = "S";

        accepting_states = new HashSet<>();
        // Define accepting state(s)
        accepting_states.add("c");
    }

    // Attempt a transition based on the current state and input symbol
    public boolean transition(char symbol) {
        // Pair the current state with the input symbol and looks into created map for transitions 
        Map.Entry<String, Character> key = new AbstractMap.SimpleEntry<>(current_state, symbol);
        // Checks if there is a transition rule defined for the current state and input symbol
        if (transitions.containsKey(key)) {
            // Update current state based on transition with new state
            current_state = transitions.get(key);
            return true;
        } else {
            // Transition not possible
            return false;
        }
    }

    // Checks if the given input string is accepted by the automaton
    public boolean isStringAccepted(String inputString) {
        for (int i = 0; i < inputString.length(); i++) {
            // Retrieves the current character (symbol) from the input string
            char symbol = inputString.charAt(i);
            // If symbol not in alphabet or transition fails, string is not accepted
            if (!alphabet.contains(symbol) || !transition(symbol)) {
                return false;
            }
        }
        // Check if ending state is an accepting state
        return accepting_states.contains(current_state);
    }

    public static void main(String[] args) {
        FiniteAutomaton fa = new FiniteAutomaton();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter a string to check:");
            String inputString = scanner.nextLine();

            boolean isValid = fa.isStringAccepted(inputString);
            if (isValid) {
                System.out.println("The string is accepted by the automaton.");
            } else {
                System.out.println("The string is not accepted by the automaton.");
            }
        }
    }
}