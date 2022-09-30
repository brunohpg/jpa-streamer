/*
 * JPAstreamer - Express JPA queries with Java Streams
 * Copyright (c) 2020-2022, Speedment, Inc. All Rights Reserved.
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * See: https://github.com/speedment/jpa-streamer/blob/master/LICENSE
 */
package com.speedment.jpastreamer.field.expression;

import com.speedment.common.function.ToCharFunction;
import com.speedment.runtime.compute.ToChar;
import com.speedment.runtime.compute.ToCharNullable;

/**
 * Specific {@link FieldMapper} implementation that also implements
 * {@link ToCharNullable}.
 *
 * @param <ENTITY>  the entity type
 * @param <T>       the column type before mapped
 *
 * @author Emil Forslund
 * @since  3.1.0
 */
public interface FieldToChar<ENTITY, T>
extends FieldMapper<ENTITY, T, Character, ToChar<ENTITY>, ToCharFunction<T>>,
        ToCharNullable<ENTITY> {}
