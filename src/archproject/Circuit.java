/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archproject;

/**
 *
 * @author mehad
 */
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
public class Circuit {

    private static final int AND_X = 12;
    private static final int OR_X = 30;
    private static final int BOX_W = 9;
    private static final int OUT_X = OR_X + BOX_W;

    private static class Literal {
        char variable;
        boolean negated;

        Literal(char variable, boolean negated) {
            this.variable = variable;
            this.negated = negated;
        }
    }

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

            String border = "+" + repeat('-', BOX_W - 2) + "+";
            String side = "|" + repeat(' ', BOX_W - 2) + "|";

            put(x, top, border);
            put(x, bottom, border);

            for (int y = top + 1; y < bottom; y++) {
                put(x, y, side);
            }

            int middle = (top + bottom) / 2;

            put(x + (BOX_W - label.length()) / 2,
                    middle, label);
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

    public static void draw(String expression) {
        System.out.println(render(expression));
    }

    public static String render(String expression) {

        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid expression");
        }

        String expr = expression.trim();

        if (expr.contains("=")) {
            expr = expr.substring(expr.indexOf('=') + 1).trim();
        }

        expr = expr.replaceAll("[\\s*.\\u00B7]", "");

        if (expr.equals("0") || expr.equals("1")) {
            return "F = " + expr + " (constant)\n";
        }

        List<List<Literal>> terms = parse(expr);

        if (terms.isEmpty()) {
            return "F = 0 (constant)\n";
        }

        Canvas canvas = new Canvas();

        List<Integer> outputRows = new ArrayList<>();

        int y = 1;

        for (List<Literal> term : terms) {

            int size = term.size();

            if (size == 1) {

                drawWire(canvas, term.get(0), y, OR_X - 1);

                outputRows.add(y);
                y += 2;

            } else {

                int top = y;
                int bottom = y + 2 * size;

                canvas.box(AND_X, top, bottom, "AND");

                for (int i = 0; i < size; i++) {

                    int inputRow = top + 1 + 2 * i;

                    drawWire(canvas, term.get(i),
                            inputRow, AND_X - 1);
                }

                int outputRow = top + size;

                canvas.hline(AND_X + BOX_W,
                        OR_X - 1, outputRow);

                outputRows.add(outputRow);

                y = bottom + 2;
            }
        }

        if (terms.size() > 1) {

            int top = outputRows.get(0) - 1;
            int bottom = outputRows.get(outputRows.size() - 1) + 1;

            canvas.box(OR_X, top, bottom, "OR");

            int middle = (top + bottom) / 2;

            canvas.put(OUT_X, middle, "---> F");

        } else {

            int row = outputRows.get(0);

            canvas.hline(OR_X, OUT_X - 1, row);

            canvas.put(OUT_X, row, "---> F");
        }

        Set<Character> variables = new LinkedHashSet<>();
        Set<Character> negatedVariables = new LinkedHashSet<>();

        int andCount = 0;

        for (List<Literal> term : terms) {

            if (term.size() > 1) {
                andCount++;
            }

            for (Literal literal : term) {

                variables.add(literal.variable);

                if (literal.negated) {
                    negatedVariables.add(literal.variable);
                }
            }
        }

        StringBuilder result = new StringBuilder();

        result.append("Logic circuit for: F = ")
                .append(expr).append("\n");

        result.append("Inputs: ");

        for (char variable : variables) {
            result.append(variable).append(" ");
        }

        result.append("\n\n");

        result.append(canvas.build());

        result.append("\nGate count: ")
                .append(negatedVariables.size())
                .append(" NOT, ")
                .append(andCount)
                .append(" AND, ")
                .append(terms.size() > 1 ? 1 : 0)
                .append(" OR\n");

        return result.toString();
    }

    private static void drawWire(Canvas canvas,
                                 Literal literal,
                                 int y,
                                 int endX) {

        canvas.put(0, y, String.valueOf(literal.variable));

        if (literal.negated) {

            canvas.put(2, y, "--[NOT]---");

            canvas.hline(12, endX, y);

        } else {

            canvas.hline(2, endX, y);
        }
    }

    private static List<List<Literal>> parse(String expression) {

        List<List<Literal>> terms = new ArrayList<>();

        String[] parts = expression.split("\\+", -1);

        for (String part : parts) {

            if (part.isEmpty()) {
                throw new IllegalArgumentException("Empty term");
            }

            List<Literal> literals = new ArrayList<>();

            for (int i = 0; i < part.length(); i++) {

                char variable = Character.toUpperCase(part.charAt(i));

                if (variable < 'A' || variable > 'E') {
                    throw new IllegalArgumentException(
                            "Invalid variable: " + variable);
                }

                boolean negated = false;

                if (i + 1 < part.length()
                        && part.charAt(i + 1) == '\'') {

                    negated = true;
                    i++;
                }

                for (Literal old : literals) {

                    if (old.variable == variable) {

                        if (old.negated != negated) {
                            throw new IllegalArgumentException(
                                    "Contradictory term");
                        }

                        throw new IllegalArgumentException(
                                "Repeated variable in term");
                    }
                }

                literals.add(new Literal(variable, negated));
            }

            terms.add(literals);
        }

        return terms;
    }
}




