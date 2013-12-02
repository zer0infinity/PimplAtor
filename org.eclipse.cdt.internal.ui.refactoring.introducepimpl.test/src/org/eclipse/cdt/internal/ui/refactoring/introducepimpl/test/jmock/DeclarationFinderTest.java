package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTranslationUnit;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.DeclarationFinder;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

@SuppressWarnings("restriction")
public class DeclarationFinderTest extends MockObjectTestCase {
	
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	public void testFindDeclarationInTranslationUnit() {
		IASTTranslationUnit tu = new CPPASTTranslationUnit();
		assertNotNull(tu);
		final IIndexName indexName = context.mock(IIndexName.class);
		context.checking(new Expectations() {{
			oneOf(indexName);
			allowing(indexName).getNodeOffset(); will(returnValue(String.class));
			allowing(indexName).getNodeLength(); will(returnValue(Integer.class));
			allowing(indexName).getFileLocation().getFileName(); will(returnValue(String.class));
		}});
		IASTName name = DeclarationFinder.findDeclarationInTranslationUnit(tu, indexName);
		assertNull(name);
	}
}
