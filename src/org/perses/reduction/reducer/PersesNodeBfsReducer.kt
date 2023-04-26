/*
 * Copyright (C) 2018-2022 University of Waterloo.
 *
 * This file is part of Perses.
 *
 * Perses is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3, or (at your option) any later version.
 *
 * Perses is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Perses; see the file LICENSE.  If not see <http://www.gnu.org/licenses/>.
 */
package org.perses.reduction.reducer

import com.google.common.collect.ImmutableList
import org.perses.reduction.AbstractTokenReducer
import org.perses.reduction.ReducerAnnotation
import org.perses.reduction.ReducerContext
import org.perses.reduction.partition.Partition
import org.perses.spartree.AbstractSparTreeNode
import org.perses.spartree.SparTree
import org.perses.util.Util.lazyAssert
import java.util.ArrayDeque
import java.util.Queue

/** Perses node reducer, with bfs delta debugging  */
open class PersesNodeBfsReducer protected constructor(
  reducerAnnotation: ReducerAnnotation,
  reducerContext: ReducerContext,
) : AbstractPersesNodeReducer(reducerAnnotation, reducerContext) {

  private val deltaDebugger = BfsDeltaDebugger(
    reducerContext.listenerManager,
    reducerContext.nodeActionSetCache,
  ) { testSparTreeEdit(it) }

  override fun createReductionQueue(): Queue<AbstractSparTreeNode> {
    return ArrayDeque(DEFAULT_INITIAL_QUEUE_CAPACITY)
  }

  override fun performDelta(
    tree: SparTree,
    actionsDescription: String,
    vararg startPartitions: Partition,
  ) {
    lazyAssert { startPartitions.isNotEmpty() }
    deltaDebugger.reduce(tree, actionsDescription, *startPartitions)
  }

  companion object {
    const val NAME = "perses_node_with_bfs_delta"

    @JvmField
    val META: ReducerAnnotation = object : ReducerAnnotation() {

      override val deterministic: Boolean
        get() = true

      override val reductionResultSizeTrend: ReductionResultSizeTrend
        get() = ReductionResultSizeTrend.BEST_RESULT_SIZE_DECREASE

      override fun shortName() = NAME

      override fun description() = ""

      override fun create(reducerContext: ReducerContext) =
        ImmutableList.of<AbstractTokenReducer>(
          PersesNodeBfsReducer(this, reducerContext),
        )
    }
  }
}
