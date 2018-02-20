package com.ngc.seaside.systemdescriptor.tests.model

import com.google.inject.Inject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.util.ResourceHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import com.ngc.seaside.systemdescriptor.tests.SystemDescriptorInjectorProvider
import com.ngc.seaside.systemdescriptor.tests.resources.Models
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import com.ngc.seaside.systemdescriptor.tests.resources.Datas
import org.junit.Ignore

@RunWith(XtextRunner)
@InjectWith(SystemDescriptorInjectorProvider)
class RefinedModelParsingTest {
    @Inject
    ParseHelper<Package> parseHelper

    @Inject
    ResourceHelper resourceHelper

    @Inject
    ValidationTestHelper validationTester

    Resource requiredResources

    @Before
    def void setup() {
        requiredResources = Models.allOf(
            resourceHelper,
            Models.EMPTY_MODEL,
            Models.ALARM,
            Datas.TIME
        )
        validationTester.assertNoIssues(requiredResources)
    }

    @Test
    def void testDoesParseEmptyRefinedModelUsingFullyQualifiedName() {
        val source = '''
            package clocks.models

            model MyModel refines foo.AnEmptyModel {
            }
        '''

        var result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
    }

    @Test
    def void testDoesParseEmptyRefinedModelUsingImport() {
        val source = '''
            package clocks.models

            import foo.AnEmptyModel

            model MyModel refines AnEmptyModel {
            }
        '''

        var result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
    }

    //DON'T Parse test methods

    @Test
    def void testDoesNotParseModelThatRefinesItself() {
        val source = '''
            package clocks.models

            model MyModel refines clocks.models.MyModel {
            }
        '''

         var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)

        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.MODEL,
            null)
    }

	@Ignore("until I figure out how to implement the validator rule for this")
    @Test
    def void testDoesNotParseModelThatRefinesData() {
        val source = '''
            package clocks.models

            model MyModel refines clocks.datatypes.time {
            }
        '''

        var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)

        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.MODEL,
            null)
    }

	@Ignore("until I figure out how to implement the validator rule for this")
    @Test
    def void testDoesNotParseModelsThatCircularlyRefineEachOther() {
        val source = '''
            package clocks.models

            model MyModelB refines clocks.models.MyModelA {
            }

            package clocks.models

            model MyModelA refines clocks.models.MyModelB {
            }
        '''

        var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)

        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.MODEL,
            null)
    }

    @Test
    def void testDoesNotParseModelRedeclaring_Inputs() {
        val source = '''
            package clocks.models

            import clocks.models.part.Alarm
            import clocks.models.part.Timer

            model RefinedModel refines Alarm {
                input {
                    Timer timer
                }
            }
        '''

        var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)

        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.MODEL,
            null)
    }

    @Test
    def void testDoesNotParseModelRedeclaring_Outputs() {
        val source = '''
            package clocks.models

            import clocks.models.part.Alarm
            import clocks.models.part.Timer

            model RefinedModel refines Alarm {
                output {
                    Timer timer
                }
            }
        '''

        var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)

        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.MODEL,
            null)
    }

    @Test
    def void testDoesNotParseModelRedeclaring_Requires() {
        val source = '''
            package clocks.models

            import clocks.models.part.Alarm
            import clocks.models.part.Timer

            model RefinedModel refines Alarm {
                requires {
                    Timer timer
                }
            }
        '''

        var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)

        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.MODEL,
            null)
    }

    @Test
    def void testDoesNotParseModelRedeclaring_Scenarios() {
        val source = '''
            package clocks.models

            import clocks.models.part.Alarm

            model RefinedModel refines Alarm {
                scenario whatever {
                }
            }
        '''

        var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)

        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.MODEL,
            null)
    }
}
