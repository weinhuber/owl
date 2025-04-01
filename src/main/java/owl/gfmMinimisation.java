package owl;

import owl.automaton.acceptance.BuchiAcceptance;
import owl.automaton.hoa.HoaWriter;
import owl.ltl.LabelledFormula;
import owl.ltl.parser.LtlfParser;
import owl.translations.LtlTranslationRepository;
import owl.translations.ltl2ldba.AnnotatedLDBA;
import owl.translations.ltl2ldba.AsymmetricLDBAConstruction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static owl.translations.LtlTranslationRepository.applyPreAndPostProcessing;

public class gfmMinimisation {
    public static void main(String[] args) {

//        LabelledFormula inputFormula = LtlfParser.parse("GF(a & X(X(X(b))))");
//        LabelledFormula inputFormula = LtlfParser.parse("GF a & GF b & GF c");
        LabelledFormula inputFormula = null;


        // get input from command line argument
        if (args.length > 0) {
            inputFormula = LtlfParser.parse(args[0]);
            System.out.printf("Input formula: %s\n", inputFormula);
        } else {
            System.out.println("No input formula provided.");
            System.exit(1);
        }


//        var ldba = AsymmetricLDBAConstruction.of(BuchiAcceptance.class).apply(inputFormula).copyAsMutable();

        Set<LtlTranslationRepository.Option> translationOptions = new HashSet<>();

        translationOptions.add(LtlTranslationRepository.Option.SIMPLIFY_AUTOMATON);
        translationOptions.add(LtlTranslationRepository.Option.SIMPLIFY_FORMULA);
        translationOptions.add(LtlTranslationRepository.Option.USE_PORTFOLIO_FOR_SYNTACTIC_LTL_FRAGMENTS);

        var ldbaPostprocessed = applyPreAndPostProcessing(AsymmetricLDBAConstruction.of(BuchiAcceptance.class).andThen(AnnotatedLDBA::copyAsMutable),
                LtlTranslationRepository.BranchingMode.DETERMINISTIC,
                translationOptions,
                BuchiAcceptance.class)
                .apply(inputFormula);


        var automataString = HoaWriter.toString(ldbaPostprocessed);
        System.out.println(automataString);

        // write string to file
        try {
            FileWriter writer = new FileWriter("OWL_Optimised_" + inputFormula + ".hoa");
            writer.write(automataString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
