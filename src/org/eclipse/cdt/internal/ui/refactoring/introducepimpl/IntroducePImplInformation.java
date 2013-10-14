package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

public class IntroducePImplInformation {

	public enum PointerType {
		standard, shared
	}

	public enum LibraryType {
		boost, tr1
	}

	public enum CopyType {
		deep, shallow, nocopy, noncopyable
	}

	private int classType = ICPPASTCompositeTypeSpecifier.k_struct;
	private PointerType pointerType = PointerType.standard;
	private LibraryType libraryType = LibraryType.boost;
	private CopyType copyType = CopyType.deep;

	private String classNameImpl;
	private String pointerNameImpl;
	
	private ArrayList<ICPPASTCompositeTypeSpecifier> classSpecifiers = new ArrayList<ICPPASTCompositeTypeSpecifier>();

	private ICPPASTCompositeTypeSpecifier classSpecifier = null;

	private IASTTranslationUnit headerUnit;
	private IASTTranslationUnit sourceUnit;

	public IASTTranslationUnit getSourceUnit() {
		return sourceUnit;
	}

	public void setSourceUnit(IASTTranslationUnit sourceUnit) {
		this.sourceUnit = sourceUnit;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public int getClassType() {
		return classType;
	}

	public void setPointerType(PointerType pointerType) {
		this.pointerType = pointerType;
	}

	public PointerType getPointerType() {
		return pointerType;
	}

	public void setLibraryType(LibraryType libraryType) {
		this.libraryType = libraryType;
	}

	public LibraryType getLibraryType() {
		return libraryType;
	}

	public void setCopyType(CopyType copyType) {
		this.copyType = copyType;
	}

	public CopyType getCopyType() {
		return copyType;
	}

	public void setHeaderUnit(IASTTranslationUnit tmpUnit) {
		this.headerUnit = tmpUnit;
	}

	public IASTTranslationUnit getHeaderUnit() {
		return headerUnit;
	}

	public void setClassSpecifier(ICPPASTCompositeTypeSpecifier classSpecifier) {
		this.classSpecifier = classSpecifier;
	}

	public ICPPASTCompositeTypeSpecifier getClassSpecifier() {
		return classSpecifier;
	}

	public void setClassNameImpl(String classNameImpl) {
		this.classNameImpl = classNameImpl;
	}

	public String getClassNameImpl() {
		return classNameImpl;
	}

	public void setPointerNameImpl(String pointerNameImpl) {
		this.pointerNameImpl = pointerNameImpl;
	}

	public String getPointerNameImpl() {
		return pointerNameImpl;
	}

	public void setClassSpecifiers(ArrayList<ICPPASTCompositeTypeSpecifier> classSpecifiers) {
		this.classSpecifiers = classSpecifiers;
	}

	public ArrayList<ICPPASTCompositeTypeSpecifier> getClassSpecifiers() {
		return classSpecifiers;
	}
}
