/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archproject;
import java.util.ArrayList;
import java.util.List;

public class TruthTable {
    private int numOfVars;
    private int numOfRows;
    private int[] outputs;
    private int[][] table;

    public TruthTable(int numOfVars, int[] outputs){
        this.numOfVars = numOfVars;
        this.outputs = outputs;
        this.numOfRows = (int) Math.pow(2, numOfVars);
        this.table = new int[numOfRows][numOfVars];
        generateTable();
    }

    public void generateTable(){

        for(int i=0; i<numOfRows; i++){

            for(int j=0; j<numOfVars; j++){

                int d = (int) Math.pow(2, numOfVars - 1 - j);
                table[i][j] = (i/d) % 2;
            }

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