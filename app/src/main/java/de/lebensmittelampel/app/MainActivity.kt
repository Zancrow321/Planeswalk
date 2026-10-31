package de.lebensmittelampel.app

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.lebensmittelampel.app.data.Category
import de.lebensmittelampel.app.design.specFor
import de.lebensmittelampel.app.notify.ExpiryCheckWorker
import de.lebensmittelampel.app.notify.Notifications
import de.lebensmittelampel.app.ui.AppTheme
import de.lebensmittelampel.app.ui.DesignPickerScreen
import de.lebensmittelampel.app.ui.ProductEditScreen
import de.lebensmittelampel.app.ui.ProductListScreen
import de.lebensmittelampel.app.ui.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as LebensmittelApp
        ExpiryCheckWorker.runNow(this)

        setContent {
            val design by app.designPrefs.design.collectAsStateWithLifecycle()
            val hintSeen by app.designPrefs.hintSeen.collectAsStateWithLifecycle()
            val spec = remember(design) { specFor(design) }

            LaunchedEffect(spec.dark) {
                val style = if (spec.dark) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }

            AppTheme(spec) {
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
                            hintSeen = hintSeen,
                            onAdd = { cat -> nav.navigate("produkt?cat=${cat.name}") },
                            onOpen = { id -> nav.navigate("produkt?id=$id") },
                            onPickDesign = {
                                app.designPrefs.markHintSeen()
                                nav.navigate("design")
                            },
                        )
                    }
                    composable(
                        "produkt?id={id}&cat={cat}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.LongType; defaultValue = -1L },
                            navArgument("cat") { type = NavType.StringType; defaultValue = Category.VORRAT.name },
                        ),
                    ) { entry ->
                        val id = entry.arguments?.getLong("id")?.takeIf { it > 0 }
                        val cat = Category.from(entry.arguments?.getString("cat"))
                        ProductEditScreen(viewModel = vm, productId = id, initialCategory = cat, onDone = { nav.popBackStack() })
                    }
                    composable("design") {
                        DesignPickerScreen(
                            current = design,
                            onPick = { d ->
                                app.designPrefs.set(d)
                                nav.popBackStack()
                            },
                            onBack = { nav.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
