package me.discordbot;

import java.util.List;

public class Rule {
    List<String> premises; // List of premises (antecedents)
    String conclusion;     // The conclusion (consequent)

    public Rule(List<String> premises, String conclusion) {
        this.premises = premises;
        this.conclusion = conclusion;
    }

    @Override
    public String toString() {
        return "\nPremises: " + premises.toString() + "\nConclusion: " + conclusion;
    }
}
