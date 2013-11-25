package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.SelectionHelper;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

@SuppressWarnings("restriction")
public class SelectionHelperTest extends MockObjectTestCase {

	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();
	
	public void testIsSelectionOnExpression() {
		final IASTNode expression = context.mock(IASTNode.class);
		context.checking(new Expectations() {{
			oneOf(expression).getNodeLocations();
			allowing(expression).getFileLocation().getNodeOffset();will(returnValue(Integer.class));
			allowing(expression).getFileLocation().getNodeLength();will(returnValue(Integer.class));
		}});
		int length = 0;
		int offset = 0;
		ITextSelection selection = new TextSelection(offset, length); 
		assertNotNull(selection);
		assertEquals(length, selection.getLength());
		assertEquals(offset, selection.getOffset());
		Region region = org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper.getRegion(selection);
		assertNotNull(region);
		boolean res = SelectionHelper.isSelectionOnExpression(region, expression);
		assertTrue(res);
	}
}
