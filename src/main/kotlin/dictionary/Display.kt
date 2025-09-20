package dictionary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.util.concurrent.Executors
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField

object Display {

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    val repository = Repository

    private val enterWordLabel = JLabel("Enter word")
    private val searchField = JTextField(20)
    private val searchButton = JButton("Search").apply {
        addActionListener {
            scope.launch {
                isEnabled = false
                resultArea.text = "Loading"
                resultArea.text = repository.loadData(searchField.text.trim()).joinToString("\n\n").ifEmpty { "Not Found" }
                isEnabled = true
            }
        }
    }
    private val resultArea = JTextArea(25, 30).apply {
        lineWrap = true
        wrapStyleWord = true
        isEditable = false
    }
    private val topPanel = JPanel().apply {
        add(enterWordLabel)
        add(searchField)
        add(searchButton)
    }
    private val mainFrame = JFrame("Dictionaru app").apply {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(resultArea), BorderLayout.CENTER)
        pack()
    }

    fun show() {
        mainFrame.isVisible = true
    }
}

fun main() {
    Display.show()
}