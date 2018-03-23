package com.ngc.seaside.systemdescriptor.tests.packages

import com.google.inject.Inject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.xtext.testing.util.ResourceHelper
import org.eclipse.emf.common.util.URI
import com.ngc.seaside.systemdescriptor.validation.SdIssueCodes
import com.ngc.seaside.systemdescriptor.tests.SystemDescriptorInjectorProvider

@RunWith(XtextRunner)
@InjectWith(SystemDescriptorInjectorProvider)
class PackageParsingTest {

	@Inject
	ParseHelper<Package> parseHelper

	@Inject
	ResourceHelper resourceHelper

	@Inject
	ValidationTestHelper validationTester

	@Test
	def void testDoesNotAllowEscapingPackageNames() {
		var source = '''
			package ^foo.datatypes
			
			data Foo {
				int i
				float f
			}
			
		'''

		var invalidResult = parseHelper.parse(source)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PACKAGE,
			null
		)

		source = '''
			package foo.^datatypes
			
			data Foo {
				int i
				float f
			}
			
		'''

		invalidResult = parseHelper.parse(source)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PACKAGE,
			null
		)
	}

	@Test
	def void testDoesRequirePackageAndFilePathToMatch() {
		val dateSourceValid = '''
			package com.ngc.seaside.common
			
			data Date {
				int day
				int month
				int year
			}
		''';

		val validDataResource = resourceHelper.resource(
			dateSourceValid,
			URI.createPlatformResourceURI("src/main/sd/com/ngc/seaside/common/datatypes.sd", false)
		)
		validationTester.assertNoIssues(validDataResource)

		val dateSourceInvalid1 = '''
			package com.ngc.seaside
			
			data Date {
				int day
				int month
				int year
			}
		''';

		val invalidDataResource1 = resourceHelper.resource(
			dateSourceInvalid1,
			URI.createPlatformResourceURI("src/main/sd/com/ngc/seaside/common/datatypes.sd", false)
		)
		validationTester.assertError(
			invalidDataResource1,
			SystemDescriptorPackage.Literals.PACKAGE,
			SdIssueCodes.MISMATCHED_PACKAGE
		)

		val dateSourceInvalid2 = '''
			package foo
			
			data Date {
				int day
				int month
				int year
			}
		''';

		val invalidDataResource2 = resourceHelper.resource(
			dateSourceInvalid2,
			URI.createPlatformResourceURI("src/main/sd/Foo.sd", false)
		)
		validationTester.assertError(
			invalidDataResource2,
			SystemDescriptorPackage.Literals.PACKAGE,
			SdIssueCodes.MISMATCHED_PACKAGE
		)
	}
}
