package archproject;
import java.util.ArrayList;
import java.util.List;

public class Decoder {
  private static final int DECODER_X = 10;
   private static final int BOX_WIDTH = 9;
   private static final int OR_X = DECODER_X + BOX_WIDTH + 8;
   private static final int OUTPUT_X = OR_X + BOX_WIDTH;
 
   private int numberOfVariables;
   private int numberOfOutput;
   private List<Integer> minterms;
   private int[] outputs;
 
   // Same idea as the Canvas class in Circuit
   private static class Canvas {
       private List<StringBuilder> rows = new ArrayList<>();
 
       void put(int x, int y, String value) {
           while (rows.size() <= y) {
               rows.add(new StringBuilder());
           }
           StringBuilder line = rows.get(y);
           while (line.length() < x + value.length()) {
               line.append(' ');
           }
           line.replace(x, x + value.length(), value);
       }
 
       void hline(int start, int end, int y) {
           for (int x = start; x <= end; x++) {
               put(x, y, "-");
           }
       }
 
       void box(int x, int top, int bottom, String label) {
           String border = "+" + repeat('-', BOX_WIDTH - 2) + "+";
           String side = "|" + repeat(' ', BOX_WIDTH - 2) + "|";
 
           put(x, top, border);
           put(x, bottom, border);
 
           for (int y = top + 1; y < bottom; y++) {
               put(x, y, side);
           }
 
           int middle = (top + bottom) / 2;
           put(x + (BOX_WIDTH - label.length()) / 2, middle, label);
       }
 
       String build() {
           StringBuilder result = new StringBuilder();
           for (StringBuilder line : rows) {
               int end = line.length();
               while (end > 0 && line.charAt(end - 1) == ' ') {
                   end--;
               }
               result.append(line.substring(0, end)).append('\n');
           }
           return result.toString();
       }
   }
 
   private static String repeat(char c, int count) {
       StringBuilder result = new StringBuilder();
       for (int i = 0; i < count; i++) {
           result.append(c);
       }
       return result.toString();
   }
 
   public Decoder(TruthTable truthTable){
       if (truthTable == null){
           throw new IllegalArgumentException("TruthTable can't be null ");
       }
       numberOfVariables = truthTable.getNumOfVars();
       outputs = truthTable.getOutputs();
       numberOfOutput = outputs.length;
       minterms = truthTable.getMinterms();
   }
 
   public int[] decode(int inputIndex){
       if (inputIndex < 0 || inputIndex >= numberOfOutput){
           throw new IllegalArgumentException("InputIndex must be between 0 and numberOfOutputs");
       }
       int[] decoderOutputs = new int[numberOfOutput];
       decoderOutputs[inputIndex] = 1;
       return decoderOutputs;
   }
 
   public void simulate(){
       System.out.println("Decoder Simulation : ");
       for (int i = 0; i < numberOfOutput; i++) {
           int[] decoderOutputs = decode(i);
           int f = 0;
           for (int m : minterms) {
               f = f | decoderOutputs[m];
           }
        System.out.println("Input " + i + " -> F = " + f+ " (Truth Table = " + outputs[i] + ")");
       }
   }
 
   public String render(){
       if (minterms.isEmpty()){
           return "F = 0 (constant)\n";
       }
       if (minterms.size() == numberOfOutput){
           return "F = 1 (constant)\n";
       }
 
       Canvas canvas = new Canvas();
 
       int top = 1;
       int bottom = top + numberOfOutput + 1;
 
       canvas.box(DECODER_X, top, bottom, "DECODER");
 
       
       for (int i = 0; i < numberOfVariables; i++) {
           int y = top + 1 + 2 * i;
           canvas.put(0, y, String.valueOf((char) ('A' + i)));
           canvas.hline(2, DECODER_X - 1, y);
       }
 
       
       List<Integer> usedRows = new ArrayList<>(); // outputs Y0 ... Y(N-1)
       for (int k = 0; k < numberOfOutput; k++) {
           int y = top + 1 + k;
 
           if (minterms.contains(k)) {
               String label = "-Y" + k + "-";
               canvas.put(DECODER_X + BOX_WIDTH, y, label);
               canvas.hline(DECODER_X + BOX_WIDTH + label.length(), OR_X - 1, y);
               usedRows.add(y);
           } else {
               canvas.put(DECODER_X + BOX_WIDTH, y, "-Y" + k);
           }
       }
 
       if (usedRows.size() > 1) {
           int orTop = usedRows.get(0) - 1;
           int orBottom = usedRows.get(usedRows.size() - 1) + 1;
 
           canvas.box(OR_X, orTop, orBottom, "OR");
           canvas.put(OUTPUT_X, (orTop + orBottom) / 2, "---> F");
       } else {
           int row = usedRows.get(0);
           canvas.hline(OR_X, OUTPUT_X - 1, row);
           canvas.put(OUTPUT_X, row, "---> F");
       }
 
       StringBuilder result = new StringBuilder();
       result.append(numberOfVariables).append("-to-")
             .append(numberOfOutput).append(" Decoder circuit\n\n");
       result.append(canvas.build());
       result.append("\nUsed output lines: ").append(minterms.size())
             .append(" of ").append(numberOfOutput).append("\n");
       return result.toString();
   }
 
   public void drawDecoder(){
       System.out.println(render());
   }
 
   public void printDecoderImplementation(){
       System.out.println("Decoder Implementation : ");
       System.out.println("Number of Inputs : " + numberOfVariables);
       System.out.println("Number of Outputs : " + numberOfOutput);
 
       if(minterms.isEmpty()){
           System.out.println("F=0");
       }
       else if(minterms.size()==numberOfOutput){
           System.out.println("F=1");
       }
       else {
           System.out.print("Boolean Function : F = ");
           for (int i = 0; i < minterms.size(); i++) {
               System.out.print("Y" + minterms.get(i));
               if (i < minterms.size() - 1) {
                   System.out.print(" + ");
               }
           }
           System.out.println();
       }
 
       System.out.println();
       drawDecoder();
       simulate();
   }
}