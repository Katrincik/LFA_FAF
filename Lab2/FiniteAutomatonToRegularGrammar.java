import java.util.*;

public class FiniteAutomatonToRegularGrammar {

    public static void main(String[] args) {
        // Initialize a map to hold the transitions of the FA
        Map<String, Map<Character, List<String>>> transitions = new HashMap<>();

        // Define transitions for state q0
        Map<Character, List<String>> q0Transitions = new HashMap<>();
        q0Transitions.put('a', List.of("q0"));
        q0Transitions.put('b', List.of("q1"));

        // Define transitions for state q1
        Map<Character, List<String>> q1Transitions = new HashMap<>();
        q1Transitions.put('a', Arrays.asList("q1", "q2"));
        q1Transitions.put('b', List.of("q3"));

        // Define transitions for state q2
        Map<Character, List<String>> q2Transitions = new HashMap<>();
        q2Transitions.put('a', List.of("q2"));
        q2Transitions.put('b', List.of("q3"));

        // Adding transitions to the map
        transitions.put("q0", q0Transitions);
        transitions.put("q1", q1Transitions);
        transitions.put("q2", q2Transitions);

        boolean isDeterministic = isDeterministicFA(transitions);
        System.out.println("The FA is " + (isDeterministic ? "deterministic." : "non-deterministic."));

        convertToRegularGrammar(transitions, "q3");
    }

    public static void convertToRegularGrammar(Map<String, Map<Character, List<String>>> transitions, String acceptingState) {
        for (String state : transitions.keySet()) {
            Map<Character, List<String>> stateTransitions = transitions.get(state);
            for (Map.Entry<Character, List<String>> transition : stateTransitions.entrySet()) {
                char input = transition.getKey();
                for (String nextState : transition.getValue()) {
                    System.out.println(state + " -> " + input + nextState);
                }
            }
            // If the state is an accepting state, it also goes to ε
            if (state.equals(acceptingState)) {
                System.out.println(state + " -> ε");
            }
        }
    }

    public static boolean isDeterministicFA(Map<String, Map<Character, List<String>>> transitions) {
        for (Map<Character, List<String>> stateTransitions : transitions.values()) {
            for (List<String> targetStates : stateTransitions.values()) {
                if (targetStates.size() > 1) {
                    // If any symbol leads to more than one state, it's non-deterministic
                    return false;
                }
            }
        }
        // If no input symbol leads to more than one state, the FA is deterministic
        return true;
    }
}