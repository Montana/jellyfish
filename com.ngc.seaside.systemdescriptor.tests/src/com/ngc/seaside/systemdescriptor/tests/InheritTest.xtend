package com.ngc.seaside.systemdescriptor.tests

import com.google.inject.Inject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package
import org.eclipse.emf.common.util.URI
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.util.ResourceHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitiveDataFieldDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.ReferencedDataFieldDeclaration
import org.eclipse.emf.ecore.resource.ResourceSet
import org.junit.Before
import com.google.inject.Provider
import org.eclipse.xtext.resource.XtextResourceSet
import com.ngc.seaside.systemdescriptor.systemDescriptor.DataFieldDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.xtext.diagnostics.Severity

@RunWith(XtextRunner)
@InjectWith(SystemDescriptorInjectorProvider)
class InheritTest {

	@Inject
	Provider<XtextResourceSet> resourceSetProvider;

	@Inject
	ParseHelper<Package> parseHelper

	@Inject
	ResourceHelper resourceHelper

	@Inject
	ValidationTestHelper validationTester

	ResourceSet resources

	@Before
	def void setup() {
		resources = resourceSetProvider.get()
	}
	
	def void equalityFieldsTest(DataFieldDeclaration A, DataFieldDeclaration B){
		//Tests that the two fields have the same name, have the same metadata, and cardinality.

		assertEquals(
			"field names do not match!",
			A.name,
			B.name
		)
		assertEquals(
			"Cardinality does not match!",
			A.cardinality,
			B.cardinality
		)
		
		if(A.metadata != null){
			var members = A.metadata.getMembers();
	
			if(members.length >= 1){
				//If there are enough fields to test...
				assertEquals(
					"Metadata key does not match!",
					A.metadata.getMembers().get(0).getKey(),
					B.metadata.getMembers().get(0).getKey()
				)		
			} else {
				assertEquals(
					"Metadata number of members does not match!",
					A.metadata.getMembers().length,
					B.metadata.getMembers().length
				)
			}
		} else {
			assertEquals(
				"Metadata are not both null!",
				A.metadata,
				B.metadata
			)	
		}
		
		
	}
	
	def void equalityPrimitiveFieldsTest(PrimitiveDataFieldDeclaration A, PrimitiveDataFieldDeclaration B) {
		//Tests that the two fields have the same type and all checks of equalityFieldsTest.
		assertEquals(
			"Data types do not match!",
			A.type,
			B.type
		)
		
		equalityFieldsTest(A, B);
	}
	
	def void equalityReferenceFieldsTest(ReferencedDataFieldDeclaration A, ReferencedDataFieldDeclaration B){
		//Tests that the two fields have the same data and all checks of equalityFieldsTest.
		
		assertEquals(
			"Data does not match!",
			A.data,
			B.data
		)
		
		equalityFieldsTest(A, B);
	}

	@Test
	def void testInheritAccessible() {
		
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		//Test that b.value exists
		
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		assertEquals(
			"data superclass not correct!",
			dataA.name,
			dataB.superclass.name
		)
	}
	
	@Test
	def void testInheritFieldType() {
		
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value {"some metadata" : " description"}
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		//Test that B.value == A.value
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		var dataAFieldValue = dataA.fields.get(0) as PrimitiveDataFieldDeclaration
		var dataBFieldValue = dataB.superclass.fields.get(0) as PrimitiveDataFieldDeclaration
		assertEquals(
			"Type does not match!",
			dataAFieldValue.type,
			dataBFieldValue.type
		)	
	}
	
		@Test
	def void testInheritFieldName() {
		
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value {"some metadata" : " description"}
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		//Test that B.value == A.value
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		var dataAFieldValue = dataA.fields.get(0) as PrimitiveDataFieldDeclaration
		var dataBFieldValue = dataB.superclass.fields.get(0) as PrimitiveDataFieldDeclaration
		assertEquals(
			"Name does not match!",
			dataAFieldValue.name,
			dataBFieldValue.name
		)	
	}
	
	@Test
	def void testInheritFieldMetadata() {
		
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value {"some metadata" : " description"}
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		//Test that B.value == A.value
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		var dataAFieldValue = dataA.fields.get(0) as PrimitiveDataFieldDeclaration
		var dataBFieldValue = dataB.superclass.fields.get(0) as PrimitiveDataFieldDeclaration

		assertEquals(
				"Metadata key does not match!",
				dataAFieldValue.metadata.getMembers().get(0).getKey(),
				dataBFieldValue.metadata.getMembers().get(0).getKey()
		)
		assertEquals(
				"Metadata key does not match!",
				dataAFieldValue.metadata.getMembers().get(0).getKey(),
				"some metadata"
		)			
	}
	
	@Test
	def void testInheritFieldManyCardinality() {
		
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					many int value 
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		//Test that B.value == A.value
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		var dataAFieldValue = dataA.fields.get(0) as PrimitiveDataFieldDeclaration
		var dataBFieldValue = dataB.superclass.fields.get(0) as PrimitiveDataFieldDeclaration
		assertEquals(
			"Cardinality does not match!",
			dataAFieldValue.cardinality,
			dataBFieldValue.cardinality
		)
	}
	
	@Test
	def void testInheritPrimitiveField() {
		
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value {"some metadata" : " description"}
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		//Test that B.value == A.value
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		var dataAFieldValue = dataA.fields.get(0) as PrimitiveDataFieldDeclaration
		var dataBFieldValue = dataB.superclass.fields.get(0) as PrimitiveDataFieldDeclaration
		equalityFieldsTest(dataAFieldValue, dataBFieldValue); //A.val == B.val
	}
	
	
	@Test
	def void testInheritSuperSuper() {
		
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val dataCSource = '''
			package datathing.otherdatatypes
			
			import datathing.otherdatatypes.B

			data C : B {
			}
		''';
		
		val dataCResult = parseHelper.parse(dataCSource, resources)
		assertNotNull(dataCResult)
		validationTester.assertNoIssues(dataCResult)
		
		//test that B.value == A.value, C.value == A.value, B.value == C.value, B == A, C == A, B =\= C
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		var dataC = dataCResult.element as Data
		
		var dataAFieldValue = dataA.fields.get(0) as PrimitiveDataFieldDeclaration
		var dataBFieldValue = dataB.superclass.fields.get(0) as PrimitiveDataFieldDeclaration
		var dataCFieldValue = dataC.superclass.superclass.fields.get(0) as PrimitiveDataFieldDeclaration
		
		equalityFieldsTest(dataAFieldValue, dataBFieldValue); //A.val == B.val
		equalityFieldsTest(dataBFieldValue, dataCFieldValue); //B.val == C.val
		equalityFieldsTest(dataAFieldValue, dataCFieldValue); //A.val == C.val
	}

	@Test
	def void testInheritDataobjectFields() {
		
		val dateSource = '''
			package clocks.datatypes
			
			data Date {
				int day
				int month
				int year
			}
		''';
		
		val dateResource = resourceHelper.resource(dateSource, URI.createURI("datetypes.sd"), resources)
		validationTester.assertNoIssues(dateResource)
		
		val dataASource = '''
			package datathing.datatypes
			
			import clocks.datatypes.Date
			
			data A {
				Date date
			}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var dataA = dataAResult.element as Data
		var dataB = dataBResult.element as Data
		
		var dataAFieldValue = dataA.fields.get(0) as ReferencedDataFieldDeclaration
		var dataBFieldValue = dataB.superclass.fields.get(0) as ReferencedDataFieldDeclaration	
		equalityReferenceFieldsTest(dataAFieldValue, dataBFieldValue); //A.val == B.val
	}
	
	@Test
	def void testWarnBaseInheritedClassInput() {
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			
			model ModelAInput {
				
				input {
					A a
				}
			}
		'''

		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)
		validationTester.assertNoErrors(result)
				
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_DECLARATION,
			null)	
	}
	
	@Test
	def void testWarnBaseInheritedClassOutput() {
	val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			
			model ModelAOutput {
				
				output {
					A a
				}
			}
		''';
		
		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)
		validationTester.assertNoErrors(result)
	
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_DECLARATION,
			null)
				
		//Asserts and testing...
		//Test a warning is given when a base class is used in the input, output, scenario, etc.
		
	}
	
	@Test
	def void testWarnBaseInheritedClassLinkPartsWithOutput() {
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelPartsSource = '''
			package datathing.models
			
			import datathing.datatypes.A

			model Thing {
				output {
				  A a
				}
			}
		''';
				
		var modelPartsResource = resourceHelper.resource(modelPartsSource, URI.createURI("model.sd"), resources);
		validationTester.assertNoErrors(modelPartsResource) //No errors as we would see an issue/warning.
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			import datathing.models.Thing
			
			model ModelALink {
			  parts {
				Thing thing	
			  }
			  output {
			  	A a
			  }
			  links {
			    link a to thing.a
			  }
			 }
		'''

		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)	
		validationTester.assertNoErrors(result)
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_REFERENCE,
			null)	
			
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.LINKABLE_REFERENCE,
			null)
		//Asserts and testing...
		//Test a warning is given when a base class is used in the input, output, scenario, etc.
	}
	
	@Test
	def void testWarnBaseInheritedClassLinkRequiresWithOutput() {
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelPartsSource = '''
			package datathing.models
			
			import datathing.datatypes.A

			model Thing {
				output {
				  A a
				}
			}
		''';
				
		var modelPartsResource = resourceHelper.resource(modelPartsSource, URI.createURI("model.sd"), resources);
		validationTester.assertNoErrors(modelPartsResource) //No errors as we would see an issue/warning.
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			import datathing.models.Thing
			
			model ModelALink {
			  requires {
				Thing thing	
			  }
			  output {
			  	A a
			  }
			  links {
			    link a to thing.a
			  }
			 }
		'''

		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)	
		validationTester.assertNoErrors(result)
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_REFERENCE,
			null)	
			
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.LINKABLE_REFERENCE,
			null)
		//Asserts and testing...
		//Test a warning is given when a base class is used in the input, output, scenario, etc.
	}
	
	@Test
	def void testWarnBaseInheritedClassLinkPartsWithInput() {
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelPartsSource = '''
			package datathing.models
			
			import datathing.datatypes.A

			model Thing {
				input {
				  A a
				}
			}
		''';
				
		var modelPartsResource = resourceHelper.resource(modelPartsSource, URI.createURI("model.sd"), resources);
		validationTester.assertNoErrors(modelPartsResource) //No errors as we would see an issue/warning.
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			import datathing.models.Thing
			
			model ModelALink {
			  parts {
				Thing thing	
			  }
			  input {
			  	A a
			  }
			  links {
			    link a to thing.a
			  }
			 }
		'''

		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)	
		validationTester.assertNoErrors(result)
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_REFERENCE,
			null)	
			
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.LINKABLE_REFERENCE,
			null)	
		//Asserts and testing...
		//Test a warning is given when a base class is used in the input, output, scenario, etc.
	}
	
	@Test
	def void testWarnBaseInheritedClassLinkRequiresWithInput() {
		val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelPartsSource = '''
			package datathing.models
			
			import datathing.datatypes.A

			model Thing {
				input {
				  A a
				}
			}
		''';
				
		var modelPartsResource = resourceHelper.resource(modelPartsSource, URI.createURI("model.sd"), resources);
		validationTester.assertNoErrors(modelPartsResource) //No errors as we would see an issue/warning.
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			import datathing.models.Thing
			
			model ModelALink {
			  requires {
				Thing thing	
			  }
			  input {
			  	A a
			  }
			  links {
			    link a to thing.a
			  }
			 }
		'''

		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)	
		validationTester.assertNoErrors(result)
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_REFERENCE,
			null)	
			
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.LINKABLE_REFERENCE,
			null)	
		//Asserts and testing...
		//Test a warning is given when a base class is used in the input, output, scenario, etc.
	}
	
	@Test
	def void testWarnBaseInheritedClassScenarioInput() {
	val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			
			model ModelAScenario {
				input {
				  	A a
				}
				scenario scenA {
					when recieving a
					then something a
				}
			}
		'''

		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)
		validationTester.assertNoErrors(result)
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_DECLARATION,
			null)	
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.STEP,
			null);	
		//Asserts and testing...
		//Test a warning is given when a base class is used in the input, output, scenario, etc.	
	}
	
		@Test
	def void testWarnBaseInheritedClassScenarioOutput() {
	val dataASource = '''
				package datathing.datatypes
				
				data A {
					int value
				}
			''';
		
		val dataAResult = parseHelper.parse(dataASource, resources)
		assertNotNull(dataAResult)
		validationTester.assertNoIssues(dataAResult)
		
		var resourceA = resourceHelper.resource(dataASource, URI.createURI("A.sd"), resources);
		validationTester.assertNoIssues(resourceA)

		val dataBSource = '''
			package datathing.otherdatatypes
			
			import datathing.datatypes.A

			data B : A {
			}
		''';
		
		val dataBResult = parseHelper.parse(dataBSource, resources)
		assertNotNull(dataBResult)
		validationTester.assertNoIssues(dataBResult)
		
		var resourceB = resourceHelper.resource(dataBSource, URI.createURI("B.sd"), resources);
		validationTester.assertNoIssues(resourceB)
		
		val modelSource = '''
			package datathing.models
			
			import datathing.datatypes.A
			
			model ModelAScenario {
				output {
				  	A a
				}
				scenario scenA {
					given have a
					when recieving a
					then something a
				}
			}
		'''

		val result = parseHelper.parse(modelSource, resources)
		assertNotNull(result)
		validationTester.assertNoErrors(result)
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.FIELD_DECLARATION,
			null)	
		
		validationTester.assertWarning(
			result,
			SystemDescriptorPackage.Literals.STEP,
			null);	
		//Asserts and testing...
		//Test a warning is given when a base class is used in the input, output, scenario, etc.	
	}
}
