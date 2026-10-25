package com.company.tripplanner.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.company.tripplanner.ui.screens.*
import com.company.tripplanner.ui.viewmodels.TripViewModel
import com.company.tripplanner.ui.viewmodels.UserViewModel
import com.company.tripplanner.utils.ThemeManager

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object CreateTrip : Screen("create_trip")
    object TripDetails : Screen("trip_details/{tripIndex}") {
        fun createRoute(index: Int) = "trip_details/$index"
    }
    object TripList : Screen("trip_list")
    object Search : Screen("search")
    object HotelSearch : Screen("hotel_search")
    object Map : Screen("map")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object ChangePassword : Screen("change_password")
    object Notifications : Screen("notifications")
    object Itinerary : Screen("itinerary/{tripIndex}") {
        fun createRoute(index: Int) = "itinerary/$index"
    }
    object PackingChecklist : Screen("packing_checklist/{tripIndex}") {
        fun createRoute(index: Int) = "packing_checklist/$index"
    }
    object Budget : Screen("budget/{tripIndex}") {
        fun createRoute(index: Int) = "budget/$index"
    }
    object Favorites : Screen("favorites")
    object AddActivity : Screen("add_activity")
    object ActivityDetails : Screen("activity_details")
    object TransportDetails : Screen("transport_details")
    object AccommodationDetails : Screen("accommodation_details")
    object Weather : Screen("weather/{destination}") {
        fun createRoute(destination: String) = "weather/$destination"
    }
    object NearbyPlaces : Screen("nearby_places")
    object Support : Screen("support")
    object About : Screen("about")
    object Language : Screen("language")
    object Terms : Screen("terms")
    object Privacy : Screen("privacy")
    object Collaboration : Screen("collaboration")
    object NotificationSettings : Screen("notification_settings")
    object Offline : Screen("offline")
    object Sync : Screen("sync")
    object Loading : Screen("loading")
    object Error : Screen("error")
    object Empty : Screen("empty")
    object SearchResults : Screen("search_results")
    object FilterSort : Screen("filter_sort")
    object NotificationDetail : Screen("notification_detail")
    object AppUpdate : Screen("app_update")
    object ThemePreview : Screen("theme_preview")
}

@Composable
fun AppNavigator(themeManager: ThemeManager) {
    val navController = rememberNavController()
    val tripViewModel: TripViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val notificationViewModel: com.company.tripplanner.ui.viewmodels.NotificationViewModel = viewModel()

    androidx.compose.runtime.LaunchedEffect(tripViewModel.trips) {
        notificationViewModel.syncNotificationsWithTrips(tripViewModel.trips)
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigate = {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinish = {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.Register.route) },
                onLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { email, password ->
                    if (email == "google_user") {
                        // Google Login was already handled inside UserViewModel, just navigate
                        tripViewModel.loadTrips()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    } else {
                        // Standard Email Login
                        userViewModel.login(email, password) {
                            tripViewModel.loadTrips()
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        }
                    }
                },
                onSignUpClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                userViewModel = userViewModel
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { name, email, password ->
                    userViewModel.registerUser(name, email, password, onSuccess = {
                        tripViewModel.loadTrips()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    })
                },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                userViewModel = userViewModel
            )
        }
        composable(Screen.Dashboard.route) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                tripViewModel.loadTrips()
            }
            DashboardScreen(
                onActionClick = { action ->
                    when(action) {
                        "create" -> navController.navigate(Screen.CreateTrip.route)
                        "trips" -> navController.navigate(Screen.TripList.route)
                        "settings" -> navController.navigate(Screen.Settings.route)
                        "favorites" -> navController.navigate(Screen.Favorites.route)
                        "notifications" -> navController.navigate(Screen.Notifications.route)
                        "budget" -> navController.navigate("budget")
                        "weather_local" -> navController.navigate(Screen.Weather.createRoute("My Current Location"))
                        "weather_destination" -> {
                            val trip = tripViewModel.trips.firstOrNull()
                            if (trip != null) {
                                navController.navigate(Screen.Weather.createRoute(trip.name))
                            }
                        }
                    }
                },
                onNavigateBottom = { route ->
                    when(route) {
                        "trips" -> navController.navigate(Screen.TripList.route)
                        "profile" -> navController.navigate(Screen.Profile.route)
                    }
                },
                userViewModel = userViewModel,
                latestTrip = tripViewModel.trips.firstOrNull(),
                allTrips = tripViewModel.trips,
                unreadNotificationsCount = notificationViewModel.unreadCount
            )
        }
        composable(Screen.CreateTrip.route) { 
            CreateTripScreen(
                tripViewModel = tripViewModel,
                onSave = { name, start, end, members, budget, currency ->
                    tripViewModel.addTrip(name, start, end, members, budget, currency) { newIndex ->
                        navController.navigate(Screen.Itinerary.createRoute(newIndex)) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                }
            )
        }
        composable(Screen.TripDetails.route) { backStackEntry ->
            val index = backStackEntry.arguments?.getString("tripIndex")?.toIntOrNull() ?: 0
            val trip = tripViewModel.trips.getOrNull(index)
            if (trip != null) {
                TripDetailsScreen(
                    trip = trip, 
                    onDelete = {
                        trip.id?.let { id ->
                            tripViewModel.deleteTrip(id) {
                                navController.popBackStack()
                            }
                        }
                    },
                    onViewWeather = { destination ->
                        navController.navigate(Screen.Weather.createRoute(destination))
                    },
                    onUpdateStatus = { newStatus ->
                        trip.id?.let { id ->
                            tripViewModel.updateTripStatus(id, newStatus)
                        }
                    },
                    onOpenChecklist = {
                        navController.navigate(Screen.PackingChecklist.createRoute(index))
                    }
                )
            } else {
                ErrorScreen(onRetry = {}, onBackHome = { navController.popBackStack() })
            }
        }
        composable(Screen.TripList.route) { 
            androidx.compose.runtime.LaunchedEffect(Unit) {
                tripViewModel.loadTrips()
            }
            TripListScreen(
                trips = tripViewModel.trips,
                onTripClick = { index ->
                    navController.navigate(Screen.Itinerary.createRoute(index))
                },
                onDeleteTrip = { tripId ->
                    tripViewModel.deleteTrip(tripId)
                }
            ) 
        }
        composable(Screen.Search.route) { DestinationSearchScreen() }
        composable(Screen.HotelSearch.route) { HotelSearchScreen() }
        composable(Screen.Map.route) { MapViewScreen() }
        composable(Screen.Settings.route) { 
            SettingsScreen(
                themeManager = themeManager,
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) 
        }
        composable(Screen.Profile.route) { 
            ProfileScreen(
                userViewModel = userViewModel,
                onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onMyTripsClick = { navController.navigate(Screen.TripList.route) },
                onLogout = {
                    userViewModel.logout {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            ) 
        }
        composable(Screen.EditProfile.route) { 
            EditProfileScreen(
                userViewModel = userViewModel,
                onSaveSuccess = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            ) 
        }
        composable(Screen.ChangePassword.route) { ChangePasswordScreen(onUpdate = { navController.popBackStack() }) }
        composable(Screen.Notifications.route) { 
            NotificationScreen(
                notificationViewModel = notificationViewModel,
                onOpenTrip = { index ->
                    navController.navigate(Screen.Itinerary.createRoute(index))
                },
                onBack = { navController.popBackStack() }
            ) 
        }
        composable(Screen.Itinerary.route) { backStackEntry ->
            val index = backStackEntry.arguments?.getString("tripIndex")?.toIntOrNull() ?: 0
            val trip = tripViewModel.trips.getOrNull(index)
            if (trip != null) {
                ItineraryScreen(tripName = trip.name, itineraryMap = trip.itinerary, trip = trip)
            } else {
                // Fallback
                ErrorScreen(onRetry = {}, onBackHome = { navController.popBackStack() })
            }
        }
        composable(Screen.PackingChecklist.route) { backStackEntry ->
            val index = backStackEntry.arguments?.getString("tripIndex")?.toIntOrNull() ?: 0
            val trip = tripViewModel.trips.getOrNull(index)
            if (trip != null) {
                PackingChecklistScreen(
                    trip = trip,
                    onBack = { navController.popBackStack() }
                )
            } else {
                ErrorScreen(onRetry = {}, onBackHome = { navController.popBackStack() })
            }
        }
        composable("budget") {
            BudgetScreen(
                trip = tripViewModel.trips.firstOrNull(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Budget.route) { backStackEntry ->
            val index = backStackEntry.arguments?.getString("tripIndex")?.toIntOrNull() ?: 0
            val trip = tripViewModel.trips.getOrNull(index) ?: tripViewModel.trips.firstOrNull()
            BudgetScreen(
                trip = trip,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Favorites.route) { FavoritesScreen() }
        composable(Screen.AddActivity.route) { AddActivityScreen(onSave = { navController.popBackStack() }) }
        composable(Screen.ActivityDetails.route) { ActivityDetailsScreen() }
        composable(Screen.TransportDetails.route) { TransportDetailsScreen(onSave = { navController.popBackStack() }) }
        composable(Screen.AccommodationDetails.route) { AccommodationDetailsScreen(onSave = { navController.popBackStack() }) }
        composable(Screen.Weather.route) { backStackEntry ->
            val destination = backStackEntry.arguments?.getString("destination") ?: "Paris, France"
            WeatherScreen(destination)
        }
        composable(Screen.NearbyPlaces.route) { NearbyPlacesScreen() }
        composable(Screen.Support.route) { SupportScreen() }
        
        // Final 20 Screens
        composable(Screen.About.route) { AboutScreen() }
        composable(Screen.Language.route) { LanguageSelectionScreen(onSave = { navController.popBackStack() }) }
        composable(Screen.Terms.route) { TermsConditionsScreen(onAccept = { navController.popBackStack() }) }
        composable(Screen.Privacy.route) { PrivacyPolicyScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Collaboration.route) { TripCollaborationScreen() }
        composable(Screen.NotificationSettings.route) { NotificationSettingsScreen(onSave = { navController.popBackStack() }) }
        composable(Screen.Offline.route) { OfflineModeScreen() }
        composable(Screen.Sync.route) { DataSyncScreen() }
        composable(Screen.Loading.route) { LoadingScreen() }
        composable(Screen.Error.route) { ErrorScreen(onRetry = { navController.popBackStack() }, onBackHome = { navController.navigate(Screen.Dashboard.route) }) }
        composable(Screen.Empty.route) { EmptyStateScreen(onCreateTrip = { navController.navigate(Screen.CreateTrip.route) }) }
        composable(Screen.SearchResults.route) { SearchResultsScreen() }
        composable(Screen.FilterSort.route) { FilterSortScreen(onApply = { navController.popBackStack() }) }
        composable(Screen.NotificationDetail.route) { NotificationDetailScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.AppUpdate.route) { AppUpdateScreen() }
        composable(Screen.ThemePreview.route) { ThemePreviewScreen(themeManager) }
    }
}
