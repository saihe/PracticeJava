package ksaito.callApi;

import ksaito.callApi.service.lams.LamsService;
import ksaito.callApi.service.visa.VisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static ksaito.callApi.Util.exit;

@Component
public class CommandLineApplication implements CommandLineRunner {
    @Autowired
    private VisaService visaService;
    @Autowired
    private LamsService lamsService;

    @Override
    public void run(String... args) throws Exception {
//        lamsService.call();
        visaService.call();
        exit(0);
    }
}
