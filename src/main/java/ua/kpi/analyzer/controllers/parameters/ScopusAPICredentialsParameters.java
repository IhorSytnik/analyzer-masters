package ua.kpi.analyzer.controllers.parameters;

import jakarta.validation.constraints.NotNull;

/**
 * @author Ihor Sytnik
 */
public record ScopusAPICredentialsParameters(
        @NotNull(message="Please provide a valid api key")
        String apikey,

        @NotNull(message="Please provide a valid insttoken")
        String insttoken) { }
