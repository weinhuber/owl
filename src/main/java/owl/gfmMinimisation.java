package owl;

import owl.automaton.Automaton;
import owl.automaton.acceptance.BuchiAcceptance;
import owl.automaton.acceptance.EmersonLeiAcceptance;
import owl.automaton.acceptance.ParityAcceptance;
import owl.automaton.acceptance.RabinAcceptance;
import owl.automaton.acceptance.degeneralization.RabinDegeneralization;
import owl.automaton.acceptance.optimization.AcceptanceOptimizations;
import owl.automaton.hoa.HoaWriter;
import owl.ltl.LabelledFormula;
import owl.ltl.parser.LtlfParser;
import owl.translations.LtlTranslationRepository;
import owl.translations.ltl2ldba.AnnotatedLDBA;
import owl.translations.ltl2ldba.AsymmetricLDBAConstruction;
import owl.translations.rabinizer.RabinizerBuilder;
import owl.translations.rabinizer.RabinizerConfiguration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static owl.translations.LtlTranslationRepository.applyPreAndPostProcessing;

public class gfmMinimisation {
    public static void main(String[] args) {

//        LabelledFormula inputFormula = LtlfParser.parse("GF(a & X(X(X(b))))");
//        LabelledFormula inputFormula = LtlfParser.parse("GF a & GF b & GF c");
        if (args.length < 2) {
            System.out.println("Usage: <type> <ltl formula>");
            return;
        }

        String translation = args[0];

        // merge remaining args to obtain the formula
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String mergedArgs = sb.toString().trim();

        LabelledFormula inputFormula = LtlfParser.parse(mergedArgs);
        System.out.printf("Input formula: %s\n", inputFormula);


//        var ldba = AsymmetricLDBAConstruction.of(BuchiAcceptance.class).apply(inputFormula).copyAsMutable();

        Set<LtlTranslationRepository.Option> translationOptions = new HashSet<>();

        translationOptions.add(LtlTranslationRepository.Option.SIMPLIFY_AUTOMATON);
        translationOptions.add(LtlTranslationRepository.Option.SIMPLIFY_FORMULA);
        translationOptions.add(LtlTranslationRepository.Option.USE_PORTFOLIO_FOR_SYNTACTIC_LTL_FRAGMENTS);

        Automaton<?, ?> automaton;

        switch (translation.toLowerCase()) {
            case "dela" -> automaton =
                    LtlTranslationRepository.LtlToDelaTranslation.DEFAULT
                            .translation(EmersonLeiAcceptance.class, translationOptions)
                            .apply(inputFormula);

            case "dpa" -> automaton =
                    LtlTranslationRepository.LtlToDpaTranslation.DEFAULT
                            .translation(ParityAcceptance.class, translationOptions)
                            .apply(inputFormula);

            case "dra" -> {
                var dgra = RabinizerBuilder.build(inputFormula, RabinizerConfiguration.of(true, true, true));
                automaton = RabinDegeneralization.degeneralize(AcceptanceOptimizations.transform(dgra));
            }

            case "nba" -> automaton =
                    LtlTranslationRepository.LtlToNbaTranslation.DEFAULT
                            .translation(BuchiAcceptance.class, translationOptions)
                            .apply(inputFormula);

            case "ldba" -> automaton =
                    applyPreAndPostProcessing(
                            AsymmetricLDBAConstruction.of(BuchiAcceptance.class).andThen(AnnotatedLDBA::copyAsMutable),
                            LtlTranslationRepository.BranchingMode.DETERMINISTIC,
                            translationOptions,
                            BuchiAcceptance.class)
                            .apply(inputFormula);

            default -> {
                System.out.printf("Unknown translation type '%s'%n", translation);
                return;
            }
        }

        var automataString = HoaWriter.toString(automaton);
        System.out.println(automataString);

//        // write string to file
//        try {
//            FileWriter writer = new FileWriter("OWL_Optimised_" + inputFormula + ".hoa");
//            writer.write(automataString);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
}
