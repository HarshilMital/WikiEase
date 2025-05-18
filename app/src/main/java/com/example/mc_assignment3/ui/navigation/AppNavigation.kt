package com.example.mc_assignment3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mc_assignment3.ui.screens.ArticleDetailScreen
import com.example.mc_assignment3.ui.screens.FavoritesScreen
import com.example.mc_assignment3.ui.screens.HomeScreen
import com.example.mc_assignment3.ui.screens.NearbyArticlesScreen
import com.example.mc_assignment3.ui.screens.SearchScreen
import com.example.mc_assignment3.ui.screens.SettingsScreen

/**
 * Navigation routes for the app.
 */
object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val ARTICLE_DETAIL = "article/{articleId}"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val NEARBY_ARTICLES = "nearby"
    
    // Helper function to create article detail route with ID
    fun articleDetail(articleId: Int) = "article/$articleId"
}

/**
 * Main navigation component for the Wikipedia app.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                navigateToArticle = { articleId ->
                    navController.navigate(Routes.articleDetail(articleId))
                },
                navigateToSearch = {
                    navController.navigate(Routes.SEARCH)
                },
                navigateToFavorites = {
                    navController.navigate(Routes.FAVORITES)
                },
                navigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                navigateToNearbyArticles = {
                    navController.navigate(Routes.NEARBY_ARTICLES)
                }
            )
        }
        
        composable(Routes.SEARCH) {
            SearchScreen(
                navigateToArticle = { articleId ->
                    navController.navigate(Routes.articleDetail(articleId))
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "article/{articleId}",
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getInt("articleId") ?: return@composable
            
            ArticleDetailScreen(
                articleId = articleId,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Routes.FAVORITES) {
            FavoritesScreen(
                navigateToArticle = { articleId ->
                    navController.navigate(Routes.articleDetail(articleId))
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Routes.SETTINGS) {
            SettingsScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.NEARBY_ARTICLES) {
            NearbyArticlesScreen(
                navigateToArticle = { articleId ->
                    navController.navigate(Routes.articleDetail(articleId))
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}