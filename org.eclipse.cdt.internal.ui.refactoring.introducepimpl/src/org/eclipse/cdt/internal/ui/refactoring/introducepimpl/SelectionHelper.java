package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.jface.text.Region;

public class SelectionHelper {
	public static boolean isSelectionOnExpression(Region textSelection, IASTNode expression) {
		int start = 0;
		int nodeLength = 0;
		IASTNodeLocation[] nodeLocations = expression.getNodeLocations();
		Region exprPos = new Region(start, nodeLength);
		if (nodeLocations.length != 1) {
			for (int i = nodeLocations.length-1; 0 <= i; i--) {
				if (nodeLocations[i] instanceof IASTMacroExpansionLocation) {
					exprPos = getRegion(nodeLocations[i]);
					break;
				}
			}
		} else if (nodeLocations[0] instanceof IASTMacroExpansionLocation) {
			exprPos = getRegion(nodeLocations[0]);
		} else {
			start = expression.getFileLocation().getNodeOffset();
			nodeLength = expression.getFileLocation().getNodeLength();
			exprPos = new Region(start, nodeLength);
		}
		int selStart = textSelection.getOffset();
		int selEnd = textSelection.getLength() + selStart;
		return exprPos.getOffset() + exprPos.getLength() >= selStart
				&& exprPos.getOffset() <= selEnd;
	}

	private static Region getRegion(IASTNodeLocation nodeLocations) {
		int start = nodeLocations.asFileLocation().getNodeOffset();
		int nodeLength = nodeLocations.asFileLocation().getNodeLength();
		return new Region(start, nodeLength);
	}
}
