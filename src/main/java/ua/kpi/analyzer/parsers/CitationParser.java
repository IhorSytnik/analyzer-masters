package ua.kpi.analyzer.parsers;

import org.grobid.core.data.BiblioItem;

import java.util.List;

/**
 * @author Ihor Sytnik
 */
public interface CitationParser {
    List<BiblioItem> processCitations(List<String> citations);
    BiblioItem processCitation(String citation);
}
