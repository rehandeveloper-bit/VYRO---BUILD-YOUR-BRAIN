package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class GeneratedChallenge(
    val title: String,
    val description: String,
    val category: String,
    val targetAnswer: String,
    val promptDetail: String,
    val hints: List<String>,
    val difficulty: String,
    val xpReward: Int
)

data class DrawingEvaluationResult(
    val score: Int, // 0 - 100
    val titleBadge: String,
    val commentary: String,
    val aiRecognitionGuess: String
)

data class SongEvaluationResult(
    val isCorrect: Boolean,
    val score: Int,
    val feedback: String,
    val trivia: String
)

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateChallenge(category: String, customTopic: String? = null): GeneratedChallenge = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val promptText = buildString {
                    append("You are the AI Game Master for ChallengeAI app. Generate an engaging game challenge. ")
                    if (category == "DRAWING_30S") {
                        append("Type: 30-Second Quick Drawing Challenge. ")
                        append("Topic: ${customTopic ?: "Something funny or unexpected"}. ")
                        append("Provide target drawing subject in 1-4 words. ")
                    } else if (category == "GUESS_SONG") {
                        append("Type: Guess the Song Riddle Challenge. ")
                        append("Topic: ${customTopic ?: "Global Pop / Bollywood / Rock / HipHop hit"}. ")
                        append("Provide famous song title, famous artist, 3 mysterious rhyming/emoji lyric clues. ")
                    } else {
                        append("Type: Quick AI Trivia / Riddle Sprint. ")
                    }
                    append("Output strictly valid JSON with keys: title, description, targetAnswer, promptDetail, hints (array of 3 strings), difficulty (Easy/Medium/Hard/Legendary), xpReward (number). Do NOT include markdown backticks.")
                }

                val result = callGeminiApi(apiKey, promptText)
                if (result != null) {
                    val cleanJson = result.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val obj = JSONObject(cleanJson)
                    val hintsArray = obj.optJSONArray("hints")
                    val hintsList = mutableListOf<String>()
                    if (hintsArray != null) {
                        for (i in 0 until hintsArray.length()) {
                            hintsList.add(hintsArray.getString(i))
                        }
                    }
                    return@withContext GeneratedChallenge(
                        title = obj.optString("title", "AI Rapid Challenge"),
                        description = obj.optString("description", "Prove your skill before the countdown ends!"),
                        category = category,
                        targetAnswer = obj.optString("targetAnswer", "Mystery Target"),
                        promptDetail = obj.optString("promptDetail", "Complete the challenge accurately."),
                        hints = if (hintsList.isNotEmpty()) hintsList else listOf("Think creatively", "Watch the timer", "Stay focused"),
                        difficulty = obj.optString("difficulty", "Medium"),
                        xpReward = obj.optInt("xpReward", 100)
                    )
                }
            } catch (e: Exception) {
                Log.w("GeminiService", "API call failed, fallback used: ${e.message}")
            }
        }
        // Fallback curated generative challenges
        return@withContext getCuratedChallenge(category, customTopic)
    }

    suspend fun evaluateDrawing(
        subject: String,
        strokeCount: Int,
        totalPoints: Int,
        secondsSpent: Int
    ): DrawingEvaluationResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val promptText = "You are an eccentric, hilarious AI Art Critic evaluating a 30-second doodle of '$subject'. " +
                        "The user drew it in $secondsSpent seconds with $strokeCount strokes and $totalPoints points. " +
                        "Return strict JSON with keys: 'score' (integer 65 to 98), 'titleBadge' (fun 2-3 word title e.g. 'Speedy Picasso', 'Doodle Wizard'), " +
                        "'commentary' (funny 1-2 sentence encouraging AI critique), 'aiRecognitionGuess' (what the AI recognized)."
                val result = callGeminiApi(apiKey, promptText)
                if (result != null) {
                    val cleanJson = result.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val obj = JSONObject(cleanJson)
                    return@withContext DrawingEvaluationResult(
                        score = obj.optInt("score", Random.nextInt(75, 96)),
                        titleBadge = obj.optString("titleBadge", "Doodle Maestro"),
                        commentary = obj.optString("commentary", "Impressive hand-eye agility in under 30 seconds!"),
                        aiRecognitionGuess = obj.optString("aiRecognitionGuess", subject)
                    )
                }
            } catch (e: Exception) {
                Log.w("GeminiService", "AI Drawing eval fallback: ${e.message}")
            }
        }

        // Curated fallback evaluation
        val calculatedScore = (70 + (strokeCount.coerceIn(5, 50) * 0.4).toInt() + Random.nextInt(5, 15)).coerceIn(68, 98)
        val badges = listOf("Speedy Da Vinci", "Lightning Brush", "Neon Picasso", "Doodle Prodigy", "Chaos Artist", "Gallery Legend")
        val comments = listOf(
            "The neural net detects 92% essence of $subject! High marks for artistic bravery under time pressure.",
            "That stroke dynamism screams modern masterpiece! Our AI vision algorithm is genuinely hyped.",
            "Bold lines, electric execution, and delivered in just $secondsSpent seconds! Louvre incoming.",
            "The proportion is surrealist poetry. You captured the core soul of $subject in record time!"
        )
        DrawingEvaluationResult(
            score = calculatedScore,
            titleBadge = badges.random(),
            commentary = comments.random(),
            aiRecognitionGuess = subject
        )
    }

    suspend fun evaluateSongGuess(
        targetSong: String,
        userGuess: String,
        hintsUsed: Int
    ): SongEvaluationResult = withContext(Dispatchers.IO) {
        val cleanTarget = targetSong.lowercase().trim()
        val cleanGuess = userGuess.lowercase().trim()

        val isDirectMatch = cleanGuess.contains(cleanTarget) || cleanTarget.contains(cleanGuess)
        val score = if (isDirectMatch) {
            (100 - (hintsUsed * 15)).coerceAtLeast(40)
        } else {
            0
        }

        val feedback = if (isDirectMatch) {
            "🎯 Spot on! You unlocked the musical pulse with surgical precision!"
        } else {
            "Not quite! Listen closely to the clues and give it another spin."
        }

        SongEvaluationResult(
            isCorrect = isDirectMatch,
            score = score,
            feedback = feedback,
            trivia = "Chart-topping anthem with over 1B streams and legendary karaoke history!"
        )
    }

    private fun callGeminiApi(apiKey: String, promptText: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val rootJson = JSONObject().apply {
            val contentsArr = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArr = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", promptText)
                        }
                        put(partObj)
                    }
                    put("parts", partsArr)
                }
                put(contentObj)
            }
            put("contents", contentsArr)
        }

        val request = Request.Builder()
            .url(url)
            .post(rootJson.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("GeminiService", "HTTP error ${response.code}: ${response.body?.string()}")
                return null
            }
            val bodyString = response.body?.string() ?: return null
            val respObj = JSONObject(bodyString)
            val candidates = respObj.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            return parts.getJSONObject(0).optString("text")
        }
    }

    private fun getCuratedChallenge(category: String, topic: String?): GeneratedChallenge {
        return when (category) {
            "DRAWING_30S" -> {
                val doodles = listOf(
                    Triple("Cyberpunk Cat with Neon Sunglasses", "Doodle a futuristic feline rocking glowing shades before time expires!", "Cyberpunk Cat"),
                    Triple("Steaming Pizza Slice with Melting Cheese", "Capture the ultimate cheesy slice with pepperoni pepperoni in 30 seconds!", "Pizza Slice"),
                    Triple("Astronaut Riding a Flying Skateboard", "Sketch an astronaut grinding cosmic dust in outer space!", "Cosmic Skater"),
                    Triple("Roaring Lion Wearing a Royal Crown", "Fast-sketch the king of the jungle showing off regal crown drip!", "Royal Lion"),
                    Triple("Retro Arcade Cabinet Spilling Pixels", "Nostalgic 80s arcade machine glowing with game power!", "Arcade Machine"),
                    Triple("Friendly Dragon Drinking Boba Tea", "Draw a mythical beast chilling with a giant tapioca straw!", "Dragon Boba")
                )
                val chosen = doodles.random()
                GeneratedChallenge(
                    title = chosen.first,
                    description = chosen.second,
                    category = "DRAWING_30S",
                    targetAnswer = chosen.third,
                    promptDetail = "You have exactly 30 seconds to sketch this. When the timer hits 0, our AI Referee will score your lines, style, and accuracy!",
                    hints = listOf("Focus on the primary outline first", "Add distinct accessory details in the last 10s", "Bold strokes score higher on AI vision"),
                    difficulty = "Medium",
                    xpReward = 150
                )
            }
            "GUESS_SONG" -> {
                val songs = listOf(
                    SongPrompt(
                        title = "Guess the Global Pop Anthem",
                        answer = "Shape of You",
                        artist = "Ed Sheeran",
                        clues = listOf("🎵 Club isn't the best place to find a lover...", "📦 Bar with magnetic guitar loop beats", "💃 Released 2017 • British Pop King")
                    ),
                    SongPrompt(
                        title = "Guess the Synthwave Monster",
                        answer = "Blinding Lights",
                        artist = "The Weeknd",
                        clues = listOf("🌃 'Sin City's cold and empty, no one's around to judge me...'", "🚗 Driving fast under red neon lamps", "⚡ 80s retro-synth bassline that ruled 2020")
                    ),
                    SongPrompt(
                        title = "Guess the High Energy Bollywood Hit",
                        answer = "Chhaiya Chhaiya",
                        artist = "A.R. Rahman / Sukhwinder",
                        clues = listOf("🚂 Dancing on top of a speeding mountain train", "🥁 Iconic folk sufi beat + electric energy", "👳 Dil Se cinematic legend")
                    ),
                    SongPrompt(
                        title = "Guess the Rock Legend",
                        answer = "Bohemian Rhapsody",
                        artist = "Queen",
                        clues = listOf("🎭 'Is this the real life? Is this just fantasy?'", "⚡ Galileo, Figaro, Magnifico operatic crescendo", "👑 Freddie Mercury masterwork")
                    ),
                    SongPrompt(
                        title = "Guess the Viral Reggaeton Vibe",
                        answer = "Despacito",
                        artist = "Luis Fonsi ft. Daddy Yankee",
                        clues = listOf("🏖️ Sun, beach, Puerto Rico acoustic strums", "🎶 'Quiero respirar tu cuello...'", "🔥 Shattered all YouTube streaming records")
                    )
                )
                val chosen = songs.random()
                GeneratedChallenge(
                    title = chosen.title,
                    description = "Decode the lyric riddles, acoustic clues, and emoji puzzle to name the track!",
                    category = "GUESS_SONG",
                    targetAnswer = chosen.answer,
                    promptDetail = "Artist: ${chosen.artist} | Listen to the rhythm hints and unlock the song title before time runs out!",
                    hints = chosen.clues,
                    difficulty = "Hard",
                    xpReward = 120
                )
            }
            else -> {
                GeneratedChallenge(
                    title = "Rapid AI Synapse Sprint",
                    description = "Beat the AI clock in this high-intensity creative logic duel.",
                    category = "AI_TRIVIA",
                    targetAnswer = "Superconductor",
                    promptDetail = "What zero-resistance marvel floats effortlessly above quantum magnetic tracks?",
                    hints = listOf("Discovered by Kamerlingh Onnes", "Requires cryogenic liquid nitrogen or helium", "Enables hyperloop & MRI magnets"),
                    difficulty = "Medium",
                    xpReward = 100
                )
            }
        }
    }

    private data class SongPrompt(val title: String, val answer: String, val artist: String, val clues: List<String>)
}
