package archproject;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Stack;

public class BooleanFunction {

    private String boolFunc;

    public BooleanFunction(String boolFunc){
        if(boolFunc == null || boolFunc.trim().isEmpty()){
            throw new IllegalArgumentException("Error: boolean function cannot be empty");
        }
        this.boolFunc = boolFunc;
    }

    public Queue<String> Tokenization (String boolFunc){
        Queue<String> tokens = new LinkedList<>();

        String clearExpr = boolFunc.replaceAll("\\s", ""); // \\s means white space or tab or new line 

        for(int i=0; i < clearExpr.length(); i++){
            char c =clearExpr.charAt(i);

            if(Character.isLetter(c) || c =='+' || c =='.' || c =='!' || c =='(' || c==')'){
                if(Character.isLetter(c) && i>0 && Character.isLetter(clearExpr.charAt(i-1))){
                    throw new IllegalArgumentException("Error: missing operator between variables at index"+ i);
                }
                tokens.add(String.valueOf(c));
            } else{
                throw new IllegalArgumentException("Invalid character in expression '"+c+"'");
            }
        }
        return tokens;
    }

    private int Precedence(String operator){ // As the priority increases the number increases
        switch (operator){
            case "!" : return 3;
            case "." : return 2;
            case "+" : return 1;
            default  : return 0;
        }
    }

    public Queue<String> Postfix (Queue<String> tokens){
        Queue<String> postfix = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();

        while (!tokens.isEmpty()){
            String token = tokens.poll();

            if(Character.isLetter(token.charAt(0))){
                postfix.add(token);
            }
            else if (token.equals("(")) {
                operatorStack.push(token);
            }
            else if (token.equals(")")){
                while(!operatorStack.isEmpty() && !operatorStack.peek().equals("(")){
                    postfix.add(operatorStack.pop());
                }
                if(operatorStack.isEmpty()){
                    throw new IllegalArgumentException("Error: mismatched parentheses");
                }
                operatorStack.pop();
            }
            else if (token.equals(".") || token.equals("+") || token.equals("!")) {
                while(!operatorStack.isEmpty() && Precedence(operatorStack.peek()) >= Precedence(token)){
                    postfix.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }
        while(!operatorStack.isEmpty()){
            String top = operatorStack.pop();

            if (top.equals("(")){
                throw new IllegalArgumentException("Error: mismatched parentheses");
            }
            postfix.add(top);
        }
        return postfix;
    }

    private int performOperation(String operator, int val1, int val2){ 
        switch (operator) {
            case ".":
                if (val1 == 1 && val2 == 1) {
                    return 1;
                } else {
                    return 0;
                }
            case "+":
                if (val1 == 1 || val2 == 1) {
                    return 1;
                } else {
                    return 0;
                }
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private int performNotOperation(int val){ // about not operator 
        if(val == 1)
            return 0;
        else
            return 1;
    }

    public int evaluation (Queue<String> postfixQueue, java.util.Map<String, Integer> variableValues) {
            Queue<String> queueCopy = new LinkedList<>(postfixQueue);
            Stack<Integer> evalStack = new Stack<>();

            while (!queueCopy.isEmpty()) {
                String token = queueCopy.poll();

                if (Character.isLetter(token.charAt(0))) {
                    if (!variableValues.containsKey(token)) { //if user defined a number of values that does not match the number of values in the boolean function
                        throw new IllegalArgumentException("Error: Variable '" + token + "' was not found in the truth table variables.");
                    }
                    evalStack.push(variableValues.get(token));
                }
                else if (token.equals("!")) {
                    if (evalStack.isEmpty()) {
                        throw new IllegalArgumentException("Evaluation Error: Invalid expression structure around '!'");
                    }
                    int val = evalStack.pop();
                    evalStack.push(performNotOperation(val));
                }
                else if (token.equals(".") || token.equals("+")) {
                    if (evalStack.size() < 2) {
                        throw new IllegalArgumentException("Evaluation Error: Missing operands for operator '" + token + "'");
                    }
                    int val2 = evalStack.pop();
                    int val1 = evalStack.pop();

                    int result = performOperation(token, val1, val2);
                    evalStack.push(result);
                }
            }
            if (evalStack.size() != 1) {
                throw new IllegalArgumentException("Evaluation Error: Invalid expression syntax.");
            }

            return evalStack.pop();
    }

}


