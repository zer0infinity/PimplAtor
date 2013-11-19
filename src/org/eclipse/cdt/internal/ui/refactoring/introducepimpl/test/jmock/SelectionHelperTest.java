package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

@SuppressWarnings("restriction")
public class SelectionHelperTest extends MockObjectTestCase {

	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();
	
	public void testIsSelectionOnExpression() {
		final ITextSelection selection = context.mock(ITextSelection.class);
		final IASTNode expression = context.mock(IASTNode.class);
		context.checking(new Expectations() {{
			oneOf(selection).getOffset();
			oneOf(selection).getLength();
			oneOf(expression).getNodeLocations();
			allowing(expression).getFileLocation().getNodeOffset();will(returnValue(Integer.class));
			allowing(expression).getFileLocation().getNodeLength();will(returnValue(Integer.class));
		}});
		Region region = SelectionHelper.getRegion(selection);
		assertNotNull(region);
		boolean res = org.eclipse.cdt.internal.ui.refactoring.introducepimpl.SelectionHelper.isSelectionOnExpression(region, expression);
		assertTrue(res);
	}
}
