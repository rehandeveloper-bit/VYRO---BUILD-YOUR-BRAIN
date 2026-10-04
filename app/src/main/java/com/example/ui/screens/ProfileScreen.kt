package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.AchievementEntity
import com.example.data.local.ChallengeEntity
import com.example.data.local.UserEntity
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

@Composable
fun ProfileScreen(
    user: UserEntity?,
    achievements: List<AchievementEntity>,
    completedChallenges: List<ChallengeEntity>,
    onUpdateUsername: (String) -> Unit,
    onUpdateAvatarUri: (String) -> Unit,
    onLinkGoogleAccount: (username: String, displayName: String, email: String) -> Unit,
    onUpdateProfileDetails: (displayName: String, bio: String) -> Unit,
    onClaimAchievement: (String) -> Unit,
    onShareChallengeResult: (ChallengeEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedSectionTab by remember { mutableIntStateOf(0) }
    val sectionTabs = listOf("Overview & Stats", "Achievements (${achievements.count { it.isUnlocked }})", "History (${completedChallenges.size})")

    var showGoogleLoginDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Android Zero-Permission Photo Picker for uploading profile picture
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                onUpdateAvatarUri(it.toString())
                showConfetti = true
            }
        }
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Back Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Player Profile",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.testTag("edit_profile_header_button")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = NeonPurple)
                    }
                }
            }

            // User Identity Card (Avatar + Photo Upload + Google Login Badging)
            item {
                user?.let { u ->
                    UserProfileCard(
                        user = u,
                        onUploadPhotoClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onGoogleLoginClick = { showGoogleLoginDialog = true },
                        onEditClick = { showEditProfileDialog = true }
                    )
                }
            }

            // Navigation Tabs (Overview & Stats | Achievements | History)
            item {
                TabRow(
                    selectedTabIndex = selectedSectionTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = NeonPurple,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                ) {
                    sectionTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedSectionTab == index,
                            onClick = { selectedSectionTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedSectionTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSectionTab == index) NeonPurple else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedSectionTab) {
                0 -> {
                    // Stats & Overview Section
                    item {
                        user?.let { u ->
                            ProfileStatsSection(
                                user = u,
                                completedCount = completedChallenges.size,
                                achievementsCount = achievements.count { it.isUnlocked }
                            )
                        }
                    }

                    item {
                        Text(
                            text = "🏆 Highlight Achievements",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(achievements.filter { it.isUnlocked }.take(3)) { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            onClaim = {
                                onClaimAchievement(achievement.id)
                                showConfetti = true
                            }
                        )
                    }

                    item {
                        OutlinedButton(
                            onClick = { selectedSectionTab = 1 },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View All Achievements (${achievements.size})")
                        }
                    }
                }

                1 -> {
                    // Achievements Section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Earned & In-Progress Badges",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${achievements.count { it.isUnlocked }} / ${achievements.size} Unlocked",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber
                            )
                        }
                    }

                    items(achievements) { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            onClaim = {
                                onClaimAchievement(achievement.id)
                                showConfetti = true
                            }
                        )
                    }
                }

                2 -> {
                    // Challenge History Section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Challenge History",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${completedChallenges.size} Completed",
                                fontSize = 12.sp,
                                color = NeonCyan
                            )
                        }
                    }

                    if (completedChallenges.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No completed challenges recorded yet",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(completedChallenges) { chal ->
                            ChallengeHistoryCard(
                                challenge = chal,
                                onShare = { onShareChallengeResult(chal) }
                            )
                        }
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1B1430),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("made_by_king_profile_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "made by :-KING",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = NeonAmber,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(28.dp)) }
        }

        // Google Sign-In / Username Linking Dialog
        if (showGoogleLoginDialog && user != null) {
            GoogleLoginDialog(
                currentUser = user,
                onDismiss = { showGoogleLoginDialog = false },
                onConfirmGoogleLogin = { chosenUsername, chosenName, googleEmail ->
                    onLinkGoogleAccount(chosenUsername, chosenName, googleEmail)
                    showGoogleLoginDialog = false
                    showConfetti = true
                }
            )
        }

        // Edit Profile Dialog (Username, Display Name, Bio)
        if (showEditProfileDialog && user != null) {
            EditProfileDialog(
                user = user,
                onDismiss = { showEditProfileDialog = false },
                onSave = { newUsername, newDisplayName, newBio ->
                    onUpdateUsername(newUsername)
                    onUpdateProfileDetails(newDisplayName, newBio)
                    showEditProfileDialog = false
                    showConfetti = true
                }
            )
        }

        if (showConfetti) {
            ConfettiEffect(onFinished = { showConfetti = false })
        }
    }
}

@Composable
private fun UserProfileCard(
    user: UserEntity,
    onUploadPhotoClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_profile_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture with Camera Badge overlay
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .clickable { onUploadPhotoClick() }
                    .testTag("upload_profile_picture_button"),
                contentAlignment = Alignment.Center
            ) {
                if (!user.avatarUri.isNullOrBlank()) {
                    AsyncImage(
                        model = user.avatarUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonPurple, NeonCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.avatarInitials,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                // Camera upload badge overlay
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F0B1E).copy(alpha = 0.85f))
                        .border(1.5.dp, NeonPurple, CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change photo",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.displayName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                if (user.isGoogleLinked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Google Verified",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Username
            Text(
                text = "@${user.username}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonPurple
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bio
            Text(
                text = user.bio,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Google Login / Edit Profile)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!user.isGoogleLinked) {
                    Button(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("google_login_profile_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                    ) {
                        Text(
                            text = "G  Set Username via Google",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF4285F4).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4285F4)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onGoogleLoginClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google Linked: ${user.email ?: user.username}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("edit_details_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(
    user: UserEntity,
    completedCount: Int,
    achievementsCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "📊 Challenge Analytics",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 2x2 Grid of primary stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Total Challenges",
                value = "$completedCount",
                subtitle = "Completed",
                icon = Icons.Default.CheckCircle,
                accentColor = NeonCyan,
                modifier = Modifier.weight(1f)
            )

            StatMetricCard(
                title = "Daily Streak",
                value = "${user.currentStreak}d",
                subtitle = "Best: ${user.highestStreak}d",
                icon = Icons.Default.LocalFireDepartment,
                accentColor = NeonAmber,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Total XP Score",
                value = "${user.totalScore}",
                subtitle = "Grandmaster Tier",
                icon = Icons.Default.EmojiEvents,
                accentColor = NeonPurple,
                modifier = Modifier.weight(1f)
            )

            StatMetricCard(
                title = "Badges & Shields",
                value = "$achievementsCount",
                subtitle = "${user.streakFreezeCount} Freezes Left",
                icon = Icons.Default.AutoAwesome,
                accentColor = NeonRose,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = accentColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementEntity,
    onClaim: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_card_${achievement.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) Color(0xFF1E1738) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (achievement.isUnlocked) androidx.compose.foundation.BorderStroke(1.2.dp, NeonAmber) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) NeonAmber.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.3f)
                    )
                    .border(
                        1.dp,
                        if (achievement.isUnlocked) NeonAmber else Color.Gray,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.iconEmoji,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = achievement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (achievement.isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (achievement.isUnlocked) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "UNLOCKED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = {
                        (achievement.currentProgress.toFloat() / achievement.maxProgress.toFloat()).coerceIn(0f, 1f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (achievement.isUnlocked) NeonAmber else NeonPurple,
                    trackColor = Color.DarkGray.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${achievement.currentProgress}/${achievement.maxProgress}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "+${achievement.xpReward} XP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeHistoryCard(
    challenge: ChallengeEntity,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (challenge.category == "DRAWING_30S") NeonPurple.copy(alpha = 0.2f)
                                else NeonCyan.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (challenge.category == "DRAWING_30S") Icons.Default.Brush else Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (challenge.category == "DRAWING_30S") NeonPurple else NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = challenge.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = challenge.dateString.ifBlank { "Recently" },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonEmerald.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${challenge.userScore ?: 85} pts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (!challenge.aiFeedback.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${challenge.aiFeedback}\"",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share Result", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun GoogleLoginDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onConfirmGoogleLogin: (username: String, displayName: String, email: String) -> Unit
) {
    val detectedGoogleEmail = "gs2422979@gmail.com"
    val suggestedUsernameFromEmail = detectedGoogleEmail.substringBefore("@")
    var inputUsername by remember { mutableStateOf(suggestedUsernameFromEmail) }
    var inputDisplayName by remember { mutableStateOf("GS Challenger") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFF1E1738),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Branding Emblem
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        color = Color(0xFF4285F4)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Sign In with Google",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF4285F4).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = detectedGoogleEmail.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Google Account Detected", fontSize = 11.sp, color = NeonCyan)
                            Text(text = detectedGoogleEmail, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Text(
                    text = "We suggested a handle based on your Google login. You can personalize your ChallengeAI username below:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = inputUsername,
                    onValueChange = { inputUsername = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                    label = { Text("Unique Username (@handle)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_login_username_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = inputDisplayName,
                    onValueChange = { inputDisplayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputUsername.isNotBlank()) {
                        onConfirmGoogleLogin(inputUsername, inputDisplayName, detectedGoogleEmail)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_google_login_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm & Link Google Account", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditProfileDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (username: String, displayName: String, bio: String) -> Unit
) {
    var editUsername by remember { mutableStateOf(user.username) }
    var editDisplayName by remember { mutableStateOf(user.displayName) }
    var editBio by remember { mutableStateOf(user.bio) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFF1E1738),
        title = {
            Text(
                text = "Edit Profile & Username",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                    label = { Text("Username (@handle)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_username_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editDisplayName,
                    onValueChange = { editDisplayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = { Text("Bio / Tagline") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editUsername.isNotBlank()) {
                        onSave(editUsername, editDisplayName, editBio)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Text("Save Profile", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    )
}
