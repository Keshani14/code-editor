package com.example.codeeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeeditor.ui.theme.CodeEditorTheme
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import com.example.codeeditor.SyntaxHighlighter
import com.example.codeeditor.getSyntaxRulesForFile

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var fileManager: FileManager
    private var currentFileName by mutableStateOf("Untitled")
    private val editorState = TextEditorState()
    private var syntaxRules by mutableStateOf<SyntaxRules?>(null)

    override fun onPause() {
        super.onPause()
        saveFile(currentFileName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        syntaxRules = loadSyntaxRules(this, "python.json")
        setContent {
            val clipboardManager = LocalClipboardManager.current
            var showMiniToolbar by remember { mutableStateOf(false) }
            var showFindReplace by remember { mutableStateOf(false) }
            var showCompilerInterface by remember { mutableStateOf(false) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            var compileOutput by remember { mutableStateOf("") }
        
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            LaunchedEffect(editorState.textField.value) {
                snapshotFlow { editorState.textField.value }
                    .debounce(500)
                    .collect {
                        try {
                            editorState.commitChange()
                            saveFile(currentFileName)
                        } catch (e: Exception) {
                            // Log error but don't crash the app
                            android.util.Log.e("MainActivity", "Auto-save error: ${e.message}")
                        }
                    }
            }

            fileManager = FileManager(context)
            CodeEditorTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = { 
                        DrawerContent(
                            initialFileName = currentFileName,
                            context = this,
                            onNewFile = { createNewFile(it) },
                            onOpenFile = { openFile(it) },
                            onSaveFile = { saveFile(it) }
                        )
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { 
                                    Text(text = currentFileName)
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch { drawerState.open() }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu Bar"
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { showMiniToolbar = !showMiniToolbar }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editing Option"
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            BottomAppBar(
                                actions = {
                                    IconButton(
                                        onClick = { showFindReplace = !showFindReplace },
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Find",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    
                                    androidx.compose.foundation.layout.Spacer(
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    IconButton(
                                        onClick = { editorState.undo() },
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.undo),
                                            contentDescription = "Undo",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { editorState.redo() },
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.redo),
                                            contentDescription = "Redo",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    
                                    androidx.compose.foundation.layout.Spacer(
                                        modifier = Modifier.weight(1f)
                                    )
                                },
                                floatingActionButton = {
                                    IconButton(
                                        onClick = {
                                            compileCode(context, editorState.textField.value.text, fileManager, currentFileName) { output ->
                                                compileOutput = output
                                                showCompilerInterface = true
                                            }
                                        },
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Compile",
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            if (showCompilerInterface) {
                                CompilerInterface(clipboardManager, compileOutput, onClose = { showCompilerInterface = false })
                            }

                            if (showFindReplace) {
                                FindReplaceBar(editorState = editorState, onClose = {
                                    showFindReplace = false
                                })
                            }

                            if (showMiniToolbar) {
                                MiniToolbar(
                                    onCut = { cutText(editorState.textField.value, { editorState.onTextChange(it) }, clipboardManager) },
                                    onCopy = { copyText(editorState.textField.value, clipboardManager) },
                                    onPaste = { pasteText(editorState.textField.value, { editorState.onTextChange(it) }, clipboardManager) }
                                )
                            }

                            CodeEditor(
                                modifier = Modifier.weight(1f),
                                editorState = editorState,
                                syntaxRules = syntaxRules ?: loadSyntaxRules(context, "python.json")
                            )
                            DocumentStatistics(editorState.textField.value.text)
                        }
                    }
                }
            }
        }
    }

    private fun createNewFile(filename: String) {
        val file = fileManager.createNewFile(filename)
        currentFileName = file
        editorState.textField.value = TextFieldValue("")
        // Update syntax rules based on file type
        syntaxRules = getSyntaxRulesForFile(this, filename)
    }

    private fun saveFile(filename: String) {
        fileManager.saveFile(filename, editorState.textField.value.text)
    }

    private fun openFile(filename: String) {
        val content = fileManager.openFile(filename)
        editorState.textField.value = TextFieldValue(content)
        currentFileName = filename
        // Update syntax rules based on file type
        syntaxRules = getSyntaxRulesForFile(this, filename)
    }
    
    private fun applyIndent(editorState: TextEditorState) {
        val currentText = editorState.textField.value
        val selection = currentText.selection
        
        if (selection.start == selection.end) {
            // No selection, insert spaces at cursor position
            val spaces = "    " // 4 spaces for indentation
            val newText = currentText.text.substring(0, selection.start) + spaces + currentText.text.substring(selection.start)
            val newSelection = selection.start + spaces.length
            editorState.textField.value = currentText.copy(
                text = newText,
                selection = androidx.compose.ui.text.TextRange(newSelection)
            )
        } else {
            // Selection exists, indent each selected line
            val lines = currentText.text.split("\n")
            val startLine = currentText.text.substring(0, selection.start).count { it == '\n' }
            val endLine = currentText.text.substring(0, selection.end).count { it == '\n' }
            
            val newLines = lines.mapIndexed { index, line ->
                if (index in startLine..endLine) {
                    "    $line"
                } else {
                    line
                }
            }
            
            val newText = newLines.joinToString("\n")
            val newSelection = selection.start + (endLine - startLine + 1) * 4
            editorState.textField.value = currentText.copy(
                text = newText,
                selection = androidx.compose.ui.text.TextRange(newSelection)
            )
        }
    }
    
    private fun applyDedent(editorState: TextEditorState) {
        val currentText = editorState.textField.value
        val selection = currentText.selection
        
        if (selection.start == selection.end) {
            // No selection, remove spaces at cursor position
            val beforeCursor = currentText.text.substring(0, selection.start)
            val lastNewlineIndex = beforeCursor.lastIndexOf('\n')
            val lineStart = if (lastNewlineIndex == -1) 0 else lastNewlineIndex + 1
            val currentLine = currentText.text.substring(lineStart, selection.start)
            
            if (currentLine.startsWith("    ")) {
                val newText = currentText.text.substring(0, lineStart) + currentLine.substring(4) + currentText.text.substring(selection.start)
                val newSelection = selection.start - 4
                editorState.textField.value = currentText.copy(
                    text = newText,
                    selection = androidx.compose.ui.text.TextRange(newSelection)
                )
            }
        } else {
            // Selection exists, dedent each selected line
            val lines = currentText.text.split("\n")
            val startLine = currentText.text.substring(0, selection.start).count { it == '\n' }
            val endLine = currentText.text.substring(0, selection.end).count { it == '\n' }
            
            val newLines = lines.mapIndexed { index, line ->
                if (index in startLine..endLine && line.startsWith("    ")) {
                    line.substring(4)
                } else {
                    line
                }
            }
            
            val newText = newLines.joinToString("\n")
            val newSelection = selection.start - (endLine - startLine + 1) * 4
            editorState.textField.value = currentText.copy(
                text = newText,
                selection = androidx.compose.ui.text.TextRange(newSelection)
            )
        }
    }
    
    private fun applyComment(editorState: TextEditorState) {
        val currentText = editorState.textField.value
        val selection = currentText.selection
        
        if (selection.start == selection.end) {
            // No selection, comment the current line
            val lineStart = if (selection.start > 0) {
                currentText.text.lastIndexOf('\n', selection.start - 1) + 1
            } else {
                0
            }
            
            val lineEnd = currentText.text.indexOf('\n', selection.start)
            val lineEndPos = if (lineEnd == -1) currentText.text.length else lineEnd
            
            val currentLine = currentText.text.substring(lineStart, lineEndPos)
            val commentPrefix = "// "
            
            if (currentLine.startsWith(commentPrefix)) {
                // Uncomment
                val uncommentedLine = currentLine.substring(commentPrefix.length)
                val newText = currentText.text.substring(0, lineStart) + uncommentedLine + currentText.text.substring(lineEndPos)
                val newSelection = selection.start - commentPrefix.length
                editorState.textField.value = currentText.copy(
                    text = newText,
                    selection = androidx.compose.ui.text.TextRange(newSelection)
                )
            } else {
                // Comment
                val commentedLine = commentPrefix + currentLine
                val newText = currentText.text.substring(0, lineStart) + commentedLine + currentText.text.substring(lineEndPos)
                val newSelection = selection.start + commentPrefix.length
                editorState.textField.value = currentText.copy(
                    text = newText,
                    selection = androidx.compose.ui.text.TextRange(newSelection)
                )
            }
        } else {
            // Selection exists, comment/uncomment each selected line
            val lines = currentText.text.split("\n")
            val startLine = currentText.text.substring(0, selection.start).count { it == '\n' }
            val endLine = currentText.text.substring(0, selection.end).count { it == '\n' }
            
            val commentPrefix = "// "
            val allLinesCommented = (startLine..endLine).all { index ->
                lines[index].startsWith(commentPrefix)
            }
            
            val newLines = lines.mapIndexed { index, line ->
                if (index in startLine..endLine) {
                    if (allLinesCommented) {
                        // Uncomment all
                        if (line.startsWith(commentPrefix)) line.substring(commentPrefix.length) else line
                    } else {
                        // Comment all
                        if (line.startsWith(commentPrefix)) line else commentPrefix + line
                    }
                } else {
                    line
                }
            }
            
            val newText = newLines.joinToString("\n")
            val newSelection = if (allLinesCommented) {
                selection.start - (endLine - startLine + 1) * commentPrefix.length
            } else {
                selection.start + (endLine - startLine + 1) * commentPrefix.length
            }
            
            editorState.textField.value = currentText.copy(
                text = newText,
                selection = androidx.compose.ui.text.TextRange(newSelection)
            )
        }
    }
}

@Composable
fun CodeEditor(
    modifier: Modifier,
    editorState: TextEditorState,
    syntaxRules: SyntaxRules
) {
    // Detect system theme
    val isSystemDarkMode = androidx.compose.foundation.isSystemInDarkTheme()
    
    val backgroundColor = if (isSystemDarkMode) androidx.compose.ui.graphics.Color(0xFF1E1E1E) else androidx.compose.ui.graphics.Color.White
    val textColor = if (isSystemDarkMode) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black
    val placeholderColor = if (isSystemDarkMode) androidx.compose.ui.graphics.Color(0xFF858585) else androidx.compose.ui.graphics.Color.Gray
    
    val syntaxHighlighter = remember { SyntaxHighlighter() }
    val highlightedText = remember(editorState.textField.value.text, syntaxRules, isSystemDarkMode) {
        try {
            syntaxHighlighter.highlightCode(editorState.textField.value.text, syntaxRules, isSystemDarkMode)
        } catch (e: Exception) {
            // Fallback to plain text if highlighting fails
            androidx.compose.ui.text.AnnotatedString(editorState.textField.value.text)
        }
    }
    
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        if (editorState.textField.value.text.isEmpty()) {
            androidx.compose.material3.Text(
                text = "Type your code here...",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    color = placeholderColor,
                    lineHeight = 24.sp
                )
            )
        }
        
        // Display layer: Syntax highlighted text (non-interactive)
        if (editorState.textField.value.text.isNotEmpty()) {
            androidx.compose.material3.Text(
                text = highlightedText,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Input layer: Transparent text field for user input
        androidx.compose.foundation.text.BasicTextField(
            value = editorState.textField.value,
            onValueChange = { editorState.onTextChange(it) },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                color = androidx.compose.ui.graphics.Color.Transparent,
                lineHeight = 24.sp
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(textColor),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DocumentStatistics(
    text: String
) {
    val characters = text.length
    val words = if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
    val lines = if (text.isEmpty()) 1 else text.count { it == '\n' } + 1
    
    val backgroundColor = androidx.compose.ui.graphics.Color(0xFFF5F5F5)
    val textColor = androidx.compose.ui.graphics.Color.Black
    val numberColor = androidx.compose.ui.graphics.Color(0xFF2196F3)
    val labelColor = androidx.compose.ui.graphics.Color(0xFF666666)
    
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.Text(
                text = "Document Statistics",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = textColor
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
            ) {
                StatisticItem("Characters", characters.toString(), numberColor, labelColor)
                StatisticItem("Words", words.toString(), numberColor, labelColor)
                StatisticItem("Lines", lines.toString(), numberColor, labelColor)
            }
        }
    }
}

@Composable
fun StatisticItem(
    label: String,
    value: String,
    numberColor: androidx.compose.ui.graphics.Color,
    labelColor: androidx.compose.ui.graphics.Color
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = value,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = numberColor
            )
        )
        androidx.compose.material3.Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                color = labelColor
            )
        )
    }
}
