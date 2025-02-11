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
package org.perses.reduction

import com.google.common.collect.ImmutableList
import org.perses.CommandOptions
import org.perses.cmd.InputFlags
import org.perses.cmd.OutputFlags
import org.perses.cmd.ReductionControlFlags
import org.perses.grammar.AbstractParserFacadeFactory
import org.perses.program.ScriptFile
import org.perses.program.SourceFile
import org.perses.reduction.io.RegularReductionInputs
import org.perses.reduction.io.token.RegularOutputManagerFactory
import org.perses.reduction.io.token.TokenReductionIOManager
import java.nio.file.Path

/**
 * This is the main entry to invoke Perses reducer. It does not have a main, but is the main entry
 * as a reduction library.
 */
class RegularProgramReductionDriver private constructor(
  cmd: CommandOptions,
  ioManager: TokenReductionIOManager,
  tree: SparTreeWithParsability,
  configuration: ReductionConfiguration,
  extraListeners: ImmutableList<AbstractReductionListener>
) : AbstractProgramReductionDriver(
  cmd,
  ioManager,
  tree,
  configuration,
  extraListeners
) {

  companion object {

    fun createReductionInputs(
      parserFacadeFactory: AbstractParserFacadeFactory,
      inputFlags: InputFlags
    ): RegularReductionInputs {
      val absoluteSourceFilePath: Path = inputFlags.getSourceFile().toAbsolutePath()
      val languageKind = parserFacadeFactory.computeLanguageKindOrThrow(absoluteSourceFilePath)
      val sourceFile = SourceFile(absoluteSourceFilePath, languageKind)
      val testScript = ScriptFile(inputFlags.getTestScript().toAbsolutePath())

      require(sourceFile.parentFile.toAbsolutePath() == testScript.parentFile.toAbsolutePath()) {
        "The source file and the test script should reside in the same folder. " +
          "sourceFile:$sourceFile, testScript:$testScript"
      }
      return RegularReductionInputs(testScript = testScript, mainFile = sourceFile)
    }

    fun createIOManager(
      reductionInputs: RegularReductionInputs,
      reductionControlFlags: ReductionControlFlags,
      outputFlags: OutputFlags
    ): TokenReductionIOManager {
      val workingDirectory = reductionInputs.mainFile.parentFile
      val languageKind = reductionInputs.mainFile.languageKind
      val programFormatControl = reductionControlFlags.codeFormat.let { codeFormat ->
        if (codeFormat != null) {
          check(languageKind.isCodeFormatAllowed(codeFormat)) {
            "$codeFormat is not allowed for language $languageKind"
          }
          codeFormat
        } else {
          languageKind.defaultCodeFormatControl
        }
      }
      return TokenReductionIOManager(
        workingDirectory,
        reductionInputs,
        outputManagerFactory = RegularOutputManagerFactory(
          reductionInputs.mainFile.baseName,
          programFormatControl
        ),
        outputDirectory = outputFlags.outputDir
      )
    }

    fun create(
      cmd: CommandOptions,
      parserFacadeFactory: AbstractParserFacadeFactory,
      extraListeners: ImmutableList<AbstractReductionListener> = ImmutableList.of()
    ): RegularProgramReductionDriver {
      val reductionInputs = createReductionInputs(
        parserFacadeFactory, cmd.inputFlags
      )
      val ioManager = createIOManager(
        reductionInputs,
        cmd.reductionControlFlags,
        cmd.resultOutputFlags
      )
      val reductionConfiguration = createConfiguration(
        cmd, parserFacadeFactory,
        reductionInputs.mainLanguage,
        ioManager.getDefaultProgramFormat()
      )
      val tree = createSparTree(
        reductionInputs.mainFile,
        reductionConfiguration.parserFacade
      )

      return RegularProgramReductionDriver(
        cmd, ioManager, tree, reductionConfiguration, extraListeners
      )
    }
  }
}
