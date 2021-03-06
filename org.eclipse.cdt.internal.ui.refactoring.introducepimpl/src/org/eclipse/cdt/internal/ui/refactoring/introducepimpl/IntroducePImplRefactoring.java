package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamespaceDefinition;
import org.eclipse.cdt.internal.ui.refactoring.Container;
import org.eclipse.cdt.internal.ui.refactoring.ModificationCollector;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.node.NodeContainer;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.node.NodeFactory;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.node.NodeHelper;
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
public class IntroducePImplRefactoring extends IntroducePImplCRefactoring {

	private static final String CPP_FILE_EXTENSION = "cpp";
	
	public IntroducePImplRefactoring(ICElement celem, ISelection selection, IntroducePImplInformation info) {
		super(celem, selection, info);
	}
	
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
		int workTickAvailable = 4;
		SubMonitor sm = SubMonitor.convert(pm, workTickAvailable);
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
					if (((IntroducePImplContext) refactoringContext).isSelectionOnExpression(textSelection, declaration)) {
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
					selectedNode = ((IntroducePImplContext) refactoringContext).findDeclarationInTranslationUnit(tmpUnit, indexName);
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
		int workTickAvailable = 10;
		SubMonitor sm = SubMonitor.convert(pm, workTickAvailable);
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
					info.isFileCreated = true;
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
				ICPPASTFunctionDefinition definition = getDefinitionOfDeclaration(member, info);
				if (definition == null) {
					declWithoutDefinition.add((IASTSimpleDeclaration) member);
				}
			} else if (member instanceof IASTDeclarator && publicLabel) {
				status.addWarning(Messages.IntroducePImpl_PublicField);
			}
		}
		return declWithoutDefinition;
	}
	
	@Override
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

			info.setPrivateStaticList(new ArrayList<IASTSimpleDeclaration>());
			initVisibility(info.getClassSpecifier(), implClassNode);

			workTick = handleModifications(sm, workTick, sourceClassNode, headerClassNode, implClassNode);
			insertModifications(sm, workTick, sourceClassNode, headerClassNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int handleModifications(SubMonitor sm, int workTick,
			NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> implClassNode)
			throws CoreException {
		for (IASTNode node : info.getClassSpecifier().getDeclarations(true)) {
			sm.worked(workTick++);
			boolean isNodeStatic = NodeHelper.isStatic(node);
			info.isNodeStatic = isNodeStatic;
			if (node instanceof IASTSimpleDeclaration) {
				IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) node;
				if (!NodeHelper.isEmtypDeclarator(simpleDeclaration)) {
					if (simpleDeclaration.getDeclarators()[0] instanceof IASTFunctionDeclarator) {
						handleFunctionDeclarator(simpleDeclaration, headerClassNode, sourceClassNode, implClassNode);
					} else if (simpleDeclaration.getDeclarators()[0] instanceof IASTDeclarator) {
						if (isNodeStatic) {
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
		return workTick;
	}

	private void insertModifications(SubMonitor sm, int workTick,
			NodeContainer<IASTNode> sourceClassNode,
			NodeContainer<ICPPASTCompositeTypeSpecifier> headerClassNode) {
		sm.worked(workTick++);
		if (!info.isConstructorInserted) {
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
	}

	private NodeContainer<IASTNode> getSourceClassContainer(ASTRewrite sourceRewrite) {
		final NodeContainer<IASTNode> container = new NodeContainer<IASTNode>(info.getSourceUnit(), sourceRewrite);
		if (info.isFileCreated) {
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
		} else if (info.getClassSpecifier().getParent().getParent() instanceof ICPPASTNamespaceDefinition) {
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
		return container;
	}

	private NodeContainer<ICPPASTCompositeTypeSpecifier> getHeaderClassContainer(ASTRewrite headerRewrite) {
		IASTSimpleDeclaration classNode = NodeFactory.createClassDeclaration(info.getClassSpecifier().getName().toString(), info.getClassSpecifier().getKey());
		((ICPPASTCompositeTypeSpecifier) classNode.getDeclSpecifier()).addDeclaration(NodeFactory
				.createVisibilityLabel(ICPPASTVisibilityLabel.v_public));
		if (info.getCopyType() == IntroducePImplInformation.CopyType.NONCOPYABLE) {
			((ICPPASTCompositeTypeSpecifier) classNode.getDeclSpecifier())
					.addBaseSpecifier(createNoncopyableInitializer());
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

	@Override
	protected RefactoringDescriptor getRefactoringDescriptor() {
		return null;
	}
}