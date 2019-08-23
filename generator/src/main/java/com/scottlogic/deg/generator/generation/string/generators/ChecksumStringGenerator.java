/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scottlogic.deg.generator.generation.string.generators;

import com.scottlogic.deg.generator.utils.RandomNumberGenerator;

import java.util.function.Function;
import java.util.stream.Stream;

public class ChecksumStringGenerator implements StringGenerator {

    private final StringGenerator approximatingGenerator;
    private final StringGenerator exactGenerator;
    private final ChecksumMaker checksumMaker;

    public ChecksumStringGenerator(StringGenerator approximatingGenerator, ChecksumMaker checksumMaker, StringGenerator exactGenerator) {
        this.approximatingGenerator = approximatingGenerator;
        this.checksumMaker = checksumMaker;
        this.exactGenerator = exactGenerator;
    }

    @Override
    public Stream<String> generateAllValues() {
        return approximatingGenerator.generateAllValues()
            .filter(str -> !exactGenerator.matches(str.substring(0, str.length() - 1)))
            .map(this::replaceFinalCharacterWithChecksum)
            .distinct();
    }

    @Override
    public Stream<String> generateRandomValues(RandomNumberGenerator randomNumberGenerator) {
        return approximatingGenerator.generateRandomValues(randomNumberGenerator)
            .filter(str -> !exactGenerator.matches(str.substring(0, str.length() - 1)))
            .map(this::replaceFinalCharacterWithChecksum)
            .distinct();
    }

    @Override
    public Stream<String> generateInterestingValues() {
        return approximatingGenerator.generateInterestingValues()
            .filter(str -> !exactGenerator.matches(str.substring(0, str.length() - 1)))
            .map(this::replaceFinalCharacterWithChecksum)
            .distinct();
    }

    private String replaceFinalCharacterWithChecksum(String str) {
        String prechecksum = str.substring(0, str.length() - 1);
        return prechecksum + checksumMaker.makeChecksum(prechecksum);
    }

    @Override
    public boolean matches(String string) {
        if (string.length() < 1) {
            return false;
        }
        String preChecksumComponent = string.substring(0, string.length() - 1);
        Character checksumComponent = string.charAt(string.length() - 1);
        return
            exactGenerator.matches(preChecksumComponent) &&
            checksumMaker.makeChecksum(preChecksumComponent).equals(checksumComponent);
    }

    @Override
    public StringGenerator intersect(StringGenerator stringGenerator) {
        throw new UnsupportedOperationException("Checksum constraints can only be used with length and equalTo constraints.");
    }

    public StringGenerator getApproximatingGenerator() {
        return this.approximatingGenerator;
    }
}
