package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isTyping: Boolean = false,
    val currentLanguage: AppLanguage = AppLanguage.HINDI,
    val suggestions: List<String> = emptyList()
)

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        startNewChat()
    }

    fun onInputTextChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    fun toggleLanguage() {
        val nextLang = if (_uiState.value.currentLanguage == AppLanguage.HINDI) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.HINDI
        }
        setLanguage(nextLang)
    }

    fun setLanguage(language: AppLanguage) {
        _uiState.update { current ->
            current.copy(
                currentLanguage = language,
                suggestions = getSuggestionsFor(language)
            )
        }
    }

    fun startNewChat() {
        val lang = _uiState.value.currentLanguage
        val welcomeText = if (lang == AppLanguage.HINDI) {
            "नमस्ते! मैं आपका AI दोस्त हूँ।\nआप मुझसे हिंदी या अंग्रेज़ी में कोई भी सवाल पूछ सकते हैं। आज मैं आपकी क्या मदद करूँ?"
        } else {
            "Hello! I am your AI friend, \"Mera AI Dost\".\nYou can ask me anything in Hindi or English. How can I help you today?"
        }

        val initialMessage = ChatMessage(
            text = welcomeText,
            isUser = false,
            language = lang
        )

        _uiState.update {
            it.copy(
                messages = listOf(initialMessage),
                inputText = "",
                isTyping = false,
                suggestions = getSuggestionsFor(lang)
            )
        }
    }

    fun sendMessage(textOverride: String? = null) {
        val textToSend = (textOverride ?: _uiState.value.inputText).trim()
        if (textToSend.isBlank() || _uiState.value.isTyping) return

        val detectedLang = repository.detectLanguage(textToSend, _uiState.value.currentLanguage)

        val userMessage = ChatMessage(
            text = textToSend,
            isUser = true,
            language = detectedLang
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isTyping = true
            )
        }

        viewModelScope.launch {
            try {
                val replyText = repository.getResponse(textToSend, detectedLang)
                val aiMessage = ChatMessage(
                    text = replyText,
                    isUser = false,
                    language = detectedLang
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + aiMessage,
                        isTyping = false
                    )
                }
            } catch (e: Exception) {
                val errorText = if (detectedLang == AppLanguage.HINDI) {
                    "माफ़ कीजिए, अभी जवाब देने में समस्या आई। कृपया दोबारा प्रयास करें।"
                } else {
                    "Sorry, I encountered an issue replying. Please try again."
                }
                val errorMessage = ChatMessage(
                    text = errorText,
                    isUser = false,
                    language = detectedLang
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + errorMessage,
                        isTyping = false
                    )
                }
            }
        }
    }

    private fun getSuggestionsFor(lang: AppLanguage): List<String> {
        return if (lang == AppLanguage.HINDI) {
            listOf(
                "नमस्ते! आप क्या कर सकते हैं?",
                "मुझे एक अच्छी प्रेरक कहानी सुनाओ",
                "कोई मज़ेदार चुटकुला सुनाओ",
                "स्वस्थ रहने के 3 आसान नियम बताओ",
                "आज का विचार क्या है?"
            )
        } else {
            listOf(
                "Hello! What can you do?",
                "Tell me an inspiring story",
                "Tell me a funny joke",
                "Share 3 daily health tips",
                "What is today's thought?"
            )
        }
    }
}
