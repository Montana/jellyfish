package com.ngc.seaside.systemdescriptor.test.systemdescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ngc.seaside.systemdescriptor.model.api.FieldCardinality;
import com.ngc.seaside.systemdescriptor.model.api.INamedChild;
import com.ngc.seaside.systemdescriptor.model.api.INamedChildCollection;
import com.ngc.seaside.systemdescriptor.model.api.IPackage;
import com.ngc.seaside.systemdescriptor.model.api.data.DataTypes;
import com.ngc.seaside.systemdescriptor.model.api.data.IData;
import com.ngc.seaside.systemdescriptor.model.api.data.IDataField;
import com.ngc.seaside.systemdescriptor.model.api.data.IEnumeration;
import com.ngc.seaside.systemdescriptor.model.api.metadata.IMetadata;
import com.ngc.seaside.systemdescriptor.model.api.model.IDataReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.IModel;
import com.ngc.seaside.systemdescriptor.model.api.model.IModelReferenceField;
import com.ngc.seaside.systemdescriptor.model.api.model.link.IModelLink;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenario;
import com.ngc.seaside.systemdescriptor.model.api.model.scenario.IScenarioStep;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.CorrelateStepHandler;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.PublishStepHandler;
import com.ngc.seaside.systemdescriptor.scenario.impl.standardsteps.ReceiveStepHandler;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Utility class for mocking system descriptor elements.
 */
public class ModelUtils {

   /**
    * An instance of {@link IModel} with convenience methods for adding pub-sub scenarios and correlations.
    */
   public static class PubSubModel implements IModel {

      private final String name;
      private final IPackage parent;
      private final NamedChildCollection<IModel, IDataReferenceField> inputs;
      private final NamedChildCollection<IModel, IDataReferenceField> outputs;
      private final NamedChildCollection<IModel, IScenario> scenarios;

      public PubSubModel(String fullyQualifiedName) {
         int index = fullyQualifiedName.lastIndexOf('.');
         assertTrue(fullyQualifiedName + " is not a fully qualified name", index >= 0);
         name = fullyQualifiedName.substring(index + 1);
         parent = mock(IPackage.class);
         when(parent.getName()).thenReturn(fullyQualifiedName.substring(0, index));
         inputs = new NamedChildCollection<>(this, IDataReferenceField.class);
         outputs = new NamedChildCollection<>(this, IDataReferenceField.class);
         scenarios = new NamedChildCollection<>(this, IScenario.class);
      }

      /**
       * Adds the given field to this model's inputs.
       *
       * @param field data reference field
       */
      public void addInput(IDataReferenceField field) {
         inputs.add(field);
      }

      /**
       * Adds the given field to this model's outputs.
       *
       * @param field data reference field
       */
      public void addOutput(IDataReferenceField field) {
         outputs.add(field);
      }

      public void addScenario(IScenario scenario) {
         scenarios.add(scenario);
      }

      /**
       * Creates a mocked {@link IDataReferenceField} with the given name, given data, and {@link FieldCardinality#SINGLE} and adds it to this model's inputs.
       *
       * @param name reference field name
       * @param data reference field type
       * @return the mocked {@link IDataReferenceField}
       */
      public IDataReferenceField addInput(String name, IData data) {
         IDataReferenceField field = mock(IDataReferenceField.class);
         when(field.getName()).thenReturn(name);
         when(field.getParent()).thenReturn(this);
         when(field.getCardinality()).thenReturn(FieldCardinality.SINGLE);
         when(field.getType()).thenReturn(data);
         inputs.add(field);
         return field;
      }

      /**
       * Creates a mocked {@link IDataReferenceField} with the given name, given data, and {@link FieldCardinality#SINGLE} and adds it to this model's outputs.
       *
       * @param name reference field name
       * @param data reference field type
       * @return the mocked {@link IDataReferenceField}
       */
      public IDataReferenceField addOutput(String name, IData data) {
         IDataReferenceField field = mock(IDataReferenceField.class);
         when(field.getName()).thenReturn(name);
         when(field.getParent()).thenReturn(this);
         when(field.getCardinality()).thenReturn(FieldCardinality.SINGLE);
         when(field.getType()).thenReturn(data);
         outputs.add(field);
         return field;
      }

      /**
       * Creates a mocked pub sub {@link IScenario} with the given name, receiving field, and publishing field and adds it to this model's scenarios. This method also adds the reference field types to
       * this model's the inputs and outputs.
       *
       * @param name name of scenario
       * @param receiving receiving field
       * @param publishing publishing field
       * @param correlations pairs of correlation statements
       * @return the mocked pub sub {@link IScenario}
       */
      public IScenario addPubSub(String name, IDataReferenceField receiving, IDataReferenceField publishing,
               String... correlations) {
         return addPubSub(name, 0, 1, 1, receiving, publishing, correlations);
      }

      /**
       * Creates a mocked pub sub {@link IScenario} with the given name, receiving field, and publishing field and adds it to this model's scenarios. This method creates mocked
       * {@link IDataReferenceField} with the given names, given types, and {@link FieldCardinality#SINGLE} and adds the reference field types to
       * this model's the inputs and outputs.
       *
       * @param name name of scenario
       * @param receivingName name of receiving field
       * @param receiving type of receiving field
       * @param publishingName name of publishing field
       * @param publishing type of publishing field
       * @param correlations pairs of correlation statements
       * @return the mocked pub sub {@link IScenario}
       */
      public IScenario addPubSub(String name, String receivingName, IData receiving, String publishingName,
               IData publishing, String... correlations) {
         assertEquals("Correlations must be given in pairs", 0, correlations.length % 2);
         IScenario scenario = addPubSub(name, 0, 1, 1, receivingName, receiving, publishingName, publishing);
         for (int i = 0; i < correlations.length; i += 2) {
            correlate(name, correlations[i], correlations[i + 1]);
         }
         return scenario;
      }

      /**
       * Creates a mocked pub sub {@link IScenario} with the given name.
       *
       * <p>
       * The step parameters supplied should be ordered by given statements first, followed by when statement, followed by then
       * statements. The step parameters can either be {@link IDataReferenceField} or a pair of {@link String} followed by {@link IData}.
       * <p>
       * The given and when steps will be treated as {@link ReceiveStepHandler receive} steps, and then steps will be treated as {@link PublishStepHandler publish} steps.
       *
       * <p>
       * This method adds the scenario to this model's scenarios and data types to this model's inputs and outputs.
       *
       * @param name
       * @param givens number of given steps
       * @param whens number of when steps
       * @param thens number of then steps
       * @param stepParameters steps
       * @return the mocked pub sub {@link IScenario}
       */
      public IScenario addPubSub(String name, int givens, int whens, int thens, Object... stepParameters) {
         IScenario scenario = mock(IScenario.class);
         when(scenario.getName()).thenReturn(name);
         when(scenario.getParent()).thenReturn(this);
         when(scenario.getSteps(any(), any())).thenCallRealMethod();

         List<IScenarioStep> steps = new ArrayList<>(givens + whens + thens);
         List<IDataReferenceField> references = new ArrayList<>();
         for (int n = 0; n < stepParameters.length; n++) {
            IDataReferenceField reference;
            if (stepParameters[n] instanceof String) {
               assertNotEquals(n + 1, stepParameters.length);
               assertTrue(stepParameters[n + 1] + " is not of type IData", stepParameters[n + 1] instanceof IData);
               reference = mock(IDataReferenceField.class);
               when(reference.getCardinality()).thenReturn(FieldCardinality.SINGLE);
               when(reference.getName()).thenReturn((String) stepParameters[n]);
               when(reference.getParent()).thenReturn(this);
               when(reference.getType()).thenReturn((IData) stepParameters[n + 1]);
               n++;
            } else if (stepParameters[n] instanceof IDataReferenceField) {
               reference = (IDataReferenceField) stepParameters[n];
            } else {
               throw new AssertionError("Unknown type for pub sub scenario step: " + stepParameters[n]);
            }
            references.add(reference);
         }
         assertEquals(givens + whens + thens, references.size());

         references.forEach(reference -> {
            IScenarioStep step = mock(IScenarioStep.class);
            when(step.getParent()).thenReturn(scenario);
            String referenceName = reference.getName();
            when(step.getParameters()).thenReturn(Collections.singletonList(referenceName));
            steps.add(step);
         });

         steps.subList(0, givens).forEach(step -> {
            when(step.getKeyword()).thenReturn(ReceiveStepHandler.PAST.getVerb());
         });

         steps.subList(givens, givens + whens).forEach(step -> {
            when(step.getKeyword()).thenReturn(ReceiveStepHandler.PRESENT.getVerb());
         });

         steps.subList(givens + whens, givens + whens + thens).forEach(step -> {
            when(step.getKeyword()).thenReturn(PublishStepHandler.FUTURE.getVerb());
         });

         when(scenario.getGivens()).thenReturn(new ArrayList<>(steps.subList(0, givens)));
         when(scenario.getWhens()).thenReturn(new ArrayList<>(steps.subList(givens, givens + whens)));
         when(scenario.getThens()).thenReturn(new ArrayList<>(steps.subList(givens + whens, givens + whens + thens)));

         scenarios.add(scenario);
         inputs.addAll(references.subList(0, givens + whens));
         outputs.addAll(references.subList(givens + whens, givens + whens + thens));

         return scenario;
      }

      /**
       * Adds a correlation to the given scenario.
       *
       * @param scenario name of scenario
       * @param from first correlation statement
       * @param to second correlation statement
       */
      public void correlate(String scenario, String from, String to) {
         IScenario sc = this.getScenarios().getByName(scenario).orElseThrow(
            () -> new AssertionError("Scenario " + scenario + " has not been defined"));
         String[] first = from.split("\\.");
         String[] second = to.split("\\.");
         assertTrue("Invalid correlation: " + from, first.length > 1);
         assertTrue("Invalid correlation: " + to, second.length > 1);
         IScenarioStep correlationStep = mock(IScenarioStep.class);
         when(correlationStep.getParameters()).thenReturn(Arrays.asList(from, "to", to));
         when(correlationStep.getParent()).thenReturn(sc);
         if (inputs.getByName(first[0]).isPresent() && inputs.getByName(second[0]).isPresent()) {
            when(correlationStep.getKeyword()).thenReturn(CorrelateStepHandler.PRESENT.getVerb());
            sc.getWhens().add(correlationStep);
         } else {
            when(correlationStep.getKeyword()).thenReturn(CorrelateStepHandler.FUTURE.getVerb());
            sc.getThens().add(correlationStep);
         }
      }

      @Override
      public INamedChildCollection<IModel, IDataReferenceField> getInputs() {
         return inputs;
      }

      @Override
      public INamedChildCollection<IModel, IDataReferenceField> getOutputs() {
         return outputs;
      }

      @Override
      public INamedChildCollection<IModel, IScenario> getScenarios() {
         return scenarios;
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public IPackage getParent() {
         return parent;
      }

      @Override
      public String getFullyQualifiedName() {
         return parent.getName() + '.' + getName();
      }

      @Override
      public IMetadata getMetadata() {
         return null;
      }

      @Override
      public IModel setMetadata(IMetadata metadata) {
         throw new UnsupportedOperationException();
      }

      @Override
      public INamedChildCollection<IModel, IModelReferenceField> getRequiredModels() {
         return mockedNamedCollectionOf();
      }

      @Override
      public INamedChildCollection<IModel, IModelReferenceField> getParts() {
         return mockedNamedCollectionOf();
      }

      @Override
      public Collection<IModelLink<?>> getLinks() {
         return Collections.emptySet();
      }

      @Override
      public Optional<IModelLink<?>> getLinkByName(String name) {
         return Optional.empty();
      }
   }

   /**
    * Returns a mock of the given {@link INamedChild} class. This method will mock out the name, the parent's name, and the fullyQualified name.
    *
    * @param cls INamed child instance class
    * @param fullyQualifiedName fully qualified name
    * @return a mock of the given {@link INamedChild} class
    */
   public static <T extends INamedChild<IPackage>> T getMockNamedChild(Class<? extends T> cls,
            String fullyQualifiedName) {
      T data = mock(cls);
      int index = fullyQualifiedName.lastIndexOf('.');
      assertTrue(fullyQualifiedName + " is not a fully qualified name", index >= 0);
      when(data.getName()).thenReturn(fullyQualifiedName.substring(index + 1));
      when(data.getParent()).thenReturn(mock(IPackage.class));
      when(data.getParent().getName()).thenReturn(fullyQualifiedName.substring(0, index));
      if (data instanceof IData) {
         when(((IData) data).getFullyQualifiedName()).thenReturn(fullyQualifiedName);
      } else if (data instanceof IEnumeration) {
         when(((IEnumeration) data).getFullyQualifiedName()).thenReturn(fullyQualifiedName);
      } else if (data instanceof IModel) {
         when(((IModel) data).getFullyQualifiedName()).thenReturn(fullyQualifiedName);
      }
      return data;
   }

   /**
    * Adds mocking to the given data for the given superData type and fields. Each data field can be an {@link IDataField} or the fields can be specified with the parameters as follows: [String]
    * [FieldCardinality] (IData|IEnumeration|DataTypes). That is, an optional String for the field name, an optional cardinality, and either an IData, IEnumeration or DataTypes instance.
    *
    * @param data mocked data instance
    * @param superData super data type (can be null)
    * @param fields data fields
    */
   public static void mockData(IData data, IData superData, Object... fields) {
      when(data.getExtendedDataType()).thenReturn(Optional.ofNullable(superData));
      NamedChildCollection<IData, IDataField> collection = new NamedChildCollection<>(data, IDataField.class);
      for (int n = 0; n < fields.length; n++) {
         if (fields[n] instanceof IDataField) {
            collection.add((IDataField) fields[n]);
         } else if (fields[n] instanceof String) {
            assertNotEquals(n + 1, fields.length);
            Object o = fields[n + 1];
            if (o instanceof FieldCardinality) {
               collection.addDataField((String) fields[n], fields[n + 2], o == FieldCardinality.SINGLE);
               n += 2;
            } else {
               collection.addDataField((String) fields[n], fields[n + 1]);
               n++;
            }
         } else {
            Object o = fields[n + 1];
            if (o instanceof FieldCardinality) {
               collection.addDataField("field_" + n, fields[n + 1], o == FieldCardinality.SINGLE);
               n++;
            } else {
               collection.addDataField("field_" + n, fields[n]);
            }
         }
      }
      when(data.getFields()).thenReturn(collection);
   }

   /**
    * Returns a fully-implemented {@link INamedChildCollection} containing the given children.
    *
    * @param children collection of elements
    * @return an {@link INamedChildCollection} containing the given children
    */
   @SafeVarargs
   public static <P, T extends INamedChild<P>> INamedChildCollection<P, T> mockedNamedCollectionOf(T... children) {
      Map<String, T> map = new TreeMap<>();
      for (T child : children) {
         map.put(child.getName(), child);
      }
      class NamedChildCollectionInstance extends AbstractCollection<T> implements INamedChildCollection<P, T> {
         @Override
         public Optional<T> getByName(String name) {
            return Optional.of(map.get(name));
         }

         @Override
         public Iterator<T> iterator() {
            return map.values().iterator();
         }

         @Override
         public int size() {
            return map.size();
         }
      }
      return new NamedChildCollectionInstance();
   }

   /**
    * A {@link INamedChildCollection} instance with helper methods for easily adding fields.
    *
    * @param <P> parent type
    * @param <T> collection element type
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static class NamedChildCollection<P extends INamedChild<?>, T extends INamedChild<P>>
            extends AbstractCollection<T>
            implements INamedChildCollection<P, T> {

      private final Map<String, T> map = new LinkedHashMap<>();
      private final P parent;
      private final Class<? extends T> childClass;

      public NamedChildCollection(P parent, Class<? extends T> childClass) {
         this.parent = parent;
         this.childClass = childClass;
      }

      private <I extends INamedChild<?>> I getMock(String name, Class<? extends I> expectedChildClass) {
         assertTrue(childClass + " cannot be assigned to " + expectedChildClass,
            expectedChildClass.isAssignableFrom(childClass));
         I value = (I) mock(childClass);
         when(value.getName()).thenReturn(name);
         when(((INamedChild) value).getParent()).thenReturn(parent);
         return value;
      }

      /**
       * Creates a mocked {@link IDataField} with the given name and {@link FieldCardinality#SINGLE} and adds it to this collection. The value can either be an {@link IData} instance, an
       * {@link IEnumeration} instance, or a {@link DataTypes}.
       *
       * @param name name of the field
       * @param value an IData, IEnumeration, or DataTypes
       * @return a mocked {@link IDataField}
       * @throws AssertionError if this is not a collection of {@link IDataField}
       */
      public T addDataField(String name, Object value) {
         return addDataField(name, value, true);
      }

      /**
       * Creates a mocked {@link IDataField} with the given name and cardinality and adds it to this collection. The value can either be an {@link IData} instance, an {@link IEnumeration} instance, or
       * a {@link DataTypes}.
       *
       * @param name name of the field
       * @param value an IData, IEnumeration, or DataTypes
       * @param single if true, the cardinality will be {@link FieldCardinality#SINGLE}
       * @return a mocked {@link IDataField}
       * @throws AssertionError if this is not a collection of {@link IDataField}
       */
      public T addDataField(String name, Object value, boolean single) {
         IDataField field = getMock(name, IDataField.class);
         if (value instanceof IData) {
            when(field.getType()).thenReturn(DataTypes.DATA);
            when(field.getReferencedDataType()).thenReturn((IData) value);
         } else if (value instanceof IEnumeration) {
            when(field.getType()).thenReturn(DataTypes.ENUM);
            when(field.getReferencedEnumeration()).thenReturn((IEnumeration) value);
         } else if (value instanceof DataTypes) {
            when(field.getType()).thenReturn((DataTypes) value);
         }
         when(field.getCardinality()).thenReturn(single ? FieldCardinality.SINGLE : FieldCardinality.MANY);
         map.put(name, (T) field);
         return (T) field;
      }

      /**
       * Creates a mocked {@link IDataReferenceField} with the given name, data, and {@link FieldCardinality#SINGLE}.
       *
       * @param name name of the field
       * @param data data reference
       * @return a mocked {@link IDataReferenceField}
       * @throws AssertionError if this is not a collection of {@link IDataReferenceField}
       */
      public T addDataReference(String name, IData data) {
         return addDataReference(name, data, true);
      }

      /**
       * Creates a mocked {@link IDataReferenceField} with the given name, data, and cardinality.
       *
       * @param name name of the field
       * @param data data reference
       * @param single if true, the cardinality will be {@link FieldCardinality#SINGLE}
       * @return a mocked {@link IDataReferenceField}
       * @throws AssertionError if this is not a collection of {@link IDataReferenceField}
       */
      public T addDataReference(String name, IData data, boolean single) {
         IDataReferenceField field = getMock(name, IDataReferenceField.class);
         when(field.getCardinality()).thenReturn(single ? FieldCardinality.SINGLE : FieldCardinality.MANY);
         when(field.getType()).thenReturn(data);
         map.put(name, (T) field);
         return (T) field;
      }

      /**
       * Creates a mocked {@link IModelReferenceField} with the given name and model.
       *
       * @param name name of the field
       * @param model model reference
       * @return a mocked {@link IModelReferenceField}
       * @throws AssertionError if this is not a collection of {@link IModelReferenceField}
       */
      public T addModelReference(String name, IModel model) {
         IModelReferenceField field = getMock(name, IModelReferenceField.class);
         when(field.getType()).thenReturn(model);
         map.put(name, (T) field);
         return (T) field;
      }

      @Override
      public boolean add(T element) {
         map.put(element.getName(), element);
         return true;
      }

      @Override
      public Optional<T> getByName(String name) {
         return Optional.ofNullable(map.get(name));
      }

      @Override
      public Iterator<T> iterator() {
         return map.values().iterator();
      }

      @Override
      public int size() {
         return map.size();
      }

      private String toString(T element) {
         if (element instanceof IDataField) {
            switch (((IDataField) element).getType()) {
            case DATA:
               return ((IDataField) element).getReferencedDataType().getFullyQualifiedName() + ' ' + element.getName();
            case ENUM:
               return ((IDataField) element).getReferencedEnumeration().getFullyQualifiedName() + ' '
                  + element.getName();
            default:
               return ((IDataField) element).getType().name().toLowerCase() + ' ' + element.getName();
            }
         } else if (element instanceof IDataReferenceField) {
            return ((IDataReferenceField) element).getType().getFullyQualifiedName() + ' ' + element.getName();
         } else if (element instanceof IModelReferenceField) {
            return ((IModelReferenceField) element).getType().getFullyQualifiedName() + ' ' + element.getName();
         } else {
            return element.getParent().getName() + '.' + element.getName();
         }
      }

      @Override
      public String toString() {
         return "INamedCollection<" + parent.getName() + ">["
            + this.stream().map(this::toString).collect(Collectors.joining(", ")) + ']';
      }

   }

}
