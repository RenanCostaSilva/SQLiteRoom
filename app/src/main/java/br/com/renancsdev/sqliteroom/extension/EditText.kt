package br.com.renancsdev.sqliteroom.extension

import android.widget.EditText

fun EditText.naoNuloOUVazio(): Boolean{
    return !this.text.isNullOrEmpty()
}

fun EditText.naoNuloOUPrenchido(): Boolean{
    return this.text.isNullOrBlank()
}

fun EditText.seVazio(): Boolean{
    return this.text.isEmpty()
}

fun EditText.seLoginValido(login: EditText , senha: EditText ): Boolean{
    return !login.text.isNullOrEmpty() && !senha.text.isNullOrEmpty()
}