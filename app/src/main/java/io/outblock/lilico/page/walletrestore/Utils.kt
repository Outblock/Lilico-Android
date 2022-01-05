package io.outblock.lilico.page.walletrestore

import io.outblock.lilico.R


const val WALLET_RESTORE_STEP_GUIDE = 0
const val WALLET_RESTORE_STEP_DRIVE_USERNAME = 1
const val WALLET_RESTORE_STEP_DRIVE_PASSWORD = 2
const val WALLET_RESTORE_STEP_MNEMONIC = 3


private val WALLET_STEP_ROOT_ID = mapOf(
    WALLET_RESTORE_STEP_GUIDE to R.id.fragment_wallet_restore_guide,
    WALLET_RESTORE_STEP_DRIVE_USERNAME to R.id.fragment_wallet_restore_drive_username,
    WALLET_RESTORE_STEP_DRIVE_PASSWORD to R.id.fragment_wallet_restore_drive_password,
    WALLET_RESTORE_STEP_MNEMONIC to R.id.fragment_wallet_restore_mnemonic,
)

fun getRootIdByStep(step: Int) = WALLET_STEP_ROOT_ID[step]!!