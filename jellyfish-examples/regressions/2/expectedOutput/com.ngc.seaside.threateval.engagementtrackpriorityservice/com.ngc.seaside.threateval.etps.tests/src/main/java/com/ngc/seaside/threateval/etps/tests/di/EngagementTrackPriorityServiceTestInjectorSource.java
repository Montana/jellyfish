package com.ngc.seaside.threateval.etps.tests.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import com.ngc.blocs.guice.module.LogServiceModule;
import com.ngc.blocs.guice.module.ResourceServiceModule;
import com.ngc.blocs.guice.module.ThreadServiceModule;
import com.ngc.seaside.threateval.etps.testsconfig.EngagementTrackPriorityServiceTestConfigurationModule;

import cucumber.api.guice.CucumberModules;
import cucumber.runtime.java.guice.InjectorSource;

/**
 * This class handles the creation of the Guice injector.
 */
public class EngagementTrackPriorityServiceTestInjectorSource implements InjectorSource {

   @Override
   public Injector getInjector() {
      return Guice.createInjector(Stage.PRODUCTION, CucumberModules.SCENARIO,
                                  new LogServiceModule(),
                                  new ResourceServiceModule(),
                                  new ThreadServiceModule(),
                                  new EngagementTrackPriorityServiceTestConfigurationModule(),
                                  new EngagementTrackPriorityServiceTestModule());
   }
}
