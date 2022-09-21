package io.outblock.lilico.page.swap

import io.outblock.lilico.manager.flowjvm.CADENCE_SWAP_EXACT_TOKENS_TO_OTHER_TOKENS
import io.outblock.lilico.manager.flowjvm.CADENCE_SWAP_TOKENS_FROM_EXACT_TOKENS
import io.outblock.lilico.manager.flowjvm.transactionByMainWallet
import io.outblock.lilico.network.model.SwapEstimateResponse
import io.outblock.lilico.wallet.toAddress


suspend fun swapSend(data: SwapEstimateResponse.Data): String? {
    val binding = swapPageBinding() ?: return ""
    val viewModel = binding.viewModel()
    val tokenKeyFlatSplitPath = data.routes.mapNotNull { it?.route }.flatten()
    val amountInSplit = data.routes.mapNotNull { it?.routeAmountIn }
    val amountOutSplit = data.routes.mapNotNull { it?.routeAmountOut }

    val deadline = System.currentTimeMillis() / 1000 + 60 * 10

    val slippageRate = 0.1f

    val estimateOut = data.tokenOutAmount
    val amountOutMin = estimateOut * (1.0f - slippageRate)
    val storageIn = viewModel.fromCoin()!!.storagePath
    val storageOut = viewModel.toCoin()!!.storagePath

    val estimateIn = data.tokenInAmount
    val amountInMax = estimateIn / (1.0f - slippageRate)

    return swapSendInternal(
        swapPaths = tokenKeyFlatSplitPath,
        tokenInMax = amountInMax,
        tokenOutMin = amountOutMin,
        tokenInVaultPath = storageIn.vault.split("/").last(),
        tokenOutSplit = amountOutSplit,
        tokenInSplit = amountInSplit,
        tokenOutVaultPath = storageOut.vault.split("/").last(),
        tokenOutReceiverPath = storageOut.receiver.split("/").last(),
        tokenOutBalancePath = storageOut.balance.split("/").last(),
        deadline = deadline,
    )
}

private suspend fun swapSendInternal(
    swapPaths: List<String>,
    tokenInMax: Float,
    tokenOutMin: Float,
    tokenInVaultPath: String,
    tokenOutSplit: List<Float>,
    tokenInSplit: List<Float>,
    tokenOutVaultPath: String,
    tokenOutReceiverPath: String,
    tokenOutBalancePath: String,
    deadline: Long,
): String? {
    val binding = swapPageBinding() ?: return ""
    val viewModel = binding.viewModel()

    // want use how many token to swap other token
    val isExactFrom = viewModel.exactToken == ExactToken.FROM

    val cadence = if (isExactFrom) CADENCE_SWAP_EXACT_TOKENS_TO_OTHER_TOKENS else CADENCE_SWAP_TOKENS_FROM_EXACT_TOKENS

    val tokenName = swapPaths.last().split(".").last()
    val tokenAddress = swapPaths.last().split(".")[1].toAddress()
    return cadence.replace("Token1Name", tokenName).replace("Token1Addr", tokenAddress)
        .transactionByMainWallet {
            arg { array { swapPaths.map { string(it) } } }

            if (isExactFrom) {
                arg { array(tokenInSplit.map { ufix64(it) }) }
                arg { ufix64(tokenOutMin) }
            } else {
                arg { array(tokenOutSplit.map { ufix64(it) }) }
                arg { ufix64(tokenInMax) }
            }
            arg { ufix64(deadline) }
            arg { path(domain = "storage", identifier = tokenInVaultPath) }
            arg { path(domain = "storage", identifier = tokenOutVaultPath) }
            arg { path(domain = "public", identifier = tokenOutReceiverPath) }
            arg { path(domain = "public", identifier = tokenOutBalancePath) }
        }
}
