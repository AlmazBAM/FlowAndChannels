package dictionaryScreenState

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.concurrent.Executors
import javax.swing.*

@OptIn(FlowPreview::class)
object Display {

    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    val repository = Repository

    private val queries = Channel<String>()
    private val state = MutableStateFlow<ScreenState>(ScreenState.Initial)

    init {
        state.onEach { state ->
            when (state) {
                ScreenState.Initial -> {
                    resultArea.text = ""
                    searchButton.isEnabled = false
                }

                ScreenState.Loading -> {
                    resultArea.text = "Loading"
                    searchButton.isEnabled = false
                }

                is ScreenState.Success -> {
                    resultArea.text = state.definitions.joinToString("\n\n")
                    searchButton.isEnabled = true
                }

                ScreenState.Error -> {
                    resultArea.text = "Not Found"
                    searchButton.isEnabled = true
                }
            }
        }.launchIn(scope)

        queries.consumeAsFlow()
            .onEach {
                state.emit(ScreenState.Loading)
            }
            .debounce(500)
            .map { value ->
                if (value.isEmpty())
                    state.emit(ScreenState.Initial)
                else {
                    val result = repository.loadData(value.trim())
                    if (result.isEmpty())
                        state.emit(ScreenState.Error)
                    else
                        state.emit(ScreenState.Success(value, result))
                }
            }
            .launchIn(scope)
    }

    private val enterWordLabel = JLabel("Enter word")
    private val searchField = JTextField(20).apply {
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                loadDefinitions()
            }
        })
    }
    private val searchButton = JButton("Search").apply {
        addActionListener {
            loadDefinitions()
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
    private val mainFrame = JFrame("Dictionary app").apply {
        layout = BorderLayout()
        add(topPanel, BorderLayout.NORTH)
        add(JScrollPane(resultArea), BorderLayout.CENTER)
        pack()
    }

    private fun loadDefinitions() {
        scope.launch { queries.send(searchField.text.trim()) }
    }

    fun show() {
        mainFrame.isVisible = true
    }
}

fun main() {
    Display.show()
}