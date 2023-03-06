package com.example.barchart3d

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barchart3d.ui.theme.*
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarChart3DTheme {
                var showDescription by remember {
                    mutableStateOf(false)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.7f))
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Preferred Programming Languages",
                            fontWeight = FontWeight.Bold,
                            color = white,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center
                        )
                        BarChart(
                            inputList = listOf(
                                BarChartInput(28, "Kotlin", orange),
                                BarChartInput(15, "Swift", brightBlue),
                                BarChartInput(11, "Ruby", green),
                                BarChartInput(7, "Cobol", purple),
                                BarChartInput(14, "C++", blueGray),
                                BarChartInput(9, "C", redOrange),
                                BarChartInput(21, "Python", darkGray)
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            showDescription = showDescription
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Show description",
                                color = white,
                                fontWeight = FontWeight.SemiBold
                            )
                            Switch(
                                checked = showDescription,
                                onCheckedChange = {
                                    showDescription = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = orange,
                                    uncheckedThumbColor = white
                                )
                            )


                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BarChart(
    inputList: List<BarChartInput>,
    modifier: Modifier = Modifier,
    showDescription: Boolean
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val listSum by remember {
            mutableStateOf(inputList.sumOf { it.value })
        }
        inputList.forEach { input ->
            val percentage = input.value / listSum.toFloat()

            Bar(
                modifier = Modifier
                    .height(120.dp * percentage * inputList.size)
                    .width(40.dp),
                primaryColor = input.color,
                percentage = percentage,
                description = input.description,
                showDescription = showDescription
            )
        }
    }
}

@Composable
fun Bar(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    percentage: Float,
    description: String,
    showDescription: Boolean
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val barWidth = width / 5 * 3
            var barHeight = height / 8 * 7
            //until here is enough for 2D chart
            val barHeight3DPart = height - barHeight
            val barWidth3DPart = (width - barWidth) * (height * 0.002f)

            var path = Path().apply {
                moveTo(0f, height)
                lineTo(barWidth, height)
                lineTo(barWidth, height - barHeight)
                lineTo(0f, height - barHeight)
                close()
            }
            drawPath(
                path,
                brush = Brush.linearGradient(
                    colors = listOf(gray, primaryColor)
                )
            )

            path = Path().apply {
                moveTo(barWidth, height - barHeight)
                lineTo(barWidth3DPart + barWidth, 0f)
                lineTo(barWidth3DPart + barWidth, barHeight)
                lineTo(barWidth, height)
                close()
            }

            drawPath(
                path,
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, gray)
                )
            )
            path = Path().apply {
                moveTo(0f, barHeight3DPart)
                lineTo(barWidth, barHeight3DPart)
                lineTo(barWidth + barWidth3DPart, 0f)
                lineTo(barWidth3DPart, 0f)
                close()
            }
            drawPath(
                path,
                brush = Brush.linearGradient(
                    colors = listOf(gray, primaryColor)
                )
            )
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${(percentage * 100).roundToInt()} %",
                    barWidth / 5f,
                    height + 55f,
                    android.graphics.Paint().apply {
                        color = white.toArgb()
                        textSize = 11.dp.toPx()
                        isFakeBoldText = true
                    }
                )
            }
            if (showDescription) {
                drawContext.canvas.nativeCanvas.apply {
                    rotate(-50f, pivot = Offset(barHeight3DPart - 20, 0f)) {
                        drawText(
                            description,
                            0f,
                            0f,
                            android.graphics.Paint().apply {
                                color = white.toArgb()
                                textSize = 14.dp.toPx()
                                isFakeBoldText = true
                            }
                        )
                    }
                }
            }
        }
    }
}

data class BarChartInput(
    val value: Int,
    val description: String,
    val color: Color
)