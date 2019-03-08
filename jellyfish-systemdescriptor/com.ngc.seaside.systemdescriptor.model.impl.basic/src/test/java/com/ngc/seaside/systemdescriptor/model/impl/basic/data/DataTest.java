/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2019, Northrop Grumman Systems Corporation
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Northrop Grumman Systems Corporation. The intellectual and technical concepts
 * contained herein are proprietary to Northrop Grumman Systems Corporation and
 * may be covered by U.S. and Foreign Patents or patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Northrop Grumman.
 */
package com.ngc.seaside.systemdescriptor.model.impl.basic.data;

import com.ngc.seaside.systemdescriptor.model.api.IPackage;
import com.ngc.seaside.systemdescriptor.model.impl.basic.metadata.Metadata;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class DataTest {

   private Data data;
   private Metadata metadata;
   private DataField field;

   @Mock
   private IPackage parent;

   @Before
   public void setup() throws Throwable {
      field = new DataField("field1");
      metadata = new Metadata();
      metadata.setJson(Json.createObjectBuilder().add("foo", "bar").build());
   }

   @Test
   public void testDoesCreateData() throws Throwable {
      data = new Data("FooData");
      assertEquals("name not correct!", "FooData", data.getName());
   }

   @Test
   public void testDoesManageFields() throws Throwable {
      data = new Data("FooData");
      data.getFields().add(field);
      assertEquals("parent not correct!", field.getParent(), data);

      data.getFields().remove(field);
      assertNull("parent not removed!", field.getParent());
   }
}
