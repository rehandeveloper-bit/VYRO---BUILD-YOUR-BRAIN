package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 'current_user' LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("UPDATE users SET currentStreak = currentStreak + 1, totalScore = totalScore + :scoreDelta, completedChallengesCount = completedChallengesCount + 1 WHERE id = 'current_user'")
    suspend fun recordChallengeSuccess(scoreDelta: Int)

    @Query("UPDATE users SET streakFreezeCount = streakFreezeCount - 1 WHERE id = 'current_user' AND streakFreezeCount > 0")
    suspend fun useStreakFreeze()

    @Query("UPDATE users SET username = :newUsername WHERE id = 'current_user'")
    suspend fun updateUsername(newUsername: String)

    @Query("UPDATE users SET avatarUri = :uriString WHERE id = 'current_user'")
    suspend fun updateAvatarUri(uriString: String)

    @Query("UPDATE users SET username = :newUsername, displayName = :newDisplayName, email = :newEmail, isGoogleLinked = 1 WHERE id = 'current_user'")
    suspend fun linkGoogleAccount(newUsername: String, newDisplayName: String, newEmail: String)

    @Query("UPDATE users SET displayName = :displayName, bio = :bio WHERE id = 'current_user'")
    suspend fun updateProfileDetails(displayName: String, bio: String)
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY isDaily DESC, id ASC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE isDaily = 1 LIMIT 1")
    fun getDailyChallenge(): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    suspend fun getChallengeById(id: String): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE isCompleted = 1 ORDER BY completionDurationSecs DESC, id ASC")
    fun getCompletedChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Query("UPDATE challenges SET isCompleted = 1, userScore = :score, aiFeedback = :feedback, completionDurationSecs = :duration WHERE id = :id")
    suspend fun markCompleted(id: String, score: Int, feedback: String, duration: Int)
}

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends ORDER BY totalScore DESC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Query("SELECT * FROM duels ORDER BY createdAt DESC")
    fun getAllDuels(): Flow<List<DuelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuel(duel: DuelEntity)

    @Update
    suspend fun updateDuel(duel: DuelEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET isUnlocked = 1, currentProgress = maxProgress, unlockedDate = :date WHERE id = :id")
    suspend fun unlockAchievement(id: String, date: String)
}
