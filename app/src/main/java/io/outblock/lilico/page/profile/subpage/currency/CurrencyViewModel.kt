package io.outblock.lilico.page.profile.subpage.currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.outblock.lilico.manager.price.CurrencyManager
import io.outblock.lilico.page.profile.subpage.currency.model.Currency
import io.outblock.lilico.page.profile.subpage.currency.model.CurrencyItemModel
import io.outblock.lilico.utils.getCurrencyFlag
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.updateCurrencyFlag

class CurrencyViewModel : ViewModel() {

    val dataLiveData = MutableLiveData<List<CurrencyItemModel>>()

    private var flag = ""

    fun load() {
        ioScope {
            flag = flag.ifEmpty { getCurrencyFlag() }
            dataLiveData.postValue(Currency.values().map { CurrencyItemModel(it, isSelected = it.flag == flag) })
        }
    }

    fun updateFlag(flag: String) {
        ioScope {
            this.flag = flag
            updateCurrencyFlag(flag)
            load()
            CurrencyManager.updateCurrency(flag)
        }
    }

}