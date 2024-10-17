package com.hanikorm.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
/**
 * класс сохранения данных при изменение ориентации экрана
 */
class CalculatorViewModel : ViewModel() {
    /**
     * @see temporaryValue - переменная с временным значением с display
     * @see firstValueEntered - переменная куда попадает первое введённое число
     * @see signSelectionVariable - переменная с выбором знака действия {+ - / *}
     */
    val temporaryValue: MutableLiveData<String> = MutableLiveData("0")
    val firstValueEntered: MutableLiveData<String> = MutableLiveData("")
    val signSelectionVariable: MutableLiveData<String> = MutableLiveData("")
}