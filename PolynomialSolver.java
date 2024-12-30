import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

public class PolynomialSolver {
    public static void main(String[] args) {
        try {
            // Read JSON input
            JSONTokener tokener = new JSONTokener(new FileReader("testcase.json"));
            JSONObject jsonObject = new JSONObject(tokener);

            // Extract n and k values
            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            // Decode roots
            Map<Integer, Integer> roots = new TreeMap<>();
            for (String key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key);
                    JSONObject root = jsonObject.getJSONObject(key);
                    int base = Integer.parseInt(root.getString("base"));
                    String value = root.getString("value");
                    int y = Integer.parseInt(value, base);
                    roots.put(x, y);
                }
            }

            // Ensure sufficient roots for interpolation
            if (roots.size() < k) {
                throw new IllegalArgumentException("Not enough roots to solve the polynomial.");
            }

            // Perform Lagrange interpolation to find the constant term
            double constantTerm = lagrangeInterpolation(roots, k);

            // Output the result
            System.out.println("The constant term (c) is: " + (int) constantTerm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs Lagrange interpolation to calculate the constant term of the polynomial.
     *
     * @param roots Map of x and y values (decoded).
     * @param k     Minimum number of roots required.
     * @return The constant term of the polynomial.
     */
    private static double lagrangeInterpolation(Map<Integer, Integer> roots, int k) {
        double constant = 0.0;

        // Use the first k roots
        int[] xValues = new int[k];
        int[] yValues = new int[k];
        int index = 0;

        for (Map.Entry<Integer, Integer> entry : roots.entrySet()) {
            if (index < k) {
                xValues[index] = entry.getKey();
                yValues[index] = entry.getValue();
                index++;
            } else {
                break;
            }
        }

        // Calculate the polynomial constant term using Lagrange's formula
        for (int i = 0; i < k; i++) {
            double term = yValues[i];
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term *= (0.0 - xValues[j]) / (xValues[i] - xValues[j]);
                }
            }
            constant += term;
        }

        return constant;
    }
}
