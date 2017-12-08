package com.ngc.seaside.threateval.tps.tests.main;

import com.ngc.seaside.threateval.tps.tests.steps.TrackPriorityServiceSteps;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This application runs the {@link TrackPriorityServiceSteps}.
 */
public class TrackPriorityServiceTestMain {

   public static final String APP_HOME_SYS_PROPERTY = "appHome";

   public static void main(String[] args) throws Throwable {
      String appHome = System.getProperty(APP_HOME_SYS_PROPERTY);

      if (args.length == 0) {
         Path folder;
         if (appHome != null && !appHome.trim().equals("")) {
            folder = Paths.get(appHome, "resources");
         } else {
            folder = Paths.get("src", "main", "resources");
            System.setProperty("NG_FW_HOME", Paths.get("src", "main").toAbsolutePath().toString());
         }
         if (folder != null) {
            String featurePath = folder.toString();

            String cucumberResultsDir = "build/test-results/cucumber/";

            args = new String[] {
              "--glue", TrackPriorityServiceSteps.class.getPackage().getName(),
              "--plugin", "pretty",
              "--plugin", "html:" + cucumberResultsDir + "html",
              "--plugin", "junit:" + cucumberResultsDir + "cucumber-results.xml",
              "--plugin", "json:" + cucumberResultsDir + "cucumber-results.json",
              featurePath };
         }

      }
      cucumber.api.cli.Main.main(args);
   }
}