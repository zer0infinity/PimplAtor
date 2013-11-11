package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.jface.text.Region;

public class SelectionHelper {
	public static boolean isSelectionOnExpression(Region textSelection, IASTNode expression) {
		Region exprPos = createExpressionPosition(expression);
		int selStart = textSelection.getOffset();
		int selEnd = textSelection.getLength() + selStart;
		return exprPos.getOffset()+exprPos.getLength() >= selStart && exprPos.getOffset() <= selEnd;
	}
	
	protected static Region createExpressionPosition(IASTNode expression) {
		int start = 0;
		int nodeLength = 0;
		IASTNodeLocation[] nodeLocations = expression.getNodeLocations();
		if (nodeLocations.length != 1) {
			for (IASTNodeLocation location : nodeLocations) {
				if (location instanceof IASTMacroExpansionLocation) {
					IASTMacroExpansionLocation macroLoc = (IASTMacroExpansionLocation) location;
					start = macroLoc.asFileLocation().getNodeOffset();
					nodeLength = macroLoc.asFileLocation().getNodeLength();
				}
			}
		} else {
			if (nodeLocations[0] instanceof IASTMacroExpansionLocation) {
				IASTMacroExpansionLocation macroLoc = (IASTMacroExpansionLocation) nodeLocations[0];
				start = macroLoc.asFileLocation().getNodeOffset();
				nodeLength = macroLoc.asFileLocation().getNodeLength();
			} else {
				IASTFileLocation loc = expression.getFileLocation();
				start = loc.getNodeOffset();
				nodeLength = loc.getNodeLength();
			}
		}
		return new Region(start, nodeLength);
	}
}
