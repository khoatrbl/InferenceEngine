package me.discordbot;

import java.util.List;


/** Rule is a model for the rules specified in the input files. A rule contains the premises
 * and the conclusion drawn from the premises.*/
public class Rule {
    List<String> premises; // List of premises
    String conclusion;     // The conclusion

    public Rule(List<String> premises, String conclusion) {
        this.premises = premises;
        this.conclusion = conclusion;
    }

    @Override
    public String toString() {
        return "\nPremises: " + premises.toString() + "\nConclusion: " + conclusion;
    }
}
