package me.discordbot;

import java.util.*;
import java.io.*;

class CNFConverter {

    static abstract class Expression {}

    static class And extends Expression {
        Expression left, right;
        And(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
    }

    static class Or extends Expression {
        Expression left, right;
        Or(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
    }

    static class Not extends Expression {
        Expression expr;
        Not(Expression expr) {
            this.expr = expr;
        }

        @Override
        public String toString() {
            return "~" + expr;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Not not = (Not) obj;
            return Objects.equals(expr, not.expr);
        }
        @Override
        public int hashCode() {
            return Objects.hash(expr);
        }
    }

    static class Implies extends Expression {
        Expression left, right;
        Implies(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
    }

    static class Biconditional extends Expression {
        Expression left, right;
        Biconditional(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
    }

    static class Variable extends Expression {
        String name;
        Variable(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Variable variable = (Variable) obj;
            return Objects.equals(name, variable.name);
        }
        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }


    static Expression parseExpression(String input) {
        Stack<Expression> exprStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();
        String[] tokens = input.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ").trim().split("\\s+");

        for (String token : tokens) {
            switch (token) {
                case "(":
                    operatorStack.push(token);
                    break;

                case ")":
                    while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                        exprStack.push(buildExpression(operatorStack.pop(), exprStack));
                    }
                    operatorStack.pop(); // Pop "("
                    break;

                case "&":
                case "||":
                case "=>":
                case "<=>":
                case "~":
                    operatorStack.push(token);
                    break;

                default:
                    exprStack.push(new Variable(token)); // Assume variable
            }
        }

        while (!operatorStack.isEmpty()) {
            exprStack.push(buildExpression(operatorStack.pop(), exprStack));
        }

        return exprStack.pop();
    }

    static Expression buildExpression(String operator, Stack<Expression> exprStack) {
        switch (operator) {
            case "&":
                return new And(exprStack.pop(), exprStack.pop());
            case "||":
                return new Or(exprStack.pop(), exprStack.pop());
            case "~":
                return new Not(exprStack.pop());
            case "=>":
                Expression right = exprStack.pop();
                Expression left = exprStack.pop();
                return new Implies(left, right);
            case "<=>":
                right = exprStack.pop();
                left = exprStack.pop();
                return new Biconditional(left, right);
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    static Expression eliminateBiconditional(Expression expr) {
        if (expr instanceof Biconditional) {
            Biconditional biconditional = (Biconditional) expr;
            Expression leftImpliesRight = new Implies(biconditional.left, biconditional.right);
            Expression rightImpliesLeft = new Implies(biconditional.right, biconditional.left);
            return new And(eliminateBiconditional(leftImpliesRight), eliminateBiconditional(rightImpliesLeft));
        } else if (expr instanceof And) {
            return new And(eliminateBiconditional(((And) expr).left), eliminateBiconditional(((And) expr).right));
        } else if (expr instanceof Or) {
            return new Or(eliminateBiconditional(((Or) expr).left), eliminateBiconditional(((Or) expr).right));
        } else if (expr instanceof Not) {
            return new Not(eliminateBiconditional(((Not) expr).expr));
        }
        return expr;
    }

    static Expression eliminateImplications(Expression expr) {
        if (expr instanceof Implies) {
            Implies implies = (Implies) expr;
            return new Or(new Not(eliminateImplications(implies.left)), eliminateImplications(implies.right));
        } else if (expr instanceof And) {
            return new And(eliminateImplications(((And) expr).left), eliminateImplications(((And) expr).right));
        } else if (expr instanceof Or) {
            return new Or(eliminateImplications(((Or) expr).left), eliminateImplications(((Or) expr).right));
        } else if (expr instanceof Not) {
            return new Not(eliminateImplications(((Not) expr).expr));
        }
        return expr;
    }

    static Expression pushNotInwards(Expression expr) {
        if (expr instanceof Not) {
            Expression inner = ((Not) expr).expr;
            if (inner instanceof Not) {
                return pushNotInwards(((Not) inner).expr);
            } else if (inner instanceof And) {
                return new Or(pushNotInwards(new Not(((And) inner).left)),
                        pushNotInwards(new Not(((And) inner).right)));
            } else if (inner instanceof Or) {
                return new And(pushNotInwards(new Not(((Or) inner).left)),
                        pushNotInwards(new Not(((Or) inner).right)));
            }
        } else if (expr instanceof And) {
            return new And(pushNotInwards(((And) expr).left), pushNotInwards(((And) expr).right));
        } else if (expr instanceof Or) {
            return new Or(pushNotInwards(((Or) expr).left), pushNotInwards(((Or) expr).right));
        }
        return expr;
    }

    static Expression distributeOrOverAnd(Expression expr) {
        if (expr instanceof Or) {
            Expression left = distributeOrOverAnd(((Or) expr).left);
            Expression right = distributeOrOverAnd(((Or) expr).right);
            if (left instanceof And) {
                return new And(new Or(((And) left).left, right), new Or(((And) left).right, right));
            } else if (right instanceof And) {
                return new And(new Or(left, ((And) right).left), new Or(left, ((And) right).right));
            } else {
                return new Or(left, right);
            }
        } else if (expr instanceof And) {
            return new And(distributeOrOverAnd(((And) expr).left), distributeOrOverAnd(((And) expr).right));
        }
        return expr;
    }

    static Expression toCNF(Expression expr) {
        expr = eliminateBiconditional(expr);
        expr = eliminateImplications(expr);
        expr = pushNotInwards(expr);
        expr = distributeOrOverAnd(expr);
        return expr;
    }

    static String printExpression(Expression expr) {
        if (expr instanceof And) {
            return "(" + printExpression(((And) expr).left) + " & " + printExpression(((And) expr).right) + ")";
        } else if (expr instanceof Or) {
            return "(" + printExpression(((Or) expr).left) + " || " + printExpression(((Or) expr).right) + ")";
        } else if (expr instanceof Not) {
            return "~" + printExpression(((Not) expr).expr);
        } else if (expr instanceof Variable) {
            return ((Variable) expr).name;
        }
        return "";
    }
}
