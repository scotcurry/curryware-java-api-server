package org.curryware.currywarejavaapiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.curryware")

public class CurrywareJavaApiServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(CurrywareJavaApiServerApplication.class, args);
    }
}
