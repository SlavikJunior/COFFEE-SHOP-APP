package com.coffeeshop.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.designsystem.common.DarkBrown
import com.coffeeshop.designsystem.R
import com.coffeeshop.designsystem.common.CoffeeShopTopBarTextStyle
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.common.White

private val TopBarHeight = 56.dp

/**
 * Шапка главного экрана каталога: текстовый логотип «1804» слева,
 * иконка профиля справа.
 * Используется только на экране каталога.
 */
@Composable
fun HomeTopBar(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CoffeeTopBarBase(modifier = modifier) {
        Text(
            text = stringResource(R.string.logo_text),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown,
            modifier = Modifier.weight(1f),
        )
        CircleIconButton(
            onClick = onProfileClick,
            contentDescription = stringResource(R.string.cd_profile),
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = DarkBrown,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() = HomeTopBar(onProfileClick = {})

@Composable
fun SimpleTopBar(
    title: String,
    modifier: Modifier = Modifier,
) {

    CoffeeTopBarBase(
        modifier = modifier
    ) {
        Text(
            text = title.uppercase(),
            style = CoffeeShopTopBarTextStyle,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
fun SimpleTopBarPreview() = SimpleTopBar(title = "ВХОД")

/**
 * Шапка экранов профиля и авторизации: иконка-аватар слева,
 * заголовок по центру («Войти», «Профиль»), кнопка закрытия X справа.
 * Используется на экране профиля и экранах логина/OTP.
 */
@Composable
fun ProfileTopBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CoffeeTopBarBase(modifier = modifier) {
        CircleIconButton(
            onClick = {},
            contentDescription = stringResource(R.string.cd_profile),
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = title.uppercase(),
            style = CoffeeShopTopBarTextStyle,
            modifier = Modifier.weight(1f),
        )
        CircleIconButton(
            onClick = onCloseClick,
            contentDescription = stringResource(R.string.cd_close),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileTopBarPreview() = ProfileTopBar(
    title = "Профиль",
    onCloseClick = {},
)

/**
 * Универсальная шапка вторичных экранов: опциональная иконка слева,
 * заголовок по центру, кнопка X справа.
 * Используется на экране корзины, истории заказов, оплаты, избранного.
 */
@Composable
fun ScreenTopBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    CoffeeTopBarBase(modifier = modifier) {
        Box(modifier = Modifier.size(40.dp)) {
            leadingIcon?.invoke()
        }
        Text(
            text = title.uppercase(),
            style = CoffeeShopTopBarTextStyle,
            modifier = Modifier.weight(1f),
        )
        CircleIconButton(
            onClick = onCloseClick,
            contentDescription = stringResource(R.string.cd_close),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenTopBarPreview() = ScreenTopBar(
    title = "Корзина",
    onCloseClick = {},
)

@Composable
private fun CoffeeTopBarBase(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TopBarHeight)
            .background(White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
internal fun CircleIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Beige)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}