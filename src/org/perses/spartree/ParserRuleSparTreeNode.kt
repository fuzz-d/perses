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
package org.perses.spartree

import org.perses.antlr.RuleHierarchyInfo
import org.perses.antlr.RuleType

/** A spar-tree node for a parser rule.  */
class ParserRuleSparTreeNode internal constructor(
  nodeId: Int,
  antlrRule: RuleHierarchyInfo?
) : AbstractSparTreeNode(nodeId, antlrRule) {

  init {
    require(nodeType != RuleType.TOKEN) { nodeType }
  }

  override fun deleteCurrentNode() {
    super.deleteCurrentNode()
    assert(checkNodeIntegrity() == null) { checkNodeIntegrity()!! }
  }

  fun getKleeneElementRuleTypeOrThrow(): RuleHierarchyInfo {
    assert(isKleenePlusRuleNode || isKleeneStarRuleNode) { this }
    assert(childCount > 0) { this }
    val elementTypeCandidates = children
      .asSequence()
      .map { it.payload!!.expectedAntlrRuleType }
      .filter { it != null }
      .distinct()
      .toList()
    assert(elementTypeCandidates.size == 1) { elementTypeCandidates }
    return elementTypeCandidates.first()!!
  }

  /**
   * This method should be only called when a spar-tree is constructed from a Antlr parse tree. Add
   * a child to the current node.
   *
   * Note that the child's parent should be null.
   *
   * @param child, a tree node whose parent is null.
   */
  override fun addChild(
    child: AbstractSparTreeNode,
    payload: AbstractNodePayload
  ) {
    super.addChild(child, payload)
    assert(checkNodeIntegrity() == null) { checkNodeIntegrity()!! }
  }

  override var beginToken: LexerRuleSparTreeNode? = null

  override var endToken: LexerRuleSparTreeNode? = null

  override fun onChildRemoved(index: Int, child: AbstractSparTreeNode) = Unit

  override val isTokenNode: Boolean
    get() = false

  override fun buildTokenIntervalInfoForCurrentNode() {
    beginToken = leftmostToken
    endToken = rightmostToken
  }

  override val labelPrefix: String
    get() = when (nodeType) {
      RuleType.KLEENE_PLUS -> "(+)"
      RuleType.KLEENE_STAR -> "(*)"
      RuleType.OPTIONAL -> "(?)"
      RuleType.ALT_BLOCKS -> "(|:$ruleName)"
      RuleType.OTHER_RULE -> ruleName
      else -> throw AssertionError("Cannot reach here. $this")
    }

  override fun asParserRule(): ParserRuleSparTreeNode {
    return this
  }

  val leftmostToken: LexerRuleSparTreeNode?
    get() {
      val size = childCount
      for (i in 0 until size) {
        val child = getChild(i)
        val leftToken = child.beginToken
        if (leftToken != null) {
          return leftToken
        }
      }
      return null
    }

  val rightmostToken: LexerRuleSparTreeNode?
    get() {
      for (i in childCount - 1 downTo 0) {
        val child = getChild(i)
        val rightToken = child.endToken
        if (rightToken != null) {
          return rightToken
        }
      }
      return null
    }

  override fun checkNodeIntegrity(): ErrorMessage? {
    return null
  }

  override fun copyCurrentNode(): ParserRuleSparTreeNode {
    return ParserRuleSparTreeNode(nodeId, antlrRule)
  }
}
