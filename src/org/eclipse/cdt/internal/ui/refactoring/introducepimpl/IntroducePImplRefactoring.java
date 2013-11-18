package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBinding;
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
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBaseSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
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
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamespaceDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNewExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoring;
import org.eclipse.cdt.internal.ui.refactoring.Container;
import org.eclipse.cdt.internal.ui.refactoring.ModificationCollector;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.TextEditGroup;

@SuppressWarnings("restriction")
public class IntroducePImplRefactoring extends CRefactoring {

	private static final String NONCOPYABLE = "noncopyable";
	private static final String STD = "std";
	private static final String SHARED_PTR = "shared_ptr";
	private static final String UNIQUE_PTR = "unique_ptr";
	private static final String BOOST = "boost";
	private static final String INCLUDE_LABEL = "#include ";
	private static final String BOOST_SHARED_PTR_INCLUDE = "<boost/shared_ptr.hpp>";
	private static final String SHARED_AND_UNIQUE_PTR_INCLUDE = "<memory>";
	private static final String BOOST_NONCOPYABLE_INCLUDE = "<boost/noncopyable.hpp>";
	private static final String CPP_FILE_EXTENSION = "cpp";
	private static final String COPY_PARAM_NAME = "toCopy";
	private static final String MAKE_SHARED = "make_shared";
	private boolean fileCreated = false;
	private int actualOriginalVisibility = ICPPASTVisibilityLabel.v_public;
	private int actualHeaderVisibility = ICPPASTVisibilityLabel.v_public;
	private int actualImplVisibility = 0;
	private boolean hasConstructor = false;
	private boolean isStatic = false;
	private IntroducePImplInformation info;
	private ArrayList<IASTSimpleDeclaration> privateStaticList;

	public IntroducePImplRefactoring(ISelection selection, ICElement celem, IntroducePImplInformation info) {
		super(celem, selection, null);
		this.info = info;
		name = Messages.IntroducePImpl_IntroducePImpl;
	}
	
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
		/**
		 * TODO: replace "Magic Numbers"
		 */
		SubMonitor sm = SubMonitor.convert(pm, 4);
		RefactoringStatus status = new RefactoringStatus();
		try {
			status = super.checkInitialConditions(sm.newChild(6));
			if (status.hasError()) {
				return status;
			}
			sm.worked(1);

			IASTNode selectedNode = findFirstSelectedNode(selectedRegion, tu);
			sm.worked(2);

			if (tu.isHeaderUnit()) {
				info.setHeaderUnit(getAST(tu, null));
			} else {
				while (!(selectedNode instanceof CPPASTFunctionDefinition) && selectedNode != null) {
					selectedNode = selectedNode.getParent();
				}
				if (selectedNode == null) {
					status.addFatalError(Messages.IntroducePImpl_SelectionInvalid);
					return status;
				}
				sm.worked(3);
				selectedNode = loadHeaderUnit(selectedNode, status);
				if (status.hasError()) {
					return status;
				}
			}
			info.setClassSpecifier((CPPASTCompositeTypeSpecifier) getClassNodeOf(selectedNode));
			sm.done();
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
		return status;
	}

	private IASTDeclaration findFirstSelectedNode(final Region textSelection, ITranslationUnit tu) {
		final Container<IASTDeclaration> container = new Container<IASTDeclaration>();
		try {
			getAST(tu,null).accept(new ASTVisitor() {
				{
					shouldVisitDeclarations = true;
				}
				public int visit(IASTDeclaration declaration) {
					if (org.eclipse.cdt.internal.ui.refactoring.introducepimpl.SelectionHelper.isSelectionOnExpression(textSelection, declaration)) {
						container.setObject((IASTDeclaration) declaration);
					}
					return super.visit(declaration);
				}
			});
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return container.getObject();
	}
	
	private IASTNode loadHeaderUnit(IASTNode selectedNode, RefactoringStatus status) throws CoreException {
		IASTName name = ((ICPPASTFunctionDefinition) selectedNode).getDeclarator().getName();
		IBinding bind = name.resolveBinding();
		IIndexName[] foundDecl = getIndex().findDeclarations(bind);
		IASTTranslationUnit tmpUnit = null;
		for (IIndexName indexName : foundDecl) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(indexName.getFileLocation().getFileName()));
			ITranslationUnit tu = CoreModelUtil.findTranslationUnit(file);
			tmpUnit = getAST(tu, null);
			if (tmpUnit != null) {
				if (tmpUnit.isHeaderUnit()) {
					selectedNode = DeclarationFinder.findDeclarationInTranslationUnit(tmpUnit, indexName);
					break;
				}
			}
		}
		if (tmpUnit == null) {
			status.addFatalError(Messages.IntroducePImpl_HeaderFileNotFound);
		} else {
			info.setHeaderUnit(tmpUnit);
		}
		return selectedNode;
	}

	private CPPASTCompositeTypeSpecifier getClassNodeOf(IASTNode node) {
		if (node != null) {
			if (node instanceof IASTSimpleDeclaration) {
				if (((IASTSimpleDeclaration) node).getDeclSpecifier() instanceof ICPPASTCompositeTypeSpecifier) {
					node = ((IASTSimpleDeclaration) node).getDeclSpecifier();
				}
			}
			while (!(node instanceof ICPPASTCompositeTypeSpecifier) && node != null) {
				node = node.getParent();
			}
		} else {
			try {
				getAST(tu , null).accept(new ASTVisitor() {
					{
						shouldVisitDeclSpecifiers = true;
					}
					public int visit(IASTDeclSpecifier declSpec) {
						if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
							info.getClassSpecifiers().add((ICPPASTCompositeTypeSpecifier) declSpec);
						}
						return super.visit(declSpec);
					}
				});
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			if (info.getClassSpecifiers().size() == 1) {
				node = info.getClassSpecifiers().get(0);
			}
		}
		return (CPPASTCompositeTypeSpecifier) node;
	}
	
	@Override
	protected RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext checkContext) {
		/**
		 * TODO: replace "Magic Numbers"
		 */
		SubMonitor sm = SubMonitor.convert(pm, 10);
		RefactoringStatus status = null;
		try {
			sm.worked(0);
			status = super.checkFinalConditions(pm, checkContext);
			sm.worked(1);

			ArrayList<IFile> cppFiles = collectDecl();
			sm.worked(5);
			if (cppFiles.size() > 1) {
				status.addInfo(Messages.IntroducePImpl_FunctionInOneFile);
				for (IFile file : cppFiles) {
					status.addFatalError(Messages.IntroducePImpl_TooManyCppFiles + ": " + file.getFullPath());
				}
			} else {
				if (cppFiles.size() == 1) {
					ITranslationUnit tu = CoreModelUtil.findTranslationUnit(cppFiles.get(0));
					info.setSourceUnit(getAST(tu, sm));
				}
				ArrayList<IASTSimpleDeclaration> declWithoutDefinition = checkDefinitionOfDeclarations(status);
				for (IASTSimpleDeclaration simplDecl : declWithoutDefinition) {
					status.addFatalError(Messages.IntroducePImpl_NoDefinitionFound + ": \""	+ simplDecl.getRawSignature() + "\"");
				}
				if (cppFiles.size() == 0) {
					/*
					 * TODO: If CreateFileChange is implemented, this must be implemented here.
					 * At this time, the file will be created but not removed if the process is aborted.
					 */
					fileCreated = true;
					IPath path = new Path(info.getHeaderUnit().getFilePath()).removeFileExtension().addFileExtension(CPP_FILE_EXTENSION);
					IFile cppFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
					if (!cppFile.exists()) {
						status.addWarning(Messages.IntroducePImpl_CppFileCreated);
						// If InputStream is null, file will be marked as notLocal and will not be created!
						ByteArrayInputStream dummyStream =new ByteArrayInputStream("".getBytes());
						cppFile.create(dummyStream, true, pm);
					}
					ResourcesPlugin.getWorkspace().getRoot().refreshLocal(1, pm);
					ITranslationUnit tu = CoreModelUtil.findTranslationUnit(cppFile);
					IASTTranslationUnit sourceUnit = getAST(tu, sm);
					sourceUnit.setIsHeaderUnit(false);
					info.setSourceUnit(sourceUnit);
				}
			}
			sm.done();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	private ArrayList<IFile> collectDecl() throws CoreException {
		ArrayList<IFile> cppFiles = new ArrayList<IFile>();
		for (IASTDeclaration tmpMember : info.getClassSpecifier().getDeclarations(false)) {
			if (NodeHelper.isFunctionDeclarator(tmpMember)) {
				IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) tmpMember;
				ICPPASTFunctionDeclarator funcDecl = (ICPPASTFunctionDeclarator) simpleDecl.getDeclarators()[0];
				IBinding bind = funcDecl.getName().resolveBinding();
				IIndexName[] indexNames = getIndex().findDefinitions(bind);
				for (IIndexName indexName : indexNames) {
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
							new Path(indexName.getFileLocation().getFileName()));
					if (file.getFileExtension().equals(CPP_FILE_EXTENSION)) {
						if (!cppFiles.contains(file)) {
							cppFiles.add(file);
						}
					}
				}
			}
		}
		return cppFiles;
	}

	private ArrayList<IASTSimpleDeclaration> checkDefinitionOfDeclarations(RefactoringStatus status) throws CoreException {
		boolean publicLabel = false;
		ArrayList<IASTSimpleDeclaration> declWithoutDefinition = new ArrayList<IASTSimpleDeclaration>();
		for (IASTDeclaration member : info.getClassSpecifier().getDeclarations(true)) {
			if (NodeHelper.isEmtypDeclarator(member)) {
				status.addWarning(Messages.IntroducePImpl_EmptyDeclarationFound + ": \"" + member.getRawSignature()	+ "\"");
			} else if (member instanceof ICPPASTVisibilityLabel) {
				if (((ICPPASTVisibilityLabel) member).getVisibility() == 3) {
					publicLabel = false;
				} else {
					publicLabel = true;
				}
			} else if (NodeHelper.isFunctionDeclarator(member)) {
				ICPPASTFunctionDefinition definition = getDefinitionOfDeclaration(member);
				if (definition == null) {
					declWithoutDefinition.add((IASTSimpleDeclaration) member);
				}
			} else if (member instanceof IASTDeclarator && publicLabel) {
				status.addWarning(Messages.IntroducePImpl_PublicField);
			}
		}
		return declWithoutDefinition;
	}

	private ICPPASTFunctionDefinition getDefinitionOfDeclaration(IASTDeclaration member) throws CoreException {
		IASTDeclarator[] declarators = ((IASTSimpleDeclaration) member).getDeclarators();
		if (declarators.length == 0) {
			return null;
		}
		IASTName name = declarators[0].getName();
		IBinding bind = name.resolveBinding();
		IIndexName[] iNames = getIndex().findDefinitions(bind);
		for (IIndexName iName : iNames) {
			IASTNode cppDecName = null;
			if (info.getSourceUnit() != null) {
				cppDecName = DeclarationFinder.findDeclarationInTranslationUnit(info.getSourceUnit(), iName);
			} else if (info.getHeaderUnit() != null) {
				cppDecName = DeclarationFinder.findDeclarationInTranslationUnit(info.getHeaderUnit(), iName);
			}
			if (!(cppDecName == null)) {
				while (!(cppDecName instanceof ICPPASTFunctionDefinition)) {
					cppDecName = cppDecName.getParent();
				}
				return (ICPPASTFunctionDefinition) cppDecName;
			}
		}
		return null;
	}
	
	protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) {
		final int TICK_COUNT_COLLECT_MODIFICATIONS = 9;
		int tickCount = TICK_COUNT_COLLECT_MODIFICATIONS + info.getClassSpecifier().getDeclarations(true).length;
		SubMonitor sm = SubMonitor.convert(pm, tickCount);
		try {
			int workTick = 0;
			sm.worked(workTick++);
			ASTRewrite headerRewrite = collector.rewriterForTranslationUnit(info.getHeaderUnit());
			insertHeaderIncludes(headerRewrite);
			sm.worked(workTick++);
			ASTRewrite sourceRewrite = collector.rewriterForTranslationUnit(info.getSourceUnit());
			insertSourceIncludes(sourceRewrite);

			sm.worked(workTick++);
			NodeContainer<IASTNode> sourceClassNode = getSourceClassContainer(sourceRewrite);
			sm.worked(workTick++);
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode = getHeaderClassContainer(headerRewrite);
			sm.worked(workTick++);
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode = getSourceImplClassContainer(sourceClassNode);

			privateStaticList = new ArrayList<IASTSimpleDeclaration>();
			initVisibility(info.getClassSpecifier(), implClassNode);

			for (IASTNode node : info.getClassSpecifier().getDeclarations(true)) {
				sm.worked(workTick++);
				isStatic = NodeHelper.isStatic(node);
				if (node instanceof IASTSimpleDeclaration) {
					IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) node;
					if (!NodeHelper.isEmtypDeclarator(simpleDeclaration)) {
						if (simpleDeclaration.getDeclarators()[0] instanceof IASTFunctionDeclarator) {
							handleFunctionDeclarator(simpleDeclaration, headerClassNode, sourceClassNode, implClassNode);
						} else if (simpleDeclaration.getDeclarators()[0] instanceof IASTDeclarator) {
							if (isStatic) {
								handleStaticDeclarator(simpleDeclaration.copy(), headerClassNode);
							} else {
								handleDeclarator(simpleDeclaration.copy(), implClassNode);
							}
						}
					}
				} else if (node instanceof ICPPASTVisibilityLabel) {
					ICPPASTVisibilityLabel visibilityLabel = (ICPPASTVisibilityLabel) node;
					handleVisibilitiyLabel(visibilityLabel.copy(), implClassNode);
				} else if (node instanceof ICPPASTFunctionDefinition) {
					ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) node;
					handleFunctionDefinition(functionDefinition.copy(), headerClassNode, sourceClassNode, implClassNode);
				}
			}
			sm.worked(workTick++);
			if (!hasConstructor) {
				insertHeaderVisibility(ICPPASTVisibilityLabel.v_public, headerClassNode);
				insertBasicConstructor(headerClassNode, sourceClassNode);
			}
			sm.worked(workTick++);
			if (info.getPointerType() == IntroducePImplInformation.PointerType.STANDARD
					|| info.getPointerType() == IntroducePImplInformation.PointerType.UNIQUE) {
				insertHeaderVisibility(ICPPASTVisibilityLabel.v_public, headerClassNode);
				insertDestructor(headerClassNode, sourceClassNode);
			}
			sm.worked(workTick++);
			if (info.getCopyType() == IntroducePImplInformation.CopyType.DEEP) {
				insertHeaderVisibility(ICPPASTVisibilityLabel.v_public, headerClassNode);
				insertCopyConstructor(headerClassNode, sourceClassNode);
			} else if (info.getCopyType() == IntroducePImplInformation.CopyType.NOCOPY) {
				insertHeaderVisibility(ICPPASTVisibilityLabel.v_private, headerClassNode);
				insertCopyConstructor(headerClassNode, sourceClassNode);
			}

			insertHeaderVisibility(ICPPASTVisibilityLabel.v_private, headerClassNode);
			sm.worked(workTick++);
			insertPointer(headerClassNode);
			sm.worked(workTick++);
			insertPrivateStatic(headerClassNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initVisibility(ICPPASTCompositeTypeSpecifier originalHeaderClass,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		if (originalHeaderClass.getDeclarations(true).length > 0
				&& !(originalHeaderClass.getDeclarations(true)[0] instanceof ICPPASTVisibilityLabel)) {
			if (originalHeaderClass.getKey() == ICPPASTCompositeTypeSpecifier.k_class) {
				this.actualOriginalVisibility = ICPPASTVisibilityLabel.v_private;
				initImplVisibility(ICPPASTVisibilityLabel.v_private, originalHeaderClass.getKey(), implClassNode);
			} else {
				this.actualOriginalVisibility = ICPPASTVisibilityLabel.v_public;
				initImplVisibility(ICPPASTVisibilityLabel.v_public, originalHeaderClass.getKey(), implClassNode);
			}
		}
	}

	private void initImplVisibility(int visibility, int originalHeaderType,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		if (originalHeaderType == info.getClassType()) {
			this.actualImplVisibility = visibility;
		} else {
			insertImplVisibility(visibility, implClassNode);
		}
	}

	private NodeContainer<IASTNode> getSourceClassContainer(ASTRewrite sourceRewrite) {
		final NodeContainer<IASTNode> container = new NodeContainer<IASTNode>(info.getSourceUnit(), sourceRewrite);
		if (fileCreated) {
			info.getHeaderUnit().accept(new ASTVisitor() {
				{
					shouldVisitNamespaces = true;
				}

				public int visit(ICPPASTNamespaceDefinition namespaceInHeader) {
					ICPPASTNamespaceDefinition namespaceCopy = new CPPASTNamespaceDefinition(namespaceInHeader.getName().copy());
					ASTRewrite rewrite = container.getRewrite().insertBefore(container.getNode(), null, namespaceCopy,
							new TextEditGroup(Messages.IntroducePImpl_Rewrite_NamespaceInserted));
					container.setNode(namespaceCopy);
					container.setRewrite(rewrite);
					return super.visit(namespaceInHeader);
				}
			});
		} else {
			if (info.getClassSpecifier().getParent().getParent() instanceof ICPPASTNamespaceDefinition) {
				ICPPASTNamespaceDefinition namespaceInHeader = (ICPPASTNamespaceDefinition) info.getClassSpecifier().getParent().getParent();
				final String namespaceNameInHeader = namespaceInHeader.getName().toString();
				info.getSourceUnit().accept(new ASTVisitor() {
					{
						shouldVisitNamespaces = true;
					}

					public int visit(ICPPASTNamespaceDefinition namespaceInSource) {
						if (namespaceInSource.getName().toString().equals(namespaceNameInHeader)) {
							container.setNode(namespaceInSource);
							return ASTVisitor.PROCESS_ABORT;
						}
						return super.visit(namespaceInSource);
					}
				});
			}
		}
		return container;
	}

	private NodeContainer<ICPPASTCompositeTypeSpecifier> getHeaderClassContainer(ASTRewrite headerRewrite) {
		IASTSimpleDeclaration classNode = NodeFactory.createClassDeclaration(info.getClassSpecifier().getName().toString(), info.getClassSpecifier().getKey());
		((ICPPASTCompositeTypeSpecifier) classNode.getDeclSpecifier()).addDeclaration(NodeFactory
				.createVisibilityLabel(ICPPASTVisibilityLabel.v_public));
		if (info.getCopyType() == IntroducePImplInformation.CopyType.NONCOPYABLE) {
			((ICPPASTCompositeTypeSpecifier) classNode.getDeclSpecifier())
					.addBaseSpecifier(createNoncopyableInitailizer());
		}
		ASTRewrite classNodeRewrite = headerRewrite.replace(info.getClassSpecifier(), classNode.getDeclSpecifier(),
				new TextEditGroup(Messages.IntroducePImpl_Rewrite_HeaderClassReplace));
		return new NodeContainer<ICPPASTCompositeTypeSpecifier>((ICPPASTCompositeTypeSpecifier) classNode
				.getDeclSpecifier(), classNodeRewrite);
	}

	private NodeContainer<ICPPASTCompositeTypeSpecifier> getSourceImplClassContainer(NodeContainer<IASTNode> sourceNodeContainer) {
		IASTSimpleDeclaration implClassNode = NodeFactory.createClassDeclaration(info.getClassNameImpl(), info.getClassType());
		ASTRewrite classRewrite;
		classRewrite = sourceNodeContainer.insertBefore(null, implClassNode, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_ImplClassInsertSource));

		return new NodeContainer<ICPPASTCompositeTypeSpecifier>((ICPPASTCompositeTypeSpecifier) implClassNode
				.getDeclSpecifier(), classRewrite);
	}

	private void handleFunctionDeclarator(IASTSimpleDeclaration simpleDeclaration,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode, NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) throws CoreException {
		ICPPASTFunctionDefinition functionDefinition = getDefinitionOfDeclaration(simpleDeclaration);
		sourceClassNode.remove(functionDefinition, new TextEditGroup("Definition removed from source"));
		handleFunctionDefinition(functionDefinition.copy(), headerClassNode, sourceClassNode, implClassNode);
	}

	private void handleDeclarator(IASTSimpleDeclaration simpleDeclaration,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		implClassNode.insertBefore(null, simpleDeclaration.copy(), new TextEditGroup("Copy Declarator to implFile"));
	}

	private void handleStaticDeclarator(IASTSimpleDeclaration simpleDeclaration,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
		if (actualOriginalVisibility != ICPPASTVisibilityLabel.v_public) {
			privateStaticList.add(simpleDeclaration.copy());
		} else {
			headerClassNode.insertBefore(null, simpleDeclaration.copy(), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_StaticFieldInsertHeader));
		}
	}

	private void handleVisibilitiyLabel(ICPPASTVisibilityLabel visibilityLabel,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		insertImplVisibility(visibilityLabel.getVisibility(), implClassNode);
		this.actualOriginalVisibility = visibilityLabel.getVisibility();
	}

	private void handleFunctionDefinition(ICPPASTFunctionDefinition functionDefinition,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode, NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode) {
		if (NodeHelper.isConstructor(functionDefinition)) {
			hasConstructor = true;
			handleConstructor(functionDefinition, headerClassNode, sourceClassNode, implClassNode);
		} else if (NodeHelper.isDestructor(functionDefinition)) {
			handleImplDestructor(functionDefinition, implClassNode);
		} else if (NodeHelper.isCopyConstructor(functionDefinition)) {
			handleCopyConstructor(functionDefinition, implClassNode);
		} else {
			if (isStatic) {
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
		if (actualOriginalVisibility == ICPPASTVisibilityLabel.v_public) {
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
		if (actualOriginalVisibility != ICPPASTVisibilityLabel.v_public) {
			privateStaticList.add(decl);
		} else {
			decl.getDeclSpecifier().setStorageClass(IASTSimpleDeclSpecifier.sc_static);
			headerClassNode.insertBefore(null, decl, new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_MemberInsertHeader));
		}
	}

	private void insertPrivateStatic(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
		for (IASTSimpleDeclaration node : privateStaticList) {
			IASTSimpleDeclaration nodecopy = node.copy();
			nodecopy.getDeclSpecifier().setStorageClass(IASTSimpleDeclSpecifier.sc_static);
			headerClassNode.insertBefore(null, nodecopy, new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_MemberInsertHeader));
		}
	}

	private void insertPointer(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
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

	private void insertBasicConstructor(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode,
			NodeContainer<IASTNode> sourceClassNode) {
		ICPPASTFunctionDefinition basicConstructor = createBasicConstructorDefinition();
		sourceClassNode.insertBefore(null, createBasicConstructorDefinition(), new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_BasicConstructorInsertSource));
		headerClassNode.insertBefore(null, NodeFactory.createDeclarationFromDefinition(basicConstructor),
				new TextEditGroup(Messages.IntroducePImpl_Rewrite_BasicConstructorInsertHeader));
	}

	private void insertDestructor(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode,
			NodeContainer<IASTNode> sourceClassNode) {
		ICPPASTFunctionDefinition destructor = createDestructorDefinition();
		headerClassNode.insertBefore(null, NodeFactory.createDeclarationFromDefinition(destructor), new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_DestructorInsertHeader));
		sourceClassNode.insertBefore(null, destructor, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_DestructorInsertSource));
	}

	private void insertCopyConstructor(NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode,
			NodeContainer<IASTNode> sourceClassNode) {
		ICPPASTFunctionDefinition copyConstructor = createCopyConstructorDefinition();
		headerClassNode.insertBefore(null, NodeFactory.createDeclarationFromDefinition(copyConstructor),
				new TextEditGroup(Messages.IntroducePImpl_Rewrite_CopyConstructorInsertHeader));
		sourceClassNode.insertBefore(null, copyConstructor, new TextEditGroup(
				Messages.IntroducePImpl_Rewrite_CopyConstructorInsertSource));
	}

	private void insertHeaderVisibility(int visibility, NodeContainer<ICPPASTCompositeTypeSpecifier> classNode) {
		if (this.actualHeaderVisibility != visibility) {
			classNode.insertBefore(null, NodeFactory.createVisibilityLabel(visibility), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_PrivateLabelInsertHeader));
			this.actualHeaderVisibility = visibility;
		}
	}

	private void insertImplVisibility(int visibility, NodeContainer<ICPPASTCompositeTypeSpecifier> classNode) {
		if (this.actualImplVisibility != visibility) {
			classNode.insertBefore(null, NodeFactory.createVisibilityLabel(visibility), new TextEditGroup(
					Messages.IntroducePImpl_Rewrite_PrivateLabelInsertHeader));
			this.actualImplVisibility = visibility;
		}
	}
	
	private void insertHeaderIncludes(ASTRewrite headerRewrite) {
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

	private void insertSourceIncludes(ASTRewrite sourceRewrite) {
		String filename=new File(info.getHeaderUnit().getFilePath()).getName();
		insertInclude("\"" + filename + "\"", sourceRewrite, info.getSourceUnit());
	}

	private void insertInclude(String libraryStmt, ASTRewrite rewrite, IASTTranslationUnit unit) {
		if (!existIncludeLibrary(libraryStmt, unit)) {
			IASTNode insertPoint = null;
			if (unit.getDeclarations().length > 0) {
				insertPoint = unit.getDeclarations()[0];
			}
			rewrite.insertBefore(unit, insertPoint, rewrite.createLiteralNode(INCLUDE_LABEL + libraryStmt + "\n"),
					new TextEditGroup(Messages.IntroducePImpl_Rewrite_IncludeInsert));
		}
	}

	private boolean existIncludeLibrary(String libraryStmt, IASTTranslationUnit unit) {
		final String INCLUDE_REPLACE_REGEX = "(<|>|\")";
		for (IASTPreprocessorStatement preprocStmt : unit.getAllPreprocessorStatements()) {
			if (preprocStmt instanceof IASTPreprocessorIncludeStatement) {
				IASTPreprocessorIncludeStatement include = (IASTPreprocessorIncludeStatement) preprocStmt;
				if (include.getName().toString().replaceAll(INCLUDE_REPLACE_REGEX, "").equals(
						libraryStmt.replaceAll(INCLUDE_REPLACE_REGEX, ""))) {
					return true;
				}
			}
		}
		return false;
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
	
	private CPPASTQualifiedName insertMakeSharedDefinition() {
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
		return qname;
	}

	private ICPPASTConstructorChainInitializer createConstructorPImplInitializer(ICPPASTFunctionDefinition memberDefinition) {
		ICPPASTConstructorChainInitializer initializer = new CPPASTConstructorChainInitializer();
		IASTName pointerName = new CPPASTName(info.getPointerNameImpl().toCharArray());
		initializer.setMemberInitializerId(pointerName);
		if (info.getPointerType() == IntroducePImplInformation.PointerType.SHARED) {
			IASTIdExpression expression = new CPPASTIdExpression();
			expression.setName(insertMakeSharedDefinition());
			ICPPASTFunctionCallExpression function = new CPPASTFunctionCallExpression();
			function.setFunctionNameExpression(expression);
			initializer.setInitializer(new CPPASTConstructorInitializer(new IASTInitializerClause[] { function }));
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
			IASTIdExpression expression = new CPPASTIdExpression();
			expression.setName(insertMakeSharedDefinition());
			ICPPASTFunctionCallExpression function = new CPPASTFunctionCallExpression();
			function.setFunctionNameExpression(expression);
			
			ICPPASTUnaryExpression oldImplReference = getImplReference(copyConstructorDefinition, pointerName);
			
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
		
		ICPPASTUnaryExpression oldImplReference = getImplReference(copyConstructorDefinition, pointerName);
		
		newExpression.setInitializer(new CPPASTConstructorInitializer(new IASTExpression[] { oldImplReference }));
		initializer.setInitializer(new CPPASTConstructorInitializer(new IASTExpression[] { newExpression }));
		return initializer;
	}

	private ICPPASTUnaryExpression getImplReference(ICPPASTFunctionDefinition copyConstructorDefinition, IASTName pointerName) {
		ICPPASTUnaryExpression oldImplReference = new CPPASTUnaryExpression();
		oldImplReference.setOperator(CPPASTUnaryExpression.op_star);
		ICPPASTFieldReference oldImplPointerField = new CPPASTFieldReference();
		IASTIdExpression oldImplExpression = new CPPASTIdExpression();
		IASTName paramName = ((CPPASTFunctionDeclarator) copyConstructorDefinition.getDeclarator()).getParameters()[0].getDeclarator().getName().copy();
		oldImplExpression.setName(paramName);
		oldImplPointerField.setFieldOwner(oldImplExpression);
		oldImplPointerField.setFieldName(pointerName.copy());
		oldImplReference.setOperand(oldImplPointerField);
		return oldImplReference;
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

	private ICPPASTBaseSpecifier createNoncopyableInitailizer() {
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

	@Override
	protected RefactoringDescriptor getRefactoringDescriptor() {
		return null;  // Refactoring history is not supported.
	}
}