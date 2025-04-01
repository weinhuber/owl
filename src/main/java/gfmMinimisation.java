import owl.automaton.AnnotatedStateOptimisation;
import owl.automaton.acceptance.BuchiAcceptance;
import owl.automaton.acceptance.optimization.AcceptanceOptimizations;
import owl.automaton.algorithm.simulations.Transition;
import owl.automaton.hoa.HoaWriter;
import owl.ltl.LabelledFormula;
import owl.ltl.parser.LtlfParser;
import owl.translations.LtlTranslationRepository;
import owl.translations.ltl2ldba.AnnotatedLDBA;
import owl.translations.ltl2ldba.AsymmetricLDBAConstruction;

import java.util.HashSet;
import java.util.Set;

import static owl.translations.LtlTranslationRepository.applyPreAndPostProcessing;

public class gfmMinimisation {
    public static void main(String[] args) {

//        LabelledFormula inputFormula = LtlfParser.parse("GF(a & X(X(X(b))))");
        LabelledFormula inputFormula = LtlfParser.parse("GF a & GF b & GF c");

        var ldba = AsymmetricLDBAConstruction.of(BuchiAcceptance.class).apply(inputFormula).copyAsMutable();

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



    }
}
