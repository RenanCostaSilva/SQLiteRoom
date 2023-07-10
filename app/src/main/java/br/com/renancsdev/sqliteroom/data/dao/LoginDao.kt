package br.com.renancsdev.sqliteroom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.renancsdev.sqliteroom.model.Login

@Dao
interface LoginDao {

    /*Room Database never returns a boolean for whether it exists or not on your database table
    * 0 = data is not exist in your table*/


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserirLogin(login: Login)

    // Select
    @Query("SELECT * FROM Login")
    fun selecionarTodosUsuarios(): List<Login>

    @Query("SELECT * FROM Login WHERE id = :id")
    fun verificaSeRegistroExisteBanco(id: Long): Int

    @Query("SELECT id FROM Login")
    fun encontrarLoginPorID(): List<Long>

    @Query("SELECT COUNT(1) id FROM Login WHERE login =:login")
    fun checarSeExisteLoginPorNome(login: String): Int


    //update
    @Query("SELECT * FROM Login WHERE login = :login")
    fun encontrarLoginPorNome(login: String): Login

    @Query("UPDATE Login SET login = :login WHERE id = :id")
    fun updateLoginNome(login: String , id: Long)

    // Delete
    @Query("DELETE FROM Login")
    fun deletarTodosRegistos()

    @Query("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Login' ")
    fun deletarSequenciaID()

}