package br.com.renancsdev.sqliteroom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class Login(

    @ColumnInfo(name = "Login")
    var login : String,

    @ColumnInfo(name = "Senha")
    var senha : String
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}
