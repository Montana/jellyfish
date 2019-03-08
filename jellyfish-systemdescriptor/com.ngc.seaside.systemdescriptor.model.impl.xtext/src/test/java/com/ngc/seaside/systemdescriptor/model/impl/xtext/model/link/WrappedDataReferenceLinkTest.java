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
package com.ngc.seaside.systemdescriptor.model.impl.xtext.model.link;

import com.ngc.seaside.systemdescriptor.model.api.INamedChildCollection;
import com.ngc.seaside.systemdescriptor.model.api.model.IDataReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.impl.basic.NamedChildCollection;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.AbstractWrappedXtextTest;
import com.ngc.seaside.systemdescriptor.systemDescriptor.BaseLinkDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.FieldReference;
import com.ngc.seaside.systemdescriptor.systemDescriptor.InputDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.LinkableExpression;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

// Note we run this test with MockitoJUnitRunner.Silent to avoid UnnecessaryStubbingExceptions.  This happens because
// we are cheating and reusing the setup code for the model link test.
@RunWith(MockitoJUnitRunner.Silent.class)
public class WrappedDataReferenceLinkTest extends AbstractWrappedXtextTest {

   private WrappedDataReferenceLink wrapped;

   private BaseLinkDeclaration declaration;

   @Mock
   private IModel parent;

   @Mock
   private IModel sourceWrapper;

   @Mock
   private IModel targetWrapper;

   @Mock
   private IDataReferenceField wrappedSourceField;

   @Mock
   private IDataReferenceField wrappedTargetField;

   @Before
   public void setup() throws Throwable {
      Model sourceParent = factory().createModel();
      InputDeclaration source = factory().createInputDeclaration();
      source.setName("someSource");
      sourceParent.setInput(factory().createInput());
      sourceParent.getInput().getDeclarations().add(source);

      Model targetParent = factory().createModel();
      InputDeclaration target = factory().createInputDeclaration();
      target.setName("someTarget");
      targetParent.setInput(factory().createInput());
      targetParent.getInput().getDeclarations().add(target);

      FieldReference sourceRef = factory().createFieldReference();
      sourceRef.setFieldDeclaration(source);

      LinkableExpression targetExpression = factory().createLinkableExpression();
      targetExpression.setTail(target);

      Model xtextParent = factory().createModel();
      declaration = factory().createBaseLinkDeclaration();
      declaration.setSource(sourceRef);
      declaration.setTarget(targetExpression);
      xtextParent.setLinks(factory().createLinks());
      xtextParent.getLinks().getDeclarations().add(declaration);

      when(wrappedSourceField.getName()).thenReturn(source.getName());
      when(wrappedTargetField.getName()).thenReturn(target.getName());

      INamedChildCollection<IModel, IDataReferenceField> sourceInputs = new NamedChildCollection<>();
      INamedChildCollection<IModel, IDataReferenceField> targetInputs = new NamedChildCollection<>();
      sourceInputs.add(wrappedSourceField);
      targetInputs.add(wrappedTargetField);
      when(sourceWrapper.getInputs()).thenReturn(sourceInputs);
      when(targetWrapper.getInputs()).thenReturn(targetInputs);

      when(resolver().getWrapperFor(sourceParent)).thenReturn(sourceWrapper);
      when(resolver().getWrapperFor(targetParent)).thenReturn(targetWrapper);
      when(resolver().getWrapperFor(xtextParent)).thenReturn(parent);
   }

   @Test
   public void testDoesWrapXTextObject() throws Throwable {
      wrapped = new WrappedDataReferenceLink(resolver(), declaration);
      assertEquals("parent not correct!",
                   parent,
                   wrapped.getParent());
      assertEquals("source not correct!",
                   wrappedSourceField,
                   wrapped.getSource());
      assertEquals("target not correct!",
                   wrappedTargetField,
                   wrapped.getTarget());
   }

   @Test
   public void testDoesTryToWrapXTextObject() throws Throwable {
      assertTrue("tryToWrap failed to wrap valid data declaration!",
                 WrappedDataReferenceLink.tryToWrap(resolver(), declaration).isPresent());

      LinkTestUtil.LinkTestSetup setup = WrappedModelReferenceLinkTest.getModelLinkDeclaration();
      assertFalse("tryToWrap should not wrap model declaration!",
                  WrappedDataReferenceLink.tryToWrap(setup.resolver, setup.declaration)
                        .isPresent());
   }

   /**
    * Gets a mocked link test setup.
    */
   public static LinkTestUtil.LinkTestSetup getDataLinkDeclaration() throws Throwable {
      WrappedDataReferenceLinkTest test = new WrappedDataReferenceLinkTest();
      MockitoAnnotations.initMocks(test);
      test.setup();
      return new LinkTestUtil.LinkTestSetup(test.declaration, test.resolver());
   }
}
