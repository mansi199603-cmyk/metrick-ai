package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ChatMessage
import com.example.model.SenderType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

  private val initialGreeting = ChatMessage(
    text = "नमस्ते! 🙏 मैं हूँ आपका “मेरा AI दोस्त”। आप मुझसे हिंदी में कोई भी सवाल पूछ सकते हैं। मैं आपकी हरसंभव मदद करने के लिए यहाँ हूँ!",
    sender = SenderType.AI
  )

  private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(initialGreeting))
  val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

  private val _inputText = MutableStateFlow("")
  val inputText: StateFlow<String> = _inputText.asStateFlow()

  private val _isTyping = MutableStateFlow(false)
  val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

  val suggestedQuestions = listOf(
    "नमस्ते! आप क्या-क्या कर सकते हैं?",
    "आज का एक अच्छा सुविचार बताइए ✨",
    "एक छोटी और प्रेरणादायक कहानी सुनाइए 📖",
    "समय का सही उपयोग कैसे करें? ⏰",
    "स्वस्थ रहने के आसान उपाय क्या हैं? 🥗"
  )

  fun onInputTextChanged(text: String) {
    _inputText.value = text
  }

  fun sendMessage(userText: String = _inputText.value) {
    val trimmed = userText.trim()
    if (trimmed.isEmpty() || _isTyping.value) return

    val userMessage = ChatMessage(
      text = trimmed,
      sender = SenderType.USER
    )

    _messages.value = _messages.value + userMessage
    _inputText.value = ""
    _isTyping.value = true

    viewModelScope.launch {
      // Simulate natural thinking delay
      delay(900)
      val aiResponseText = generateHindiResponse(trimmed)
      val aiMessage = ChatMessage(
        text = aiResponseText,
        sender = SenderType.AI
      )
      _messages.value = _messages.value + aiMessage
      _isTyping.value = false
    }
  }

  fun sendSuggestedQuestion(question: String) {
    sendMessage(question)
  }

  fun startNewChat() {
    _messages.value = listOf(
      ChatMessage(
        text = "नई बातचीत शुरू हो गई है! नमस्ते, आज आपका दिन कैसा रहा? आप क्या जानना चाहते हैं?",
        sender = SenderType.AI
      )
    )
    _inputText.value = ""
    _isTyping.value = false
  }

  private fun generateHindiResponse(query: String): String {
    val lower = query.lowercase()
    return when {
      lower.contains("नमस्ते") || lower.contains("हेलो") || lower.contains("hi") || lower.contains("hello") || lower.contains("प्रणाम") -> {
        "नमस्ते! आपका दिन शुभ हो। मैं आपका AI दोस्त हमेशा आपके साथ हूँ। बताइए, आज आपके मन में क्या विचार या सवाल है?"
      }
      lower.contains("क्या कर सकते") || lower.contains("मदद") || lower.contains("काम") -> {
        "मैं आपके लिए कई चीज़ें कर सकता हूँ:\n\n• रोचक कहानियाँ और प्रेरक प्रसंग सुनाना\n• सामान्य ज्ञान और विज्ञान के प्रश्नों के उत्तर देना\n• हिंदी निबंध, पत्र या कविताएँ लिखना\n• स्वास्थ्य, पढ़ाई और दैनिक जीवन की उपयोगी सलाह देना\n\nआप बेझिझक कोई भी सवाल पूछिए!"
      }
      lower.contains("सुविचार") || lower.contains("विचार") || lower.contains("quote") || lower.contains("प्रेरणा") -> {
        "आज का अनमोल विचार:\n\n“सफलता की शुरुआत हमेशा एक छोटे कदम से होती है। यदि आप आज कुछ नया सीखने का प्रयास करते हैं, तो कल का दिन बेहतर अवश्य होगा।”\n\nकर्म करते रहें और सकारात्मक सोच बनाए रखें!"
      }
      lower.contains("कहानी") || lower.contains("story") -> {
        "एक बार एक छोटे गाँव में दीपक नाम का एक मेहनती लड़का रहता था। वह मिट्टी के सुंदर दीये बनाता था। एक बार तेज़ आंधी आई और उसकी सारी मेहनत पर पानी फिरने लगा। उसने हार नहीं मानी और एक नया मजबूत दीया बनाया जो हवा में भी जलता रहा।\n\nसीख: परिस्थितियाँ चाहे कितनी भी कठिन हों, निरंतर प्रयास से हर अंधकार को दूर किया जा सकता है।"
      }
      lower.contains("समय") || lower.contains("टाइम") -> {
        "समय प्रबंधन के कुछ आसान नियम:\n\n1. दिन की शुरुआत में 3 मुख्य काम तय करें।\n2. काम के बीच 5 मिनट का छोटा विश्राम लें।\n3. गैर-ज़रूरी फोन के इस्तेमाल से बचें।\n4. जो काम आज हो सकता है, उसे कल पर न टालें।"
      }
      lower.contains("स्वस्थ") || lower.contains("स्वास्थ्य") || lower.contains("सेहत") -> {
        "अच्छे स्वास्थ्य के लिए 4 मूल मंत्र:\n\n1. रोज़ाना कम से कम 7-8 गिलास पानी पिएँ।\n2. 20-30 मिनट पैदल चलें या हल्का व्यायाम करें।\n3. हरी सब्ज़ियाँ और मौसमी फल आहार में शामिल करें।\n4. प्रतिदिन 7-8 घंटे की गहरी नींद लें।"
      }
      lower.contains("दोस्त") || lower.contains("नाम") -> {
        "मेरा नाम “मेरा AI दोस्त” है! मैं एक हिंदी भाषी सहायक हूँ जो हर समय आपके सवालों के सरल और सटीक जवाब देने के लिए तत्पर रहता हूँ।"
      }
      lower.contains("धन्यवाद") || lower.contains("शुक्रिया") || lower.contains("thanks") -> {
        "आपका बहुत-बहुत स्वागत है! यदि आपके पास कोई और सवाल हो, तो बिना किसी झिझक के पूछ सकते हैं। मैं हमेशा आपकी सेवा में उपस्थित हूँ।"
      }
      else -> {
        "आपका सवाल “$query” बहुत ही रोचक है!\n\nहिंदी में आपके इस विषय पर विचार करना सुखद अनुभव है। मैं आपकी हर जिज्ञासा का समाधान करने के लिए यहाँ हूँ। क्या आप इस बारे में कुछ और विस्तार से जानना चाहते हैं?"
      }
    }
  }
}
