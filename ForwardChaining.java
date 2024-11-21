package me.discordbot;

import java.util.*;

public class ForwardChaining {
    /** Using Forward Chaining algorithm, this method returns "YES",
     * followed by the list of propositional symbols entailed from KB that has been
     * found during the execution of the FC algorithm.
     * @param facts : A set of string that contains all the fact obtained from the input.
     * @param rules : A list of Rules obtained from the input file.
     * @param query : The query needed for evaluation of entailment.
     * @return a string that indicates if query is entailed by KB with YES and NO*/
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
