package com.kaznowski.hugh.checkstyleorderedproperties;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderedPropertiesCheck extends AbstractCheck {

  Logger log = Logger.getLogger(OrderedPropertiesCheck.class.getName());

  private boolean debug = false;

  @Override
  public int[] getDefaultTokens() {
    return new int[]{TokenTypes.VARIABLE_DEF};
  }

  @Override
  public int[] getAcceptableTokens() {
    return new int[]{TokenTypes.VARIABLE_DEF};
  }

  @Override
  public int[] getRequiredTokens() {
    return new int[]{};
  }

  @Override
  public void visitToken(DetailAST ast) {
    // First we establish that this is a variable declaration that we care about
    DetailAST classdef = findParent(ast, and(notType(TokenTypes.METHOD_DEF), isType(TokenTypes.CLASS_DEF)));
    if (classdef != null) {
      // Now we pick up the important details i.e. the name of the variable
      DetailAST variableName = findVariableName(ast);
      if (variableName == null) {
        riskyMessage(ast, "unable to retrieve variable name");
        return;
      }
      debug = false;
      List<DetailAST> otherVariables = findChildren(classdef, and(notType(TokenTypes.METHOD_DEF, TokenTypes.CLASS_DEF, TokenTypes.TYPE), isType(TokenTypes.VARIABLE_DEF)), 30); // TODO 3?
      debug = false;
      for (DetailAST cmpVar : otherVariables) {
        DetailAST cmpLabel = findVariableName(cmpVar);
        if (cmpLabel == null) {
          riskyMessage(cmpVar, "there was an issue processing names of variables to compare against");
          return;
        }
        boolean shouldBeBefore = cmpLabel.getText().compareTo(variableName.getText()) > 0;
        boolean isBefore = cmpLabel.getLineNo() - variableName.getLineNo() > 0;
        if (shouldBeBefore && !isBefore) {
          log(ast, String.format("%s should be before %s on line %d", variableName.getText(), cmpLabel.getText(), cmpLabel.getLineNo()));
          return;
        }
      }
      if (otherVariables.size() == 0) {
        riskyMessage(ast, "failed to find children");
      }
    }
  }

  private DetailAST findVariableName(DetailAST identifier) {
    // TODO validate it is an identifier type
    List<DetailAST> identifiers = findChildren(identifier, and(notType(TokenTypes.TYPE), isType(TokenTypes.IDENT)), 1);
    if (identifiers.size() != 1) {
      String msg = String.format("mismatch identifier, expected 1 got %d: %s", identifiers.size(), parentTrail(identifier));
      return riskyMessage(identifier, msg);
    }
    return identifiers.get(0);
  }

  private DetailAST riskyMessage(DetailAST identifier, String msg) {
    try {
      log(identifier, msg);
      return null;
    } catch (Exception e) {
      throw new RuntimeException(msg, e);
    }
  }

  private String parentTrail(DetailAST input) {
    List<String> types = new LinkedList<>();
    DetailAST current = input;
    types.add(current.getText());
    while (true) {
      try {
        current = current.getParent();
        if (current == null) {
          break;
        }
        types.add(current.getText());
      } catch (Exception e) {
        e.printStackTrace(System.err);
        break;
      }
    }
    Collections.reverse(types);
    String trace = String.join(", ", types);
    return String.format("[%d:%d] %s", input.getLineNo(), input.getColumnNo(), trace);
  }

  private Function<DetailAST, Boolean> and(Function<DetailAST, Boolean>... rules) {
    return ast -> {
      for (Function<DetailAST, Boolean> rule : rules) {
        Boolean valid = rule.apply(ast);
        if (valid == null) {
          continue;
        }
        return valid;
      }
      return null;
    };
  }

  private Function<DetailAST, Boolean> isType(int... types) {
    return ast -> {
      for (int type : types) {
        if (ast.getType() == type) {
          return Boolean.TRUE;
        }
      }
      return null;
    };
  }

  private Function<DetailAST, Boolean> notType(int... types) {
    return ast -> {
      for (int type : types) {
        if (ast.getType() == type) {
          return Boolean.FALSE;
        }
      }
      return null;
    };
  }

  private List<DetailAST> findChildren(DetailAST ast, Function<DetailAST, Boolean> qualifier, int depth) {
    List<DetailAST> found = new ArrayList<>();
    if (ast == null || depth <= 0) {
      return found;
    }
    DetailAST current = ast.getFirstChild();
    while (current != null) {
      Boolean valid = qualifier.apply(current);
      if (valid == null) {
        found.addAll(findChildren(current, qualifier, depth - 1));
      } else if (valid) {
        debugf("Found child %s\n", parentTrail(current));
        found.add(current);
      }
      current = current.getNextSibling();
    }
    return found;
  }

  private DetailAST findParent(DetailAST ast, Function<DetailAST, Boolean> qualifier) {
    if (ast == null || ast.getParent() == null) {
      return null;
    }
    Boolean valid = qualifier.apply(ast.getParent());
    if (valid == null) {
      return findParent(ast.getParent(), qualifier);
    }
    if (valid) {
      return ast;
    }
    return null;
  }

  private void debugf(String msg, Object... args) {
    if (debug) {
      System.err.printf(msg, args);
    }
  }
}