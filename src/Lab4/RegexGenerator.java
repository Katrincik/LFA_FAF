package src.Lab4;

import java.util.Random;

public class RegexGenerator {
    // Initialize a Random object for generating random numbers.
    private static final Random random = new Random();

    public static String generateFromRegex(String regex) {
        StringBuilder result = new StringBuilder();
        // Store the last appended sequence or character
        String lastAppended = "";
        // Iterate through each character of the regex pattern.
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case '(':
                    // Find the matching ')'
                    int closingParethesis = regex.indexOf(')', i);
                    // Check if followed by '*'.
                    boolean asterisk = closingParethesis + 1 < regex.length() && regex.charAt(closingParethesis + 1) == '*';
                    // Split options within the group.
                    String[] options = regex.substring(i + 1, closingParethesis).split("\\|");

                    // case '*'
                    if (asterisk) {
                        // If followed by '*', decide to append an option and possibly repeat it.
                        if (random.nextBoolean()) {
                            int option = random.nextInt(options.length);
                            lastAppended = options[option];
                            result.append(lastAppended);

                            // Decide on repeating the chosen option 0 to 4 more times
                            int repetitions = random.nextInt(5);
                            for (int j = 0; j < repetitions; j++) {
                                result.append(lastAppended);
                            }
                        } // else {
                          //   Decide to skip appending any option
                          // }
                    } else {
                        // If not followed by '*', select and append a random option.
                        lastAppended = options[random.nextInt(options.length)];
                        result.append(lastAppended);
                    }

                    // Skip to ')'
                    i = closingParethesis;
                    if (asterisk) {
                        // Skip also the '*' character
                        i++;
                    }
                    break;
                case '+':
                    // Append 1 to 4 repetitions of the previous character/group
                    int newRepetitions = 1 + random.nextInt(4);
                    for (int j = 0; j < newRepetitions; j++) {
                        result.append(lastAppended);
                    }
                    break;
                case '^':
                    // Find the next '.' to get the repetition count
                    int dot = regex.indexOf('.', i);
                    if (dot != -1) {
                        // Extract the number between '^' and '.' for repetition count
                        String numStr = regex.substring(i + 1, dot);
                        int repeatCount;
                        try {
                            repeatCount = Integer.parseInt(numStr);
                        } catch (NumberFormatException e) {
                            // Default repetition count in case of parsing failure.
                            repeatCount = 1;
                        }
                        // Repeat the previous character/group as specified. Subtract 1 because it already exists once
                        for (int j = 0; j < repeatCount - 1; j++) {
                            result.append(lastAppended);
                        }
                        // Move the index to the position of the dot
                        i = dot;
                    } else {
                        lastAppended = Character.toString(c);
                        // If no '.' is found, treat '^' as a literal
                        result.append(c);
                    }
                    break;
                default:
                    // Directly append literal characters and fixed sequences
                    lastAppended = Character.toString(c);
                    result.append(c);
                    break;
            }
        }
        // Return generated string
        return result.toString();
    }

    public static void describeRegexProcessing(String regex) {
        StringBuilder description = new StringBuilder();
        int stepCounter = 1;

        description.append("Processing sequence for regex: ").append(regex).append("\n");

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case '(':
                    int closingParenthesis = regex.indexOf(')', i);
                    boolean asterisk = closingParenthesis + 1 < regex.length() && regex.charAt(closingParenthesis + 1) == '*';
                    description.append(stepCounter++).append(". Found a group '(").append(regex.substring(i + 1, closingParenthesis)).append(")'.\n");
                    if (asterisk) {
                        description.append(stepCounter++).append(". This group is followed by '*', indicating it can be chosen zero or more times.\n");
                    } else {
                        description.append(stepCounter++).append(". This group will be chosen exactly once.\n");
                    }
                    i = closingParenthesis;
                    break;
                case '+':
                    description.append(stepCounter++).append(". Found a '+', indicating the previous character/group will be repeated one to four times.\n");
                    break;
                case '*':
                    break;
                case '^':
                    int dot = regex.indexOf('.', i);
                    if (dot != -1) {
                        String numStr = regex.substring(i + 1, dot);
                        int repeatCount = 1;
                        try {
                            repeatCount = Integer.parseInt(numStr);
                        } catch (NumberFormatException e) {
                            repeatCount = 1;
                        }
                        description.append(stepCounter++).append(". Found a '^', specifying to repeat the previous character/group ").append(repeatCount).append(" times.\n");
                        i = dot;
                    } else {
                        description.append(stepCounter++).append(". Found a '^' but no following numerical repetition specification, treating as literal.\n");
                    }
                    break;
                default:
                    description.append(stepCounter++).append(". Found a literal character '").append(c).append("'.\n");
                    break;
            }
        }

        description.append(stepCounter).append(". End of processing.\n");
        System.out.println(description);
    }

    public static void main(String[] args) {
        System.out.println("Generating strings for regex 1:");
        System.out.println(generateFromRegex("(a|b)(c|d)E+G"));

        System.out.println("Generating strings for regex 2:");
        System.out.println(generateFromRegex("P(Q|R|S)T(UV|W|X)*Z+"));

        System.out.println("Generating strings for regex 3:");
        System.out.println(generateFromRegex("1(0|1)*2(3|4)^5.36"));

        System.out.println("Describe regex processing for next regex:");
        describeRegexProcessing("1(0|1)*2(3|4)^5.36");
    }
}
