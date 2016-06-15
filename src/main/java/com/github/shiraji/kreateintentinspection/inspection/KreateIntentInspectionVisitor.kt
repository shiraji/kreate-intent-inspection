package com.github.shiraji.kreateintentinspection.inspection

import com.github.shiraji.kreateintentinspection.util.InspectionPsiUtil
import com.intellij.codeInspection.ProblemsHolder
import org.jetbrains.kotlin.asJava.KtLightClassForExplicitDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

class KreateIntentInspectionVisitor(val holder: ProblemsHolder, val name: String) : KtVisitorVoid() {

    private val QUALIFIED_NAME_OF_SUPER_CLASS = "android.app.Activity"
    private val INTENT_CLASS_NAME = "Intent"
    private val INTENT_FULL_QUALIFIED_NAME = "android.content.$INTENT_CLASS_NAME"
    private val CONTENT_FULL_QUALIFIED_NAME = "android.content.Context"

    override fun visitClass(klass: KtClass) {
        if (klass.isAbstract()) return
        val baseClass = InspectionPsiUtil.createPsiClass(QUALIFIED_NAME_OF_SUPER_CLASS, klass.project) ?: return
        val fqName = klass.fqName ?: return
        if (!KtLightClassForExplicitDeclaration(fqName, klass).isInheritor(baseClass, true)) return

        var hasMethod = false
        klass.getCompanionObjects().forEach {
            it.children.forEach {
                if (it is KtClassBody) {
                    it.children.forEach {
                        if (it is KtNamedFunction && it.name == name) {
                            hasMethod = true
                        }
                    }
                }
            }
        }

        if (!hasMethod) {
            System.out.println("No createIntent!!! Report Issue!!!")
        } else {
            System.out.println("Yea! This is good boy.")
        }
    }

}