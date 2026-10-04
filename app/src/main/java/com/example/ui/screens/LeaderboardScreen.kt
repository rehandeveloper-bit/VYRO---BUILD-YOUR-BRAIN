package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FriendEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

data class LeaderboardEntry(
    val id: String,
    val name: String,
    val username: String,
    val initials: String,
    val color: Long,
    val streak: Int,
    val score: Int,
    val isUser: Boolean,
    val badge: String
)

@Composable
fun LeaderboardScreen(
    user: UserEntity?,
    friends: List<FriendEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Global Squad", "Friends Only")

    // Compile leaderboard entries
    val friendEntries = remember(friends, user) {
        val list = mutableListOf<LeaderboardEntry>()
        if (user != null) {
            list.add(
                LeaderboardEntry(
                    id = user.id,
                    name = "${user.displayName} (You)",
                    username = user.username,
                    initials = user.avatarInitials,
                    color = user.avatarColor,
                    streak = user.currentStreak,
                    score = user.totalScore,
                    isUser = true,
                    badge = "Champion"
                )
            )
        }
        friends.forEach { f ->
            list.add(
                LeaderboardEntry(
                    id = f.id,
                    name = f.name,
                    username = f.username,
                    initials = f.avatarInitials,
                    color = f.avatarColor,
                    streak = f.currentStreak,
                    score = f.totalScore,
                    isUser = false,
                    badge = f.rankBadge
                )
            )
        }
        list.sortedByDescending { it.score }
    }

    val globalEntries = remember(friendEntries) {
        val extraGlobals = listOf(
            LeaderboardEntry("g1", "Valkyrie AI", "valk_prime", "VA", 0xFF6366F1, 14, 4820, false, "Legendary"),
            LeaderboardEntry("g2", "Kira Shadow", "kira_99", "KS", 0xFF14B8A6, 11, 4120, false, "Grandmaster"),
            LeaderboardEntry("g3", "Nova Blast", "nova_speed", "NB", 0xFFF43F5E, 10, 3950, false, "Master")
        )
        (friendEntries + extraGlobals).sortedByDescending { it.score }
    }

    val activeList = if (selectedTabIndex == 0) globalEntries else friendEntries

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("leaderboard_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🏆 Leaderboard & Streaks",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = NeonPurple,
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) NeonPurple else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        // Podium Top 3 (if at least 3 entries)
        if (activeList.size >= 3) {
            item {
                PodiumSection(topThree = activeList.take(3))
            }
        }

        // Section Title
        item {
            Text(
                text = "⚡ Ranks & Streaks",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Rankings list
        itemsIndexed(activeList) { index, entry ->
            LeaderboardRowItem(
                rank = index + 1,
                entry = entry
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun PodiumSection(topThree: List<LeaderboardEntry>) {
    val first = topThree[0]
    val second = topThree.getOrNull(1)
    val third = topThree.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place
        second?.let {
            PodiumPillar(
                entry = it,
                rank = 2,
                height = 110.dp,
                pillarColor = Color(0xFF94A3B8),
                badgeColor = Color(0xFFE2E8F0)
            )
        }

        // 1st Place
        PodiumPillar(
            entry = first,
            rank = 1,
            height = 140.dp,
            pillarColor = NeonAmber,
            badgeColor = Color(0xFFFFD700)
        )

        // 3rd Place
        third?.let {
            PodiumPillar(
                entry = it,
                rank = 3,
                height = 95.dp,
                pillarColor = Color(0xFFB45309),
                badgeColor = Color(0xFFD97706)
            )
        }
    }
}

@Composable
private fun PodiumPillar(
    entry: LeaderboardEntry,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    pillarColor: Color,
    badgeColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (rank == 1) 48.dp else 40.dp)
                .clip(CircleShape)
                .background(Color(entry.color))
                .border(2.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initials,
                fontWeight = FontWeight.Bold,
                fontSize = if (rank == 1) 14.sp else 12.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = entry.name.take(9),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White,
            maxLines = 1
        )

        Text(
            text = "${entry.score} pts",
            fontSize = 11.sp,
            color = NeonAmber,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pillar block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(pillarColor.copy(alpha = 0.4f), pillarColor.copy(alpha = 0.15f))
                    )
                )
                .border(1.dp, pillarColor.copy(alpha = 0.6f), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = badgeColor
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${entry.streak}d",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRowItem(
    rank: Int,
    entry: LeaderboardEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_$rank"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isUser) NeonPurple.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (entry.isUser) androidx.compose.foundation.BorderStroke(1.5.dp, NeonPurple) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank number
                Text(
                    text = "$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = when (rank) {
                        1 -> NeonAmber
                        2 -> Color(0xFFE2E8F0)
                        3 -> Color(0xFFD97706)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.width(28.dp)
                )

                // Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(entry.color)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = entry.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (entry.isUser) NeonPurple else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "@${entry.username} • ${entry.badge}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Streak & Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.score} pts",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = NeonAmber
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${entry.streak} days",
                        fontSize = 11.sp,
                        color = NeonAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
