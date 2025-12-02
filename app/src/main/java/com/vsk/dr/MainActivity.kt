package com.vsk.dr

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.vsk.dr.ui.theme.Dr___Theme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private var counter: Int = 0

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val appsInfo = viewModel.getInstalledApps()
        Timber.d("installed applications size: ${appsInfo.size}")
        enableEdgeToEdge()

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                Dr___Theme {
                    val navController = rememberNavController()
                    Scaffold(topBar = {
                        TopAppBar(
                            title = {
                                Text(text = getString(R.string.app_name))
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    if(counter > 0) {
                                        counter--
                                        navController.popBackStack()
                                    } else {
                                        finishAndRemoveTask()
                                    }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack,"")
                                }
                            }
                        )

                    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
                        ShowApplications(
                            this,
                            viewModel = viewModel,
                            appInfoList = appsInfo
                        )
                    }
                }
            }
        }
    }

//    @ExperimentalCoilApi
//    @Composable
//    private fun ItemComposable(navController: NavHostController) {
//        counter++
//        Scaffold(modifier = Modifier.padding(16.dp), content = {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Top,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Image(
//                    painter = rememberImagePainter(content!!.photo),
//                    contentDescription = null,
//                    modifier = Modifier.size(256.dp)
//                )
//                Text(style = typography.h6,
//                    text = content!!.name)
//                Text(text = content!!.address)
//                Text(text = content!!.description)
//            }
//        })
//    }

}

@Composable
fun ShowApplications(ctx: Context, viewModel: MainViewModel, appInfoList: List<ApplicationInfo>) {

    LazyColumn {
        items(appInfoList.size) { item ->
            val interactionSource = remember { MutableInteractionSource() }
            viewModel.getApplicationName(appInfoList[item])?.let {
                Text(
                    text = it,
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
                        .padding(16.dp)
                        .clickable (
                            onClick = {
                                Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
                            },
                            interactionSource = interactionSource,
                            indication = ripple()
                        ),
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                    )
            }
        }
    }
}