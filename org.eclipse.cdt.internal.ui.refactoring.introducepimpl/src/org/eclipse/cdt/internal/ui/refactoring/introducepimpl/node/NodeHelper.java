package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.node;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;

public class NodeHelper {
	public static boolean isConstructor(ICPPASTFunctionDefinition function) {
		boolean isConstructor = isStructor(function, false);
		if (isConstructor){
			return !isCopyConstructor(function);
		} else {
			return false;
		}
	}

	public static boolean isDestructor(ICPPASTFunctionDefinition function) {
		return isStructor(function, true);
	}

	private static boolean isStructor(ICPPASTFunctionDefinition function, boolean isDestructor) {
		if (function.getDeclSpecifier() instanceof ICPPASTSimpleDeclSpecifier) {
			IASTName name = function.getDeclarator().getName();
			if (name instanceof ICPPASTQualifiedName) {
				name = ((ICPPASTQualifiedName) name).getLastName();
			}
			return ((ICPPASTSimpleDeclSpecifier) function.getDeclSpecifier()).getType() == ICPPASTSimpleDeclSpecifier.sc_unspecified
					&& name.toString().startsWith("~") == isDestructor
					&& ((ICPPASTSimpleDeclSpecifier) function.getDeclSpecifier()).getStorageClass() == IASTDeclSpecifier.sc_unspecified;
		}
		return false;
	}

	public static boolean isEmtypDeclarator(IASTDeclaration node) {
		if (node instanceof IASTSimpleDeclaration) {
			return ((IASTSimpleDeclaration) node).getDeclarators().length == 0;
		}
		return false;
	}

	public static boolean isFunctionDeclarator(IASTDeclaration node) {
		if (node instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) node;
			return (simpleDecl.getDeclarators() != null && simpleDecl.getDeclarators().length > 0 && simpleDecl
					.getDeclarators()[0] instanceof ICPPASTFunctionDeclarator);
		}
		return false;
	}

	public static boolean isStatic(IASTNode node) {
		if (node instanceof IASTSimpleDeclaration) {
			return ((IASTSimpleDeclaration) node).getDeclSpecifier().getStorageClass() == IASTSimpleDeclSpecifier.sc_static;
		} else if (node instanceof ICPPASTFunctionDefinition) {
			return ((ICPPASTFunctionDefinition) node).getDeclSpecifier().getStorageClass() == IASTSimpleDeclSpecifier.sc_static;
		}
		return false;
	}

	public static boolean isCopyConstructor(ICPPASTFunctionDefinition memberDefinition) {
		if (isStructor(memberDefinition, false)) {
			ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) memberDefinition.getDeclarator();
			String className;
			if (functionDeclarator.getName() instanceof ICPPASTQualifiedName){
				className = functionDeclarator.getName().getLastName().toString();
			} else {
				className = functionDeclarator.getName().toString();
			}
			IASTParameterDeclaration[] parameters = functionDeclarator.getParameters();
			if (parameters.length == 1) {
				IASTParameterDeclaration parameter = parameters[0];
				if (parameter.getDeclSpecifier() instanceof ICPPASTNamedTypeSpecifier) {
					ICPPASTNamedTypeSpecifier parameterSpecifier = (ICPPASTNamedTypeSpecifier) parameter
							.getDeclSpecifier();
					if (parameterSpecifier.isConst() && hasPointerOperator(parameter)
							&& isPointerOperatorReference(parameter.getDeclarator().getPointerOperators()[0])) {
						if (parameterSpecifier.getName().toString().equals(className)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean hasPointerOperator(IASTParameterDeclaration parameter) {
		return parameter.getDeclarator().getPointerOperators().length == 1;
	}
	
	private static boolean isPointerOperatorReference(IASTPointerOperator pointer){
		return pointer instanceof ICPPASTReferenceOperator;
	}
}
