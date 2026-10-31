/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package archproject;

<<<<<<< HEAD
import java.util.*;

public class ArchProject {

    public static void main(String[] args) {
       Scanner sc = new Scanner(System.in);
       int numberOfVariables = 0 ;
       while(true){
       try{
        System.out.println("enter number of variables : ");
         numberOfVariables = sc.nextInt(); // 2
        if(numberOfVariables >=1 && numberOfVariables <=5){
            break;
        }
        else{
            System.out.println("enter a psoitive integer between 1 and 5  ");
        }
       }
       catch(InputMismatchException e){
           System.out.println("enter integer only ");
           sc.nextLine();
       }
    }
       int key = 1 ;
       while(key !=5){
        System.out.println("enter a menu option :");
        System.out.println("1-Simplify using K-Map ");
        System.out.println("2-Simplify using Boolean Algebra ");
        System.out.println("3-Draw the logic circuit");
        System.out.println("4-Implement using a Decoder");
        System.out.println("5-Exit");
       try{
        key = sc.nextInt(); // me
        switch(key){
            case 1 :
            System.out.println("k map ");   
            break;
            case 2 :
            System.out.println("Boolean Algebra");  
            break;
            case 3 :
            System.out.println("logic circuit ");    
            break;
            case 4 :
            System.out.println("Decoder");    
            break;
            case 5 :
                System.out.println("Exiting Program ");
                break;
            default:
                System.out.println("number 1 to 5 only ");
        }
       }catch(InputMismatchException e ){
                System.out.println("enter integer only for menu options ");
                sc.nextLine();
                }
       }
=======
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author mehad
 */
public class ArchProject {

    public static void main(String[] args) {
        
>>>>>>> master
    }
}

       
           
       

    


