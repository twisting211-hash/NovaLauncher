package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.HapticFeedbackUtil

@Composable
fun PinAuthDialog(
    currentPin: String,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
    onSetNewPin: ((String) -> Unit)? = null,
    hapticEnabled: Boolean = true
) {
    val context = LocalContext.current
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isSettingNewPin by remember { mutableStateOf(currentPin.isEmpty() && onSetNewPin != null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("pin_auth_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = if (isSettingNewPin) "Yangi PIN Kod o'rnating" else "Maxfiy Ilovalar Qulfi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isSettingNewPin) "Maxfiy ilovalaringizni himoya qilish uchun 4 xonali PIN kiriting" else "Davom etish uchun 4 xonali PIN kodni kiriting",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 4 PIN Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    repeat(4) { idx ->
                        val filled = idx < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isError -> MaterialTheme.colorScheme.error
                                        filled -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                        )
                    }
                }

                if (isError) {
                    Text(
                        text = "Noto'g'ri PIN kod! Qayta urinib ko'ring.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Numeric Keypad (1..9, Backspace, 0, OK)
                val keypad = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("del", "0", "ok")
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    keypad.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { key ->
                                when (key) {
                                    "del" -> {
                                        OutlinedButton(
                                            onClick = {
                                                if (enteredPin.isNotEmpty()) {
                                                    enteredPin = enteredPin.dropLast(1)
                                                    isError = false
                                                    HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                                }
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.weight(1f).height(48.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Backspace, contentDescription = "O'chirish", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    "ok" -> {
                                        Button(
                                            onClick = {
                                                HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                                 if (isSettingNewPin) {
                                                    if (enteredPin.length == 4) {
                                                        onSetNewPin?.invoke(enteredPin)
                                                        onSuccess()
                                                    } else {
                                                        isError = true
                                                    }
                                                } else {
                                                    if (enteredPin == currentPin) {
                                                        onSuccess()
                                                    } else {
                                                        isError = true
                                                        enteredPin = ""
                                                    }
                                                }
                                            },
                                            enabled = enteredPin.length == 4,
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.weight(1f).height(48.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = "Tasdiqlash", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    else -> {
                                        OutlinedButton(
                                            onClick = {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += key
                                                    isError = false
                                                    HapticFeedbackUtil.performHaptic(context, hapticEnabled)
                                                    if (enteredPin.length == 4) {
                                                        if (isSettingNewPin) {
                                                            onSetNewPin?.invoke(enteredPin)
                                                            onSuccess()
                                                        } else if (enteredPin == currentPin) {
                                                            onSuccess()
                                                        } else {
                                                            isError = true
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.weight(1f).height(48.dp)
                                        ) {
                                            Text(key, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish")
            }
        }
    )
}
