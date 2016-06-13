package com.github.shiraji.kreateintentinspection.util

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope

object InspectionPsiUtil {

    fun createPsiClass(qualifiedName: String, project: Project): PsiClass? {
        val psiFacade = JavaPsiFacade.getInstance(project)
        val searchScope = GlobalSearchScope.allScope(project)
        return psiFacade.findClass(qualifiedName, searchScope)
    }


    fun isAbstactClass(aClass: PsiClass): Boolean {
        return aClass.modifierList?.hasModifierProperty("abstract") ?: false
    }

    fun hasMethodModifier(method: PsiMethod, propertyName: String): Boolean {
        return method.modifierList.hasModifierProperty(propertyName)
    }

}