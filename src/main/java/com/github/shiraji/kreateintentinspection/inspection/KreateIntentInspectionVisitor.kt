package com.github.shiraji.kreateintentinspection.inspection

import com.github.shiraji.kreateintentinspection.util.InspectionPsiUtil
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiClass
import com.siyeh.ig.BaseInspectionVisitor

class KreateIntentInspectionVisitor(val holder: ProblemsHolder, val name: String) : BaseInspectionVisitor() {

    private val QUALIFIED_NAME_OF_SUPER_CLASS = "android.app.Activity"
    private val INTENT_CLASS_NAME = "Intent"
    private val INTENT_FULL_QUALIFIED_NAME = "android.content.$INTENT_CLASS_NAME"
    private val CONTENT_FULL_QUALIFIED_NAME = "android.content.Context"


    override fun visitClass(aClass: PsiClass?) {
        aClass ?: return
        val flag = InspectionPsiUtil.isAbstactClass(aClass)
        System.out.println(flag)
    }
}