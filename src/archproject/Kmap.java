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
        private int[] outputs;
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

                outputs = truthTable.getOutputs();
                int expectedoutputs = (int) Math.pow(2, numVars);

                if (outputs == null || outputs.length != expectedoutputs) {
                    errorMessage = "The output values do not match the number of variables";
                    return;
                }

                for (int i = 0; i < outputs.length; i++) {
                    if (outputs[i] != 0 && outputs[i] != 1) {
                        errorMessage = "each output must be 0 or 1";
                        return;
                    }
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

        System.out.println("SOP: " + simplify());
    }

    private String getVariableNames(int start, int count) {
        String names = "";

        for (int i = 0; i < count; i++) {
            names += (char) ('A' + start + i);
        }

        return names;
    }

    public String simplify() {
        try {
            if (!errorMessage.equals("")) {
                return errorMessage;
            }

            boolean Same = true;

            for (int i = 1; i < outputs.length; i++) {
                if (outputs[i] != outputs[0]) {
                    Same = false;
                    break;
                }
            }

            if (Same) {
                if (outputs[0] == 1) {
                    return "1";
                } else {
                    return "0";
                }
            }

            boolean[] covered = new boolean[outputs.length];
            String result = "";

            while (true) {
                boolean OnesCovered = true;

                for (int i = 0; i < outputs.length; i++) {
                    if (outputs[i] == 1 && !covered[i]) {
                        OnesCovered = false;
                        break;
                    }
                }

                if (OnesCovered) {
                    break;
                }

                int[] bestGroup = findBestGroup(covered);

                if (bestGroup == null) {
                    return "Could not find a group.";
                }

                if (!result.equals("")) {
                    result += " + ";
                }

                result += makeTerm(bestGroup);

                for (int minterm : bestGroup) {
                    covered[minterm] = true;
                }
            }

            return result;

        } catch (Exception e) {
            return "Could not simplify: " + e.getMessage();
        }
    }

    private int[] findBestGroup(boolean[] covered) {
        int bestCells = 0;
        int bestArea = 0;
        int[] bestGroup = null;

        for (int rowSize = 1; rowSize <= map.length; rowSize *= 2) {
            for (int colSize = 1; colSize <= map[0].length; colSize *= 2) {
                int area = rowSize * colSize;

                for (int startRow = 0; startRow < map.length; startRow++) {
                    for (int startCol = 0; startCol < map[0].length; startCol++) {
                        int[] group = new int[area];
                        int position = 0;
                        int newCells = 0;
                        boolean valid = true;

                        for (int r = 0; r < rowSize; r++) {
                            for (int c = 0; c < colSize; c++) {
                                int row = (startRow + r) % map.length;
                                int col = (startCol + c) % map[0].length;

                                String bits = getGrayCode(row, rowBits)
                                        + getGrayCode(col, colBits);

                                int minterm = binaryToDecimal(bits);
                                group[position] = minterm;
                                position++;

                                if (outputs[minterm] != 1) {
                                    valid = false;
                                } else if (!covered[minterm]) {
                                    newCells++;
                                }
                            }
                        }

                        if (valid && newCells > 0
                                && (newCells > bestCells
                                || (newCells == bestCells
                                && area > bestArea))) {
                            bestGroup = group;
                            bestCells = newCells;
                            bestArea = area;
                        }
                    }
                }
            }
        }

        return bestGroup;
    }

    private String makeTerm(int[] group) {
        String term = "";

        for (int variable = 0; variable < numVars; variable++) {
            int firstValue = getVariableValue(group[0], variable);
            boolean sameValue = true;

            for (int i = 1; i < group.length; i++) {
                if (getVariableValue(group[i], variable) != firstValue) {
                    sameValue = false;
                    break;
                }
            }

            if (sameValue) {
                char variableName = (char) ('A' + variable);

                if (firstValue == 1) {
                    term += variableName;
                } else {
                    term += variableName + "'";
                }
            }
        }

        if (term.equals("")) {
            return "1";
        }

        return term;
    }

    private int getVariableValue(int minterm, int variable) {
        String bits = Integer.toBinaryString(minterm);

        while (bits.length() < numVars) {
            bits = "0" + bits;
        }

        return bits.charAt(variable) - '0';
    }

    }

