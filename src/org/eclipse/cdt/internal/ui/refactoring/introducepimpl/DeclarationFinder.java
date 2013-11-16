package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.internal.ui.refactoring.Container;
import org.eclipse.core.runtime.Path;

@SuppressWarnings("restriction")
public class DeclarationFinder {	
	
	public static IASTName findDeclarationInTranslationUnit(IASTTranslationUnit transUnit, final IIndexName indexName) {
		final Container<IASTName> defName = new Container<IASTName>();
		transUnit.accept(new ASTVisitor() {
			{
				shouldVisitNames = true;
			}

			@Override
			public int visit(IASTName name) {
				if (name.isDeclaration() && name.getNodeLocations().length > 0) {
					IASTNodeLocation nodeLocation = name.getNodeLocations()[0];
					if (indexName.getNodeOffset() == nodeLocation.getNodeOffset() 
							&& indexName.getNodeLength() == nodeLocation.getNodeLength()
							&& new Path(indexName.getFileLocation().getFileName()).equals(new Path(nodeLocation.asFileLocation().getFileName()))) {
						defName.setObject(name);
						return ASTVisitor.PROCESS_ABORT;
					}
				}
				return ASTVisitor.PROCESS_CONTINUE;
			}

		});
		return defName.getObject();
	}
}