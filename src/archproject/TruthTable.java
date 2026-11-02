package archproject;

import java.util.*;

public class TruthTable {
    private int numOfVars;
    private int numOfRows;
    private int[] outputs;
    private int[][] table;
    private String booleanExpression;

    public TruthTable(int numOfVars, String booleanExpression){
        if (numOfVars <= 1) {
            throw new IllegalArgumentException("Error: Number of variables must be greater than 1");
        }
        this.numOfVars = numOfVars;
        this.booleanExpression = booleanExpression;
        this.numOfRows = (int) Math.pow(2, numOfVars);
        this.table = new int[numOfRows][numOfVars];
        this.outputs = new int [numOfRows];
        generateTable();
    }

    public void generateTable(){
        BooleanFunction bf = new BooleanFunction(booleanExpression);
        Queue<String> tokens = bf.Tokenization(booleanExpression);
        Queue<String> postfixQueue = bf.Postfix(tokens);

        for(int i=0; i<numOfRows; i++){
            Map<String, Integer> variableValuesMap = new HashMap<>();

            for(int j=0; j<numOfVars; j++){

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
            if(outputs[i] == 1){
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