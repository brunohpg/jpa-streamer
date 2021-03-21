/*
 * JPAstreamer - Express JPA queries with Java Streams
 * Copyright (c) 2020-2020, Speedment, Inc. All Rights Reserved.
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * See: https://github.com/speedment/jpa-streamer/blob/master/LICENSE
 */
package com.speedment.jpastreamer.interopoptimizer.standard.internal.strategy;

import com.speedment.jpastreamer.interopoptimizer.standard.internal.strategy.squash.abstracts.AbstractNoValueSquash;
import com.speedment.jpastreamer.pipeline.intermediate.IntermediateOperation;
import com.speedment.jpastreamer.pipeline.intermediate.IntermediateOperationFactory;
import com.speedment.jpastreamer.pipeline.intermediate.IntermediateOperationType;

import java.util.function.Supplier;

public final class SquashDistinct extends AbstractNoValueSquash {

    private final IntermediateOperationFactory intermediateOperationFactory;

    public SquashDistinct(final IntermediateOperationFactory intermediateOperationFactory) {
        this.intermediateOperationFactory = intermediateOperationFactory;
    }

    @Override
    public Supplier<IntermediateOperation<?, ?>> operationProvider() {
        return intermediateOperationFactory::acquireDistinct;
    }

    @Override
    public IntermediateOperationType operationType() {
        return IntermediateOperationType.DISTINCT;
    }
}