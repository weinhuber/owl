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

public class ltl2ldba {
    public static void main(String[] args) {
        LabelledFormula inputFormula = null;

        // get all args and merge them together in case of white-space
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String mergedArgs = sb.toString().trim();

        // get input from command line argument
        if (args.length > 0) {
            inputFormula = LtlfParser.parse(mergedArgs);
            System.out.printf("Input formula: %s\n", inputFormula);
        } else {
            System.out.println("No input formula provided.");
            System.exit(1);
        }

        Set<LtlTranslationRepository.Option> translationOptions = new HashSet<>();

        translationOptions.add(LtlTranslationRepository.Option.SIMPLIFY_AUTOMATON);
        translationOptions.add(LtlTranslationRepository.Option.SIMPLIFY_FORMULA);
        translationOptions.add(LtlTranslationRepository.Option.USE_PORTFOLIO_FOR_SYNTACTIC_LTL_FRAGMENTS);

        var ldbaPostprocessed = applyPreAndPostProcessing(AsymmetricLDBAConstruction.of(BuchiAcceptance.class).andThen(AnnotatedLDBA::copyAsMutable),
                LtlTranslationRepository.BranchingMode.DETERMINISTIC,
                translationOptions,
                BuchiAcceptance.class)
                .apply(inputFormula);

        // Output the automaton to the console
        var automataString = HoaWriter.toString(ldbaPostprocessed);
        System.out.println(automataString);
    }
}
