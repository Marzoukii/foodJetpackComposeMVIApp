package com.example.foodapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodapp.R
import com.example.foodapp.ui.theme.Spacing

/** Champ de formulaire : libellé + champ arrondi + message d'erreur. */
@Composable
fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {},
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.onSurface)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = colors.onSurface)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(colors.surfaceContainer)
                .border(
                    BorderStroke(if (error != null) 1.5.dp else 1.dp, if (error != null) colors.error else colors.outlineVariant),
                    MaterialTheme.shapes.medium
                )
                .padding(start = Spacing.l, end = if (isPassword) Spacing.xs else Spacing.l)
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(placeholder, style = textStyle, color = colors.onSurfaceVariant)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = true,
                    textStyle = textStyle,
                    cursorBrush = SolidColor(colors.primary),
                    visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        capitalization = capitalization,
                        autoCorrectEnabled = false,
                        imeAction = imeAction
                    ),
                    // Seul « Terminé » est personnalisé : « Suivant » garde le passage au champ suivant.
                    keyboardActions = KeyboardActions(onDone = { onImeAction() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (isPassword) {
                // Hors du parcours clavier : « Suivant » va directement au champ suivant.
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) BoucheeIcons.EyeOff else BoucheeIcons.Eye,
                        contentDescription = stringResource(if (passwordVisible) R.string.component_hide_password else R.string.component_show_password),
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        if (error != null) {
            Text(error, style = MaterialTheme.typography.bodySmall, color = colors.error)
        }
    }
}

/** Séparateur « ou » entre e-mail et Google. */
@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        Text(stringResource(R.string.common_or), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** Bouton « Continuer avec Google » (contour, pastille G). */
@Composable
fun GoogleButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF4285F4)),
            contentAlignment = Alignment.Center
        ) {
            Text("G", color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }
        Text(
            stringResource(R.string.component_continue_with_google),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = Spacing.m)
        )
    }
}
