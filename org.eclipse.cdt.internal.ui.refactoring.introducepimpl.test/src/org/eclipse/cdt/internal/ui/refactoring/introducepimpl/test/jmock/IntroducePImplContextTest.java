package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTranslationUnit;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplContext;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplInformation;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplRefactoring;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

@SuppressWarnings("restriction")
public class IntroducePImplContextTest extends MockObjectTestCase {

	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();
	
	private IntroducePImplContext pContext;
	
	@Override
	protected void setUp() throws Exception {
		final ICElement element = context.mock(ICElement.class);
		final ISelection selection = context.mock(ISelection.class);
		context.checking(new Expectations() {{
			oneOf(element);
			oneOf(selection);
		}});
		IntroducePImplInformation info = new IntroducePImplInformation();
		IntroducePImplRefactoring refactoring = new IntroducePImplRefactoring(element, selection, info);
		pContext = new IntroducePImplContext(refactoring);
	}
	
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
		IASTName name = pContext.findDeclarationInTranslationUnit(tu, indexName);
		assertNull(name);
	}
	
	public void testIsSelectionOnExpression() {
		final IASTNode expression = context.mock(IASTNode.class);
		context.checking(new Expectations() {{
			oneOf(expression).getNodeLocations();
			allowing(expression).getFileLocation().getNodeOffset();will(returnValue(Integer.class));
			allowing(expression).getFileLocation().getNodeLength();will(returnValue(Integer.class));
		}});
		int length = 0;
		int offset = 0;
		ITextSelection textSelection = new TextSelection(offset, length); 
		assertNotNull(textSelection);
		assertEquals(length, textSelection.getLength());
		assertEquals(offset, textSelection.getOffset());
		Region region = SelectionHelper.getRegion(textSelection);
		assertNotNull(region);
		boolean res = pContext.isSelectionOnExpression(region, expression);
		assertTrue(res);
	}
}
