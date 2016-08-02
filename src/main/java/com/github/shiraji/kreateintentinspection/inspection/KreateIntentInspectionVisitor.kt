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
import org.jetbrains.kotlin.resolve.ImportPath

class KreateIntentInspectionVisitor(val holder: ProblemsHolder, val name: String) : KtVisitorVoid() {

    companion object {
        private const val QUALIFIED_NAME_OF_SUPER_CLASS = "android.app.Activity"
        private const val INTENT_CLASS_NAME = "Intent"
        private const val INTENT_FULL_QUALIFIED_NAME = "android.content.$INTENT_CLASS_NAME"
        private const val CONTEXT_CLASS_NAME = "Context"
        private const val CONTEXT_FULL_QUALIFIED_NAME = "android.content.$CONTEXT_CLASS_NAME"
    }

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
                    |fun $methodName(context: $CONTEXT_CLASS_NAME) = $INTENT_CLASS_NAME(context, ${klass.name}::class.java).apply {
                    |}
                    """.trimMargin()
            )

            runWriteAction {
                addImport(CONTEXT_FULL_QUALIFIED_NAME, factory, klass.containingFile as? KtFile)
                addImport(INTENT_FULL_QUALIFIED_NAME, factory, klass.containingFile as? KtFile)
                klass.getOrCreateCompanionObject().getOrCreateBody().addAfter(method, klass.getOrCreateCompanionObject().getOrCreateBody().lBrace)
            }
        }

        private fun addImport(importPath: String, factory: KtPsiFactory, file: KtFile?) {
            file ?: return
            val importDirective = factory.createImportDirective(ImportPath(importPath))
            if (file.importDirectives.none { it.importPath == importDirective.importPath }) {
                file.importList?.add(importDirective)
            }
        }
    }
}