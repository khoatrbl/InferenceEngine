package me.discordbot;

import java.util.*;

public class ForwardChaining {
    // Forward Chaining Algorithm
    public static String evaluateFC(Set<String> facts, List<Rule> rules, String query) {
        Set<String> inferred = new HashSet<>(facts);
        boolean newFactInferred;

        do {
            newFactInferred = false;

            for (Rule rule : rules) {
                // Skip if the conclusion is already inferred or in facts
                if (facts.contains(rule.conclusion) || inferred.contains(rule.conclusion)) {
                    continue;
                }

                // Check if all premises of the rule are in the facts
                if (facts.containsAll(rule.premises)) {
                    // Infer the conclusion
                    facts.add(rule.conclusion);
                    inferred.add(rule.conclusion);
                    newFactInferred = true;

                }
            }
        } while (newFactInferred);

        // If no new facts can be inferred and the query is not inferred
        return facts.contains(query) ? "YES: " + inferred : "NO";
    }

}
