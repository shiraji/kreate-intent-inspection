package com.github.shiraji.kreateintentinspection.inspection

import com.intellij.codeInspection.ProblemsHolder
import org.jetbrains.kotlin.psi.*

class KreateIntentInspectionVisitor(val holder: ProblemsHolder, val name: String) : KtVisitorVoid() {

    private val QUALIFIED_NAME_OF_SUPER_CLASS = "android.app.Activity"
    private val INTENT_CLASS_NAME = "Intent"
    private val INTENT_FULL_QUALIFIED_NAME = "android.content.$INTENT_CLASS_NAME"
    private val CONTENT_FULL_QUALIFIED_NAME = "android.content.Context"

    override fun visitClass(klass: KtClass) {
        System.out.println(klass)
        System.out.println(klass.getSuperTypeList())
        klass.parent

    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        super.visitObjectDeclaration(declaration)
    }

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
    }

}