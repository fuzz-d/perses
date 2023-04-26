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
package org.perses.cmd

import com.beust.jcommander.Parameter
import com.google.common.flogger.FluentLogger
import org.perses.program.EnumFormatControl
import org.perses.util.cmd.CommonCmdOptionGroupOrder
import org.perses.util.cmd.ICommandLineFlags
import org.perses.util.ktWarning

class ReductionControlFlags : ICommandLineFlags {
  @JvmField
  @Parameter(
    names = ["--fixpoint"],
    description = "iterative reduction till fixpoint",
    arity = 1,
    order = CommonCmdOptionGroupOrder.REDUCTION_CONTROL + 0,
  )
  var fixpoint = true

  @Parameter(
    names = ["--threads"],
    description = "Number of reduction threads: a positive integer, or 'auto'.",
    order = CommonCmdOptionGroupOrder.REDUCTION_CONTROL + 1,
  )
  private var numOfThreads = "auto"

  @Parameter(
    names = ["--code-format"],
    description = "The format of the reduced program.",
    arity = 1,
    order = CommonCmdOptionGroupOrder.REDUCTION_CONTROL + 2,
  )
  var codeFormat: EnumFormatControl? = null

  @Parameter(
    names = ["--non-deletion-iteration-limit"],
    description = "The maximum number of continuous non-deletion iterations allowed",
    hidden = true,
  )
  var nonDeletionIterationLimit = 1000

  override fun validate() {
    if ("auto" != numOfThreads) {
      val num = numOfThreads.toInt()
      check(num > 0) { numOfThreads }
      if (num > AVAILABLE_PROCESSORS) {
        logger.ktWarning {
          "The num of threads $num is greater than the available processors $AVAILABLE_PROCESSORS."
        }
      }
    }
  }

  fun getNumOfThreads(): Int {
    return if ("auto" == numOfThreads) {
      AVAILABLE_PROCESSORS
    } else {
      numOfThreads.toInt()
    }
  }

  companion object {
    val logger = FluentLogger.forEnclosingClass()

    private val AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors()
  }
}
