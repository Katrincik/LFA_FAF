package src.Lab6;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum TokenType {
    INTEGER, OPERATOR, PUNCTUATION, WHITESPACE, SEPARATORS, IDENTIFIER, EOF
}

class Token {
    TokenType type;
    String value;

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    // Overrides the default toString method to print token in a readable format
    @Override
    public String toString() {
        return type + "('" + value + "')";
    }
}

// Lexer class responsible for converting a string of characters into a list of tokens
class Lexer {
    private final String input;
    private int pos = 0;
    private char currentChar;

    // Constructor initializes the lexer with input and sets the first character
    Lexer(String input) {
        this.input = input + '\0';
        currentChar = input.charAt(pos);
    }

    // Advances the position in the input and updates the current character
    void advance() {
        pos++;
        if (pos >= input.length()) {
            currentChar = '\0'; // End of file
        } else {
            currentChar = input.charAt(pos);
        }
    }

    void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    Token integer() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        return new Token(TokenType.INTEGER, result.toString());
    }

    Token identifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar))) {
            result.append(currentChar);
            advance();
        }
        return new Token(TokenType.IDENTIFIER, result.toString());
    }

    Token operator() {
        Token token = new Token(TokenType.OPERATOR, Character.toString(currentChar));
        advance();
        return token;
    }

    Token punctuation() {
        Token token = new Token(TokenType.PUNCTUATION, Character.toString(currentChar));
        advance();
        return token;
    }

    Token separators() {
        Token token = new Token(TokenType.SEPARATORS, Character.toString(currentChar));
        advance();
        return token;
    }

    // Tokenize the entire input string and returns a list of tokens
    List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (currentChar != '\0') {
            // Handle different character types to tokenize the input.
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            if (Character.isDigit(currentChar)) {
                tokens.add(integer());
                continue;
            }

            if (Character.isLetter(currentChar)) {
                tokens.add(identifier());
                continue;
            }

            if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == '%' || currentChar == '=' || currentChar == '>' || currentChar == '<') {
                tokens.add(operator());
                continue;
            }

            if (currentChar == '.' || currentChar == ',' || currentChar == '?' || currentChar == '!' || currentChar == ':' || currentChar == ';') {
                tokens.add(punctuation());
                continue;
            }

            if (currentChar == ')' || currentChar == '(' || currentChar == '}' || currentChar == '{' || currentChar == ']' || currentChar == '[' || currentChar == '"' || currentChar == '\'') {
                tokens.add(separators());
                continue;
            }

            // Handle unexpected characters and throw an exception
            throw new RuntimeException("Unexpected character: " + currentChar);
        }

        // Add an EOF token at the end of the token list
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
}

public class SampleLexer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter: ");
        String input = scanner.nextLine();
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        scanner.close();
    }
}