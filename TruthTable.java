package me.discordbot;

import java.util.*;

public class TruthTable {
    /** Using the TT_Entails algorithm, this method returns "YES",
     *  followed by the number of every true kb models where query is also true,
     *  or "NO" if KB does not entail query.
     * @param kb: A list of string clauses parsed.
     * @param query: The query to check for entailment
     * @return a string that indicates if query is entailed by KB with YES and NO*/
    public static String evaluateTT(List<String> kb, String query) {
        Set<String> symbols = extractPropositions(kb);
        List<Map<String, Boolean>> models = generateModels(symbols);

        int countModel = 0; // number of models where kb is true
        int count = 0; // number of models where kb is true and query is also true
        for (Map<String, Boolean> model : models) {
            if (isKBTrue(kb, model)) {
                countModel++;
               if (model.getOrDefault(query, false)) {
                    count++;
               }
            }
        }
        // countModel == count means in all models where KB is true, we also have query is true
        return countModel == count ? "YES: " + count : "NO";
    }

    // This method extracts the symbols from the clauses
    private static Set<String> extractPropositions(List<String> kb) {
        Set<String> propositions = new HashSet<>();
        for (String clause : kb) {
            clause = clause.replaceAll("[&||~=>;<=>]", " ");
            propositions.addAll(Arrays.asList(clause.trim().split("\\s+")));
        }
        return propositions;
    }

    // This method generates all the possible models using bitwise operations
    private static List<Map<String, Boolean>> generateModels(Set<String> symbols) {
        List<Map<String, Boolean>> models = new ArrayList<>();

        int n = symbols.size();
        String[] propArray = symbols.toArray(new String[0]);

        for (int i = 0; i < (1 << n); i++) { // while i < 2^n
            Map<String, Boolean> model = new HashMap<>();
            for (int j = 0; j < n; j++) {
                model.put(propArray[j], (i & (1 << j)) != 0); // for current model of i, assigns either true or false to j-th proposition
            }
            models.add(model);
        }
        return models;
    }

    // This method evaluates if KB is true
    private static boolean isKBTrue(List<String> kb, Map<String, Boolean> model) {
        for (String clause : kb) {
            if (Boolean.FALSE.equals(evaluateClause(clause, model))) {
                return false;
            }
        }
        return true;
    }

    private static Boolean evaluateClause(String clause, Map<String, Boolean> model) {
        // Split the clause by its logical connective "<=>" and evaluate each side of the clause
        if (clause.contains("<=>")) {
            String[] parts = clause.split("<=>");
            boolean leftValue = evaluateExpression(parts[0].trim(), model);
            boolean rightValue = evaluateExpression(parts[1].trim(), model);
            return leftValue == rightValue;
        }

        // Split the clause by its logical connective "=>" and evaluate the expression on each side
        if (clause.contains("=>")) {
            String[] parts = clause.split("=>");
            String premise = parts[0].trim();
            String conclusion = parts.length > 1 ? parts[1].trim() : null;

            Boolean premiseValue = evaluateExpression(premise, model);
            boolean conclusionValue = conclusion == null || evaluateExpression(conclusion, model);

            return !premiseValue || conclusionValue;
        }
        return evaluateExpression(clause, model);
    }

    private static Boolean evaluateExpression(String expression, Map<String, Boolean> model) {
        // Split the expression by its logical connective "&" and evaluate each literal
        if (expression.contains("&")) {
            String[] parts = expression.split("&");
            for (String part : parts) {
                Boolean value = evaluateLiteral(part.trim(), model);

                // If one of the part is false, return false immediately for the logical rule of "and"
                if (!value) {
                    return false;
                }
            }

            return true;
        } else if (expression.contains("||")) {
            String[] parts = expression.split("\\|\\|");
            for (String part : parts) {
                Boolean value = evaluateLiteral(part.trim(), model);

                if (value) {
                    return true; // OR: If any part is true, the expression is true
                }
            }
            return false;
        } else {
            return evaluateLiteral(expression.trim(), model);
        }
    }

    // This method evaluates a single literal (e.g., A, ~A)
    private static Boolean evaluateLiteral(String literal, Map<String, Boolean> model) {
        if (literal.startsWith("~")) {
            String prop = literal.substring(1); // Remove negation (~)
            return model.getOrDefault(prop, false);
        } else {
            return model.getOrDefault(literal, false);
        }
    }
}

