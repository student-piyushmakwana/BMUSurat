package com.piyush.bmusurat.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class InfoItem(
    val date: String,
    val description: String,
    val link: String
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandableInfoSection(
    title: String,
    items: List<InfoItem>,
    onItemClick: (InfoItem) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val itemsToShow = if (isExpanded) items else items.take(3)

    Column(modifier = Modifier.padding(horizontal = 0.dp)) {
        SectionTitle(title = title, modifier = Modifier.padding(horizontal = 16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsToShow.forEachIndexed { index, item ->
                val topRadius = if (index == 0) 24.dp else 4.dp
                val bottomRadius = if (index == itemsToShow.lastIndex) 24.dp else 4.dp

                val itemShape = RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomStart = bottomRadius,
                    bottomEnd = bottomRadius
                )

                InfoCard(
                    title = item.description,
                    date = item.date,
                    onClick = { onItemClick(item) },
                    shape = itemShape
                )
            }
        }

        if (items.size > 3) {
            TextButton(
                onClick = { isExpanded = !isExpanded },
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (isExpanded) {
                    Text(text = "View Less")
                } else {
                    Text(text = "View All (${items.size})")
                }
            }
        }
    }
}