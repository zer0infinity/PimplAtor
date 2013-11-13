package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ISelection;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

@SuppressWarnings("restriction")
public class SelectionHelperTest {

	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();
	
	public void testIsSelectionOnExpression() {
		final ISelection selection = context.mock(ISelection.class);
		final IASTNode expression = context.mock(IASTNode.class);
		context.checking(new Expectations() {{
			oneOf(selection);
			allowing(expression).getNodeLocations();will(returnValue(IASTNodeLocation.class));
			allowing(expression).getFileLocation();will(returnValue(IASTFileLocation.class));
		}});
		
		Region textSelection = SelectionHelper.getRegion(selection);
		assertNotNull(textSelection);
		boolean res = org.eclipse.cdt.internal.ui.refactoring.introducepimpl.SelectionHelper.isSelectionOnExpression(textSelection, expression);
		assertFalse(res);
	}
}
