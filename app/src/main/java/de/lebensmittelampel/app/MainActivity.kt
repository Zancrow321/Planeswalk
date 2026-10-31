package de.lebensmittelampel.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.lebensmittelampel.app.notify.ExpiryCheckWorker
import de.lebensmittelampel.app.notify.Notifications
import de.lebensmittelampel.app.ui.AppTheme
import de.lebensmittelampel.app.ui.ProductEditScreen
import de.lebensmittelampel.app.ui.ProductListScreen
import de.lebensmittelampel.app.ui.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as LebensmittelApp
        ExpiryCheckWorker.runNow(this)

        setContent {
            AppTheme {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { granted -> if (granted) ExpiryCheckWorker.runNow(this) }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Notifications.canNotify(this@MainActivity)) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val vm: ProductViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            ProductViewModel(app.repository) { ExpiryCheckWorker.runNow(app) }
                        }
                    },
                )
                val nav = rememberNavController()
                NavHost(navController = nav, startDestination = "liste") {
                    composable("liste") {
                        ProductListScreen(
                            viewModel = vm,
                            onAdd = { nav.navigate("produkt") },
                            onOpen = { id -> nav.navigate("produkt?id=$id") },
                        )
                    }
                    composable(
                        "produkt?id={id}",
                        arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L }),
                    ) { entry ->
                        val id = entry.arguments?.getLong("id")?.takeIf { it > 0 }
                        ProductEditScreen(viewModel = vm, productId = id, onDone = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
