package com.ngc.seaside.threateval.etps.impl;

import com.ngc.blocs.service.log.api.ILogService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class EngagementTrackPriorityServiceTest {

   private EngagementTrackPriorityService service;

   @Mock
   private ILogService logService;

   @Before
   public void setup() throws Throwable {
      service = new EngagementTrackPriorityService();
      service.setLogService(logService);
      service.activate();
   }

   @Test
   public void doCalculateTrackPriorityTest() throws Exception {
      // TODO: implement this
      fail("not implemented");
   }

   @After
   public void cleanup() throws Throwable {
      service.deactivate();
   }
}
