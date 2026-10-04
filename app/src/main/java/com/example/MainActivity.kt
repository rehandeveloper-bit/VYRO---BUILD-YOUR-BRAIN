package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ChallengeViewModel
import com.example.ui.Screen
import com.example.ui.components.AppHeader
import com.example.ui.components.ConfettiEffect
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreateChallengeScreen
import com.example.ui.screens.DrawingChallengeScreen
import com.example.ui.screens.FriendsDuelsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.SongGuessChallengeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.material.icons.filled.AccountCircle
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PartyGamesHubScreen
import com.example.ui.screens.UndercoverSpyScreen
import com.example.ui.screens.WordBombScreen
import com.example.ui.screens.GrabTheMicScreen
import com.example.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    private val viewModel: ChallengeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: ChallengeViewModel) {
    val context = LocalContext.current
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val dailyChallenge by viewModel.dailyChallenge.collectAsStateWithLifecycle()
    val challenges by viewModel.challenges.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val duels by viewModel.duels.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifsCount by viewModel.unreadNotifsCount.collectAsStateWithLifecycle()
    val isEvaluatingDrawing by viewModel.isEvaluatingDrawing.collectAsStateWithLifecycle()
    val isGeneratingChallenge by viewModel.isGeneratingChallenge.collectAsStateWithLifecycle()
    val drawingResult by viewModel.lastDrawingResult.collectAsStateWithLifecycle()
    val songResult by viewModel.lastSongResult.collectAsStateWithLifecycle()
    val activeChatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val completedChallenges by viewModel.completedChallenges.collectAsStateWithLifecycle()

    var showGlobalConfetti by remember { mutableStateOf(false) }

    // Listen for celebration events to burst confetti
    LaunchedEffect(Unit) {
        viewModel.celebrationEvents.collectLatest {
            showGlobalConfetti = true
        }
    }

    // Request Notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* handled */ }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val showBottomNav = activeScreen is Screen.Home ||
            activeScreen is Screen.FriendsDuels ||
            activeScreen is Screen.Leaderboard ||
            activeScreen is Screen.Profile ||
            activeScreen is Screen.PartyGamesHub ||
            (activeScreen is Screen.Chat && (activeScreen as Screen.Chat).conversationId == "GLOBAL_SQUAD")

    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(
            onAnimationFinished = { showSplash = false }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                if (activeScreen is Screen.Home) {
                    AppHeader(
                        user = user,
                        unreadNotifsCount = unreadNotifsCount,
                        onNotificationsClick = { viewModel.navigateTo(Screen.Notifications) },
                        onStreakClick = { viewModel.navigateTo(Screen.Leaderboard) },
                        onCreateChallengeClick = { viewModel.navigateTo(Screen.CreateChallenge) },
                        onProfileClick = { viewModel.navigateTo(Screen.Profile) }
                    )
                }
            },
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = activeScreen is Screen.Home,
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPurple,
                            selectedTextColor = NeonPurple,
                            indicatorColor = NeonPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_home_button")
                    )

                    NavigationBarItem(
                        selected = activeScreen is Screen.FriendsDuels,
                        onClick = { viewModel.navigateTo(Screen.FriendsDuels) },
                        icon = { Icon(Icons.Default.SportsKabaddi, contentDescription = "Duels") },
                        label = { Text("Duels") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPurple,
                            selectedTextColor = NeonPurple,
                            indicatorColor = NeonPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_duels_button")
                    )

                    NavigationBarItem(
                        selected = activeScreen is Screen.Leaderboard,
                        onClick = { viewModel.navigateTo(Screen.Leaderboard) },
                        icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Ranks") },
                        label = { Text("Leaderboard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPurple,
                            selectedTextColor = NeonPurple,
                            indicatorColor = NeonPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_leaderboard_button")
                    )

                    NavigationBarItem(
                        selected = activeScreen is Screen.Chat,
                        onClick = { viewModel.openConversation("GLOBAL_SQUAD", "Global Squad") },
                        icon = { Icon(Icons.Default.Forum, contentDescription = "Squad Chat") },
                        label = { Text("Squad") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPurple,
                            selectedTextColor = NeonPurple,
                            indicatorColor = NeonPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_squad_chat_button")
                    )

                    NavigationBarItem(
                        selected = activeScreen is Screen.Profile,
                        onClick = { viewModel.navigateTo(Screen.Profile) },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonPurple,
                            selectedTextColor = NeonPurple,
                            indicatorColor = NeonPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_profile_button")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = activeScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        user = user,
                        dailyChallenge = dailyChallenge,
                        challenges = challenges,
                        duels = duels,
                        onPlayChallenge = { challenge ->
                            when (challenge.category) {
                                "DRAWING_30S" -> viewModel.navigateTo(Screen.DrawingChallenge(challenge))
                                "GUESS_SONG" -> viewModel.navigateTo(Screen.SongGuessChallenge(challenge))
                                else -> viewModel.navigateTo(Screen.DrawingChallenge(challenge))
                            }
                        },
                        onFriendsDuelsClick = { viewModel.navigateTo(Screen.FriendsDuels) },
                        onLeaderboardClick = { viewModel.navigateTo(Screen.Leaderboard) },
                        onCreateChallengeClick = { viewModel.navigateTo(Screen.CreateChallenge) },
                        onShareChallenge = { challenge, score ->
                            viewModel.shareChallengeWithFriends(context, challenge, score)
                        },
                        onUseStreakFreeze = { viewModel.useStreakFreeze() },
                        onPlayUndercoverSpy = { viewModel.navigateTo(Screen.UndercoverSpy) },
                        onPlayWordBomb = { viewModel.navigateTo(Screen.WordBomb) },
                        onPlayGrabTheMic = { viewModel.navigateTo(Screen.GrabTheMic) },
                        onPartyGamesHubClick = { viewModel.navigateTo(Screen.PartyGamesHub) }
                    )
                }

                is Screen.DrawingChallenge -> {
                    DrawingChallengeScreen(
                        challenge = screen.challenge,
                        friends = friends,
                        isEvaluating = isEvaluatingDrawing,
                        evaluationResult = drawingResult,
                        onSubmitDrawing = { strokeCount, totalPoints, secondsSpent ->
                            viewModel.evaluateAndSubmitDrawing(screen.challenge, strokeCount, totalPoints, secondsSpent)
                        },
                        onSendDuelToFriend = { friend, score ->
                            viewModel.sendFriendDuel(friend, screen.challenge, score)
                        },
                        onShareScore = { score ->
                            viewModel.shareChallengeWithFriends(context, screen.challenge, score)
                        },
                        onBack = { viewModel.navigateBack() },
                        onClearResult = { viewModel.clearDrawingResult() }
                    )
                }

                is Screen.SongGuessChallenge -> {
                    SongGuessChallengeScreen(
                        challenge = screen.challenge,
                        friends = friends,
                        evaluationResult = songResult,
                        onSubmitGuess = { guess, hintsUsed, secondsSpent ->
                            viewModel.evaluateAndSubmitSongGuess(screen.challenge, guess, hintsUsed, secondsSpent)
                        },
                        onSendDuelToFriend = { friend, score ->
                            viewModel.sendFriendDuel(friend, screen.challenge, score)
                        },
                        onShareScore = { score ->
                            viewModel.shareChallengeWithFriends(context, screen.challenge, score)
                        },
                        onBack = { viewModel.navigateBack() },
                        onClearResult = { viewModel.clearSongResult() }
                    )
                }

                is Screen.Leaderboard -> {
                    LeaderboardScreen(
                        user = user,
                        friends = friends,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.FriendsDuels -> {
                    FriendsDuelsScreen(
                        friends = friends,
                        duels = duels,
                        challenges = challenges,
                        onSendDuel = { friend, challenge, score ->
                            viewModel.sendFriendDuel(friend, challenge, score)
                        },
                        onOpenChat = { friendId, friendName ->
                            viewModel.openConversation(friendId, friendName)
                        },
                        onShareAppInvite = {
                            dailyChallenge?.let { dc ->
                                viewModel.shareChallengeWithFriends(context, dc, null)
                            }
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.Chat -> {
                    ChatScreen(
                        conversationId = screen.conversationId,
                        conversationTitle = screen.conversationTitle,
                        messages = activeChatMessages,
                        onSendMessage = { text, celebrationType, score ->
                            viewModel.sendChatMessage(text, celebrationType, score)
                        },
                        onPlayChallengeFromChat = { challengeId ->
                            val match = challenges.firstOrNull { it.id == challengeId } ?: dailyChallenge
                            match?.let {
                                if (it.category == "DRAWING_30S") {
                                    viewModel.navigateTo(Screen.DrawingChallenge(it))
                                } else {
                                    viewModel.navigateTo(Screen.SongGuessChallenge(it))
                                }
                            }
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.Notifications -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onTriggerNotificationTest = { type ->
                            viewModel.triggerTestNotification(type)
                        },
                        onMarkAsRead = { id -> viewModel.markNotificationRead(id) },
                        onMarkAllRead = { viewModel.markAllNotificationsRead() },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.CreateChallenge -> {
                    CreateChallengeScreen(
                        isGenerating = isGeneratingChallenge,
                        onGenerate = { category, topic ->
                            viewModel.generateNewChallenge(category, topic)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        user = user,
                        achievements = achievements,
                        completedChallenges = completedChallenges,
                        onUpdateUsername = { viewModel.updateUsername(it) },
                        onUpdateAvatarUri = { viewModel.updateAvatarUri(it) },
                        onLinkGoogleAccount = { u, d, e -> viewModel.linkGoogleAccount(u, d, e) },
                        onUpdateProfileDetails = { d, b -> viewModel.updateProfileDetails(d, b) },
                        onClaimAchievement = { viewModel.unlockAchievement(it) },
                        onShareChallengeResult = {
                            viewModel.shareChallengeWithFriends(context, it, it.userScore)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.PartyGamesHub -> {
                    PartyGamesHubScreen(
                        onPlayUndercoverSpy = { viewModel.navigateTo(Screen.UndercoverSpy) },
                        onPlayWordBomb = { viewModel.navigateTo(Screen.WordBomb) },
                        onPlayGrabTheMic = { viewModel.navigateTo(Screen.GrabTheMic) },
                        onPlayDrawing = {
                            val drawChal = challenges.firstOrNull { it.category == "DRAWING_30S" } ?: dailyChallenge
                            drawChal?.let { viewModel.navigateTo(Screen.DrawingChallenge(it)) }
                        },
                        onPlaySongGuess = {
                            val songChal = challenges.firstOrNull { it.category == "GUESS_SONG" } ?: dailyChallenge
                            songChal?.let { viewModel.navigateTo(Screen.SongGuessChallenge(it)) }
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.UndercoverSpy -> {
                    UndercoverSpyScreen(
                        onGameVictory = { name, xp -> viewModel.awardGameVictory(name, xp) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.WordBomb -> {
                    WordBombScreen(
                        onGameVictory = { name, xp -> viewModel.awardGameVictory(name, xp) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is Screen.GrabTheMic -> {
                    GrabTheMicScreen(
                        onGameVictory = { name, xp -> viewModel.awardGameVictory(name, xp) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }

            if (showGlobalConfetti) {
                ConfettiEffect(
                    onFinished = { showGlobalConfetti = false }
                )
            }
        }
    }
}
}
