package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AppNotificationEntity
import com.example.data.local.ChallengeEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DuelEntity
import com.example.data.local.FriendEntity
import com.example.data.local.UserEntity
import com.example.data.remote.DrawingEvaluationResult
import com.example.data.remote.GeminiService
import com.example.data.remote.SongEvaluationResult
import com.example.data.repository.ChallengeRepository
import com.example.notification.ChallengeNotificationManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface Screen {
    data object Home : Screen
    data class DrawingChallenge(val challenge: ChallengeEntity) : Screen
    data class SongGuessChallenge(val challenge: ChallengeEntity) : Screen
    data object Leaderboard : Screen
    data object FriendsDuels : Screen
    data class Chat(val conversationId: String, val conversationTitle: String) : Screen
    data object Notifications : Screen
    data object CreateChallenge : Screen
    data object Profile : Screen
    data object PartyGamesHub : Screen
    data object UndercoverSpy : Screen
    data object WordBomb : Screen
    data object GrabTheMic : Screen
}

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val geminiService = GeminiService()
    private val notificationManager = ChallengeNotificationManager(application)
    val repository = ChallengeRepository(database, geminiService, notificationManager)

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow(Screen.Home)

    private val _activeScreen = MutableStateFlow<Screen>(Screen.Home)
    val activeScreen: StateFlow<Screen> = _activeScreen.asStateFlow()

    // Confetti celebration trigger event
    private val _celebrationEvents = MutableSharedFlow<String>()
    val celebrationEvents: SharedFlow<String> = _celebrationEvents.asSharedFlow()

    // Loading & Generating state
    private val _isGeneratingChallenge = MutableStateFlow(false)
    val isGeneratingChallenge: StateFlow<Boolean> = _isGeneratingChallenge.asStateFlow()

    private val _isEvaluatingDrawing = MutableStateFlow(false)
    val isEvaluatingDrawing: StateFlow<Boolean> = _isEvaluatingDrawing.asStateFlow()

    private val _lastDrawingResult = MutableStateFlow<DrawingEvaluationResult?>(null)
    val lastDrawingResult: StateFlow<DrawingEvaluationResult?> = _lastDrawingResult.asStateFlow()

    private val _lastSongResult = MutableStateFlow<SongEvaluationResult?>(null)
    val lastSongResult: StateFlow<SongEvaluationResult?> = _lastSongResult.asStateFlow()

    // Active Chat Conversation
    private val _selectedConversationId = MutableStateFlow("GLOBAL_SQUAD")
    val selectedConversationId: StateFlow<String> = _selectedConversationId.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _activeChatMessages.asStateFlow()

    // Data streams from repository
    val user: StateFlow<UserEntity?> = repository.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val challenges: StateFlow<List<ChallengeEntity>> = repository.allChallengesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyChallenge: StateFlow<ChallengeEntity?> = repository.dailyChallengeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val friends: StateFlow<List<FriendEntity>> = repository.friendsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val duels: StateFlow<List<DuelEntity>> = repository.duelsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notifications: StateFlow<List<AppNotificationEntity>> = repository.notificationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadNotifsCount: StateFlow<Int> = repository.unreadNotifsCountFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val achievements: StateFlow<List<com.example.data.local.AchievementEntity>> = repository.achievementsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val completedChallenges: StateFlow<List<ChallengeEntity>> = repository.completedChallengesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
        observeChatConversation("GLOBAL_SQUAD")
    }

    fun navigateTo(screen: Screen) {
        val current = _screenStack.value
        _screenStack.value = current + screen
        _activeScreen.value = screen
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        if (current.size > 1) {
            val updated = current.dropLast(1)
            _screenStack.value = updated
            _activeScreen.value = updated.last()
            return true
        }
        return false
    }

    fun openConversation(convId: String, title: String) {
        _selectedConversationId.value = convId
        observeChatConversation(convId)
        navigateTo(Screen.Chat(convId, title))
    }

    private fun observeChatConversation(convId: String) {
        viewModelScope.launch {
            repository.getChatMessages(convId).collect { msgs ->
                _activeChatMessages.value = msgs
            }
        }
    }

    fun generateNewChallenge(category: String, topic: String? = null) {
        viewModelScope.launch {
            _isGeneratingChallenge.value = true
            val challenge = repository.generateNewDailyChallenge(category, topic)
            _isGeneratingChallenge.value = false
            when (challenge.category) {
                "DRAWING_30S" -> navigateTo(Screen.DrawingChallenge(challenge))
                "GUESS_SONG" -> navigateTo(Screen.SongGuessChallenge(challenge))
                else -> navigateTo(Screen.DrawingChallenge(challenge))
            }
        }
    }

    fun evaluateAndSubmitDrawing(
        challenge: ChallengeEntity,
        strokeCount: Int,
        totalPoints: Int,
        secondsSpent: Int
    ) {
        viewModelScope.launch {
            _isEvaluatingDrawing.value = true
            val result = repository.evaluateDrawing(
                subject = challenge.targetAnswer,
                strokeCount = strokeCount,
                totalPoints = totalPoints,
                secondsSpent = secondsSpent
            )
            _lastDrawingResult.value = result
            _isEvaluatingDrawing.value = false

            // Mark challenge completed
            repository.completeChallenge(
                challengeId = challenge.id,
                score = result.score,
                feedback = "${result.titleBadge}: ${result.commentary}",
                durationSecs = secondsSpent
            )

            // Trigger confetti event!
            _celebrationEvents.emit("WIN_DRAWING")
        }
    }

    fun clearDrawingResult() {
        _lastDrawingResult.value = null
    }

    fun evaluateAndSubmitSongGuess(
        challenge: ChallengeEntity,
        userGuess: String,
        hintsUsed: Int,
        secondsSpent: Int
    ) {
        viewModelScope.launch {
            val result = repository.evaluateSongGuess(
                targetSong = challenge.targetAnswer,
                userGuess = userGuess,
                hintsUsed = hintsUsed
            )
            _lastSongResult.value = result

            if (result.isCorrect) {
                repository.completeChallenge(
                    challengeId = challenge.id,
                    score = result.score,
                    feedback = result.feedback,
                    durationSecs = secondsSpent
                )
                _celebrationEvents.emit("WIN_SONG")
            }
        }
    }

    fun clearSongResult() {
        _lastSongResult.value = null
    }

    fun sendFriendDuel(friend: FriendEntity, challenge: ChallengeEntity, score: Int) {
        viewModelScope.launch {
            repository.sendFriendDuel(friend, challenge, score)
            _celebrationEvents.emit("DUEL_SENT")
        }
    }

    fun sendChatMessage(text: String, celebrationType: String = "NONE", score: Int? = null) {
        viewModelScope.launch {
            val convId = _selectedConversationId.value
            repository.sendChatMessage(
                conversationId = convId,
                text = text,
                celebrationType = celebrationType,
                challengeScore = score
            )
            if (celebrationType != "NONE") {
                _celebrationEvents.emit("CHAT_CELEBRATION")
            }
        }
    }

    fun shareChallengeWithFriends(context: Context, challenge: ChallengeEntity, userScore: Int?) {
        val shareText = buildString {
            append("🔥 I'm playing ChallengeAI! ")
            if (userScore != null && userScore > 0) {
                append("I scored $userScore on '${challenge.title}'! ")
            } else {
                append("Check out today's duel: '${challenge.title}'! ")
            }
            append("Can you beat my score? Download ChallengeAI & accept my duel! ⚡🏆")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Challenge Friends Via...")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun useStreakFreeze() {
        viewModelScope.launch {
            repository.useStreakFreeze()
            _celebrationEvents.emit("STREAK_FREEZE_USED")
        }
    }

    fun triggerTestNotification(type: String) {
        when (type) {
            "DAILY" -> notificationManager.showDailyChallengeNotification(
                "30-Sec Cyberpunk Cat",
                "Your daily 30-sec drawing duel is waiting! Prove your reflexes."
            )
            "DUEL" -> notificationManager.showFriendDuelNotification(
                "Priya Sharma",
                "Guess the Synthwave Monster"
            )
            "STREAK" -> notificationManager.showStreakAlertNotification(
                user.value?.currentStreak ?: 5
            )
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            repository.updateUsername(newUsername)
            _celebrationEvents.emit("USERNAME_UPDATED")
        }
    }

    fun updateAvatarUri(uri: String) {
        viewModelScope.launch {
            repository.updateAvatarUri(uri)
            _celebrationEvents.emit("AVATAR_UPDATED")
        }
    }

    fun linkGoogleAccount(username: String, displayName: String, email: String) {
        viewModelScope.launch {
            repository.linkGoogleAccount(username, displayName, email)
            _celebrationEvents.emit("GOOGLE_LINKED")
        }
    }

    fun updateProfileDetails(displayName: String, bio: String) {
        viewModelScope.launch {
            repository.updateProfileDetails(displayName, bio)
        }
    }

    fun unlockAchievement(achievementId: String) {
        viewModelScope.launch {
            repository.unlockAchievement(achievementId)
            _celebrationEvents.emit("ACHIEVEMENT_UNLOCKED")
        }
    }

    fun awardGameVictory(gameName: String, xpEarned: Int) {
        viewModelScope.launch {
            repository.recordVictoryBonus(xpEarned)
            repository.sendChatMessage(
                conversationId = "GLOBAL_SQUAD",
                text = "🎉 Won a match in '$gameName' (+${xpEarned} XP)! Who wants to challenge me in the Party Arcade? 👑",
                celebrationType = "WIN_TROPHY",
                challengeScore = xpEarned
            )
            _celebrationEvents.emit("PARTY_GAME_WIN")
        }
    }
}
