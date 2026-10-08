package com.example.foodapp.ui.confirmation

enum class StepStatus { Done, Current, Upcoming }

data class OrderStep(val label: String, val subtitle: String, val status: StepStatus)

data class ConfirmationState(
    val orderNumber: String = "",
    val steps: List<OrderStep> = listOf(
        OrderStep("Commande reçue", "Le restaurant a bien reçu votre commande", StepStatus.Done),
        OrderStep("En préparation", "Vos plats sont en cuisine", StepStatus.Current),
        OrderStep("En route", "Un livreur récupère la commande", StepStatus.Upcoming),
        OrderStep("Livrée", "Bon appétit !", StepStatus.Upcoming)
    )
)

sealed interface ConfirmationIntent {
    data object BackToHomeClicked : ConfirmationIntent
}

sealed interface ConfirmationEffect {
    data object NavigateToHome : ConfirmationEffect
}
