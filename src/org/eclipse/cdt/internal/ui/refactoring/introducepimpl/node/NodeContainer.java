package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.node;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEditGroup;

public class NodeContainer <T extends IASTNode> {
	private T node;
	private ASTRewrite rewrite;
	
	public NodeContainer(T node, ASTRewrite rewrite){
		this.node = node;
		this.rewrite = rewrite;
	}
	
	public ASTRewrite insertBefore(IASTNode insertionPoint, IASTNode newNode, TextEditGroup editGroup){
			return rewrite.insertBefore(node, insertionPoint, newNode, editGroup);
	}
	
	public void remove(IASTNode node, TextEditGroup editGroup){
		rewrite.remove(node, editGroup);
	}
	
	public ASTRewrite replace(IASTNode node, IASTNode replacement, TextEditGroup editGroup){
		return rewrite.replace(node, replacement, editGroup);
	}
	
	public void setNode(T classSpecifier) {
		this.node = classSpecifier;
	}
	public T getNode() {
		return node;
	}
	public void setRewrite(ASTRewrite rewrite) {
		this.rewrite = rewrite;
	}
	public ASTRewrite getRewrite() {
		return rewrite;
	}
	
	
}
