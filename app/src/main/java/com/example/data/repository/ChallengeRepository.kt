package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AppNotificationEntity
import com.example.data.local.ChallengeEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DuelEntity
import com.example.data.local.FriendEntity
import com.example.data.local.UserEntity
import com.example.data.remote.DrawingEvaluationResult
import com.example.data.remote.GeminiService
import com.example.data.remote.GeneratedChallenge
import com.example.data.remote.SongEvaluationResult
import com.example.notification.ChallengeNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ChallengeRepository(
    private val db: AppDatabase,
    private val geminiService: GeminiService,
    private val notificationManager: ChallengeNotificationManager
) {
    val userFlow: Flow<UserEntity?> = db.userDao().getUser()
    val allChallengesFlow: Flow<List<ChallengeEntity>> = db.challengeDao().getAllChallenges()
    val dailyChallengeFlow: Flow<ChallengeEntity?> = db.challengeDao().getDailyChallenge()
    val friendsFlow: Flow<List<FriendEntity>> = db.friendDao().getAllFriends()
    val duelsFlow: Flow<List<DuelEntity>> = db.friendDao().getAllDuels()
    val notificationsFlow: Flow<List<AppNotificationEntity>> = db.notificationDao().getAllNotifications()
    val unreadNotifsCountFlow: Flow<Int> = db.notificationDao().getUnreadCount()
    val achievementsFlow: Flow<List<com.example.data.local.AchievementEntity>> = db.achievementDao().getAllAchievements()
    val completedChallengesFlow: Flow<List<ChallengeEntity>> = db.challengeDao().getCompletedChallenges()

    fun getChatMessages(conversationId: String): Flow<List<ChatMessageEntity>> =
        db.chatMessageDao().getMessagesForConversation(conversationId)

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existingUser = db.userDao().getUser().firstOrNull()
        if (existingUser == null) {
            val user = UserEntity(
                id = "current_user",
                username = "NeonPlayer",
                displayName = "Alex Vance",
                avatarInitials = "AV",
                avatarColor = 0xFF8B5CF6,
                currentStreak = 5,
                highestStreak = 12,
                totalScore = 2850,
                gems = 180,
                completedChallengesCount = 8,
                lastActiveDate = "Today",
                streakFreezeCount = 2
            )
            db.userDao().insertOrUpdate(user)

            val initialChallenges = listOf(
                ChallengeEntity(
                    id = "daily_draw_today",
                    title = "30-Sec Cyberpunk Cat",
                    description = "Doodle a futuristic feline with glowing neon shades before the 30-second buzzer sounds!",
                    category = "DRAWING_30S",
                    targetAnswer = "Cyberpunk Cat",
                    promptDetail = "Draw a cool cat wearing oversized neon sunglasses. Include pointy ears and whisker lasers if you have 5 seconds left!",
                    hintsJson = "Start with the circular face||Draw thick horizontal sunglass frames||Add cute ear triangles",
                    difficulty = "Medium",
                    xpReward = 150,
                    isDaily = true,
                    dateString = "Today",
                    isCompleted = false
                ),
                ChallengeEntity(
                    id = "song_challenge_1",
                    title = "Guess the Synthwave Monster",
                    description = "Decode the cryptic neon lyrics and rhythmic clues to guess the chart-topping mega hit!",
                    category = "GUESS_SONG",
                    targetAnswer = "Blinding Lights",
                    promptDetail = "Artist: The Weeknd | 2020 Synthwave pop sensation that spent 90 weeks on the Billboard charts.",
                    hintsJson = "🌃 'Sin City's cold and empty, no one's around to judge me...'||🚗 Driving at night through red neon city lights||⚡ Iconic upbeat 80s synth-bass rhythm",
                    difficulty = "Medium",
                    xpReward = 120,
                    isDaily = false,
                    dateString = "Active",
                    isCompleted = false
                ),
                ChallengeEntity(
                    id = "draw_challenge_2",
                    title = "30-Sec Steaming Pizza",
                    description = "Quick-draw the ultimate cheesy pizza slice with dripping mozzarella and pepperonis!",
                    category = "DRAWING_30S",
                    targetAnswer = "Pizza Slice",
                    promptDetail = "Make the cheese stretch! Bold crust on top, triangle point facing down, circle pepperonis.",
                    hintsJson = "Draw an inverted triangle||Add curved crust at top edge||Doodle circles for toppings",
                    difficulty = "Easy",
                    xpReward = 100,
                    isDaily = false,
                    dateString = "Active",
                    isCompleted = true,
                    userScore = 88,
                    aiFeedback = "Splendid cheese dynamics! The AI art referee rated your crust texture 88/100."
                ),
                ChallengeEntity(
                    id = "song_challenge_2",
                    title = "Guess the Global Pop Anthem",
                    description = "Acoustic guitar loops and club romance: identify this multi-billion stream pop record!",
                    category = "GUESS_SONG",
                    targetAnswer = "Shape of You",
                    promptDetail = "Artist: Ed Sheeran | 2017 record-breaking pop track with infectious dancehall beat.",
                    hintsJson = "🎵 'The club isn't the best place to find a lover so the bar is where I go...'||🥊 Boxing ring and diner music video||💃 'I'm in love with your body'",
                    difficulty = "Hard",
                    xpReward = 140,
                    isDaily = false,
                    dateString = "Active",
                    isCompleted = false
                ),
                ChallengeEntity(
                    id = "draw_challenge_3",
                    title = "30-Sec Cosmic Skater",
                    description = "Draw an astronaut gliding across Saturn's rings on a rocket-powered skateboard!",
                    category = "DRAWING_30S",
                    targetAnswer = "Cosmic Skater",
                    promptDetail = "Astronaut helmet + skateboard deck with smoke trails in outer space!",
                    hintsJson = "Round helmet visor||Skateboard with two wheels||Star particles in background",
                    difficulty = "Legendary",
                    xpReward = 200,
                    isDaily = false,
                    dateString = "Active",
                    isCompleted = false
                )
            )
            db.challengeDao().insertChallenges(initialChallenges)

            val initialFriends = listOf(
                FriendEntity("f1", "Priya Sharma", "priya_art", "PS", 0xFFEC4899, currentStreak = 9, totalScore = 3420, isOnline = true, rankBadge = "Grandmaster"),
                FriendEntity("f2", "Rohan Mehta", "rohan_beats", "RM", 0xFF06B6D4, currentStreak = 6, totalScore = 3100, isOnline = true, rankBadge = "Master"),
                FriendEntity("f3", "Marcus Chen", "marcus_doodle", "MC", 0xFF10B981, currentStreak = 7, totalScore = 2750, isOnline = false, rankBadge = "Diamond"),
                FriendEntity("f4", "Zara Khan", "zara_vibes", "ZK", 0xFFF59E0B, currentStreak = 4, totalScore = 2490, isOnline = true, rankBadge = "Platinum"),
                FriendEntity("f5", "Leo Miller", "leo_racer", "LM", 0xFF8B5CF6, currentStreak = 3, totalScore = 2120, isOnline = false, rankBadge = "Gold")
            )
            db.friendDao().insertFriends(initialFriends)

            val initialDuels = listOf(
                DuelEntity(
                    id = "duel_1",
                    challengeId = "draw_challenge_2",
                    challengeTitle = "30-Sec Steaming Pizza",
                    challengeCategory = "DRAWING_30S",
                    friendId = "f2",
                    friendName = "Rohan Mehta",
                    userScore = 88,
                    friendScore = 81,
                    status = "WON"
                ),
                DuelEntity(
                    id = "duel_2",
                    challengeId = "song_challenge_1",
                    challengeTitle = "Guess the Synthwave Monster",
                    challengeCategory = "GUESS_SONG",
                    friendId = "f1",
                    friendName = "Priya Sharma",
                    userScore = 95,
                    friendScore = null,
                    status = "PENDING_FRIEND"
                )
            )
            for (duel in initialDuels) {
                db.friendDao().insertDuel(duel)
            }

            val welcomeSquadMessages = listOf(
                ChatMessageEntity(
                    conversationId = "GLOBAL_SQUAD",
                    senderId = "ai_referee",
                    senderName = "AI Referee Bot 🤖",
                    isFromUser = false,
                    text = "⚡ Welcome to the Squad! Today's 30-sec drawing challenge is LIVE. Who can top the leaderboard?",
                    timestamp = System.currentTimeMillis() - 3600000 * 4
                ),
                ChatMessageEntity(
                    conversationId = "GLOBAL_SQUAD",
                    senderId = "f1",
                    senderName = "Priya Sharma",
                    isFromUser = false,
                    text = "Just hit a 9-day streak! 🔥 That Cyberpunk Cat prompt is wildly fun, my cats actually wore shades haha.",
                    timestamp = System.currentTimeMillis() - 3600000 * 2,
                    celebrationType = "STREAK_FIRE"
                ),
                ChatMessageEntity(
                    conversationId = "GLOBAL_SQUAD",
                    senderId = "f2",
                    senderName = "Rohan Mehta",
                    isFromUser = false,
                    text = "Alex beat me on the Pizza Slice duel by 7 points! Good game man, revenge match coming up! 🍕⚔️",
                    timestamp = System.currentTimeMillis() - 3600000,
                    celebrationType = "WIN_TROPHY",
                    challengeRefId = "draw_challenge_2",
                    challengeScore = 88
                )
            )
            db.chatMessageDao().insertMessages(welcomeSquadMessages)

            val initialNotifications = listOf(
                AppNotificationEntity(
                    id = "notif_1",
                    title = "🔥 Daily Challenge Is Live!",
                    message = "Today's '30-Sec Cyberpunk Cat' is ready. Test your reflexes & keep your 5-day streak!",
                    type = "DAILY_CHALLENGE",
                    timestamp = System.currentTimeMillis() - 7200000,
                    isRead = false,
                    payloadChallengeId = "daily_draw_today"
                ),
                AppNotificationEntity(
                    id = "notif_2",
                    title = "🏆 Victory Over Rohan Mehta!",
                    message = "You won the '30-Sec Steaming Pizza' duel (88 vs 81)! Earned +100 XP & Bragging Rights.",
                    type = "WIN_CELEBRATION",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isRead = false
                ),
                AppNotificationEntity(
                    id = "notif_3",
                    title = "⚡ Priya Accepted Your Song Duel",
                    message = "Priya Sharma is currently guessing 'Synthwave Monster'. Check the leaderboard soon!",
                    type = "FRIEND_DUEL",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isRead = true
                )
            )
            for (n in initialNotifications) {
                db.notificationDao().insertNotification(n)
            }

            val initialAchievements = listOf(
                com.example.data.local.AchievementEntity(
                    id = "ach_speed_demon",
                    title = "Speed Demon",
                    description = "Finish a 30-sec drawing challenge in under 15 seconds",
                    iconEmoji = "⚡",
                    currentProgress = 15,
                    maxProgress = 15,
                    isUnlocked = true,
                    xpReward = 150,
                    category = "DRAWING",
                    unlockedDate = "Yesterday"
                ),
                com.example.data.local.AchievementEntity(
                    id = "ach_picasso",
                    title = "Picasso's Heir",
                    description = "Score 90+ on an AI drawing evaluation",
                    iconEmoji = "🎨",
                    currentProgress = 1,
                    maxProgress = 1,
                    isUnlocked = true,
                    xpReward = 200,
                    category = "DRAWING",
                    unlockedDate = "2 days ago"
                ),
                com.example.data.local.AchievementEntity(
                    id = "ach_7_streak",
                    title = "Seven-Day Streak",
                    description = "Reach a 7-day challenge streak without breaking",
                    iconEmoji = "🔥",
                    currentProgress = 5,
                    maxProgress = 7,
                    isUnlocked = false,
                    xpReward = 300,
                    category = "STREAK"
                ),
                com.example.data.local.AchievementEntity(
                    id = "ach_golden_ear",
                    title = "Golden Ear",
                    description = "Guess a song correctly with only 1 hint used",
                    iconEmoji = "🎵",
                    currentProgress = 1,
                    maxProgress = 1,
                    isUnlocked = true,
                    xpReward = 150,
                    category = "SONG",
                    unlockedDate = "3 days ago"
                ),
                com.example.data.local.AchievementEntity(
                    id = "ach_duelist",
                    title = "Arena Duelist",
                    description = "Win 5 friend duels in ChallengeAI",
                    iconEmoji = "⚔️",
                    currentProgress = 3,
                    maxProgress = 5,
                    isUnlocked = false,
                    xpReward = 250,
                    category = "DUEL"
                ),
                com.example.data.local.AchievementEntity(
                    id = "ach_ai_prompt",
                    title = "AI Connoisseur",
                    description = "Generate 3 custom AI challenges with Gemini",
                    iconEmoji = "🤖",
                    currentProgress = 2,
                    maxProgress = 3,
                    isUnlocked = false,
                    xpReward = 200,
                    category = "SPECIAL"
                ),
                com.example.data.local.AchievementEntity(
                    id = "ach_shield",
                    title = "Immortal Streak",
                    description = "Activate a Streak Freeze shield",
                    iconEmoji = "🛡️",
                    currentProgress = 1,
                    maxProgress = 1,
                    isUnlocked = true,
                    xpReward = 100,
                    category = "STREAK",
                    unlockedDate = "5 days ago"
                ),
                com.example.data.local.AchievementEntity(
                    id = "ach_grandmaster",
                    title = "Grandmaster Rank",
                    description = "Earn over 3,000 total XP points across all challenges",
                    iconEmoji = "👑",
                    currentProgress = 3450,
                    maxProgress = 3000,
                    isUnlocked = true,
                    xpReward = 500,
                    category = "SPECIAL",
                    unlockedDate = "Today"
                )
            )
            db.achievementDao().insertAchievements(initialAchievements)
        }
    }

    suspend fun generateNewDailyChallenge(category: String, topic: String? = null): ChallengeEntity = withContext(Dispatchers.IO) {
        val generated = geminiService.generateChallenge(category, topic)
        val newId = "ai_chal_${UUID.randomUUID().toString().take(8)}"
        val challenge = ChallengeEntity(
            id = newId,
            title = generated.title,
            description = generated.description,
            category = generated.category,
            targetAnswer = generated.targetAnswer,
            promptDetail = generated.promptDetail,
            hintsJson = generated.hints.joinToString("||"),
            difficulty = generated.difficulty,
            xpReward = generated.xpReward,
            isDaily = true,
            dateString = "Today",
            isCompleted = false
        )
        db.challengeDao().insertChallenge(challenge)

        // Show system notification for daily challenge
        notificationManager.showDailyChallengeNotification(challenge.title, challenge.description)

        // Record in in-app notification center
        val notif = AppNotificationEntity(
            id = "notif_${System.currentTimeMillis()}",
            title = "✨ New AI Challenge Generated!",
            message = "${challenge.title}: ${challenge.description}",
            type = "DAILY_CHALLENGE",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            payloadChallengeId = challenge.id
        )
        db.notificationDao().insertNotification(notif)

        challenge
    }

    suspend fun evaluateDrawing(
        subject: String,
        strokeCount: Int,
        totalPoints: Int,
        secondsSpent: Int
    ): DrawingEvaluationResult {
        return geminiService.evaluateDrawing(subject, strokeCount, totalPoints, secondsSpent)
    }

    suspend fun evaluateSongGuess(
        targetSong: String,
        userGuess: String,
        hintsUsed: Int
    ): SongEvaluationResult {
        return geminiService.evaluateSongGuess(targetSong, userGuess, hintsUsed)
    }

    suspend fun completeChallenge(
        challengeId: String,
        score: Int,
        feedback: String,
        durationSecs: Int
    ) = withContext(Dispatchers.IO) {
        db.challengeDao().markCompleted(challengeId, score, feedback, durationSecs)
        db.userDao().recordChallengeSuccess(score)

        // Add celebratory post to Global Squad chat
        val challenge = db.challengeDao().getChallengeById(challengeId)
        val title = challenge?.title ?: "AI Challenge"
        val message = ChatMessageEntity(
            conversationId = "GLOBAL_SQUAD",
            senderId = "current_user",
            senderName = "Alex Vance (You)",
            isFromUser = true,
            text = "🎉 Just completed '$title' with a score of $score! $feedback",
            timestamp = System.currentTimeMillis(),
            celebrationType = "WIN_TROPHY",
            challengeRefId = challengeId,
            challengeScore = score
        )
        db.chatMessageDao().insertMessage(message)

        // AI Bot response cheering the win
        CoroutineScope(Dispatchers.IO).launch {
            kotlinx.coroutines.delay(1200)
            val cheers = listOf(
                "🔥 Sensational run, Alex! That $score score moves you up the ranks!",
                "👑 AI Referee verified: Top-tier agility! Can your friends beat $score?",
                "⚡ Electric performance! Daily streak increased. Keep that momentum going!"
            )
            val aiMsg = ChatMessageEntity(
                conversationId = "GLOBAL_SQUAD",
                senderId = "ai_referee",
                senderName = "AI Referee Bot 🤖",
                isFromUser = false,
                text = cheers.random(),
                timestamp = System.currentTimeMillis(),
                celebrationType = "STREAK_FIRE"
            )
            db.chatMessageDao().insertMessage(aiMsg)
        }
    }

    suspend fun sendFriendDuel(friend: FriendEntity, challenge: ChallengeEntity, userScore: Int) = withContext(Dispatchers.IO) {
        val duelId = "duel_${UUID.randomUUID().toString().take(8)}"
        val duel = DuelEntity(
            id = duelId,
            challengeId = challenge.id,
            challengeTitle = challenge.title,
            challengeCategory = challenge.category,
            friendId = friend.id,
            friendName = friend.name,
            userScore = userScore,
            friendScore = null,
            status = "PENDING_FRIEND"
        )
        db.friendDao().insertDuel(duel)

        // Post challenge card in chat
        val chatCard = ChatMessageEntity(
            conversationId = friend.id,
            senderId = "current_user",
            senderName = "Alex Vance (You)",
            isFromUser = true,
            text = "⚔️ I challenged you on '${challenge.title}'! My score is $userScore. Can you top it?",
            timestamp = System.currentTimeMillis(),
            celebrationType = "CHALLENGE_INVITE",
            challengeRefId = challenge.id,
            challengeScore = userScore
        )
        db.chatMessageDao().insertMessage(chatCard)

        // Trigger system notification
        notificationManager.showFriendDuelNotification(friend.name, challenge.title)
    }

    suspend fun sendChatMessage(
        conversationId: String,
        text: String,
        celebrationType: String = "NONE",
        challengeRefId: String? = null,
        challengeScore: Int? = null
    ) = withContext(Dispatchers.IO) {
        val msg = ChatMessageEntity(
            conversationId = conversationId,
            senderId = "current_user",
            senderName = "Alex Vance (You)",
            isFromUser = true,
            text = text,
            timestamp = System.currentTimeMillis(),
            celebrationType = celebrationType,
            challengeRefId = challengeRefId,
            challengeScore = challengeScore
        )
        db.chatMessageDao().insertMessage(msg)

        // Auto-reply simulation from friend or bot
        if (conversationId != "GLOBAL_SQUAD") {
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(1800)
                val replies = listOf(
                    "Challenge accepted! Booting up the timer now 🔥",
                    "Haha nice score! Give me 5 minutes, I'm going to beat that.",
                    "Woah impressive! You're on fire today 🎉",
                    "Let's gooo! Win or lose, that was super fun."
                )
                val replyMsg = ChatMessageEntity(
                    conversationId = conversationId,
                    senderId = conversationId,
                    senderName = "Friend",
                    isFromUser = false,
                    text = replies.random(),
                    timestamp = System.currentTimeMillis(),
                    celebrationType = "NONE"
                )
                db.chatMessageDao().insertMessage(replyMsg)
            }
        }
    }

    suspend fun useStreakFreeze() = withContext(Dispatchers.IO) {
        db.userDao().useStreakFreeze()
    }

    suspend fun markNotificationRead(id: String) = withContext(Dispatchers.IO) {
        db.notificationDao().markAsRead(id)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        db.notificationDao().markAllAsRead()
    }

    fun triggerStreakAlertNotification(streak: Int) {
        notificationManager.showStreakAlertNotification(streak)
    }

    suspend fun updateUsername(newUsername: String) = withContext(Dispatchers.IO) {
        db.userDao().updateUsername(newUsername)
    }

    suspend fun updateAvatarUri(uriString: String) = withContext(Dispatchers.IO) {
        db.userDao().updateAvatarUri(uriString)
    }

    suspend fun linkGoogleAccount(username: String, displayName: String, email: String) = withContext(Dispatchers.IO) {
        db.userDao().linkGoogleAccount(username, displayName, email)
    }

    suspend fun updateProfileDetails(displayName: String, bio: String) = withContext(Dispatchers.IO) {
        db.userDao().updateProfileDetails(displayName, bio)
    }

    suspend fun unlockAchievement(id: String) = withContext(Dispatchers.IO) {
        db.achievementDao().unlockAchievement(id, "Today")
        db.userDao().recordChallengeSuccess(100)
    }

    suspend fun recordVictoryBonus(scoreDelta: Int) = withContext(Dispatchers.IO) {
        db.userDao().recordChallengeSuccess(scoreDelta)
    }
}
