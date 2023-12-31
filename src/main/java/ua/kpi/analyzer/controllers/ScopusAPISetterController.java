package ua.kpi.analyzer.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.analyzer.controllers.parameters.ScopusAPICredentialsParameters;
import ua.kpi.analyzer.services.ScopusAPISetterService;

import java.util.Set;

/**
 * <p>Controllers with routes to set Scopus api credentials.</p>
 *
 * <p><b>!!!/set method should be called first, prior to calling
 * {@link FileProcessingController#process(MultipartFile, Set)}!!!</b></p>
 *
 * @author Ihor Sytnik
 */
@RestController
@Validated
@RequestMapping(value = "/scopus/api")
public class ScopusAPISetterController {
    @Autowired
    private ScopusAPISetterService scopusAPISetterService;

    /**
     * <p>Sets API key and insttoken for Scopus.</p>
     * <p>While insttoken isn't necessary, API key <b>must</b> be set prior to calling
     * {@link FileProcessingController#process(MultipartFile, Set)}.</p>
     *
     * @see <a href="https://dev.elsevier.com/tecdoc_api_authentication.html">About Scopus API authentication</a>
     * @param scopusAPICredentialsParameters contains <i>apikey</i> - Scopus api key to set,
     *                                      <i>insttoken</i> - Scopus insttoken to set
     */
    @PostMapping("/set")
    public void setAPIKeyAndInsttoken(@RequestBody @Valid ScopusAPICredentialsParameters scopusAPICredentialsParameters) {
        scopusAPISetterService.setApiCredentials(
                scopusAPICredentialsParameters.apikey(),
                scopusAPICredentialsParameters.insttoken());
    }
}
