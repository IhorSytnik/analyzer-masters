package ua.kpi.analyzer.exceptions;

/**
 * @author Ihor Sytnik
 */
public class WrongRuleSyntaxException extends RuntimeException {
    public WrongRuleSyntaxException(String message) {
        super(message);
    }
}
