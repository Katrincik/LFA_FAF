package src.Lab3;

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

    @Override
    public String toString() {
        return type + "('" + value + "')";
    }
}

class Lexer {
    private final String input;
    private int pos = 0;
    private char currentChar;

    Lexer(String input) {
        this.input = input + '\0'; // Sentinel character to indicate end of input
        currentChar = input.charAt(pos);
    }

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

    List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (currentChar != '\0') {
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

            // Handle unexpected characters
            throw new RuntimeException("Unexpected character: " + currentChar);
        }

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
        tokens.forEach(System.out::println);
        scanner.close();
    }
}