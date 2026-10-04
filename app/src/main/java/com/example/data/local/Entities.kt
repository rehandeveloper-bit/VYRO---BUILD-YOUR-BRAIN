package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "current_user",
    val username: String = "NeonPlayer",
    val displayName: String = "Alex Vance",
    val avatarInitials: String = "AV",
    val avatarColor: Long = 0xFF8B5CF6,
    val avatarUri: String? = null,
    val email: String? = null,
    val isGoogleLinked: Boolean = false,
    val bio: String = "AI Challenge Enthusiast & Speed Sketcher ⚡",
    val currentStreak: Int = 5,
    val highestStreak: Int = 12,
    val totalScore: Int = 3450,
    val gems: Int = 180,
    val completedChallengesCount: Int = 14,
    val lastActiveDate: String = "Today",
    val streakFreezeCount: Int = 2
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String, // "DRAWING_30S", "GUESS_SONG", "AI_TRIVIA", "CREATIVE_SPRINT"
    val targetAnswer: String, // e.g. "Space Rocket" or "Shape of You"
    val promptDetail: String, // prompt instructions / lyrics snippet / riddle
    val hintsJson: String = "", // extra clues separated by delimiter
    val difficulty: String = "Medium", // "Easy", "Medium", "Hard", "Legendary"
    val xpReward: Int = 100,
    val isDaily: Boolean = false,
    val dateString: String = "", // YYYY-MM-DD
    val isCompleted: Boolean = false,
    val userScore: Int? = null,
    val aiFeedback: String? = null,
    val completionDurationSecs: Int? = null
)

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val avatarInitials: String,
    val avatarColor: Long,
    val currentStreak: Int,
    val totalScore: Int,
    val isOnline: Boolean = true,
    val rankBadge: String = "Master"
)

@Entity(tableName = "duels")
data class DuelEntity(
    @PrimaryKey val id: String,
    val challengeId: String,
    val challengeTitle: String,
    val challengeCategory: String,
    val friendId: String,
    val friendName: String,
    val userScore: Int,
    val friendScore: Int?,
    val status: String, // "PENDING_FRIEND", "PENDING_USER", "WON", "LOST", "TIED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String, // friendId or "GLOBAL_SQUAD"
    val senderId: String,
    val senderName: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val celebrationType: String = "NONE", // "NONE", "WIN_TROPHY", "STREAK_FIRE", "CHALLENGE_INVITE"
    val challengeRefId: String? = null,
    val challengeScore: Int? = null
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // "DAILY_CHALLENGE", "FRIEND_DUEL", "STREAK_ALERT", "WIN_CELEBRATION"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val payloadChallengeId: String? = null
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val isUnlocked: Boolean,
    val xpReward: Int,
    val category: String, // "DRAWING", "STREAK", "SONG", "DUEL", "SPECIAL"
    val unlockedDate: String? = null
)
