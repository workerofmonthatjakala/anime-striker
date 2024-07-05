import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import idle_game.composeapp.generated.resources.Hitergrud
import idle_game.composeapp.generated.resources.Res
import idle_game.composeapp.generated.resources.bild
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = darkColors(
            primary = Color(0xFFFFA500),     // Orange
            background = Color(0xFFFFF5E6),  // Cream
            surface = Color(0xFFFFE4B5),     // Light Cream
            onPrimary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    ) {
        Screen()
    }
}

@Composable
@Preview
fun Screen() {
    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(GameViewModel(scope = coroutineScope))
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }
            Image(
                painter = painterResource(Res.drawable.Hitergrud),
                contentDescription = "A square",
                modifier = Modifier.fillMaxWidth().fillMaxHeight()

            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {


                Image(
                    painter = painterResource(Res.drawable.bild),
                    contentDescription = "Clicker",
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            gameState?.let { state -> viewModel.clickMoney(state) }
                        }
                )




                NarutoTitle()
                Spacer(modifier = Modifier.height(16.dp))
                ResetButton { viewModel.reset() }
                Spacer(modifier = Modifier.height(16.dp))

                gameState?.let { state ->
                    BankInfo(currentMoney)
                    Spacer(modifier = Modifier.height(8.dp))
                    MoneyClickButton { viewModel.clickMoney(state) }
                    Spacer(modifier = Modifier.height(16.dp))

                    state.availableJobs.forEach { availableJob ->
                        Generator(
                            gameJob = availableJob,
                            alreadyBought = state.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(state, availableJob) },
                            onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun NarutoTitle() {
    Text(
        "Ninja Tycoon",
        style = MaterialTheme.typography.h4.copy(
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        ),
    )
}

@Composable
fun ResetButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            backgroundColor = MaterialTheme.colors.primary  // Orange
        ),
    ) {
        Text("Reset Game")
    }
}

@Composable
fun BankInfo(currentMoney: Gelds?) {
    Text(
        "Bank: ${currentMoney?.toHumanReadableString()} Ryo",
        style = MaterialTheme.typography.h5.copy(
            color = MaterialTheme.colors.onBackground,  // Black
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun MoneyClickButton(onClick: () -> Unit) {

}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFF5E6), Color(0xFFFFE4B5)),  // Cream gradient
                ),
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Ninja ${gameJob.name}",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = MaterialTheme.colors.primary,  // Orange
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                "Level: ${gameJob.level.level}",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onBackground  // Black
                )
            )
            Text(
                "Costs: ${gameJob.level.cost.toHumanReadableString()} Ryo",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onBackground  // Black
                )
            )
            Text(
                "Earns: ${gameJob.level.earn.toHumanReadableString()} Ryo",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onBackground  // Black
                )
            )
            Text(
                "Duration: ${gameJob.level.duration.inWholeSeconds} Seconds",
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onBackground  // Black
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    backgroundColor = MaterialTheme.colors.primary  // Orange
                )
            ) {
                Text("Recruit")
            }
        } else {
            Text(
                "Recruited",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = MaterialTheme.colors.primary,  // Orange
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                backgroundColor = MaterialTheme.colors.primary  // Orange
            )
        ) {
            Text("Train")
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(2000L) // Delay for 2 seconds
        navController.navigate("main") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Surface(
        color = Color(0xFFFFF5E6),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {


            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ninja Tycoon",
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
            )
        }
    }
}
