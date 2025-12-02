package com.proj.controleenergia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.proj.controleenergia.ui.theme.ControleEnergiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TelaAparelho(
                aparelhos = listOf(
                    Aparelho("Ventilador", android.R.drawable.ic_menu_camera, 120, 5),
                    Aparelho("Geladeira", android.R.drawable.ic_menu_gallery, 300, 24)
                )
            )
        }
    }
}

@Composable
fun TelaAparelho(aparelhos: List<Aparelho>) {

    var valorKwm by remember { mutableStateOf("0.85") }

    var listaAparelhos by remember { mutableStateOf(aparelhos.toMutableList()) }

    var totalMes by remember {
        mutableStateOf(
            calcularTotalMes(listaAparelhos, valorKwm.toDoubleOrNull() ?: 0.0)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // TOPO
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Controle de conta de energia")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Valor KWM + botão recarregar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = valorKwm,
                        onValueChange = { valorKwm = it },
                        label = { Text("kWh") },
                        modifier = Modifier.width(120.dp)
                    )

                    IconButton(
                        onClick = {
                            totalMes =
                                calcularTotalMes(listaAparelhos, valorKwm.toDoubleOrNull() ?: 0.0)
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recalcular")
                    }
                }

                Text("Gasto do mês: R$ ${"%.2f".format(totalMes)}")
            }
        }

        // MEIO: LISTA DE APARELHOS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            listaAparelhos.forEach { aparelho ->
                AparelhoItem(
                    aparelho = aparelho,
                    onEditar = {},
                    onExcluir = {
                        listaAparelhos = listaAparelhos.toMutableList().apply {
                            remove(aparelho)
                        }
                        totalMes = calcularTotalMes(
                            listaAparelhos,
                            valorKwm.toDoubleOrNull() ?: 0.0
                        )
                    }
                )
            }
        }

        // FUNDO: botão
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {}) {
                Text("Adicionar Aparelho")
            }
        }
    }
}

@Composable
fun AparelhoItem(
    aparelho: Aparelho,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // ESQUERDA: imagem + infos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = aparelho.imagem),
                contentDescription = aparelho.nome,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = aparelho.nome)
                Text(text = "Potência: ${aparelho.potenciaWatts}W")
                Text(text = "Uso diário: ${aparelho.usoDiarioHoras}h/dia")
            }
        }

        // DIREITA: botões
        Row {
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }

            IconButton(onClick = onExcluir) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

fun calcularTotalMes(aparelhos: List<Aparelho>, valorKwm: Double): Double {
    return aparelhos.sumOf { aparelho ->
        val consumoDiario = aparelho.potenciaWatts * aparelho.usoDiarioHoras / 1000.0
        val consumoMensal = consumoDiario * 30
        consumoMensal * valorKwm
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ControleEnergiaTheme {
        TelaAparelho(
            aparelhos = listOf(
                Aparelho(
                    nome = "Ventilador",
                    imagem = android.R.drawable.ic_menu_camera,
                    potenciaWatts = 120,
                    usoDiarioHoras = 5
                ),
                Aparelho(
                    nome = "Micro-Ondas",
                    imagem = android.R.drawable.ic_menu_camera,
                    potenciaWatts = 90,
                    usoDiarioHoras = 9
                )
            )
        )
    }
}

data class Aparelho(
    val nome: String,
    val imagem: Int,
    val potenciaWatts: Int,
    val usoDiarioHoras: Int
)
