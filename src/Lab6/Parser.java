package src.Lab6;

import java.util.List;


abstract class ASTNode {
    // Abstract class for all AST nodes
}

// Represents binary operations in the AST
class BinaryOperatorNode extends ASTNode {
    ASTNode left;
    ASTNode right;
    Token operator;

    BinaryOperatorNode(ASTNode left, Token operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

// Represents unary operations in the AST (negation of a number, variable with - or !)
class UnaryOperatorNode extends ASTNode {
    Token operator;
    ASTNode node;

    UnaryOperatorNode(Token operator, ASTNode node) {
        this.operator = operator;
        this.node = node;
    }
}

// Represents numbers in the AST
class NumberNode extends ASTNode {
    Token token;

    NumberNode(Token token) {
        this.token = token;
    }
}

// Represents variables in the AST
class VariableNode extends ASTNode {
    Token token;

    VariableNode(Token token) {
        this.token = token;
    }
}

// Represents assignment statements in the AST (variable = expression)
class AssignmentNode extends ASTNode {
    VariableNode variable;
    ASTNode expression;

    AssignmentNode(VariableNode variable, ASTNode expression) {
        this.variable = variable;
        this.expression = expression;
    }
}


class Parser {
    List<Token> tokens;
    int currentPosition = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Token currentToken() {
        // Check if the current position is within the bounds of the list
        if (currentPosition < tokens.size()) {
            // Return the token at the current position
            return tokens.get(currentPosition);
        }
        return new Token(TokenType.EOF, "");
    }

    void check(TokenType type) {
        // Check if the current token's type matches the specified type
        if (currentToken().type == type) {
            currentPosition++;
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken() + ", expected: " + type);
        }
    }

    // Method to parse an expression
    ASTNode expression() {
        // Start with a term and look for add/subtract operators
        ASTNode result = term();
        while (currentToken().type == TokenType.OPERATOR && (currentToken().value.equals("+") || currentToken().value.equals("-"))) {
            // Get the operator token
            Token op = currentToken();
            // Check the operator token
            check(TokenType.OPERATOR);
            // Parse the next term
            result = new BinaryOperatorNode(result, op, term());
        }
        return result;
    }

    // Method to parse a term
    ASTNode term() {
        // Start with a factor and look for multiply/divide operators
        ASTNode result = factor();
        while (currentToken().type == TokenType.OPERATOR && (currentToken().value.equals("*") || currentToken().value.equals("/"))) {
            // Get the operator token
            Token op = currentToken();
            // Check the operator token
            check(TokenType.OPERATOR);
            // Parse the next term
            result = new BinaryOperatorNode(result, op, factor());
        }
        return result;
    }

    // Method to parse a factor
    ASTNode factor() {
        // Get the current token
        Token token = currentToken();
        // Check the type of the token
        if (token.type == TokenType.INTEGER) {
            check(TokenType.INTEGER);
            return new NumberNode(token);
        } else if (token.type == TokenType.IDENTIFIER) {
            check(TokenType.IDENTIFIER);
            return new VariableNode(token);
        } else if (token.type == TokenType.OPERATOR && token.value.equals("(")) {
            // If it's an opening parenthesis, consume the token
            check(TokenType.OPERATOR); // Eat '('
            // Parse the expression within the parentheses
            ASTNode node = expression();
            // Check the closing parenthesis
            check(TokenType.OPERATOR); // Eat ')'
            // Return the expression within the parentheses
            return node;
        }
        throw new RuntimeException("Unexpected token: " + token);
    }

    // Method to parse an assignment expression
    ASTNode assignment() {
        // Create a VariableNode for the variable being assigned
        VariableNode variable = new VariableNode(currentToken());
        // Check the identifier token
        check(TokenType.IDENTIFIER);
        // Check the assignment operator
        check(TokenType.OPERATOR);
        // Parse the expression on the right-hand side of the assignment ( of "=")
        ASTNode expr = expression();
        // Return an AssignmentNode representing the assignment expression
        return new AssignmentNode(variable, expr);
    }

    // Method to parse the input and construct the AST
    ASTNode parse() {
        // Check if the input represents an assignment expression, if it is parse it, if not parse it as regular expression
        if (currentToken().type == TokenType.IDENTIFIER && tokens.get(currentPosition + 1).value.equals("=")) {
            return assignment();
        } else {
            return expression();
        }
    }
}

