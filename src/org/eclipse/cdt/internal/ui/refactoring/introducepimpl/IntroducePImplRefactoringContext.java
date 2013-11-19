package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeleteExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpressionList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBaseSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompoundStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTConstructorChainInitializer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTConstructorInitializer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeleteExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionList;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNewExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.text.edits.TextEditGroup;

@SuppressWarnings("restriction")
public class IntroducePImplRefactoringContext {
	
	private static final String NONCOPYABLE = "noncopyable";
	private static final String SHARED_PTR = "shared_ptr";
	private static final String UNIQUE_PTR = "unique_ptr";
	private static final String STD = "std";
	private static final String BOOST = "boost";
	private static final String COPY_PARAM_NAME = "toCopy";
	private static final String INCLUDE_LABEL = "#include ";
	private static final String BOOST_SHARED_PTR_INCLUDE = "<boost/shared_ptr.hpp>";
	private static final String SHARED_AND_UNIQUE_PTR_INCLUDE = "<memory>";
	private static final String BOOST_NONCOPYABLE_INCLUDE = "<boost/noncopyable.hpp>";
	private static final String MAKE_SHARED = "make_shared";
	
	private IntroducePImplInformation info;
	private IntroducePImplRefactoring refactoring;

	IntroducePImplRefactoringContext(IntroducePImplRefactoring refactoring, IntroducePImplInformation info) {
		this.refactoring = refactoring;
		this.info = info;
	}

	void handleFunctionDeclarator(IASTSimpleDeclaration simpleDeclaration,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode, NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) throws CoreException {
		ICPPASTFunctionDefinition functionDefinition = refactoring.getDefinitionOfDeclaration(simpleDeclaration);
		sourceClassNode.remove(functionDefinition, new TextEditGroup("Definition removed from source"));
		handleFunctionDefinition(functionDefinition.copy(), headerClassNode, sourceClassNode, implClassNode);
	}

	void handleDeclarator(IASTSimpleDeclaration simpleDeclaration,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		implClassNode.insertBefore(null, simpleDeclaration.copy(), new TextEditGroup("Copy Declarator to implFile"));
	}

	void handleStaticDeclarator(IASTSimpleDeclaration simpleDeclaration,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
		if (info.getActualOriginalVisibility() != ICPPASTVisibilityLabel.v_public) {
			info.getPrivateStaticList().add(simpleDeclaration.copy());
		} else {
			headerClassNode.insertBefore(null, simpleDeclaration.copy(), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_StaticFieldInsertHeader));
		}
	}

	void handleVisibilitiyLabel(ICPPASTVisibilityLabel visibilityLabel,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		insertImplVisibility(visibilityLabel.getVisibility(), implClassNode);
		info.setActualOriginalVisibility(visibilityLabel.getVisibility());
	}

	void handleFunctionDefinition(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode, NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		boolean isContructor = NodeHelper.isConstructor(functionDefinition);
		if (isContructor) {
			info.isConstructorInserted = isContructor;
			handleConstructor(functionDefinition, headerClassNode, sourceClassNode, implClassNode);
		} else if (NodeHelper.isDestructor(functionDefinition)) {
			handleImplDestructor(functionDefinition, implClassNode);
		} else if (NodeHelper.isCopyConstructor(functionDefinition)) {
			handleCopyConstructor(functionDefinition, implClassNode);
		} else {
			if (info.isNodeStatic) {
				handleStaticFunctionDefinition(functionDefinition, headerClassNode, sourceClassNode);
			} else {
				handleStandardFunctionDefintion(functionDefinition, headerClassNode, sourceClassNode, implClassNode);
			}
		}
	}

	private void handleConstructor(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode, NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		handleHeaderConstructor(functionDefinition, headerClassNode);
		handleSourceConstructor(functionDefinition, sourceClassNode);
		handleImplConstructor(functionDefinition, implClassNode);
	}

	private void handleHeaderConstructor(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
		IASTSimpleDeclaration headerDeclaration = NodeFactory.createDeclarationFromDefinition(functionDefinition);
		headerClassNode.insertBefore(null, headerDeclaration, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_ConstructerInsertHeader));
	}

	private void handleSourceConstructor(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<IASTNode> sourceClassNode) {
		IASTParameterDeclaration[] parameterList = ((ICPPASTFunctionDeclarator) functionDefinition.getDeclarator())
				.getParameters();
		ICPPASTFunctionDefinition newDefinition = NodeFactory.createConstructorDefinition(info.getClassSpecifier()
				.getName().toString(), true, parameterList);
		newDefinition.addMemberInitializer(createConstructorPImplInitializer(functionDefinition));
		newDefinition.setBody(new CPPASTCompoundStatement());
		sourceClassNode.insertBefore(null, newDefinition, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_ConstructorInsertSource));
	}

	private void handleImplConstructor(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		ICPPASTFunctionDefinition implDefinition = functionDefinition.copy();
		implDefinition.getDeclarator().setName(new CPPASTName(info.getClassNameImpl().toCharArray()));
		implClassNode.insertBefore(null, implDefinition, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_ConstructorInsertImpl));
	}

	private void handleCopyConstructor(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		((ICPPASTNamedTypeSpecifier) ((ICPPASTFunctionDeclarator) functionDefinition.getDeclarator()).getParameters()[0]
				.getDeclSpecifier()).setName(new CPPASTName(info.getClassNameImpl().toCharArray()));

		handleImplConstructor(functionDefinition, implClassNode);
	}

	private void handleImplDestructor(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		ICPPASTFunctionDefinition destructorImplDefinition = functionDefinition.copy();
		IASTName destructorImplName = new CPPASTName(new String("~" + info.getClassNameImpl()).toCharArray());
		destructorImplDefinition.getDeclarator().setName(destructorImplName);
		implClassNode.insertBefore(null, destructorImplDefinition, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_DestructorInsertImpl));
	}

	private void handleStandardFunctionDefintion(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode, NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		if (info.getActualOriginalVisibility() == ICPPASTVisibilityLabel.v_public) {
			IASTSimpleDeclaration declaration = NodeFactory.createDeclarationFromDefinition(functionDefinition);
			headerClassNode.insertBefore(null, declaration, new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_MemberInsertHeader));

			ICPPASTFunctionDefinition mappingDefinition = createMappingFromDefinition(functionDefinition);
			sourceClassNode.insertBefore(null, mappingDefinition, new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_MemberInsertSource));
		}

		ICPPASTFunctionDefinition definitionToInsert = functionDefinition.copy();
		definitionToInsert.getDeclarator().setName(functionDefinition.getDeclarator().getName().getLastName());
		implClassNode.insertBefore(null, definitionToInsert, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_MemberInsertImpl));
	}

	private void handleStaticFunctionDefinition(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode, NodeContainer<IASTNode> sourceClassNode) {
		ICPPASTFunctionDefinition memberDef = functionDefinition.copy();
		ICPPASTQualifiedName memberDefName = new CPPASTQualifiedName();
		memberDefName.addName(info.getClassSpecifier().getName().copy());
		memberDefName.addName(functionDefinition.getDeclarator().getName().getLastName().copy());
		memberDef.getDeclarator().setName(memberDefName);
		memberDef.getDeclSpecifier().setStorageClass(IASTSimpleDeclSpecifier.sc_unspecified);
		sourceClassNode.insertBefore(null, memberDef, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_MemberInsertSource));

		IASTSimpleDeclaration decl = NodeFactory.createDeclarationFromDefinition(functionDefinition);
		if (info.getActualOriginalVisibility() != ICPPASTVisibilityLabel.v_public) {
			info.getPrivateStaticList().add(decl);
		} else {
			decl.getDeclSpecifier().setStorageClass(IASTSimpleDeclSpecifier.sc_static);
			headerClassNode.insertBefore(null, decl, new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_MemberInsertHeader));
		}
	}
	
	void insertPrivateStatic(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
		for (IASTSimpleDeclaration node : info.getPrivateStaticList()) {
			IASTSimpleDeclaration nodecopy = node.copy();
			nodecopy.getDeclSpecifier().setStorageClass(IASTSimpleDeclSpecifier.sc_static);
			headerClassNode.insertBefore(null, nodecopy, new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_MemberInsertHeader));
		}
	}

	void insertPointer(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
		if (info.getPointerType() == IntroducePImplInformation.PointerType.STANDARD) {
			headerClassNode.insertBefore(null, createSimplePointer(), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_PointerInsertHeader));
		} else if (info.getPointerType() == IntroducePImplInformation.PointerType.SHARED) {
			headerClassNode.insertBefore(null, createSharedPointer(), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_PointerInsertHeader));
		} else {
			headerClassNode.insertBefore(null, createUniquePointer(), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_PointerInsertHeader));
		}
	}

	void insertBasicConstructor(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode,
			NodeContainer<IASTNode> sourceClassNode) {
		ICPPASTFunctionDefinition basicConstructor = createBasicConstructorDefinition();
		sourceClassNode.insertBefore(null, createBasicConstructorDefinition(), new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_BasicConstructorInsertSource));
		headerClassNode.insertBefore(null, NodeFactory.createDeclarationFromDefinition(basicConstructor),
				new TextEditGroup(Messages.IntroducePImpl_Rewrite_BasicConstructorInsertHeader));
	}

	void insertDestructor(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode,
			NodeContainer<IASTNode> sourceClassNode) {
		ICPPASTFunctionDefinition destructor = createDestructorDefinition();
		headerClassNode.insertBefore(null, NodeFactory.createDeclarationFromDefinition(destructor), new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_DestructorInsertHeader));
		sourceClassNode.insertBefore(null, destructor, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_DestructorInsertSource));
	}

	void insertCopyConstructor(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode,
			NodeContainer<IASTNode> sourceClassNode) {
		ICPPASTFunctionDefinition copyConstructor = createCopyConstructorDefinition();
		headerClassNode.insertBefore(null, NodeFactory.createDeclarationFromDefinition(copyConstructor),
				new TextEditGroup(Messages.IntroducePImpl_Rewrite_CopyConstructorInsertHeader));
		sourceClassNode.insertBefore(null, copyConstructor, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_CopyConstructorInsertSource));
	}

	void insertHeaderVisibility(int visibility, NodeContainer<ICPPASTCompositeTypeSpecifier> classNode) {
		if (info.getActualHeaderVisibility() != visibility) {
			classNode.insertBefore(null, NodeFactory.createVisibilityLabel(visibility), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_PrivateLabelInsertHeader));
			info.setActualHeaderVisibility(visibility);
		}
	}

	void insertImplVisibility(int visibility, NodeContainer<ICPPASTCompositeTypeSpecifier> classNode) {
		if (info.getActualImplVisibility() != visibility) {
			classNode.insertBefore(null, NodeFactory.createVisibilityLabel(visibility), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_PrivateLabelInsertHeader));
			info.setActualImplVisibility(visibility);
		}
	}
	
	void insertHeaderIncludes(ASTRewrite headerRewrite) {
		boolean includesInsserted = false;
		if (info.getPointerType() == IntroducePImplInformation.PointerType.SHARED) {
			if (info.getLibraryType() == IntroducePImplInformation.LibraryType.BOOST) {
				insertInclude(BOOST_SHARED_PTR_INCLUDE, headerRewrite, info.getHeaderUnit());
			} else {
				insertInclude(SHARED_AND_UNIQUE_PTR_INCLUDE, headerRewrite, info.getHeaderUnit());
			}
			includesInsserted = true;
		} else if (info.getPointerType() == IntroducePImplInformation.PointerType.UNIQUE) {
			insertInclude(SHARED_AND_UNIQUE_PTR_INCLUDE, headerRewrite, info.getHeaderUnit());
		}
		if (info.getCopyType() == IntroducePImplInformation.CopyType.NONCOPYABLE) {
			insertInclude(BOOST_NONCOPYABLE_INCLUDE, headerRewrite, info.getHeaderUnit());
			includesInsserted = true;
		}
		if (includesInsserted) {
			headerRewrite.insertBefore(info.getHeaderUnit(), info.getHeaderUnit().getDeclarations()[0], headerRewrite
						.createLiteralNode("\n"), new TextEditGroup(Messages.IntroducePImpl_Rewrite_NewLineInsertHeader));
		}
	}

	void insertSourceIncludes(ASTRewrite sourceRewrite) {
		String filename=new File(info.getHeaderUnit().getFilePath()).getName();
		insertInclude("\"" + filename + "\"", sourceRewrite, info.getSourceUnit());
	}

	private void insertInclude(String libraryStmt, ASTRewrite rewrite, IASTTranslationUnit unit) {
		if (!refactoring.existIncludeLibrary(libraryStmt, unit)) {
			IASTNode insertPoint = null;
			if (unit.getDeclarations().length > 0) {
				insertPoint = unit.getDeclarations()[0];
			}
			rewrite.insertBefore(unit, insertPoint, rewrite.createLiteralNode(INCLUDE_LABEL + libraryStmt + "\n"),
					new TextEditGroup(Messages.IntroducePImpl_Rewrite_IncludeInsert));
		}
	}
	
	private ICPPASTFunctionCallExpression insertMakeSharedDefinition() {
		ICPPASTNamedTypeSpecifier declSpec = new CPPASTNamedTypeSpecifier();
		declSpec.setName(new CPPASTName(info.getClassNameImpl().toCharArray()));
		CPPASTTypeId genericType = new CPPASTTypeId();
		genericType.setDeclSpecifier((IASTDeclSpecifier) declSpec);
		CPPASTTemplateId make_shared = new CPPASTTemplateId(new CPPASTName(MAKE_SHARED.toCharArray()));
		make_shared.addTemplateArgument(genericType);
		CPPASTQualifiedName qname = new CPPASTQualifiedName();
		if (info.getLibraryType() == IntroducePImplInformation.LibraryType.BOOST) {
			qname.addName(new CPPASTName(BOOST.toCharArray()));
		} else {
			qname.addName(new CPPASTName(STD.toCharArray()));
		}
		qname.addName(make_shared);
		
		IASTIdExpression expression = new CPPASTIdExpression();
		expression.setName(qname);
		ICPPASTFunctionCallExpression function = new CPPASTFunctionCallExpression();
		function.setFunctionNameExpression(expression);
		return function;
	}
	
	private ICPPASTFunctionDefinition createBasicConstructorDefinition() {
		ICPPASTFunctionDefinition constructorDefinition = NodeFactory.createBasicConstructorDefinition(info
				.getClassSpecifier().getName().toString(), true);
		constructorDefinition.addMemberInitializer(createConstructorPImplInitializer(constructorDefinition));
		constructorDefinition.setBody(new CPPASTCompoundStatement());
		return constructorDefinition;
	}

	private ICPPASTFunctionDefinition createCopyConstructorDefinition() {
		ICPPASTFunctionDefinition constructorDefinition = NodeFactory.createCopyConstructorDefinition(info
				.getClassSpecifier().getName().toString(), COPY_PARAM_NAME);
		constructorDefinition.addMemberInitializer(createCopyConstructorPImplInitializer(constructorDefinition));
		constructorDefinition.setBody(new CPPASTCompoundStatement());
		return constructorDefinition;
	}

	private ICPPASTFunctionDefinition createDestructorDefinition() {
		ICPPASTFunctionDefinition definition = NodeFactory.createDestructorDefinition(info.getClassSpecifier().getName().toString(), true);
		if (info.getPointerType() == IntroducePImplInformation.PointerType.UNIQUE) {
			definition.setBody(new CPPASTCompoundStatement());
			return definition;
		}
		IASTCompoundStatement deleteStatement = createPointerDelete();
		definition.setBody(deleteStatement);
		return definition;
	}

	private IASTCompoundStatement createPointerDelete() {
		IASTCompoundStatement compoundStatement = new CPPASTCompoundStatement();
		IASTExpressionStatement deleteStatement = new CPPASTExpressionStatement();
		ICPPASTDeleteExpression deleteExpression = new CPPASTDeleteExpression();
		IASTIdExpression idExpression = new CPPASTIdExpression();
		IASTName pointerName = new CPPASTName(info.getPointerNameImpl().toCharArray());
		idExpression.setName(pointerName);
		deleteExpression.setOperand(idExpression);
		deleteStatement.setExpression(deleteExpression);
		compoundStatement.addStatement(deleteStatement);
		return compoundStatement;
	}

	private ICPPASTFunctionDefinition createMappingFromDefinition(ICPPASTFunctionDefinition functionDefinition) {
		ICPPASTFunctionDefinition mappingDefinition = functionDefinition.copy();
		ICPPASTQualifiedName mappingName = new CPPASTQualifiedName();
		mappingName.addName(info.getClassSpecifier().getName().copy());
		mappingName.addName(functionDefinition.getDeclarator().getName().getLastName().copy());
		mappingDefinition.getDeclarator().setName(mappingName);
		mappingDefinition.setBody(createPointerMappingBody(functionDefinition));
		return mappingDefinition;
	}

	private IASTCompoundStatement createPointerMappingBody(ICPPASTFunctionDefinition functionDefinition) {
		IASTCompoundStatement compoundStatement = new CPPASTCompoundStatement();
		ICPPASTFunctionCallExpression memberToCallExpression = new CPPASTFunctionCallExpression();

		memberToCallExpression.setArguments(createParameterExpression(functionDefinition));
		ICPPASTFieldReference fieldReference = new CPPASTFieldReference();
		fieldReference.setIsPointerDereference(true);
		IASTName methodName = ((ICPPASTFunctionDefinition) functionDefinition).getDeclarator().getName().getLastName().copy();
		fieldReference.setFieldName(methodName);
		IASTIdExpression idExpression = new CPPASTIdExpression();
		IASTName pointerName = new CPPASTName(info.getPointerNameImpl().toCharArray());
		idExpression.setName(pointerName);
		fieldReference.setFieldOwner(idExpression);
		memberToCallExpression.setFunctionNameExpression(fieldReference);

		if ((((CPPASTFunctionDefinition) functionDefinition).getDeclSpecifier() == null)
				|| (((ICPPASTSimpleDeclSpecifier) ((CPPASTFunctionDefinition) functionDefinition).getDeclSpecifier()).getType() == ICPPASTSimpleDeclSpecifier.t_void)) {
			IASTExpressionStatement mappingStatement = new CPPASTExpressionStatement();
			mappingStatement.setExpression(memberToCallExpression);
			compoundStatement.addStatement(mappingStatement);
		} else {
			IASTReturnStatement mappingStatement = new CPPASTReturnStatement();
			mappingStatement.setReturnValue(memberToCallExpression);
			compoundStatement.addStatement(mappingStatement);
		}
		return compoundStatement;
	}
	
	private ICPPASTConstructorChainInitializer createConstructorPImplInitializer(ICPPASTFunctionDefinition memberDefinition) {
		ICPPASTConstructorChainInitializer initializer = new CPPASTConstructorChainInitializer();
		IASTName pointerName = new CPPASTName(info.getPointerNameImpl().toCharArray());
		initializer.setMemberInitializerId(pointerName);
		if (info.getPointerType() == IntroducePImplInformation.PointerType.SHARED) {
			initializer.setInitializer(new CPPASTConstructorInitializer(new IASTInitializerClause[] { insertMakeSharedDefinition() }));
			return initializer;
		}
		ICPPASTNewExpression newExpression = new CPPASTNewExpression();

		IASTParameterDeclaration[] parameters = ((ICPPASTFunctionDeclarator) memberDefinition.getDeclarator()).getParameters();
		if (parameters.length == 1) {
			IASTIdExpression paramExpression = new CPPASTIdExpression();
			paramExpression.setName(parameters[0].getDeclarator().getName().copy());
			newExpression.setInitializer(new CPPASTConstructorInitializer(new IASTExpression[] { paramExpression }));
		} else if (parameters.length > 1) {
			ICPPASTExpressionList parameterList = new CPPASTExpressionList();
			for (IASTParameterDeclaration parameter : parameters) {
				IASTIdExpression parameterExpression = new CPPASTIdExpression();
				parameterExpression.setName(parameter.getDeclarator().getName().copy());
				parameterList.addExpression(parameterExpression);
			}
			newExpression.setInitializer(new CPPASTConstructorInitializer(parameterList.getExpressions()));
		}

		IASTTypeId typeId = new CPPASTTypeId();
		ICPPASTNamedTypeSpecifier implTypeSpecifier = new CPPASTNamedTypeSpecifier();
		implTypeSpecifier.setName(new CPPASTName(info.getClassNameImpl().toCharArray()));
		typeId.setDeclSpecifier(implTypeSpecifier);
		newExpression.setTypeId(typeId);
		initializer.setInitializer(new CPPASTConstructorInitializer(new IASTExpression[] { newExpression }));
		return initializer;
	}

	private ICPPASTConstructorChainInitializer createCopyConstructorPImplInitializer(ICPPASTFunctionDefinition copyConstructorDefinition) {
		ICPPASTConstructorChainInitializer initializer = new CPPASTConstructorChainInitializer();
		IASTName pointerName = new CPPASTName(info.getPointerNameImpl().toCharArray());
		initializer.setMemberInitializerId(pointerName);
		if (info.getPointerType() == IntroducePImplInformation.PointerType.SHARED) {
			ICPPASTUnaryExpression oldImplReference = refactoring.getOldImplReference(copyConstructorDefinition, pointerName);
			ICPPASTFunctionCallExpression function = insertMakeSharedDefinition();
			function.setArguments(new IASTInitializerClause[] { oldImplReference });
			initializer.setInitializer(new CPPASTConstructorInitializer(new IASTInitializerClause[] { function }));
			return initializer;
		}
		ICPPASTNewExpression newExpression = new CPPASTNewExpression();
		IASTTypeId typeId = new CPPASTTypeId();
		ICPPASTNamedTypeSpecifier implTypeSpecifier = new CPPASTNamedTypeSpecifier();
		implTypeSpecifier.setName(new CPPASTName(info.getClassNameImpl().toCharArray()));
		typeId.setDeclSpecifier(implTypeSpecifier);
		newExpression.setTypeId(typeId);
		
		ICPPASTUnaryExpression oldImplReference = refactoring.getOldImplReference(copyConstructorDefinition, pointerName);
		
		newExpression.setInitializer(new CPPASTConstructorInitializer(new IASTExpression[] { oldImplReference }));
		initializer.setInitializer(new CPPASTConstructorInitializer(new IASTExpression[] { newExpression }));
		return initializer;
	}

	private IASTExpression[] createParameterExpression(IASTDeclaration member) {
		IASTExpression[] parameterExpression = null;
		IASTParameterDeclaration[] parameters = ((ICPPASTFunctionDeclarator) ((CPPASTFunctionDefinition) member)
				.getDeclarator()).getParameters();
		if (parameters != null) {
			if (parameters.length == 1) {
				IASTIdExpression singleParameterExpression = new CPPASTIdExpression();
				singleParameterExpression.setName(parameters[0].getDeclarator().getName().copy());
				parameterExpression = new IASTExpression[] { singleParameterExpression };
			} else {
				ICPPASTExpressionList parameterList = new CPPASTExpressionList();
				for (IASTParameterDeclaration parameter : parameters) {
					IASTIdExpression expression = new CPPASTIdExpression();
					expression.setName(parameter.getDeclarator().getName().copy());
					parameterList.addExpression(expression);
				}
				parameterExpression = parameterList.getExpressions();
			}
		}
		return parameterExpression;
	}

	ICPPASTBaseSpecifier createNoncopyableInitailizer() {
		ICPPASTBaseSpecifier includeSpecifier = new CPPASTBaseSpecifier();
		ICPPASTQualifiedName noncopyableName = new CPPASTQualifiedName();
		IASTName libraryName = new CPPASTName(BOOST.toCharArray());
		IASTName className = new CPPASTName(NONCOPYABLE.toCharArray());
		noncopyableName.addName(libraryName);
		noncopyableName.addName(className);
		includeSpecifier.setName(noncopyableName);
		return includeSpecifier;
	}
	
	private IASTSimpleDeclaration createSharedPointer() {
		ICPPASTElaboratedTypeSpecifier declSpec = new CPPASTElaboratedTypeSpecifier();
		declSpec.setName(new CPPASTName(info.getClassNameImpl().toCharArray()));
		declSpec.setKind(info.getClassType());

		CPPASTTypeId genericType = new CPPASTTypeId();
		genericType.setDeclSpecifier((IASTDeclSpecifier) declSpec);

		CPPASTTemplateId sharedPtr = new CPPASTTemplateId(new CPPASTName(SHARED_PTR.toCharArray()));
		sharedPtr.addTemplateArgument(genericType);

		CPPASTQualifiedName qname = new CPPASTQualifiedName();
		if (info.getLibraryType() == IntroducePImplInformation.LibraryType.BOOST) {
			qname.addName(new CPPASTName(BOOST.toCharArray()));
		} else {
			qname.addName(new CPPASTName(STD.toCharArray()));
		}
		qname.addName(sharedPtr);

		CPPASTNamedTypeSpecifier typeSpec = new CPPASTNamedTypeSpecifier(qname);

		IASTDeclarator pointerDeclarator = new CPPASTDeclarator();
		pointerDeclarator.setName(new CPPASTName(info.getPointerNameImpl().toCharArray()));

		IASTSimpleDeclaration declaration = new CPPASTSimpleDeclaration();
		declaration.setDeclSpecifier(typeSpec);
		declaration.addDeclarator(pointerDeclarator);
		return declaration;
	}
	
	private IASTSimpleDeclaration createUniquePointer() {
		ICPPASTElaboratedTypeSpecifier declSpec = new CPPASTElaboratedTypeSpecifier();
		declSpec.setName(new CPPASTName(info.getClassNameImpl().toCharArray()));
		declSpec.setKind(info.getClassType());

		CPPASTTypeId genericType = new CPPASTTypeId();
		genericType.setDeclSpecifier((IASTDeclSpecifier) declSpec);

		CPPASTTemplateId uniquePtr = new CPPASTTemplateId(new CPPASTName(UNIQUE_PTR.toCharArray()));
		uniquePtr.addTemplateArgument(genericType);

		CPPASTQualifiedName qname = new CPPASTQualifiedName();
		qname.addName(new CPPASTName(STD.toCharArray()));
		qname.addName(uniquePtr);

		CPPASTNamedTypeSpecifier typeSpec = new CPPASTNamedTypeSpecifier(qname);

		IASTDeclarator pointerDeclarator = new CPPASTDeclarator();
		pointerDeclarator.setName(new CPPASTName(info.getPointerNameImpl().toCharArray()));

		IASTSimpleDeclaration declaration = new CPPASTSimpleDeclaration();
		declaration.setDeclSpecifier(typeSpec);
		declaration.addDeclarator(pointerDeclarator);
		return declaration;
	}

	private IASTSimpleDeclaration createSimplePointer() {
		IASTSimpleDeclaration declaration = new CPPASTSimpleDeclaration();
		ICPPASTElaboratedTypeSpecifier declSpec = new CPPASTElaboratedTypeSpecifier();
		CPPASTName implName = (CPPASTName) info.getClassSpecifier().getName().copy();
		implName.setName(info.getClassNameImpl().toCharArray());
		declSpec.setName(implName);
		declSpec.setKind(info.getClassType());
		declaration.setDeclSpecifier(declSpec);
		IASTPointer operator = new CPPASTPointer();
		IASTName pointerName = new CPPASTName(info.getPointerNameImpl().toCharArray());
		IASTDeclarator pointerDeclarator = new CPPASTDeclarator();
		pointerDeclarator.addPointerOperator(operator);
		pointerDeclarator.setName(pointerName);
		declaration.addDeclarator(pointerDeclarator);
		return declaration;
	}
}
