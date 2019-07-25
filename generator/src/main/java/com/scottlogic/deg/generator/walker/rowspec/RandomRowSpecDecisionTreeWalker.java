package com.scottlogic.deg.generator.walker.rowspec;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import com.scottlogic.deg.generator.generation.databags.RowSpecDataBagGenerator;
import com.scottlogic.deg.generator.walker.DecisionTreeWalker;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomRowSpecDecisionTreeWalker implements DecisionTreeWalker {
    private final RowSpecTreeSolver rowSpecTreeSolver;
    private final RowSpecDataBagGenerator rowSpecDataBagGenerator;
    private final PotentialRowSpecCount potentialRowSpecCount;

    @Inject
    public RandomRowSpecDecisionTreeWalker(RowSpecTreeSolver rowSpecTreeSolver,
                                           RowSpecDataBagGenerator rowSpecDataBagGenerator,
                                           PotentialRowSpecCount potentialRowSpecCount) {
        this.rowSpecTreeSolver = rowSpecTreeSolver;
        this.rowSpecDataBagGenerator = rowSpecDataBagGenerator;
        this.potentialRowSpecCount = potentialRowSpecCount;
    }

    @Override
    public Stream<DataBag> walk(DecisionTree tree) {
        if (tree.rootNode.getDecisions().isEmpty()){
            return generateWithoutRestarting(tree);
        }

        Stream<RowSpec> rowSpecStream =
            potentialRowSpecCount.lessThanMax(tree)
                ? reuseRowSpecs(tree)
                : getRowSpecAndRestart(tree);

        return rowSpecStream
            .map(this::createDataBag);
    }

    private Stream<RowSpec> reuseRowSpecs(DecisionTree tree) {
        List<RowSpec> collect = rowSpecTreeSolver.createRowSpecs(tree).collect(Collectors.toList());

        return Stream.generate(() -> selectRandom(collect));
    }

    private <T> T selectRandom(List<T> collect) {
        return collect.get(new Random().nextInt(collect.size()));
    }

    private Stream<DataBag> generateWithoutRestarting(DecisionTree tree) {
        RowSpec rowSpec = getFirstRowSpec(tree).get();
        return rowSpecDataBagGenerator.createDataBags(rowSpec);
    }

    private Stream<RowSpec> getRowSpecAndRestart(DecisionTree tree) {
        Optional<RowSpec> firstRowSpecOpt = getFirstRowSpec(tree);
        if (!firstRowSpecOpt.isPresent()) {
            return Stream.empty();
        }

        return Stream.generate(() -> getFirstRowSpec(tree))
            .map(Optional::get);
    }

    private Optional<RowSpec> getFirstRowSpec(DecisionTree tree) {
        return rowSpecTreeSolver.createRowSpecs(tree).findFirst();
    }

    private DataBag createDataBag(RowSpec rowSpec) {
        return rowSpecDataBagGenerator.createDataBags(rowSpec).findFirst().get();
    }
}
