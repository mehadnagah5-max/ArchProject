/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package archproject;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author mehad
 */
public class ArchProject {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("enter number of variables: ");
        int numOfVars = input.nextInt();

        int numOfRows = (int) Math.pow(2, numOfVars);
        int[] outputs = new int[numOfRows];

        System.out.println("enter output values (0 or 1) for the "+numOfRows+" rows:");
        for (int i = 0; i < numOfRows; i++) {
            System.out.print("Row " + i + " output: ");
            outputs[i] = input.nextInt();
        }
        TruthTable t = new TruthTable(numOfVars, outputs);

        System.out.println("\n=== Truth Table ===");
        t.printTable();

        System.out.println("\n=== Minterms (Output = 1) ===");
        List<Integer> minterms = t.getMinterms();
        System.out.println(minterms);

        input.close();
    }
    
}
