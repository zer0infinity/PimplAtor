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
		if(0 < nodeLocations.length) {
			for(int i = nodeLocations.length; nodeLocations.length < i; i--) {
				if (nodeLocations[i] instanceof IASTMacroExpansionLocation) {
					IASTMacroExpansionLocation macroLoc = (IASTMacroExpansionLocation) nodeLocations[i];
					start = macroLoc.asFileLocation().getNodeOffset();
					nodeLength = macroLoc.asFileLocation().getNodeLength();
					break;
				}
			}
		} else {
			start = expression.getFileLocation().getNodeOffset();
			nodeLength = expression.getFileLocation().getNodeLength();
		}
		Region exprPos = new Region(start, nodeLength);
		int selStart = textSelection.getOffset();
		int selEnd = textSelection.getLength() + selStart;
		return exprPos.getOffset()+exprPos.getLength() >= selStart && exprPos.getOffset() <= selEnd;
	}
}
