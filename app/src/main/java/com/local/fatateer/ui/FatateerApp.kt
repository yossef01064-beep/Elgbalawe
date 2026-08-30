package com.local.fatateer.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Category
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.local.fatateer.data.Categories
import com.local.fatateer.data.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FatateerApp(
    isDark: Boolean = false,
    onToggleTheme: () -> Unit = {},
    vm: StockViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var editor by remember { mutableStateOf<Item?>(null) }
    var showNew by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Item?>(null) }

    val title = when (state.tab) {
        MainTab.HOME -> "الرئيسية"
        MainTab.SPARE -> "قطع الغيار"
        MainTab.SALES -> "البيع"
    }

    val chipCats = when (state.tab) {
        MainTab.HOME -> emptyList()
        MainTab.SPARE -> Categories.spareParts
        MainTab.SALES -> Categories.sales
    }

    val defaultCategory = when (state.tab) {
        MainTab.SPARE -> Categories.spareParts.first()
        MainTab.SALES -> Categories.sales.first()
        MainTab.HOME -> Categories.all.first()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                var menuOpen by remember { mutableStateOf(false) }
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "تبديل الثيم",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        if (state.tab != MainTab.HOME) {
                            IconButton(onClick = { menuOpen = true }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "الأقسام",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            DropdownMenu(
                                expanded = menuOpen,
                                onDismissRequest = { menuOpen = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("كل الأقسام") },
                                    onClick = {
                                        vm.setCategory(null)
                                        menuOpen = false
                                    }
                                )
                                chipCats.forEach { cat ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                cat,
                                                fontWeight = if (state.selectedCategory == cat)
                                                    FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            vm.setCategory(cat)
                                            menuOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = state.tab == MainTab.HOME,
                        onClick = { vm.setTab(MainTab.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("الرئيسية") }
                    )
                    NavigationBarItem(
                        selected = state.tab == MainTab.SPARE,
                        onClick = { vm.setTab(MainTab.SPARE) },
                        icon = { Icon(Icons.Default.Build, contentDescription = null) },
                        label = { Text("قطع الغيار") }
                    )
                    NavigationBarItem(
                        selected = state.tab == MainTab.SALES,
                        onClick = { vm.setTab(MainTab.SALES) },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text("البيع") }
                    )
                }
            },
            floatingActionButton = {
                if (state.tab != MainTab.HOME) {
                    FloatingActionButton(onClick = { showNew = true }) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة")
                    }
                }
            }
        ) { padding ->
            when (state.tab) {
                MainTab.HOME -> HomeScreen(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    onOpenSpare = { vm.setTab(MainTab.SPARE) },
                    onOpenSales = { vm.setTab(MainTab.SALES) }
                )
                MainTab.SPARE, MainTab.SALES -> InventoryScreen(
                    state = state,
                    chipCats = chipCats,
                    onQuery = vm::setQuery,
                    onCategory = vm::setCategory,
                    onPlus = vm::plus,
                    onMinus = vm::minus,
                    onEdit = { editor = it },
                    onDelete = { toDelete = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

        if (showNew) {
            // When the user opens the editor from a category list, keep that
            // context instead of forcing the first category every time.
            val startCat = state.selectedCategory ?: defaultCategory
            ItemEditorDialog(
                title = "إضافة صنف",
                initial = Item(
                    name = "",
                    category = startCat,
                    subCategory = "",
                    brand = "",
                    quantity = 0,
                    minQuantity = 1
                ),
                allowedCategories = Categories.all,
                askMainSection = true,
                onDismiss = { showNew = false },
                onSave = {
                    vm.save(it)
                    showNew = false
                }
            )
        }
        editor?.let { current ->
            ItemEditorDialog(
                title = "تعديل",
                initial = current,
                allowedCategories = Categories.all,
                askMainSection = true,
                onDismiss = { editor = null },
                onSave = {
                    vm.save(it)
                    editor = null
                }
            )
        }
        toDelete?.let { item ->
            AlertDialog(
                onDismissRequest = { toDelete = null },
                title = { Text("حذف؟") },
                text = { Text("هيتشال ${item.name}") },
                confirmButton = {
                    TextButton(onClick = {
                        vm.delete(item)
                        toDelete = null
                    }) { Text("حذف") }
                },
                dismissButton = {
                    TextButton(onClick = { toDelete = null }) { Text("إلغاء") }
                }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    state: StockUiState,
    modifier: Modifier = Modifier,
    onOpenSpare: () -> Unit,
    onOpenSales: () -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("ملخص المحل", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniStat("قطع الغيار", "${state.spareCount}", Modifier.weight(1f), onClick = onOpenSpare)
            MiniStat("البيع", "${state.salesCount}", Modifier.weight(1f), onClick = onOpenSales)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniStat("الكميات", "${state.totalQty}", Modifier.weight(1f))
            MiniStat("ناقص", "${state.neededCount}", Modifier.weight(1f), alert = state.neededCount > 0)
        }
        if (state.neededCount > 0) {
            Text(
                "في ${state.neededCount} صنف محتاج طلب — استخدم الشريط تحت عشان تراجع",
                fontSize = 13.sp,
                color = Color(0xFFC44536)
            )
        } else {
            Text(
                "مفيش أصناف ناقصة حالياً",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun MiniStat(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    alert: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert) Color(0xFFC44536) else MaterialTheme.colorScheme.primary
        )
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
            Text(title, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    highlight: Boolean = false
) {
    val colors = if (highlight) {
        CardDefaults.cardColors(containerColor = Color(0xFFC44536), contentColor = Color.White)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    }
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier,
        colors = colors,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                title,
                color = if (highlight) Color.White.copy(alpha = 0.85f)
                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
            Text(
                value,
                color = if (highlight) Color.White else MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
private fun InventoryScreen(
    state: StockUiState,
    chipCats: List<String>,
    onQuery: (String) -> Unit,
    onCategory: (String?) -> Unit,
    onPlus: (Item) -> Unit,
    onMinus: (Item) -> Unit,
    onEdit: (Item) -> Unit,
    onDelete: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        // أول اختيار = موجز كل الأقسام
        if (state.selectedCategory == null && state.query.isBlank()) {
            Text("التصنيفات", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "ناقص: ${state.filtered.count { it.quantity <= it.minQuantity }}",
                fontWeight = FontWeight.SemiBold,
                color = if (state.filtered.any { it.quantity <= it.minQuantity })
                    Color(0xFFC44536) else MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                gridItems(chipCats, key = { it }) { cat ->
                    val inCat = state.filtered.filter { it.category == cat }
                    val needed = inCat.count { it.quantity <= it.minQuantity }
                    Card(
                        onClick = { onCategory(cat) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (needed > 0) Color(0xFFFFE8E4)
                            else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.height(120.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = categoryIcon(cat),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                cat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                state.selectedCategory ?: "بحث",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = onQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("بحث") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { onCategory(null); onQuery("") }) {
                Text("← رجوع للموجز")
            }
            Spacer(Modifier.height(8.dp))
            if (state.filtered.isEmpty()) {
                Text(
                    "مفيش أصناف هنا. اضغط + وأضف.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    state.grouped.forEach { (header, itemsInGroup) ->
                        if (state.selectedCategory == null) {
                            item(key = "header_$header") {
                                Text(
                                    header,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        items(itemsInGroup, key = { it.id }) { item ->
                            ItemCard(
                                item = item,
                                onPlus = { onPlus(item) },
                                onMinus = { onMinus(item) },
                                onEdit = { onEdit(item) },
                                onDelete = { onDelete(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun categoryIcon(category: String): ImageVector {
    return when (category) {
        "IC الصوت" -> Icons.Default.Memory
        "IC TV" -> Icons.Default.Tv
        "المكثفات" -> Icons.Default.Settings
        "الدوائر الكاملة" -> Icons.Default.ElectricalServices
        "IC فرتكال" -> Icons.Default.Memory
        "قطع سماعات" -> Icons.Default.Headphones
        "ريموتات" -> Icons.Default.Settings
        "رسيفرات" -> Icons.Default.Wifi
        "تلفزيونات" -> Icons.Default.Tv
        "عدسات دش" -> Icons.Default.Wifi
        "عدسات رقمية" -> Icons.Default.PhoneAndroid
        "كابلات" -> Icons.Default.ElectricalServices
        "أدابتر 12V" -> Icons.Default.Power
        "لفات سلاك دش" -> Icons.Default.ElectricalServices
        "أطباق دش" -> Icons.Default.Wifi
        "فلانشات طبق" -> Icons.Default.Inventory2
        "إكسسوار دش" -> Icons.Default.Category
        "بطاريات قلم 1.5V" -> Icons.Default.BatteryStd
        else -> Icons.Default.Inventory2
    }
}

@Composable
private fun ItemCard(
    item: Item,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val low = item.quantity <= item.minQuantity
    Card(
        onClick = onEdit,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (low) Color(0xFFFFE8E4) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (item.brand.isNotBlank()) {
                    Text(item.brand, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    "المتبقي: ${item.quantity}",
                    fontSize = 14.sp,
                    color = if (item.quantity <= 0) Color(0xFFC44536)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
                if (low) {
                    Text(
                        if (item.quantity <= 0) "نفد — مطلوب طلب" else "قرب يخلص",
                        fontSize = 12.sp,
                        color = Color(0xFFC44536),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (item.notes.isNotBlank()) {
                    Text(
                        item.notes,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "حذف")
            }
            IconButton(
                onClick = onMinus,
                enabled = item.quantity > 0,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "نقص")
            }
            Spacer(Modifier.width(4.dp))
            IconButton(
                onClick = onPlus,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "زيادة")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemEditorDialog(
    title: String,
    initial: Item,
    allowedCategories: List<String>,
    askMainSection: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (Item) -> Unit
) {
    val initialMain = if (initial.category in Categories.sales) "البيع" else "قطع الغيار"
    var mainSection by remember { mutableStateOf(initialMain) }
    var name by remember { mutableStateOf(initial.name) }
    var category by remember {
        mutableStateOf(
            if (initial.category in allowedCategories) initial.category
            else allowedCategories.firstOrNull().orEmpty()
        )
    }
    var subCategory by remember { mutableStateOf(initial.subCategory) }
    var brand by remember { mutableStateOf(initial.brand) }
    var qty by remember {
        mutableStateOf(if (initial.id == 0L && initial.quantity == 0) "" else initial.quantity.toString())
    }
    var minQty by remember { mutableStateOf(initial.minQuantity.toString()) }
    var notes by remember { mutableStateOf(initial.notes) }
    var error by remember { mutableStateOf<String?>(null) }
    var mainExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }
    var subExpanded by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }

    val sectionCats = if (askMainSection) {
        if (mainSection == "البيع") Categories.sales else Categories.spareParts
    } else allowedCategories

    val isRemote = category == "ريموتات"
    val isTv = category == "تلفزيونات"
    val isDishLens = category == "عدسات دش"
    val isDigitalLens = category == "عدسات رقمية"

    val brandOptions: List<String> = when {
        isRemote && subCategory == "HD" -> Categories.remoteHdBrands
        isRemote && subCategory == "SD" -> Categories.remoteSdTypes
        isRemote && subCategory == "تلفزيون" -> Categories.remoteTvBrands
        isTv -> Categories.brandsTv
        isDishLens -> Categories.dishLensTypes
        isDigitalLens -> Categories.digitalLensBrands
        else -> emptyList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    name, { name = it },
                    label = { Text("الاسم") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (askMainSection) {
                    ExposedDropdownMenuBox(
                        expanded = mainExpanded,
                        onExpandedChange = { mainExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = mainSection,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("القسم الرئيسي") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(mainExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = mainExpanded,
                            onDismissRequest = { mainExpanded = false }
                        ) {
                            listOf("قطع الغيار", "البيع").forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m) },
                                    onClick = {
                                        mainSection = m
                                        mainExpanded = false
                                        val list = if (m == "البيع") Categories.sales else Categories.spareParts
                                        if (category !in list) {
                                            category = list.first()
                                            subCategory = ""
                                            brand = ""
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("القسم الداخلي") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        sectionCats.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    catExpanded = false
                                         if (cat != "ريموتات") subCategory = ""
                                         // Brand/type belongs to the selected category.
                                         // Clear it so an old TV or lens value is not
                                         // accidentally saved with the new category.
                                         brand = ""
                                }
                            )
                        }
                    }
                }
                if (isRemote) {
                    ExposedDropdownMenuBox(
                        expanded = subExpanded,
                        onExpandedChange = { subExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = subCategory.ifBlank { "اختار النوع" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("نوع الريموت") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(subExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = subExpanded,
                            onDismissRequest = { subExpanded = false }
                        ) {
                            Categories.remoteGroups.forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g) },
                                    onClick = {
                                        subCategory = g
                                        brand = ""
                                        subExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                if (brandOptions.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = brandExpanded,
                        onExpandedChange = { brandExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = brand.ifBlank { "اختار" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("النوع / الماركة") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(brandExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = brandExpanded,
                            onDismissRequest = { brandExpanded = false }
                        ) {
                            brandOptions.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b) },
                                    onClick = {
                                        brand = b
                                        brandExpanded = false
                                        if (name.isBlank()) name = b
                                    }
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    qty, { qty = it },
                    label = { Text("المتبقي في المحل") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    minQty, { minQty = it },
                    label = { Text("حد الطلب") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    notes, { notes = it },
                    label = { Text("ملاحظة") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val q = qty.trim().ifEmpty { "0" }.toIntOrNull()
                val m = minQty.trim().ifEmpty { "0" }.toIntOrNull()
                when {
                    name.isBlank() -> error = "اكتب الاسم"
                    category.isBlank() -> error = "اختار القسم"
                    isRemote && subCategory.isBlank() -> error = "اختار نوع الريموت"
                    q == null || q < 0 -> error = "كمية غلط"
                    m == null || m < 0 -> error = "حد الطلب غلط"
                    else -> onSave(
                        initial.copy(
                            name = name.trim(),
                            category = category,
                            subCategory = if (isRemote) subCategory else "",
                            brand = brand.trim(),
                            quantity = q,
                            minQuantity = m,
                            notes = notes.trim()
                        )
                    )
                }
            }) { Text("حفظ") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
