/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package archproject;
import java.util.*;

public class ArchProject {
    public static void main(String[] args) {
 
      Scanner sc = new Scanner(System.in);
 
       //  read the number of variables
       int n = 0;
       while (true) {
           System.out.print("Enter the number of input variables (2 to 5): ");
           String line = sc.nextLine().trim();
 
           try {
               n = Integer.parseInt(line);
               if (n >= 2 && n <= 5) {
                   break;
               }
               System.out.println("Error: the number must be between 2 and 5");
           } catch (NumberFormatException e) { // if the input isn't integer
               System.out.println("Error: please enter a whole number");
           }
       }
 
       // read the Boolean function and build the truth table
       char last = (char) ('A' + n - 1);
       TruthTable table = null;
       while (table == null) {
           System.out.println("Variables: A to " + last + "   Operators: + (OR)  . (AND)  ! (NOT)  ( )");
           System.out.print("Enter the Boolean function: ");
           String expression = sc.nextLine().trim().toUpperCase();
 
           try {
               table = new TruthTable(n, expression);
           } catch (RuntimeException e) {
               System.out.println(e.getMessage());
           }
       }
 
       System.out.println("\nTruth Table:");
       table.printTable();
 
       // minterms as int[] (BooleanAlgebra needs an array)
       List<Integer> minList = table.getMinterms();
       int[] minterms = new int[minList.size()];
       for (int i = 0; i < minList.size(); i++) {
           minterms[i] = minList.get(i);
       }
 
       //  the menu
       int choice = 0;
       while (choice != 5) {
           System.out.println("\n       Menu        ");
           System.out.println("1. Simplify using K-Map");
           System.out.println("2. Simplify using Boolean Algebra");
           System.out.println("3. Draw the logic circuit");
           System.out.println("4. Implement using a Decoder");
           System.out.println("5. Exit");
           System.out.print("Choose an option 1 to 5: ");
 
           String input = sc.nextLine().trim();
           try {
               choice = Integer.parseInt(input);
           } catch (NumberFormatException e) {
               choice = -1;
           }
 
           if (choice < 1 || choice > 5) {
               System.out.println("Invalid choice. Please enter a number from 1 to 5.");
               choice = -1;
               continue;
           }
 
           System.out.println();
 
           switch (choice) {
               case 1:
                   try {
                       Kmap kmap = new Kmap(table);
                       kmap.printMap();
                   } catch (RuntimeException e) {
                       System.out.println("Error: " + e.getMessage());
                   }
                   break;
 
               case 2:
                   try {
                       BooleanAlgebra.simplify(n, minterms, true);
                   } catch (RuntimeException e) {
                       System.out.println("Error: " + e.getMessage());
                   }
                   break;
 
               case 3:
                   try {
                       String simplified = BooleanAlgebra.simplify(n, minterms, false);
                       Circuit.draw(simplified);
                   } catch (RuntimeException e) {
                       System.out.println("Error: " + e.getMessage());
                   }
                   break;
 
               case 4:
                   try {
                       Decoder decoder = new Decoder(table);
                       decoder.printDecoderImplementation();
                   } catch (RuntimeException e) {
                       System.out.println("Error: " + e.getMessage());
                   }
                   break;
 
               case 5:
                   break;
           }
       }
   }
}
           
       

    


