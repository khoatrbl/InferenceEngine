package me.discordbot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BackwardChaining {

    private static Set<String> visitedPremises = new HashSet<>(); // Set of visited premises

    /** Using Backward Chaining algorithm, this method returns "YES",
     * followed by the list of propositional symbols entailed from KB that has been
     * found during the execution of the FC algorithm.
     * @param facts : A set of string that contains all the fact obtained from the input.
     * @param rules : A list of Rules obtained from the input file.
     * @param visited: A set of visited premises to identify symbols that are previously looked at.
     * @param query : The query needed for evaluation of entailment.
     * @return a string that indicates if query is entailed by KB with YES and NO*/
    public static String evaluateBC(Set<String> facts, List<Rule> rules, Set<String> visited, String query) {
        visitedPremises.add(query);

        if (facts.contains(query)) {
            return "YES: " + visitedPremises;
        }

        if (visited.contains(query)) {
            return "NO";
        }

        visited.add(query);

        // Check each rule to see if it supports the query
        for (Rule rule : rules) {
            if (rule.conclusion.equals(query)) {
                boolean allPremisesTrue = true;

                // Check each premise recursively
                for (String premise : rule.premises) {
                    String result = evaluateBC(facts, rules, visited, premise);
                    if (result.startsWith("NO")) {
                        allPremisesTrue = false;
                        break;
                    }
                }

                // If all premises are true, the query is true
                if (allPremisesTrue) {
                    visited.remove(query); // remove so when a premise is re-evaluated, it does not return false.
                    return "YES: " + visitedPremises;
                }
            }
        }

        // If no rules support the query, return false
        visited.remove(query);
        return "NO";
    }


}
