package ua.kpi.analyzer.controllers.parameters;

import jakarta.validation.constraints.NotNull;

/**
 * @author Ihor Sytnik
 */
public record FileProcessingParameters(
        @NotNull(message="Please provide a valid file name")
        String filename) { }
