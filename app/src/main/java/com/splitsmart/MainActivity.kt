package com.splitsmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.splitsmart.navigation.AppNavHost
import com.splitsmart.ui.theme.SplitSmartTheme

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
		AppNavHost(navController = navController)
	}
}

@Preview
@Composable
fun PreviewApp() {
	SplitSmartTheme { AppRoot() }
}
