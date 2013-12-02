package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.junittest;

import junit.framework.TestCase;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReferenceOperator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.node.NodeHelper;
import org.junit.Test;

@SuppressWarnings("restriction")
public class NodeHelperTest extends TestCase {

	private ICPPASTFunctionDefinition createConstructor(String classname, int type, boolean qualifiedname) {
		CPPASTFunctionDefinition functionDef = new CPPASTFunctionDefinition();
		CPPASTFunctionDeclarator functionDec = new CPPASTFunctionDeclarator();
		IASTName name = new CPPASTName(classname.toCharArray());
		if (qualifiedname) {
			CPPASTQualifiedName qname = new CPPASTQualifiedName();
			qname.addName(new CPPASTName(classname.toCharArray()));
			qname.addName(new CPPASTName(classname.toCharArray()));
			name = qname;
		}
		functionDec.setName(name);
		functionDef.setDeclarator(functionDec);
		CPPASTSimpleDeclSpecifier simpleDecl = new CPPASTSimpleDeclSpecifier();
		simpleDecl.setType(type);
		functionDef.setDeclSpecifier(simpleDecl);
		return functionDef;
	}

	@Test
	public void testIsConstructorDefinition() {
		assertFalse(NodeHelper.isConstructor(createConstructor("Constructor", ICPPASTSimpleDeclSpecifier.t_void, true)));

		assertTrue(NodeHelper.isConstructor(createConstructor("Constructor", ICPPASTSimpleDeclSpecifier.sc_unspecified,
				false)));

		assertFalse(NodeHelper.isConstructor(createConstructor("~Destructor",
				ICPPASTSimpleDeclSpecifier.sc_unspecified, true)));
		
		ICPPASTFunctionDefinition copyConstructor = createConstructor("CopyConstructor", ICPPASTSimpleDeclSpecifier.sc_unspecified, true);
		CPPASTParameterDeclaration correctParameter = new CPPASTParameterDeclaration();
		CPPASTNamedTypeSpecifier paramSpec = new CPPASTNamedTypeSpecifier(new CPPASTName("CopyConstructor".toCharArray()));
		paramSpec.setConst(true);
		correctParameter.setDeclSpecifier(paramSpec);
		CPPASTDeclarator paramDec = new CPPASTDeclarator(new CPPASTName("toCopy".toCharArray()));		
		paramDec.addPointerOperator(new CPPASTReferenceOperator(false));
		correctParameter.setDeclarator(paramDec);
		ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) copyConstructor.getDeclarator();
		functionDeclarator.addParameterDeclaration(correctParameter);
		copyConstructor.setDeclarator(functionDeclarator);
		assertFalse(NodeHelper.isConstructor(copyConstructor));
	}

	@Test
	public void testIsDestructorDefinition() {
		assertFalse(NodeHelper.isDestructor(createConstructor("Constructor", ICPPASTSimpleDeclSpecifier.t_void, false)));

		assertFalse(NodeHelper.isDestructor(createConstructor("Constructor", ICPPASTSimpleDeclSpecifier.sc_unspecified,
				false)));

		assertTrue(NodeHelper.isDestructor(createConstructor("~Destructor", ICPPASTSimpleDeclSpecifier.sc_unspecified,
				true)));
	}

	@Test
	public void testIsCopyConstructorDefinition() {
		assertFalse(NodeHelper.isCopyConstructor(createConstructor("Constructor",
				ICPPASTSimpleDeclSpecifier.sc_unspecified, true)));

		ICPPASTFunctionDefinition wrongParameter = createConstructor("CopyConstructor",
				ICPPASTSimpleDeclSpecifier.sc_unspecified, true);
		CPPASTParameterDeclaration parameter = new CPPASTParameterDeclaration();
		parameter.setDeclSpecifier(new CPPASTNamedTypeSpecifier(new CPPASTName("int".toCharArray())));
		parameter.setDeclarator(new CPPASTDeclarator(new CPPASTName("index".toCharArray())));
		ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) wrongParameter.getDeclarator();
		functionDeclarator.addParameterDeclaration(parameter);
		wrongParameter.setDeclarator(functionDeclarator);
		assertFalse(NodeHelper.isCopyConstructor(wrongParameter));

		ICPPASTFunctionDefinition toManyParameters = createConstructor("CopyConstructor", ICPPASTSimpleDeclSpecifier.sc_unspecified, true);
		CPPASTParameterDeclaration parameter1 = new CPPASTParameterDeclaration();
		parameter1.setDeclSpecifier(new CPPASTNamedTypeSpecifier(new CPPASTName("int".toCharArray())));
		parameter1.setDeclarator(new CPPASTDeclarator(new CPPASTName("index".toCharArray())));
		CPPASTParameterDeclaration parameter2 = new CPPASTParameterDeclaration();
		parameter2.setDeclSpecifier(new CPPASTNamedTypeSpecifier(new CPPASTName("char".toCharArray())));
		parameter2.setDeclarator(new CPPASTDeclarator(new CPPASTName("sign".toCharArray())));
		functionDeclarator = (ICPPASTFunctionDeclarator) toManyParameters.getDeclarator();
		functionDeclarator.addParameterDeclaration(parameter1);
		functionDeclarator.addParameterDeclaration(parameter2);
		toManyParameters.setDeclarator(functionDeclarator);
		assertFalse(NodeHelper.isCopyConstructor(toManyParameters));
		
		ICPPASTFunctionDefinition copyConstructor = createConstructor("CopyConstructor", ICPPASTSimpleDeclSpecifier.sc_unspecified, true);
		CPPASTParameterDeclaration correctParameter = new CPPASTParameterDeclaration();
		CPPASTNamedTypeSpecifier paramSpec = new CPPASTNamedTypeSpecifier(new CPPASTName("CopyConstructor".toCharArray()));
		paramSpec.setConst(true);
		correctParameter.setDeclSpecifier(paramSpec);
		CPPASTDeclarator paramDec = new CPPASTDeclarator(new CPPASTName("toCopy".toCharArray()));		
		paramDec.addPointerOperator(new CPPASTReferenceOperator(false));
		correctParameter.setDeclarator(paramDec);
		functionDeclarator = (ICPPASTFunctionDeclarator) copyConstructor.getDeclarator();
		functionDeclarator.addParameterDeclaration(correctParameter);
		copyConstructor.setDeclarator(functionDeclarator);
		assertTrue(NodeHelper.isCopyConstructor(copyConstructor));
	}

	@Test
	public void testIsEmptyDeclarator() {
		// test empty declarator: ";"
		CPPASTSimpleDeclaration simpleDec = new CPPASTSimpleDeclaration();
		CPPASTSimpleDeclSpecifier decSpecifier = new CPPASTSimpleDeclSpecifier();
		simpleDec.setDeclSpecifier(decSpecifier);
		assertTrue(NodeHelper.isEmtypDeclarator(simpleDec));

		// test with constructor declaration: "Example();"
		CPPASTFunctionDeclarator functionDec = new CPPASTFunctionDeclarator();
		functionDec.setName(new CPPASTName("Example".toCharArray()));
		simpleDec.addDeclarator(functionDec);
		decSpecifier = new CPPASTSimpleDeclSpecifier();
		decSpecifier.setType(IASTSimpleDeclSpecifier.t_unspecified);
		decSpecifier.setStorageClass(IASTSimpleDeclSpecifier.sc_unspecified);
		simpleDec.setDeclSpecifier(decSpecifier);
		assertFalse(NodeHelper.isEmtypDeclarator(simpleDec));

		// test with veriable definition: "int index;"
		CPPASTFunctionDeclarator dec = new CPPASTFunctionDeclarator();
		dec.setName(new CPPASTName("index".toCharArray()));
		simpleDec.addDeclarator(dec);
		decSpecifier = new CPPASTSimpleDeclSpecifier();
		decSpecifier.setType(IASTSimpleDeclSpecifier.t_int);
		decSpecifier.setStorageClass(IASTSimpleDeclSpecifier.sc_unspecified);
		simpleDec.setDeclSpecifier(decSpecifier);
		assertFalse(NodeHelper.isEmtypDeclarator(simpleDec));
	}

	@Test
	public void testIsFunctionDeclarator() {
		CPPASTSimpleDeclaration simpleDec = new CPPASTSimpleDeclaration();
		assertFalse(NodeHelper.isFunctionDeclarator(simpleDec));

		CPPASTDeclarator dec = new CPPASTDeclarator();
		simpleDec.addDeclarator(dec);
		assertFalse(NodeHelper.isFunctionDeclarator(simpleDec));

		simpleDec = new CPPASTSimpleDeclaration();
		CPPASTFunctionDeclarator functionDec = new CPPASTFunctionDeclarator();
		simpleDec.addDeclarator(functionDec);
		assertTrue(NodeHelper.isFunctionDeclarator(simpleDec));
	}

	@Test
	public void testIsStatic() {
		CPPASTSimpleDeclaration simpleDec = new CPPASTSimpleDeclaration();
		CPPASTCompositeTypeSpecifier compTypeSpecifier = new CPPASTCompositeTypeSpecifier();
		compTypeSpecifier.setStorageClass(IASTSimpleDeclSpecifier.sc_unspecified);
		simpleDec.setDeclSpecifier(compTypeSpecifier);
		assertFalse(NodeHelper.isStatic(simpleDec));
		compTypeSpecifier.setStorageClass(IASTSimpleDeclSpecifier.sc_static);
		assertTrue(NodeHelper.isStatic(simpleDec));

		simpleDec = new CPPASTSimpleDeclaration();
		CPPASTSimpleDeclSpecifier simpleSpecifier = new CPPASTSimpleDeclSpecifier();
		simpleSpecifier.setStorageClass(IASTSimpleDeclSpecifier.sc_static);
		simpleDec.setDeclSpecifier(simpleSpecifier);
		assertTrue(NodeHelper.isStatic(simpleDec));

		CPPASTFunctionDefinition functionDefinition = new CPPASTFunctionDefinition();
		functionDefinition.setDeclSpecifier(simpleSpecifier);
		assertTrue(NodeHelper.isStatic(functionDefinition));
	}
}
