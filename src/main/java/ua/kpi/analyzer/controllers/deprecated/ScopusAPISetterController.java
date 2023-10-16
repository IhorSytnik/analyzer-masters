package ua.kpi.analyzer.controllers.deprecated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.analyzer.services.ScopusAPISetterService;

/**
 * <p>Controllers with routes to set Scopus api credentials.</p>
 *
 * <p><b>!!!/set method should be called first, prior to calling
 * {@link FileProcessingController#process(String)}!!!</b></p>
 *
 * @author Ihor Sytnik
 */
//@Controller
//@RequestMapping(value = "/scopus/api")
@Deprecated
public class ScopusAPISetterController {
    @Autowired
    private ScopusAPISetterService scopusAPISetterService;

    /**
     * <p>Sets API key and insttoken for Scopus.</p>
     * <p>While insttoken isn't necessary, API key <b>must</b> be set prior to calling
     * {@link FileProcessingController#process(String)}.</p>
     *
     * @see <a href="https://dev.elsevier.com/tecdoc_api_authentication.html">About Scopus API authentication</a>
     * @param apikey Scopus api key to set
     * @param insttoken Scopus insttoken to set
     */
    @PostMapping("/set")
    @ResponseBody
    public void setAPIKeyAndInsttoken(@RequestParam(required = false) String apikey,
                                      @RequestParam(required = false) String insttoken) {
        scopusAPISetterService.setApiCredentials(apikey, insttoken);
    }
}
