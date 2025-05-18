package com.example.mc_assignment3.data.remote

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Client for interacting with OpenAI API to generate article summaries
 */
class OpenAIClient(private val apiKey: String) {
    
    private val openAI by lazy { OpenAI(apiKey) }
    
    /**
     * Generate a concise summary of a Wikipedia article using AI
     * @param articleText The full text of the Wikipedia article
     * @return A concise summary of the article
     */
    suspend fun generateSummary(articleText: String): String = withContext(Dispatchers.IO) {
        try {
            // Truncate article if it's too long to stay within token limits
            val truncatedArticle = if (articleText.length > 3000) {
                articleText.substring(0, 3000)
            } else {
                articleText
            }
            
            val prompt = "Summarize this Wikipedia article in 3-5 sentences:\n\n$truncatedArticle"
            
            val completionRequest = CompletionRequest(
                model = ModelId("gpt-3.5-turbo-instruct"),
                prompt = prompt,
                maxTokens = 150,
                temperature = 0.5
            )
            
            val completion = openAI.completion(completionRequest)
            return@withContext completion.choices[0].text.trim()
        } catch (e: Exception) {
            return@withContext "Unable to generate summary: ${e.message}"
        }
    }
}