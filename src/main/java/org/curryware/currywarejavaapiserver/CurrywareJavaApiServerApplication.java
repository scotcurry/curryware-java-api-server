package org.curryware.currywarejavaapiserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.curryware")

public class CurrywareJavaApiServerApplication {

    private static final Logger logger = LogManager.getLogger(CurrywareJavaApiServerApplication.class);
    public static void main(String[] args) {

        SpringApplication.run(CurrywareJavaApiServerApplication.class, args);
        logger.debug("Launching the CurrywareJavaApiServerApplication");
    }
}
