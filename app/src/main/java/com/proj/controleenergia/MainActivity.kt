package com.proj.controleenergia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.proj.controleenergia.ui.theme.ControleEnergiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppControleEnergia() }
    }
}

// APP COM NAVEGAÇÃO
@Composable
fun AppControleEnergia() {
    var telaAtual by remember { mutableStateOf("lista") }

    var listaAparelhos by remember {
        mutableStateOf(
            mutableListOf(
                Aparelho("Ventilador", android.R.drawable.ic_menu_manage, 120, 5),
                Aparelho("Geladeira", android.R.drawable.ic_menu_manage, 300, 24)
            )
        )
    }

    ControleEnergiaTheme {
        when (telaAtual) {

            "lista" -> TelaAparelho(
                aparelhos = listaAparelhos,
                onAdicionar = { telaAtual = "adicionar" },
                onExcluir = { aparelho ->
                    listaAparelhos = listaAparelhos.toMutableList().apply { remove(aparelho) }
                }
            )

            "adicionar" -> TelaAdicionarAparelho(
                onSalvar = { nome, potencia, uso ->
                    val novo = Aparelho(
                        nome = nome,
                        imagem = android.R.drawable.ic_menu_manage,
                        potenciaWatts = potencia.toIntOrNull() ?: 0,
                        usoDiarioHoras = uso.toIntOrNull() ?: 0
                    )
                    listaAparelhos.add(novo)
                    telaAtual = "lista"
                },
                onCancelar = { telaAtual = "lista" }
            )
        }
    }
}

// MODELO
data class Aparelho(
    val nome: String,
    val imagem: Int,
    val potenciaWatts: Int,
    val usoDiarioHoras: Int
)

// TELA LISTA
@Composable
fun TelaAparelho(
    aparelhos: List<Aparelho>,
    onAdicionar: () -> Unit,
    onExcluir: (Aparelho) -> Unit
) {

    var valorKwm by remember { mutableStateOf("0.85") }
    var totalMes by remember {
        mutableStateOf(calcularTotalMes(aparelhos, valorKwm.toDoubleOrNull() ?: 0.0))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Controle de conta de energia")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = valorKwm,
                        onValueChange = { valorKwm = it },
                        label = { Text("kWh") },
                        modifier = Modifier.width(120.dp)
                    )

                    IconButton(
                        onClick = {
                            totalMes = calcularTotalMes(
                                aparelhos,
                                valorKwm.toDoubleOrNull() ?: 0.0
                            )
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recalcular")
                    }
                }

                Text("Gasto do mês: R$ ${"%.2f".format(totalMes)}")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            aparelhos.forEach { aparelho ->
                AparelhoItem(
                    aparelho = aparelho,
                    onEditar = {},
                    onExcluir = { onExcluir(aparelho) }
                )
            }
        }

        Button(onClick = onAdicionar) {
            Text("Adicionar Aparelho")
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = aparelho.imagem),
                contentDescription = aparelho.nome,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(aparelho.nome)
                Text("Potência: ${aparelho.potenciaWatts}W")
                Text("Uso diário: ${aparelho.usoDiarioHoras}h/dia")
            }
        }

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

// TELA ADICIONAR
@Composable
fun TelaAdicionarAparelho(
    onSalvar: (String, String, String) -> Unit,
    onCancelar: () -> Unit
) {

    var nome by remember { mutableStateOf("") }
    var potencia by remember { mutableStateOf("") }
    var usoDiario by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do aparelho") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = potencia,
                onValueChange = { potencia = it },
                label = { Text("Potência (W)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = usoDiario,
                onValueChange = { usoDiario = it },
                label = { Text("Uso diário (h)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { onSalvar(nome, potencia, usoDiario) }) {
                    Text("Salvar")
                }

                Button(onClick = onCancelar) {
                    Text("Cancelar")
                }
            }
        }
    }
}

// PREVIEW
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ControleEnergiaTheme {
        AppControleEnergia()
    }
}
