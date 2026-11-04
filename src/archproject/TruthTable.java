package archproject;

import java.util.*;

public class TruthTable {
    private int numOfVars;
    private int numOfRows;
    private int[] outputs;
    private int[][] table;
    private String booleanExpression;

    public TruthTable(int numOfVars, String booleanExpression){ // 2 , A.B+C
        if (numOfVars <= 1) {
            throw new IllegalArgumentException("Error: Number of variables must be greater than 1");
        }
        this.numOfVars = numOfVars; // 2 
        this.booleanExpression = booleanExpression; // A.B+C
        this.numOfRows = (int) Math.pow(2, numOfVars); // 4 
        this.table = new int[numOfRows][numOfVars];
        this.outputs = new int [numOfRows];
        generateTable();
    }

    public void generateTable(){
        BooleanFunction bf = new BooleanFunction(booleanExpression); 
        Queue<String> tokens = bf.Tokenization(booleanExpression);// boolean Expression to Tokens
        Queue<String> postfixQueue = bf.Postfix(tokens); // Tokens to Postfix 

        for(int i=0; i<numOfRows; i++){ // < not <= because it starts from zero
            Map<String, Integer> variableValuesMap = new HashMap<>();

            for(int j=0; j<numOfVars; j++){ // < not <= because it starts from zero

                int d = (int) Math.pow(2, numOfVars - 1 - j);
                int bitValue = (i/d) % 2;
                table[i][j] = bitValue;

                char varName = (char) ('A' + j);
                variableValuesMap.put(String.valueOf(varName), bitValue);
            }
            this.outputs[i] = bf.evaluation(postfixQueue, variableValuesMap);
        }
    }

    public List<Integer> getMinterms(){
        List<Integer> minTerms = new ArrayList<>();

        for(int i=0; i<numOfRows; i++){
            if(outputs[i] == 1){ // SOP
                minTerms.add(i);
            }
        }
        return minTerms;
    }

    public void printTable(){
        for(int i=0; i<numOfVars; i++){
            System.out.print((char) ('A' + i) + "   |  ");
        }
        System.out.println("output");
        System.out.println("-----------------------------");

        for(int i=0; i<numOfRows; i++){
            for(int j=0; j<numOfVars; j++){
                System.out.print(table[i][j] + "      ");
            }
            System.out.println("|  " + outputs[i]);
        }
    }

    public int[][] getTable(){
        return table;
    }
    public int[] getOutputs(){
        return outputs;
    }
    public int getNumOfVars(){
        return numOfVars;
    }
}