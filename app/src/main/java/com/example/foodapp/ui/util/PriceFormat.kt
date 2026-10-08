package com.example.foodapp.ui.util

/** 1350 → "13,50 €" */
fun formatPrice(cents: Int): String = "%d,%02d €".format(cents / 100, cents % 100)
