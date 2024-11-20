package me.discordbot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BackwardChaining {
    private static Set<String> visitedPremises = new HashSet<>();

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
                    visited.remove(query);
                    return "YES: " + visitedPremises;
                }
            }
        }

        // If no rules support the query, return false
        visited.remove(query);
        return "NO";
    }


}
