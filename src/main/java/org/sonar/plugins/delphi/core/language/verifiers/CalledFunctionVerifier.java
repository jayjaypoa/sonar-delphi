/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.core.language.verifiers;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.sonar.plugins.delphi.antlr.analyzer.CodeAnalysisResults;
import org.sonar.plugins.delphi.antlr.analyzer.LexerMetrics;
import org.sonar.plugins.delphi.core.language.FunctionInterface;
import org.sonar.plugins.delphi.core.language.UnitInterface;
import org.sonar.plugins.delphi.core.language.impl.DelphiFunction;

/**
 * Veryfies if we are calling a function, from given as tree node
 */
public class CalledFunctionVerifier {

  private boolean isUnresolved = true;
  private CodeAnalysisResults results;
  private FunctionInterface calledFunction = null;

  /**
   * ctor
   * 
   * @param results
   */
  public CalledFunctionVerifier(CodeAnalysisResults results) {
    if (results == null) {
      throw new IllegalArgumentException("CalledFunctionVeifyer ctor param 'results' cannot be null.");
    }
    this.results = results;
  }

  public boolean verify(Tree node) {
    CommonTree nextNode = (CommonTree) node.getParent().getChild(node.getChildIndex() + 1);

    // if we are on a ident token and it is not last
    if (node.getType() == LexerMetrics.IDENT.toMetrics() && nextNode != null
        && (nextNode.getType() == LexerMetrics.LPAREN.toMetrics() || nextNode.getType() == LexerMetrics.SEMI.toMetrics())) {
      String functionName = node.getText().toLowerCase();
      List<UnitInterface> unitsToLook = new ArrayList<UnitInterface>();
      unitsToLook.add(results.getActiveUnit()); // first we look in current unit for function reference
      unitsToLook.addAll(results.getActiveUnit().getIncludedUnits(results.getCachedUnits()));

      // looking for called function units
      for (UnitInterface unit : unitsToLook) {
        FunctionInterface[] functions = unit.getAllFunctions();
        for (FunctionInterface func : functions) {
          if (func.getShortName().equalsIgnoreCase(functionName)) {
            calledFunction = func;
            isUnresolved = false;
            return true;
          }
        }
      }

      calledFunction = new DelphiFunction(node.getText().toLowerCase()); // create a new unresolved function
      isUnresolved = true; // no function found, but this was a function call
      return true; // so we return true
    }

    return false; // not a function call (like "foo(args);" or "foo;"
  }

  public FunctionInterface fetchCalledFunction() {
    return calledFunction;
  }

  public boolean isUnresolvedFunctionCall() {
    return isUnresolved;
  }

}