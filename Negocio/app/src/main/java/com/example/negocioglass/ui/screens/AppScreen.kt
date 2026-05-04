package com.example.negocioglass.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.QueryStats
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.negocioglass.data.BusinessEntity
import com.example.negocioglass.data.DateFilter
import com.example.negocioglass.data.EntryEntity
import com.example.negocioglass.data.EntryType
import com.example.negocioglass.ui.components.CircleAddBadge
import com.example.negocioglass.ui.theme.BlueAccent
import com.example.negocioglass.ui.theme.BorderSoft
import com.example.negocioglass.ui.theme.CyanAccent
import com.example.negocioglass.ui.theme.Danger
import com.example.negocioglass.ui.theme.DeepBlue
import com.example.negocioglass.ui.theme.GoldAccent
import com.example.negocioglass.ui.theme.Midnight
import com.example.negocioglass.ui.theme.MintAccent
import com.example.negocioglass.ui.theme.RoseAccent
import com.example.negocioglass.ui.theme.Success
import com.example.negocioglass.ui.theme.SurfaceGlass
import com.example.negocioglass.ui.theme.SurfaceGlassStrong
import com.example.negocioglass.ui.theme.TextMuted
import com.example.negocioglass.ui.theme.TextPrimary
import com.example.negocioglass.ui.theme.TextSecondary
import com.example.negocioglass.viewmodel.NegocioViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenPadding = 20.dp
private val CardRadius = 28.dp

@Composable
fun NegocioGlassApp(application: Application) {
    val navController = rememberNavController()
    val vm: NegocioViewModel = viewModel(factory = NegocioViewModel.factory(application))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Midnight,
                        DeepBlue,
                        Color(0xFF0B1430)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BlueAccent.copy(alpha = 0.14f),
                            Color.Transparent
                        )
                    )
                )
        )

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(navController, vm)
            }
            composable("detail/{businessId}") { backStack ->
                val businessId = backStack.arguments?.getString("businessId")?.toLongOrNull() ?: 0L
                DetailScreen(navController, vm, businessId)
            }
        }
    }
}

@Composable
private fun HomeScreen(
    navController: NavHostController,
    vm: NegocioViewModel
) {
    val businesses by vm.businesses.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = ScreenPadding,
            end = ScreenPadding,
            top = 18.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HomeHero()
        }

        if (businesses.isEmpty()) {
            item {
                EmptyBusinessesCard(onAdd = { showDialog = true })
            }
        } else {
            items(businesses, key = { it.id }) { business ->
                BusinessCardPremium(
                    business = business,
                    onOpen = { navController.navigate("detail/${business.id}") },
                    onDelete = { vm.deleteBusiness(business.id) }
                )
            }

            item {
                AddBusinessCard(
                    onClick = { showDialog = true }
                )
            }
        }
    }

    if (showDialog) {
        AddBusinessDialog(
            onDismiss = { showDialog = false },
            onSave = { name, icon, accent ->
                vm.addBusiness(name, icon, accent)
                showDialog = false
            }
        )
    }
}

@Composable
private fun HomeHero() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlass,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            BlueAccent.copy(alpha = 0.16f),
                            CyanAccent.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Negocio",
                    color = TextPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Registra gastos y ventas por negocio con una vista limpia, elegante y fácil de revisar.",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyBusinessesCard(
    onAdd: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Storefront,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = "Todavía no tienes negocios",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Crea uno para empezar a registrar gastos y ventas.",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueAccent
                )
            ) {
                Text("Crear negocio")
            }
        }
    }
}

@Composable
private fun BusinessCardPremium(
    business: BusinessEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                BlueAccent.copy(alpha = 0.20f),
                                CyanAccent.copy(alpha = 0.08f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = business.icon,
                    fontSize = 25.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = business.name,
                    color = TextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Abrir panel del negocio",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary
            )

            IconButton(onClick = { confirmDelete = true }) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Eliminar negocio") },
            text = { Text("Se borrarán también sus movimientos guardados.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        confirmDelete = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AddBusinessCard(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAddBadge()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Agregar nuevo negocio",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Crea otra sección para otro emprendimiento.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DetailScreen(
    navController: NavHostController,
    vm: NegocioViewModel,
    businessId: Long
) {
    val businesses by vm.businesses.collectAsStateWithLifecycle()
    val business = businesses.firstOrNull { it.id == businessId }
    val summary by vm.summaryForBusiness(businessId).collectAsStateWithLifecycle()
    var filter by rememberSaveable { mutableStateOf(DateFilter.ALL) }
    val entries by vm.filteredEntriesForBusiness(businessId, filter).collectAsStateWithLifecycle()
    var selectedType by rememberSaveable { mutableStateOf(EntryType.EXPENSE) }

    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("1") }
    var note by rememberSaveable { mutableStateOf("") }

    if (business == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = ScreenPadding,
            end = ScreenPadding,
            top = 14.dp,
            bottom = 34.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Volver",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        }

        item {
            DetailHeader(
                title = "${business.icon} ${business.name}",
                subtitle = "Ingresa compras y ventas. La utilidad se actualiza automáticamente."
            )
        }

        item {
            SummarySection(
                expenses = summary.totalExpenses,
                sales = summary.totalSales,
                balance = summary.balance
            )
        }

        item {
            SimpleFinanceChart(
                expenses = summary.totalExpenses,
                sales = summary.totalSales
            )
        }

        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DateFilter.entries.forEach { option ->
                    FilterChip(
                        selected = filter == option,
                        onClick = { filter = option },
                        label = {
                            Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueAccent.copy(alpha = 0.22f),
                            containerColor = SurfaceGlassStrong,
                            labelColor = TextPrimary,
                            selectedLabelColor = TextPrimary
                        )
                    )
                }
            }
        }

        item {
            EntryFormCard(
                selectedType = selectedType,
                onSelectType = { selectedType = it },
                title = title,
                onTitleChange = { title = it },
                category = category,
                onCategoryChange = { category = it },
                amount = amount,
                onAmountChange = { amount = it },
                quantity = quantity,
                onQuantityChange = { quantity = it },
                note = note,
                onNoteChange = { note = it },
                onSave = {
                    vm.addEntry(
                        businessId = businessId,
                        title = title,
                        category = category,
                        amountText = amount,
                        quantityText = quantity,
                        type = selectedType,
                        note = note
                    )
                    title = ""
                    category = ""
                    amount = ""
                    quantity = "1"
                    note = ""
                }
            )
        }

        item {
            Text(
                text = "Historial",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (entries.isEmpty()) {
            item {
                EmptyEntriesCard()
            }
        } else {
            items(entries, key = { it.id }) { entry ->
                EntryRowPremium(
                    entry = entry,
                    onDelete = { vm.deleteEntry(entry.id) }
                )
            }
        }
    }
}

@Composable
private fun DetailHeader(
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlass,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            GoldAccent.copy(alpha = 0.10f),
                            BlueAccent.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }
}

@Composable
private fun SummarySection(
    expenses: Double,
    sales: Double,
    balance: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resumen",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            SummaryMiniCard(
                title = "Gastos",
                value = formatCurrency(expenses),
                accent = RoseAccent
            )

            SummaryMiniCard(
                title = "Ventas",
                value = formatCurrency(sales),
                accent = MintAccent
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Balance final",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = formatCurrency(balance),
                        color = if (balance >= 0) Success else Danger,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (balance >= 0) "Vas ganando" else "Vas perdiendo",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryMiniCard(
    title: String,
    value: String,
    accent: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = accent.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 14.sp
            )
            Text(
                text = value,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
private fun SimpleFinanceChart(
    expenses: Double,
    sales: Double
) {
    val maxValue = maxOf(expenses, sales, 1.0)
    val salesRatio = (sales / maxValue).toFloat().coerceIn(0f, 1f)
    val expensesRatio = (expenses / maxValue).toFloat().coerceIn(0f, 1f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.QueryStats,
                    contentDescription = null,
                    tint = TextPrimary
                )
                Text(
                    text = "Comparación visual",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            ChartBar(
                label = "Ventas",
                value = formatCurrency(sales),
                ratio = salesRatio,
                barColor = MintAccent
            )

            ChartBar(
                label = "Gastos",
                value = formatCurrency(expenses),
                ratio = expensesRatio,
                barColor = RoseAccent
            )
        }
    }
}

@Composable
private fun ChartBar(
    label: String,
    value: String,
    ratio: Float,
    barColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 14.sp
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.07f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .height(12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(barColor.copy(alpha = 0.85f))
            )
        }
    }
}

@Composable
private fun EntryFormCard(
    selectedType: EntryType,
    onSelectType: (EntryType) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nuevo movimiento",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EntryTypeChip(
                    text = "Gasto",
                    selected = selectedType == EntryType.EXPENSE,
                    selectedColor = RoseAccent,
                    onClick = { onSelectType(EntryType.EXPENSE) }
                )
                EntryTypeChip(
                    text = "Venta",
                    selected = selectedType == EntryType.SALE,
                    selectedColor = MintAccent,
                    onClick = { onSelectType(EntryType.SALE) }
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )

            OutlinedTextField(
                value = category,
                onValueChange = onCategoryChange,
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Monto unitario en colones") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = onQuantityChange,
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Nota opcional") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueAccent
                )
            ) {
                Text(
                    text = "Guardar movimiento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EntryTypeChip(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) selectedColor.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) selectedColor.copy(alpha = 0.28f) else BorderSoft
        )
    ) {
        Text(
            text = text,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyEntriesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Storefront,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = "Todavía no hay movimientos",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Agrega gastos o ventas para empezar a medir la utilidad.",
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EntryRowPremium(
    entry: EntryEntity,
    onDelete: () -> Unit
) {
    var confirm by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardRadius),
        color = SurfaceGlassStrong,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoft)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = entry.title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${entry.category} • ${formatDate(entry.createdAt)}",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                IconButton(onClick = { confirm = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (entry.type == EntryType.SALE) {
                        MintAccent.copy(alpha = 0.18f)
                    } else {
                        RoseAccent.copy(alpha = 0.18f)
                    }
                ) {
                    Text(
                        text = if (entry.type == EntryType.SALE) "Venta" else "Gasto",
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Text(
                        text = "Cant. ${entry.quantity}",
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 14.sp
                    )
                }
            }

            Text(
                text = formatCurrency(entry.amount),
                color = if (entry.type == EntryType.SALE) Success else Danger,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            if (entry.note.isNotBlank()) {
                Divider(color = Color.White.copy(alpha = 0.08f))
                Text(
                    text = entry.note,
                    color = TextSecondary,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }

    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Eliminar movimiento") },
            text = { Text("Este movimiento se quitará del historial y del balance.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        confirm = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AddBusinessDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var icon by rememberSaveable { mutableStateOf("✨") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo negocio") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text("Emoji") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, icon, "blue") }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
    return format.format(amount)
}

private fun formatDate(time: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(time))
}