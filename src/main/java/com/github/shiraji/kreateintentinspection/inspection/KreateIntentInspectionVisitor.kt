package com.github.shiraji.kreateintentinspection.inspection

import com.github.shiraji.kreateintentinspection.util.InspectionPsiUtil
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.asJava.KtLightClassForExplicitDeclaration
import org.jetbrains.kotlin.idea.core.getOrCreateCompanionObject
import org.jetbrains.kotlin.psi.*
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
            holder.registerProblem(klass.nameIdentifier as PsiElement,
                    "Implement \"fun $name(Context): Intent\" inside companion object",
                    GenerateMethod(name))
        }
    }

    inner class GenerateMethod(val methodName: String) : LocalQuickFix {
        override fun getName() = "Implement fun $methodName(Context): Intent"
        override fun getFamilyName() = name
        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val factory = KtPsiFactory(project)
            val klass = descriptor.psiElement.parent as KtClass

            val method = factory.createFunction(
                    """
                    fun $methodName(context: $CONTENT_FULL_QUALIFIED_NAME): $INTENT_FULL_QUALIFIED_NAME {
                    val intent = $INTENT_FULL_QUALIFIED_NAME(context, ${klass.name}::class.java)
                    return intent
                    }
                    """.trimMargin()
            )

            runWriteAction {
                klass.getOrCreateCompanionObject().getOrCreateBody().addAfter(method, klass.getOrCreateCompanionObject().getOrCreateBody().lBrace)
            }
        }
    }


}