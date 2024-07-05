package com.animeboynz.kmd

import OrdersViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.animeboynz.kmd.ui.theme.KMDTheme


class MainActivity : ComponentActivity() {
    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KMDTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationHost(navController = navController, modifier = Modifier.padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}


@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    NavHost(navController = navController, startDestination = "main_screen_route") {
        composable("main_screen_route") {
            MainScreen(navController = navController)
        }
        composable("cash_row_route") {
            CashRow(
                modifier = modifier,
                navigateToTakings = {
                    navController.navigate("takings_route")
                }
            )
        }
        composable("takings_route") {
            TakingsScreen(
                modifier = modifier,
                navigateToRemainingFloat = {
                    navController.navigate("remaining_float_route")
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("remaining_float_route") {
            RemainingFloatScreen(modifier = modifier, navigateBack = {
                navController.popBackStack()
            })
        }
        composable("search_route") {
            SearchScreen()
        }
        composable("outgoing_transfers_route")
        {
            OutgoingTransfers()
        }

        composable("customer_orders_route") {

            CustomerOrders(navController = navController, viewModel = viewModel)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KMDTheme {
        // Preview your app here
    }
}
