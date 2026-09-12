package com.example.data

import kotlinx.coroutines.delay

class ChatRepository {

    /**
     * Determines whether text is predominantly Hindi or English.
     */
    fun detectLanguage(text: String, fallback: AppLanguage): AppLanguage {
        val devanagariCount = text.count { it in '\u0900'..'\u097F' }
        val latinCount = text.count { it in 'a'..'z' || it in 'A'..'Z' }

        return when {
            devanagariCount > 0 && devanagariCount >= latinCount / 2 -> AppLanguage.HINDI
            latinCount > 0 -> AppLanguage.ENGLISH
            else -> fallback
        }
    }

    /**
     * Generates a friendly, simple response in Hindi or English.
     */
    suspend fun getResponse(userMessage: String, preferredLang: AppLanguage): String {
        // Natural typing pause
        delay(600)

        val trimmed = userMessage.trim().lowercase()
        val lang = detectLanguage(userMessage, preferredLang)

        return if (lang == AppLanguage.HINDI) {
            generateHindiResponse(trimmed, userMessage)
        } else {
            generateEnglishResponse(trimmed, userMessage)
        }
    }

    private fun generateHindiResponse(clean: String, original: String): String {
        return when {
            clean.contains("नमस्ते") || clean.contains("प्रणाम") || clean.contains("हेलो") || clean.contains("हाय") || clean.contains("hello") || clean.contains("namaste") -> {
                "नमस्ते! मैं आपका AI दोस्त हूँ। आपका दिन कैसा बीत रहा है? आज मैं आपकी क्या मदद कर सकता हूँ?"
            }
            clean.contains("नाम क्या") || clean.contains("तुम कौन") || clean.contains("आप कौन") -> {
                "मेरा नाम “मेरा AI दोस्त” है! मैं आपकी बात सुनने, सवालों के आसान जवाब देने और अच्छी बातें साझा करने के लिए हमेशा यहाँ हूँ।"
            }
            clean.contains("कैसे हो") || clean.contains("कैसी हो") || clean.contains("हाल चाल") -> {
                "मैं बहुत अच्छा हूँ, धन्यवाद! आप कैसे हैं? आशा करता हूँ कि आप खुश और स्वस्थ हैं।"
            }
            clean.contains("कहानी") || clean.contains("कथा") -> {
                "एक बार की बात है, एक छोटे से गाँव में एक जिज्ञासु बालक रहता था। वह रोज़ नए पेड़ लगाता और उनकी देखभाल करता। गाँव वाले कहते कि इसमें बहुत मेहनत लगती है। लेकिन कुछ ही सालों में वो पेड़ बड़े हो गए और पूरे गाँव को ठंडी छाँव और मीठे फल देने लगे। सीख: आज की छोटी सी अच्छी कोशिश कल बहुत बड़ा फल देती है!"
            }
            clean.contains("चुटकुला") || clean.contains("हँसाओ") || clean.contains("मजाक") -> {
                "अध्यापक: अगर तुम्हारे पास 5 सेब हैं और 2 मैंने ले लिए, तो क्या बचेगा?\nपप्पू: सर, बचेगी सिर्फ आपकी डाँट और मेरी रोनी सूरत! 😄"
            }
            clean.contains("विचार") || clean.contains("सुविचार") || clean.contains("प्रेरणा") -> {
                "आज का विचार:\n“हर नई सुबह अपने साथ नई उम्मीदें लाती है। बस खुद पर भरोसा रखें और मुस्कुराते हुए आगे बढ़ें।”"
            }
            clean.contains("स्वास्थ्य") || clean.contains("सेहत") || clean.contains("नियम") -> {
                "स्वस्थ रहने के 3 आसान नियम:\n1. रोज़ाना पर्याप्त पानी पिएँ।\n2. ताज़ा भोजन करें और फल खाएँ।\n3. कम से कम 7-8 घंटे की अच्छी नींद लें और थोड़ा टहलें।"
            }
            clean.contains("दोस्त") || clean.contains("दोस्ती") -> {
                "सच्चा दोस्त वही होता है जो हर हाल में आपका साथ दे और आपकी खुशी में खुश हो। मैं हमेशा आपके लिए एक अच्छा दोस्त बना रहूँगा!"
            }
            clean.contains("मदद") || clean.contains("क्या कर सकते") -> {
                "मैं आपके किसी भी सवाल का आसान हिंदी में जवाब दे सकता हूँ, कहानियाँ सुना सकता हूँ, पढ़ाई में मदद कर सकता हूँ और अच्छी बातचीत कर सकता हूँ। कुछ भी पूछिए!"
            }
            clean.contains("धन्यवाद") || clean.contains("शुक्रिया") || clean.contains("thank") -> {
                "आपका बहुत-बहुत स्वागत है! अगर कोई और सवाल हो, तो बेझिझक पूछिए। दोस्त तो हमेशा मदद के लिए तैयार रहते हैं!"
            }
            clean.contains("अलविदा") || clean.contains("बाय") || clean.contains("bye") -> {
                "अलविदा! अपना ख्याल रखिएगा और जब भी बात करने का मन हो, मुझे याद कीजिएगा। शुभ दिन!"
            }
            else -> {
                "आपने बहुत अच्छा सवाल पूछा है: “$original”\n\nमेरी समझ से, किसी भी चीज़ को समझने के लिए सरल सोच और धैर्य सबसे ज़रूरी है। अगर आप इसके बारे में थोड़ा और विस्तार से बताएंगे, तो मैं आपको और बेहतर जानकारी दे सकूँगा। आप और क्या जानना चाहते हैं?"
            }
        }
    }

    private fun generateEnglishResponse(clean: String, original: String): String {
        return when {
            clean.contains("hello") || clean.contains("hi") || clean.contains("hey") -> {
                "Hello! I am your AI friend, \"Mera AI Dost\". How are you doing today? How can I help you?"
            }
            clean.contains("name") || clean.contains("who are you") -> {
                "My name is \"Mera AI Dost\"! I am here to chat with you, answer your questions in simple words, and be a helpful friend."
            }
            clean.contains("how are you") || clean.contains("how do you do") -> {
                "I am doing great, thank you for asking! How are you feeling today? I hope you're having a wonderful day."
            }
            clean.contains("story") -> {
                "Once upon a time, a little bird wanted to build the strongest nest in the forest. Instead of rushing, it chose each twig with patience and care. When a big storm came, its nest stayed safe and sound. Moral: Patience and steady effort always protect us."
            }
            clean.contains("joke") || clean.contains("funny") -> {
                "Why can't bicycles stand up by themselves?\nBecause they are two-tired! 😄"
            }
            clean.contains("quote") || clean.contains("thought") || clean.contains("motivation") -> {
                "Thought for the day:\n\"Every morning brings new potential. Believe in yourself, take small steps, and keep moving forward with a smile.\""
            }
            clean.contains("health") || clean.contains("tips") -> {
                "Here are 3 simple daily health tips:\n1. Drink plenty of clean water throughout the day.\n2. Eat fresh vegetables, fruits, and home-cooked meals.\n3. Get 7-8 hours of peaceful sleep and take a brisk daily walk."
            }
            clean.contains("what can you do") || clean.contains("help") -> {
                "I can chat with you in both Hindi and English, explain concepts simply, tell stories, share jokes, and help you brainstorm ideas. Feel free to ask anything!"
            }
            clean.contains("thank") -> {
                "You are most welcome! I am always happy to help. What else is on your mind?"
            }
            clean.contains("bye") || clean.contains("goodbye") -> {
                "Goodbye! Take good care of yourself, and feel free to return whenever you'd like to chat. Have a wonderful day!"
            }
            else -> {
                "That's a thoughtful question about: \"$original\"\n\nTo answer simply: keeping an open, curious mind and focusing on key basics helps solve almost anything. Feel free to ask more details or let me know how you'd like to explore this further!"
            }
        }
    }
}
