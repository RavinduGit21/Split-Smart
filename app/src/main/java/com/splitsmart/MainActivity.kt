package com.splitsmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.splitsmart.navigation.AppNavHost
import com.splitsmart.ui.theme.SplitSmartTheme
import com.splitsmart.navigation.Routes

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			SplitSmartTheme { AppRoot() }
		}
	}
}

@Composable
fun AppRoot() {
	val navController = rememberNavController()
    Surface(color = MaterialTheme.colorScheme.background) {
        androidx.compose.material3.Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { androidx.compose.material3.Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* Future: Groups */ },
                        icon = { Icon(Icons.Default.Group, contentDescription = null) },
                        label = { androidx.compose.material3.Text("Groups") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* Future: Profile */ },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { androidx.compose.material3.Text("Profile") }
                    )
                }
            }
        ) { inner ->
            androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.padding(inner)) {
                AppNavHost(navController = navController)
            }
        }
    }
}

@Preview
@Composable
fun PreviewApp() {
	SplitSmartTheme { AppRoot() }
}
