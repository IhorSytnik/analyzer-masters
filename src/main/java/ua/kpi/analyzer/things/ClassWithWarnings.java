package ua.kpi.analyzer.things;

import java.util.Collection;

/**
 * @author Ihor Sytnik
 */
public interface ClassWithWarnings {
    Collection<String> getWarnings();
    void addWarning(String str);
}
