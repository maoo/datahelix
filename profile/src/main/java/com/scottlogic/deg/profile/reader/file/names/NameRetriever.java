package com.scottlogic.deg.profile.reader.file.names;

import com.scottlogic.deg.common.profile.constraints.atomic.NameConstraintTypes;
import com.scottlogic.deg.profile.reader.file.CSVFromPathToStringsLoader;
import com.scottlogic.deg.profile.reader.file.PathToStringsLoader;
import com.scottlogic.deg.profile.reader.file.inputstream.ClasspathMapper;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scottlogic.deg.common.profile.constraints.atomic.NameConstraintTypes.FIRST;
import static com.scottlogic.deg.common.profile.constraints.atomic.NameConstraintTypes.LAST;

public class NameRetriever implements Function<NameConstraintTypes, Stream<String>> {

    private static final Map<NameConstraintTypes, Set<String>> NAME_TYPE_MAPPINGS = setupNameMappings();

    private final PathToStringsLoader filepathToNames;

    public NameRetriever() {
        this(new ClasspathMapper());
    }

    public NameRetriever(final Function<String, InputStream> pathMapper) {
        filepathToNames = new CSVFromPathToStringsLoader(pathMapper);
    }

    private static Map<NameConstraintTypes, Set<String>> setupNameMappings() {
        Map<NameConstraintTypes, Set<String>> mappings = new EnumMap<>(NameConstraintTypes.class);
        mappings.put(LAST, NameClassPathFiles.getLastNames());
        mappings.put(FIRST, NameClassPathFiles.getFirstNames());
        return mappings;
    }

    @Override
    public Stream<String> apply(NameConstraintTypes configuration) {
        switch (configuration) {
            case FIRST:
            case LAST:
                return generateSingles(NAME_TYPE_MAPPINGS.get(configuration));
            case FULL:
                return generateCombinations(generateSingles(NAME_TYPE_MAPPINGS.get(FIRST)),
                    generateSingles(NAME_TYPE_MAPPINGS.get(LAST)));
            default:
                throw new UnsupportedOperationException("Name not implemented of type: " + configuration);
        }
    }

    private Stream<String> generateSingles(Set<String> sources) {
        return sources.stream()
            .flatMap(filepathToNames::retrieveNames);
    }

    private static Stream<String> generateCombinations(Stream<String> firstNames, Stream<String> lastNames) {
        Set<String> lastNamesStored = lastNames.collect(Collectors.toSet());
        return firstNames
            .flatMap(first -> lastNamesStored.stream()
                .map(last -> combineFirstAndLastName(first, last)));
    }

    private static String combineFirstAndLastName(final String first, final String last) {
        return first + " " + last;
    }

}
