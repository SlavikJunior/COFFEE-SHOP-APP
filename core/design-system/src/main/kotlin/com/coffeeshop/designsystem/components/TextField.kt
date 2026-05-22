package com.coffeeshop.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.common.ErrorRed
import com.coffeeshop.designsystem.common.Secondary

/**
 * Строка-поле в стиле профиля: метка слева (all-caps), значение справа.
 * Используется на экране профиля для полей «Имя», «Номер телефона», «Почта»,
 * а также в форме регистрации.
 */
@Composable
fun CoffeeProfileField(
    firstLabel: String,
    secondLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = firstLabel.uppercase(),
                fontSize = 11.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = Secondary,
            )
            Text(
                text = secondLabel.uppercase(),
                textAlign = TextAlign.End,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = Secondary,
            )
        }
        HorizontalDivider(color = Secondary.copy(alpha = 0.2f))
    }
}

@Preview(showBackground = true)
@Composable
private fun CoffeeProfileFieldPreview() = CoffeeProfileField(
    firstLabel = "Имя",
    secondLabel = "Вячеслав"
)

/**
 * Инпут формы: маленькая метка сверху, поле ввода с нижней линией-разделителем.
 * Используется на экране логина («Номер телефона»), OTP («SMS-код»),
 * и в боттом-шите товара («Комментарий»).
 */
@Composable
fun CoffeeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    prefix: String = "",
    isError: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            color = Secondary,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = DarkBrown,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            decorationBox = { innerTextField ->
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (prefix.isNotEmpty()) {
                            Text(
                                text = prefix,
                                fontSize = 16.sp,
                                color = DarkBrown,
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontSize = 16.sp,
                                    color = Secondary.copy(alpha = 0.6f),
                                )
                            }
                            innerTextField()
                        }
                    }
                    HorizontalDivider(color = if (isError) ErrorRed else Secondary.copy(alpha = 0.4f))
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoffeeInputFieldPreview() = CoffeeInputField(
    label = "Номер телефона",
    value = "",
    onValueChange = {},
    placeholder = "+7 (999) 000-00-00",
    keyboardType = KeyboardType.Phone,
)
