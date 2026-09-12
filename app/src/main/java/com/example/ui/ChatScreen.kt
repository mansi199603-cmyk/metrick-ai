package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.model.ChatMessage
import com.example.model.SenderType
import com.example.ui.theme.AiBubbleBorder
import com.example.ui.theme.AiBubbleColor
import com.example.ui.theme.AiBubbleText
import com.example.ui.theme.SkyBlueBackground
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBluePrimaryDark
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.UserBubbleColor
import com.example.ui.theme.UserBubbleText
import com.example.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
  viewModel: ChatViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsState()
  val inputText by viewModel.inputText.collectAsState()
  val isTyping by viewModel.isTyping.collectAsState()
  val listState = rememberLazyListState()

  var showNewChatDialog by remember { mutableStateOf(false) }

  // Auto scroll to bottom whenever messages update or AI is typing
  LaunchedEffect(messages.size, isTyping) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size)
    }
  }

  // Confirmation dialog for New Chat
  if (showNewChatDialog) {
    AlertDialog(
      onDismissRequest = { showNewChatDialog = false },
      title = {
        Text(
          text = stringResource(R.string.dialog_new_chat_title),
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp,
          color = TextDark
        )
      },
      text = {
        Text(
          text = stringResource(R.string.dialog_new_chat_desc),
          fontSize = 16.sp,
          color = TextDark
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.startNewChat()
            showNewChatDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
        ) {
          Text(text = stringResource(R.string.confirm), fontSize = 15.sp)
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewChatDialog = false }) {
          Text(text = stringResource(R.string.cancel), color = TextMuted, fontSize = 15.sp)
        }
      },
      containerColor = SurfaceWhite,
      shape = RoundedCornerShape(20.dp)
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(SkyBlueBackground)
      .statusBarsPadding()
      .imePadding()
  ) {
    // Top Bar
    ChatTopBar(
      onNewChatClick = {
        if (messages.size > 1) {
          showNewChatDialog = true
        } else {
          viewModel.startNewChat()
        }
      }
    )

    // Indeterminate Progress Bar while waiting for AI response
    AnimatedVisibility(visible = isTyping) {
      LinearProgressIndicator(
        modifier = Modifier
          .fillMaxWidth()
          .height(3.5.dp)
          .testTag("ai_loading_progress_bar"),
        color = SkyBluePrimary,
        trackColor = SkyBlueLight.copy(alpha = 0.5f)
      )
    }

    // Message List & Suggestions
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Welcome banner & Suggestions at the top of list
        item(key = "welcome_header") {
          WelcomeBannerCard()
        }

        // Suggestions row
        if (messages.size <= 2) {
          item(key = "suggestions_section") {
            SuggestionsSection(
              suggestions = viewModel.suggestedQuestions,
              onSuggestionClick = { question ->
                viewModel.sendSuggestedQuestion(question)
              }
            )
          }
        }

        // Chat messages
        items(
          items = messages,
          key = { it.id }
        ) { message ->
          ChatMessageItem(message = message)
        }

        // Typing indicator
        if (isTyping) {
          item(key = "typing_indicator") {
            TypingIndicatorItem()
          }
        }

        // Extra spacing at bottom of list
        item(key = "bottom_spacer") {
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }

    // Bottom Input Bar
    ChatInputBar(
      inputText = inputText,
      onTextChanged = viewModel::onInputTextChanged,
      onSend = { viewModel.sendMessage() },
      isTyping = isTyping
    )
  }
}

@Composable
fun ChatTopBar(
  onNewChatClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(elevation = 3.dp, spotColor = SkyBluePrimary.copy(alpha = 0.2f)),
    color = SurfaceWhite
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Robot Avatar + Title + Status
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier.size(46.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_ai_dost_avatar),
            contentDescription = "AI दोस्त अवतार",
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
          // Online green indicator dot
          Box(
            modifier = Modifier
              .size(13.dp)
              .align(Alignment.BottomEnd)
              .background(Color(0xFF4CAF50), CircleShape)
              .clip(CircleShape)
              .padding(2.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = stringResource(R.string.app_name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SkyBluePrimaryDark,
            lineHeight = 24.sp
          )
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .background(Color(0xFF4CAF50), CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = stringResource(R.string.status_online),
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF2E7D32)
            )
            Text(
              text = " • " + stringResource(R.string.status_subtitle),
              fontSize = 13.sp,
              color = TextMuted
            )
          }
        }
      }

      // "नई बातचीत" Button
      Button(
        onClick = onNewChatClick,
        colors = ButtonDefaults.buttonColors(
          containerColor = SkyBlueLight,
          contentColor = SkyBluePrimary
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SkyBlueContainer),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = Modifier.testTag("new_chat_button")
      ) {
        Icon(
          imageVector = Icons.Default.AddComment,
          contentDescription = stringResource(R.string.new_chat),
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = stringResource(R.string.new_chat),
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Composable
fun WelcomeBannerCard(
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
    border = BorderStroke(1.dp, SkyBlueContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(SkyBlueLight.copy(alpha = 0.5f), SurfaceWhite)
          )
        )
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .background(SkyBlueContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = SkyBluePrimary,
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = stringResource(R.string.welcome_greeting),
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = SkyBluePrimaryDark
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = stringResource(R.string.welcome_description),
          fontSize = 14.sp,
          lineHeight = 20.sp,
          color = TextMuted
        )
      }
    }
  }
}

@Composable
fun SuggestionsSection(
  suggestions: List<String>,
  onSuggestionClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = stringResource(R.string.suggestions_title),
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      color = SkyBluePrimaryDark,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
      items(suggestions) { question ->
        Surface(
          color = SurfaceWhite,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, SkyBluePrimary.copy(alpha = 0.35f)),
          shadowElevation = 1.dp,
          modifier = Modifier
            .clickable { onSuggestionClick(question) }
            .testTag("suggestion_chip_$question")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = question,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = SkyBluePrimaryDark
            )
          }
        }
      }
    }
  }
}

@Composable
fun ChatMessageItem(
  message: ChatMessage,
  modifier: Modifier = Modifier
) {
  val isUser = message.sender == SenderType.USER

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    verticalAlignment = Alignment.Bottom
  ) {
    // If AI message, display avatar icon on the left
    if (!isUser) {
      Box(
        modifier = Modifier
          .padding(end = 8.dp, bottom = 2.dp)
          .size(32.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_ai_dost_avatar),
          contentDescription = "AI अवतार",
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )
      }
    }

    // Message Bubble
    Surface(
      color = if (isUser) UserBubbleColor else AiBubbleColor,
      shape = if (isUser) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
      } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
      },
      border = if (isUser) null else BorderStroke(1.dp, AiBubbleBorder),
      shadowElevation = if (isUser) 2.dp else 1.5.dp,
      modifier = Modifier
        .widthIn(max = 290.dp)
        .testTag(if (isUser) "user_message_bubble" else "ai_message_bubble")
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = message.text,
          fontSize = 17.sp,
          lineHeight = 25.sp,
          color = if (isUser) UserBubbleText else AiBubbleText,
          fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = message.timestamp,
          fontSize = 11.sp,
          color = if (isUser) UserBubbleText.copy(alpha = 0.75f) else TextMuted.copy(alpha = 0.8f),
          modifier = Modifier.align(if (isUser) Alignment.End else Alignment.Start)
        )
      }
    }
  }
}

@Composable
fun TypingIndicatorItem(
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "dots")
  val alpha1 by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot1"
  )
  val alpha2 by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, delayMillis = 200),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot2"
  )
  val alpha3 by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, delayMillis = 400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot3"
  )

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.Bottom
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_ai_dost_avatar),
      contentDescription = "AI अवतार",
      modifier = Modifier
        .padding(end = 8.dp, bottom = 2.dp)
        .size(32.dp)
        .clip(CircleShape),
      contentScale = ContentScale.Crop
    )

    Surface(
      color = AiBubbleColor,
      shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp),
      border = BorderStroke(1.dp, AiBubbleBorder),
      shadowElevation = 1.5.dp,
      modifier = Modifier.testTag("ai_typing_bubble")
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(R.string.ai_thinking),
            fontSize = 14.sp,
            color = SkyBluePrimaryDark,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.width(8.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .background(SkyBluePrimary.copy(alpha = alpha1), CircleShape)
            )
            Box(
              modifier = Modifier
                .size(7.dp)
                .background(SkyBluePrimary.copy(alpha = alpha2), CircleShape)
            )
            Box(
              modifier = Modifier
                .size(7.dp)
                .background(SkyBluePrimary.copy(alpha = alpha3), CircleShape)
            )
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          modifier = Modifier
            .width(110.dp)
            .height(2.5.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = SkyBluePrimary,
          trackColor = SkyBlueLight.copy(alpha = 0.5f)
        )
      }
    }
  }
}

@Composable
fun ChatInputBar(
  inputText: String,
  onTextChanged: (String) -> Unit,
  onSend: () -> Unit,
  isTyping: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(elevation = 6.dp, spotColor = SkyBluePrimary.copy(alpha = 0.2f)),
    color = SurfaceWhite
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Question Text Input Field
      OutlinedTextField(
        value = inputText,
        onValueChange = onTextChanged,
        placeholder = {
          Text(
            text = stringResource(R.string.input_placeholder),
            fontSize = 16.sp,
            color = TextMuted
          )
        },
        maxLines = 4,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
          fontSize = 16.sp,
          color = TextDark
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(
          onSend = {
            if (inputText.isNotBlank() && !isTyping) {
              onSend()
            }
          }
        ),
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SkyBlueLight.copy(alpha = 0.35f),
          unfocusedContainerColor = SurfaceWhite,
          focusedBorderColor = SkyBluePrimary,
          unfocusedBorderColor = SkyBlueContainer,
          cursorColor = SkyBluePrimary
        ),
        modifier = Modifier
          .weight(1f)
          .testTag("question_input")
      )

      Spacer(modifier = Modifier.width(10.dp))

      // "भेजें" (Send) Button
      val canSend = inputText.isNotBlank() && !isTyping

      Button(
        onClick = onSend,
        enabled = canSend,
        colors = ButtonDefaults.buttonColors(
          containerColor = SkyBluePrimary,
          contentColor = Color.White,
          disabledContainerColor = SkyBluePrimary.copy(alpha = 0.4f),
          disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        modifier = Modifier
          .height(52.dp)
          .testTag("send_button")
      ) {
        if (isTyping) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Color.White,
            strokeWidth = 2.5.dp
          )
        } else {
          Text(
            text = stringResource(R.string.send),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = stringResource(R.string.send),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
