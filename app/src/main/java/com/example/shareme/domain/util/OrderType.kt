package com.example.shareme.domain.util

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}