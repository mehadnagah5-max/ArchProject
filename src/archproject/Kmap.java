/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archproject;

/**
 *
 * @author malak
 */
public class Kmap {
        private int numVars;
        private int rowBits;
        private int colBits;
        private int[][] map;
        private String errorMessage;

        public Kmap(TruthTable truthTable) {
            errorMessage = "";

            try {
                if (truthTable == null) {
                    errorMessage = "Truth table is missing.";
                    return;
                }

                numVars = truthTable.getNumOfVars();

                if (numVars < 2 || numVars > 4) {
                    errorMessage = "K-map supports 2, 3, or 4 variables";
                    return;
                }

                int[] outputs = truthTable.getOutputs();
                int expectedoutputs = (int) Math.pow(2, numVars);

                if (outputs == null || outputs.length != expectedoutputs) {
                    errorMessage = "The output values do not match the number of variables";
                    return;
                }


                rowBits = numVars / 2;
                colBits = numVars - rowBits;

                int MapRows = 1 << rowBits;
                int MapColumns = 1 << colBits;

                map = new int[MapRows][MapColumns];

                for (int row = 0; row < MapRows; row++) {
                    String rowLabel = getGrayCode(row, rowBits);

                    for (int col = 0; col < MapColumns; col++) {
                        String colLabel = getGrayCode(col, colBits);

                        String inputCombo = rowLabel + colLabel;

                        map[row][col] = outputs[binaryToDecimal(inputCombo)];
                    }
                }

            } catch (Exception e) {
                errorMessage = "Could not generate the K-map: " + e.getMessage();
            }
        }

    private String getGrayCode(int number, int numberOfBits) {
        int grayNumber = number ^ (number >> 1);
        String result = Integer.toBinaryString(grayNumber);

        while (result.length() < numberOfBits) {
            result = "0" + result;
        }

        return result;
    }

        private int binaryToDecimal(String binary) {
            int result = 0;

            for (int i = 0; i < binary.length(); i++) {
                result = result * 2 + (binary.charAt(i) - '0');
            }

            return result;
        }

    public void printMap() {
        if (!errorMessage.equals("")) {
            System.out.println(errorMessage);
            return;
        }

        String rowVars = getVariableNames(0, rowBits);
        String columnVars = getVariableNames(rowBits, colBits);

        System.out.println("K-map");
        System.out.println("          " + columnVars);

        System.out.print("        ");
        for (int col = 0; col < map[0].length; col++) {
            System.out.print(getGrayCode(col, colBits) + "   ");
        }
        System.out.println();

        for (int row = 0; row < map.length; row++) {
            System.out.print(rowVars + "="
                    + getGrayCode(row, rowBits) + " | ");

            for (int col = 0; col < map[row].length; col++) {
                System.out.print(map[row][col] + "   ");
            }

            System.out.println();
        }
    }

    private String getVariableNames(int start, int count) {
        String names = "";

        for (int i = 0; i < count; i++) {
            names += (char) ('A' + start + i);
        }

        return names;
    }
    }
    

