import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = darkColors(
            primary = Color(0xFF00FF00),
            background = Color(0xFF000000),
            surface = Color(0xFF121212),
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
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
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                    )
                )
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    "PandaKrieger Tycoon",
                    style = MaterialTheme.typography.h4.copy(
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        backgroundColor = Color(0xFF00FF00)
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Reset Game")
                }

                Spacer(modifier = Modifier.height(16.dp))

                gameState?.let { state ->
                    Text(
                        "Bank: ${currentMoney?.toHumanReadableString()} Gelds",
                        style = MaterialTheme.typography.h5.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.clickMoney(state) },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            backgroundColor = Color(0xFF00FF00)
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Click Money")
                    }

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
                    colors = listOf(Color(0xFF1A1A1A), Color(0xFF2C2C2C)),

                ),
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Generator ${gameJob.id}",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = Color(0xFF00FF00),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                "Level: ${gameJob.level.level}",
                style = MaterialTheme.typography.body2.copy(
                    color = Color.White
                )
            )
            Text(
                "Costs: ${gameJob.level.cost.toHumanReadableString()} Gelds",
                style = MaterialTheme.typography.body2.copy(
                    color = Color.White
                )
            )
            Text(
                "Earns: ${gameJob.level.earn.toHumanReadableString()} Gelds",
                style = MaterialTheme.typography.body2.copy(
                    color = Color.White
                )
            )
            Text(
                "Duration: ${gameJob.level.duration.inWholeSeconds} Seconds",
                style = MaterialTheme.typography.body2.copy(
                    color = Color.White
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    backgroundColor = Color(0xFF00FF00)
                )
            ) {
                Text("Buy")
            }
        } else {
            Text(
                "Bought",
                style = MaterialTheme.typography.subtitle1.copy(
                    color = Color(0xFF00FF00),
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                backgroundColor = Color(0xFF00FF00)
            )
        ) {
            Text("Upgrade")
        }
    }
}
