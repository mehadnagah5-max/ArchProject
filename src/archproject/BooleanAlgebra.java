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
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class BooleanAlgebra {

    public static final int MIN_VARS = 2;
    public static final int MAX_VARS = 5;

    public static String simplify(int numVars, int[] minterms) {
        return simplify(numVars, minterms, true);
    }

    public static String simplify(int numVars, int[] minterms, boolean showSteps) {
        if (numVars < MIN_VARS || numVars > MAX_VARS) {
            throw new IllegalArgumentException("Number of variables must be between "
                    + MIN_VARS + " and " + MAX_VARS + ".");
        }
        int rowsCount = 1 << numVars;

        TreeSet<Integer> set = new TreeSet<>();
        for (int m : minterms) {
            if (m < 0 || m >= rowsCount) {
                throw new IllegalArgumentException("Minterm " + m + " is out of range.");
            }
            set.add(m);
        }

        if (set.isEmpty()) {
            if (showSteps) System.out.println("F has no minterms, so F = 0");
            return "0";
        }
        if (set.size() == rowsCount) {
            if (showSteps) System.out.println("F is 1 for every input, so F = 1");
            return "1";
        }

        List<Integer> mins = new ArrayList<>(set);
        List<String> current = new ArrayList<>();
        for (int m : mins) current.add(toBits(m, numVars));

        if (showSteps) {
            System.out.println("Step 0: Write F as a sum of minterms (canonical SOP)");
            System.out.println("   F(" + varList(numVars) + ") = Sum m" + mins);
            System.out.println("   F = " + join(current, numVars));
            System.out.println();
        }

        int round = 1;
        while (true) {
            Set<String> next = new LinkedHashSet<>();
            boolean[] used = new boolean[current.size()];
            List<String> lines = new ArrayList<>();

            for (int i = 0; i < current.size(); i++) {
                for (int j = i + 1; j < current.size(); j++) {
                    int diff = diffPosition(current.get(i), current.get(j));
                    if (diff < 0) continue;
                    used[i] = true;
                    used[j] = true;
                    String merged = current.get(i).substring(0, diff) + "-"
                            + current.get(i).substring(diff + 1);
                    if (next.add(merged)) {
                        lines.add(explain(current.get(i), current.get(j), diff, merged, numVars));
                    }
                }
            }

            if (lines.isEmpty()) break;

            List<String> carried = new ArrayList<>();
            for (int i = 0; i < current.size(); i++) {
                if (!used[i] && next.add(current.get(i))) carried.add(current.get(i));
            }

            if (showSteps) {
                System.out.println("Step " + round + ": Combine terms that differ in one variable"
                        + "   (X.Y + X.Y' = X)");
                for (String line : lines) System.out.println("   " + line);
                if (!carried.isEmpty()) {
                    System.out.println("   Unchanged terms: " + join(carried, numVars));
                }
                System.out.println("   (A term used in more than one combination is repeated first,"
                        + " using X = X + X)");
                System.out.println("   F = " + join(new ArrayList<>(next), numVars));
                System.out.println();
            }

            current = new ArrayList<>(next);
            round++;
        }

        List<String> primes = current;
        List<String> best = minimalCover(primes, mins, numVars);

        if (best.size() < primes.size() && showSteps) {
            List<String> dropped = new ArrayList<>(primes);
            dropped.removeAll(best);
            System.out.println("Step " + round + ": Remove redundant terms"
                    + "   (absorption / consensus: XY + X'Z + YZ = XY + X'Z)");
            System.out.println("   Removed: " + join(dropped, numVars)
                    + "  (already covered by the other terms)");
            System.out.println();
        }

        String result = join(best, numVars);
        if (showSteps) {
            System.out.println("Final simplified expression:");
            System.out.println("   F = " + result);
        }
        return result;
    }

    private static String toBits(int m, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = n - 1; i >= 0; i--) sb.append(((m >> i) & 1) == 1 ? '1' : '0');
        return sb.toString();
    }

    private static int diffPosition(String a, String b) {
        int pos = -1;
        for (int i = 0; i < a.length(); i++) {
            char x = a.charAt(i), y = b.charAt(i);
            if (x == y) continue;
            if (x == '-' || y == '-') return -1;
            if (pos != -1) return -1;
            pos = i;
        }
        return pos;
    }

    private static String varList(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(",");
            sb.append((char) ('A' + i));
        }
        return sb.toString();
    }

    private static String termToString(String p) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.length(); i++) {
            char v = (char) ('A' + i);
            if (p.charAt(i) == '1') sb.append(v);
            else if (p.charAt(i) == '0') sb.append(v).append('\'');
        }
        return sb.length() == 0 ? "1" : sb.toString();
    }

    private static String join(List<String> patterns, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < patterns.size(); i++) {
            if (i > 0) sb.append(" + ");
            sb.append(termToString(patterns.get(i)));
        }
        return sb.toString();
    }

    private static String explain(String a, String b, int diff, String merged, int n) {
        String common = termToString(merged);
        char v = (char) ('A' + diff);
        String pair = "(" + v + "' + " + v + ")";
        String left = termToString(a) + " + " + termToString(b);
        if (common.equals("1")) {
            return left + " = " + pair + " = 1";
        }
        return left + " = " + common + pair + " = " + common;
    }

    private static boolean covers(String pattern, int minterm, int n) {
        String bits = toBits(minterm, n);
        for (int i = 0; i < n; i++) {
            if (pattern.charAt(i) != '-' && pattern.charAt(i) != bits.charAt(i)) return false;
        }
        return true;
    }

    private static int literals(String p) {
        int c = 0;
        for (int i = 0; i < p.length(); i++) if (p.charAt(i) != '-') c++;
        return c;
    }

    private static List<String> bestSet;
    private static int bestTerms, bestLits;

    private static List<String> minimalCover(List<String> primes, List<Integer> mins, int n) {
        int p = primes.size();
        BitSet[] cov = new BitSet[p];
        for (int i = 0; i < p; i++) {
            cov[i] = new BitSet(mins.size());
            for (int j = 0; j < mins.size(); j++) {
                if (covers(primes.get(i), mins.get(j), n)) cov[i].set(j);
            }
        }
        bestSet = null;
        bestTerms = Integer.MAX_VALUE;
        bestLits = Integer.MAX_VALUE;
        BitSet uncovered = new BitSet(mins.size());
        uncovered.set(0, mins.size());
        search(primes, cov, uncovered, new ArrayList<String>(), 0);
        return bestSet;
    }

    private static void search(List<String> primes, BitSet[] cov, BitSet uncovered,
                               List<String> chosen, int lits) {
        if (chosen.size() > bestTerms) return;
        if (uncovered.isEmpty()) {
            if (chosen.size() < bestTerms || (chosen.size() == bestTerms && lits < bestLits)) {
                bestTerms = chosen.size();
                bestLits = lits;
                bestSet = new ArrayList<>(chosen);
            }
            return;
        }
        int pick = -1, fewest = Integer.MAX_VALUE;
        for (int m = uncovered.nextSetBit(0); m >= 0; m = uncovered.nextSetBit(m + 1)) {
            int count = 0;
            for (BitSet c : cov) if (c.get(m)) count++;
            if (count < fewest) { fewest = count; pick = m; }
        }
        for (int i = 0; i < primes.size(); i++) {
            if (!cov[i].get(pick) || chosen.contains(primes.get(i))) continue;
            BitSet rest = (BitSet) uncovered.clone();
            rest.andNot(cov[i]);
            chosen.add(primes.get(i));
            search(primes, cov, rest, chosen, lits + literals(primes.get(i)));
            chosen.remove(chosen.size() - 1);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Number of variables: ");
        int n = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Minterms (comma separated, empty for none): ");
        String line = sc.nextLine().trim();
        int[] mins;
        if (line.isEmpty()) {
            mins = new int[0];
        } else {
            String[] parts = line.split("[,\\s]+");
            mins = new int[parts.length];
            for (int i = 0; i < parts.length; i++) mins[i] = Integer.parseInt(parts[i]);
        }
        try {
            simplify(n, mins);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}