package com.github.shiraji.kreateintentinspection.inspection

import com.intellij.codeInspection.BaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.ui.DocumentAdapter
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.text.BadLocationException

class KreateIntentInspection : BaseJavaLocalInspectionTool() {

    var methodName = "createIntent";

    override fun getGroupDisplayName() = "Android"

    override fun getDisplayName() = "Activity should implement $methodName"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = KreateIntentInspectionVisitor(holder, methodName)

    override fun createOptionsPanel(): JComponent? {
        val panel = InspectionOptionPanel()
        panel.methodNameTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent?) {
                e ?: return
                val document = e.document
                try {
                    methodName = document.getText(0, document.length)
                } catch (e1: BadLocationException) {
                }
            }
        })
        panel.methodNameTextField.text = methodName
        return panel.panel
    }
}