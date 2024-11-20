package me.discordbot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InferenceEngine {

    public static Map<String, String> parseInput(String filename) throws IOException {
        Map<String, String> parsedData = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<String> lines = new ArrayList<>();
        String line;
        String tell = null;
        String ask = null;

        while ((line = reader.readLine()) != null) {
           lines.add(line);
        }

        tell = lines.get(1);
        ask = lines.get(3);

        parsedData.put("KB", tell);
        parsedData.put("Query", ask);
        return parsedData;
    }

    public static Map<String, Object> parseInputForChainingAlgorithm(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<String> lines = new ArrayList<>();
        String line;
        String tell = null;
        String ask = null;

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        tell = lines.get(1);
        ask = lines.get(3);

        reader.close();

        // Parse facts and rules from the TELL section
        Set<String> facts = new HashSet<>();
        List<Rule> rules = new ArrayList<>();
        if (tell != null) {
            String[] clauses = tell.split(";");
            for (String clause : clauses) {
                clause = clause.trim();
                if (clause.contains("=>")) {
                    String[] parts = clause.split("=>");
                    List<String> premises = Arrays.asList(parts[0].trim().split("&"));

                    String conclusion = parts[1].trim();
                    rules.add(new Rule(premises, conclusion));
                } else if (!clause.isEmpty()) {
                    facts.add(clause);
                }
            }
        }

        // Return facts, rules, and query
        Map<String, Object> parsedData = new HashMap<>();
        parsedData.put("facts", facts);
        parsedData.put("rules", rules);
        parsedData.put("query", ask);
        return parsedData;
    }
}
