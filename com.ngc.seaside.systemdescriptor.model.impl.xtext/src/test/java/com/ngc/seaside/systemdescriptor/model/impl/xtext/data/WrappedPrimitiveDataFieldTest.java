package com.ngc.seaside.systemdescriptor.model.impl.xtext.data;

import com.ngc.seaside.systemdescriptor.model.api.FieldCardinality;
import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IDataField;
import com.ngc.seaside.systemdescriptor.model.api.metadata.IMetadata;
import com.ngc.seaside.systemdescriptor.model.impl.xtext.AbstractWrappedXtextTest;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Cardinality;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitiveDataFieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitiveDataType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WrappedPrimitiveDataFieldTest extends AbstractWrappedXtextTest {

   private WrappedPrimitiveDataField wrappedDataField;

   private PrimitiveDataFieldDeclaration field;

   @Mock
   private IData parent;

   @Before
   public void setup() throws Throwable {
      Data parentData = factory().createData();
      parentData.setName("Foo");

      field = factory().createPrimitiveDataFieldDeclaration();
      field.setName("field1");
      field.setType(PrimitiveDataType.STRING);
      field.setCardinality(Cardinality.DEFAULT);
      parentData.getFields().add(field);

      when(resolver().getWrapperFor(parentData)).thenReturn(parent);
   }

   @Test
   public void testDoesWrapXtextObject() throws Throwable {
      wrappedDataField = new WrappedPrimitiveDataField(resolver(), field);
      assertEquals("name not correct!",
                   wrappedDataField.getName(),
                   field.getName());
      assertEquals("parent not correct!",
                   parent,
                   wrappedDataField.getParent());
      assertEquals("metadata not set!",
                   IMetadata.EMPTY_METADATA,
                   wrappedDataField.getMetadata());
      assertEquals("cardinality not correct!",
                   FieldCardinality.SINGLE,
                   wrappedDataField.getCardinality());
   }

   @Test
   public void testDoesUpdateXtextObject() throws Throwable {
      wrappedDataField = new WrappedPrimitiveDataField(resolver(), field);

      wrappedDataField.setType(DataTypes.INT);
      assertEquals("did not update type!",
                   PrimitiveDataType.INT,
                   field.getType());

      wrappedDataField.setCardinality(FieldCardinality.MANY);
      assertEquals("cardinality not correct!",
                   FieldCardinality.MANY,
                   wrappedDataField.getCardinality());
   }

   @Test
   public void testDoesCreateXtextObject() throws Throwable {
      IDataField newField = mock(IDataField.class);
      when(newField.getName()).thenReturn("newField");
      when(newField.getMetadata()).thenReturn(IMetadata.EMPTY_METADATA);
      when(newField.getType()).thenReturn(DataTypes.INT);
      when(newField.getCardinality()).thenReturn(FieldCardinality.MANY);

      PrimitiveDataFieldDeclaration xtext = WrappedPrimitiveDataField.toXtext(newField);
      assertEquals("name not correct!",
                   newField.getName(),
                   xtext.getName());
      assertEquals("type not correct!",
                   PrimitiveDataType.INT,
                   xtext.getType());
      assertEquals("cardinality not correct!",
                   xtext.getCardinality(),
                   Cardinality.MANY);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNotAllowDataTypeToBeChangedToNonPrimitiveType() throws Throwable {
      wrappedDataField = new WrappedPrimitiveDataField(resolver(), field);
      wrappedDataField.setType(DataTypes.DATA);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testDoesNotCreateXtextObjectForNonPrimitiveType() throws Throwable {
      IDataField newField = mock(IDataField.class);
      when(newField.getType()).thenReturn(DataTypes.DATA);

      WrappedPrimitiveDataField.toXtext(newField);
   }
}
