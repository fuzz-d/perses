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
package org.perses.reduction.cache

import com.google.common.collect.ImmutableList
import it.unimi.dsi.fastutil.ints.IntIterator
import org.perses.program.PersesTokenFactory.PersesToken

object IntervalArrayExpander {

  private val EMPTY_ITERATOR = object : IntIterator {

    override fun hasNext() = false

    override fun nextInt(): Int {
      TODO("Not yet implemented")
    }

    override fun remove() {
      TODO("Not supported")
    }
  }

  @JvmStatic
  fun expand(intervals: IntArray): IntIterator {
    val size = intervals.size
    require(size % 2 == 0)
    if (size == 0) {
      return EMPTY_ITERATOR
    }

    return object : IntIterator {

      var indexInArray = 0
      var currentIntervalStartValue = intervals[indexInArray]
      var currentIntervalEndValue = intervals[indexInArray + 1]
      var nextValue = currentIntervalStartValue

      init {
        assert(currentIntervalEndValue > currentIntervalStartValue)
      }

      override fun hasNext(): Boolean {
        if (indexInArray >= size) {
          return false
        }
        return nextValue in currentIntervalStartValue until currentIntervalEndValue
      }

      override fun nextInt(): Int {
        assert(hasNext())
        val result = nextValue
        ++nextValue
        if (nextValue == currentIntervalEndValue) {
          // The current region is exhausted. Move to the next region.
          indexInArray += 2
          if (indexInArray >= size) {
            // The array is exhausted.
            assert(!hasNext())
            return result
          }
          currentIntervalStartValue = intervals[indexInArray]
          currentIntervalEndValue = intervals[indexInArray + 1]
          assert(currentIntervalEndValue > currentIntervalStartValue)
          nextValue = currentIntervalStartValue
        }
        return result
      }

      @Deprecated("Do not call.", ReplaceWith("Never call this."))
      override fun remove() {
        TODO("Not supported")
      }
    }
  }

  @JvmStatic
  fun expand(
    intervals: IntArray,
    tokensInOrigin: ImmutableList<PersesToken>
  ): Iterator<PersesToken> = object : Iterator<PersesToken> {

    private val iterator = expand(intervals)

    override fun hasNext(): Boolean {
      return iterator.hasNext()
    }

    override fun next(): PersesToken {
      return tokensInOrigin[iterator.nextInt()]
    }
  }
}
