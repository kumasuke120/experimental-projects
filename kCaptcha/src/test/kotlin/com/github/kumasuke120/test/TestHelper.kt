package com.github.kumasuke120.test

import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.JOptionPane.OK_OPTION

fun requireConfirm(imageBytes: ByteArray,
                   message: String? = null,
                   failCause: String = "Failure to meet expectation") {
    val labels = createJLabelsForConfirm(imageBytes, message)
    val panel = createVerticalJPanel(*labels)

    val result = showTopLevelConfirmDialog("Did the result meet your expectation?", panel)
    if (result != OK_OPTION) {
        throw AssertionError("Test failed due to: $failCause")
    }
}

private fun createJLabelsForConfirm(imageBytes: ByteArray, message: String?): Array<JLabel> {
    val icon = createImageIconFromBytes(imageBytes)
    val imageLabel = JLabel(icon)
    val labels: MutableList<JLabel> = mutableListOf(imageLabel)
    if (message != null) {
        val messageLabel = JLabel(message)
        labels.add(messageLabel)
    }

    return labels.toTypedArray()
}

private fun createImageIconFromBytes(imageBytes: ByteArray): ImageIcon {
    val input = ByteArrayInputStream(imageBytes)
    input.use {
        try {
            val image = ImageIO.read(input)
            return ImageIcon(image)
        } catch (e: IOException) {
            throw AssertionError("Shouldn't happen")
        }
    }
}

private fun createVerticalJPanel(vararg labels: JLabel): JPanel {
    val panel = JPanel()
    panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
    panel.alignmentX = JPanel.CENTER_ALIGNMENT
    panel.alignmentY = JPanel.CENTER_ALIGNMENT

    labels.forEach { panel.add(it) }
    return panel
}

private fun showTopLevelConfirmDialog(title: String, message: Any?): Int {
    val dummyFrame = JFrame()
    dummyFrame.isAlwaysOnTop = true
    dummyFrame.setLocationRelativeTo(null)

    return JOptionPane.showConfirmDialog(dummyFrame, message, title, JOptionPane.YES_NO_OPTION)
}