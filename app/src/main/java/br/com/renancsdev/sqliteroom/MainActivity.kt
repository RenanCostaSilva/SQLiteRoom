package br.com.renancsdev.sqliteroom

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import br.com.renancsdev.sqliteroom.data.db.DataBase
import br.com.renancsdev.sqliteroom.databinding.ActivityMainBinding
import br.com.renancsdev.sqliteroom.extension.naoNuloOUVazio
import br.com.renancsdev.sqliteroom.model.Login
import esconder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mostrar

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var loginEncontrado   = ""
    private var idEncontrado      = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = setDataBinding()
        eventosExibicaoComponentesEsconder()
        //mostarCard(2)
        initBotoes()

    }

    // Layout
    fun setDataBinding(): ActivityMainBinding{
        return DataBindingUtil.setContentView(this , R.layout.activity_main)
    }

    /* Eventos de Banco de Dados */
    // Salvar
    private fun salvar(){

        CoroutineScope(Dispatchers.IO).launch {
            configurarSalvarNoBanco()
        }

    }

    // listar
    private fun listar(){

            CoroutineScope(Dispatchers.IO).launch {
               listarLoginsEncontrados()
            }

        }

    // deletar
    private fun deletar(){

        CoroutineScope(Dispatchers.IO).launch{
            deletarTodosLogins()
        }

    }

    // update
    private fun update(){
        binding.btnUpdateProcurar.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                buscarLoginParaAlterar()
            }

        }
        binding.btnUpdateAlterar.setOnClickListener {
            alterarParaLoginEncontrado()
        }
    }

    // Repositorio
    fun pegarLoginPorNome(login: String): Login {
       return iniciaBancoDados().loginDao().encontrarLoginPorNome(login)
    }

    fun atualizarLoginPorNome(novoLoginNome: String , idEncontrado: Long){
        iniciaBancoDados().loginDao().updateLoginNome(novoLoginNome , idEncontrado)
    }

    // -------------------------- //

    //checagem
    fun verificaSeOSDadosLoginForamPreenchidos(login : EditText , senha : EditText) : Boolean{
        return login.naoNuloOUVazio() && senha.naoNuloOUVazio()
    }

    //Eventos de exibição
    private fun eventosExibicaoComponentesEsconder(){
        binding.cardResult.visibility = View.GONE
        binding.cardUpdateDadosEncontrados.visibility = View.GONE
        binding.cardUpdateDadosEncontrados.esconder()
        binding.cardUpdateDadosAlterar.esconder()
    }
    private fun eventosExibicaoComponentesMostar(){
        binding.cardResult.visibility = View.VISIBLE
    }
    private fun eventosExibicaoUpdateMostarCard(){
        binding.cardUpdateDadosEncontrados.mostrar()
        binding.cardUpdateDadosAlterar.mostrar()
    }
    private fun eventosExibicaoUpdateEsconderCard(){
        binding.cardUpdateDadosEncontrados.esconder()
        binding.cardUpdateDadosAlterar.esconder()
    }

    //mensagem
    fun toastMensagem(mensagem: String){
        Toast.makeText(this@MainActivity , mensagem, Toast.LENGTH_SHORT).show()
    }

    // mensagem com coroutine
    fun coroutineToast(mensagem: String){
        CoroutineScope(Dispatchers.Main).launch{
            Toast.makeText(this@MainActivity , mensagem, Toast.LENGTH_SHORT).show()
        }
    }

    //Eventos para limpar campos
    fun limparCampos(){
        binding.editTextProcurarLogin.text = null
        binding.editPainelInserirDadosLogin.text = null
        binding.editPainelInserirDadosSenha.text = null
        binding.editTextProcurarLogin.text = null
        binding.editTextNovologinUpdate.text = null
    }

    fun limparCampoSenha(){
        binding.editPainelInserirDadosSenha.text = null
    }
    fun limparComTextoCoroutine(mensagem: String){
        limparCampos()
        CoroutineScope(Dispatchers.Main).launch{
            Toast.makeText(this@MainActivity , mensagem, Toast.LENGTH_SHORT).show()
        }
    }

    // eventos de banco
    fun iniciaBancoDados(): DataBase {
        return DataBase.iniciarDataBase(this@MainActivity)
    }
    fun verificaSeLoginExist(): Boolean{
        return iniciaBancoDados().loginDao().checarSeExisteLoginPorNome(binding.editPainelInserirDadosLogin.text.toString()) == 1
    }

    // salvar no banco
    private fun inserirNoBanco(){
        iniciaBancoDados().loginDao().inserirLogin(Login(binding.editPainelInserirDadosLogin.text.toString() , binding.editPainelInserirDadosSenha.text.toString()))
    }
    private fun configurarSalvarNoBanco(){
        try{
            if(verificaSeOSDadosLoginForamPreenchidos(binding.editPainelInserirDadosLogin , binding.editPainelInserirDadosSenha)){
                if(!verificaSeLoginExist()){
                  inserirNoBanco()
                  limparComTextoCoroutine("Login gravado com sucesso !")
                  limparCampos()
                }else{
                  coroutineToast("Erro: ${binding.editPainelInserirDadosLogin.text} já existe")
                }

            }else{
                coroutineToast("Campos de Login e senha em branco")
                limparCampoSenha()
            }

        }catch(e: SQLiteException){
            coroutineToast("Error: ${e.cause}")
        }
    }

    // listar do banco
    fun buscarLogins(): List<Login>{
      return  iniciaBancoDados().loginDao().selecionarTodosUsuarios()
    }

    // deletar no banco
    fun deletarTodosLogins(){
        iniciaBancoDados().loginDao().deletarTodosRegistos()
        iniciaBancoDados().loginDao().deletarSequenciaID()
        coroutineToast("Dados excluidos com sucesso !")
        limparCampos()
    }

    // update no banco
    fun verificarSeExisteRegistroNoBanco(): Boolean {
        val find2 = iniciaBancoDados().loginDao().checarSeExisteLoginPorNome(binding.editTextProcurarLogin.text.toString())
        return find2 >= 1
    }
    fun exibirDadosUpdateEncontrados(login: Login){
        CoroutineScope(Dispatchers.Main).launch {
            eventosExibicaoUpdateMostarCard()
            binding.tvIdUpdateInfo.text    = "${login.id}"
            binding.tvLoginUpdateInfo.text = login.login
            binding.tvSenhaUpdateInfo.text = login.senha
            loginEncontrado = login.login
            idEncontrado = login.id
        }
    }
    fun buscarLoginParaAlterar(){

        //var findLogin = iniciaBancoDados().loginDao().encontrarLoginPorNome(binding.editTextProcurarLogin.text.toString())
        val findLogin = pegarLoginPorNome(binding.editTextProcurarLogin.text.toString())

        if(verificarSeExisteRegistroNoBanco()){
            exibirDadosUpdateEncontrados(findLogin)
        }else{
            coroutineToast("Nenhum registro encontrado")
            CoroutineScope(Dispatchers.Main).launch {
                eventosExibicaoUpdateEsconderCard()
                limparCampos()
            }

        }
    }
    fun alterarParaLoginEncontrado(){
        if(!binding.editTextNovologinUpdate.text.isNullOrEmpty()){

            CoroutineScope(Dispatchers.IO).launch {
                atualizarLoginPorNome(binding.editTextNovologinUpdate.text.toString() , idEncontrado.toLong())
                coroutineToast("Login ( ${binding.editTextNovologinUpdate.text} ) , alterado com sucesso !")
                limparCampos()
            }

        }else{
            toastMensagem("Digite um novo login para usuario")
        }
    }

    // ----------------------- //

    fun listarLoginsEncontrados(){

        val logins = buscarLogins()
        if(logins.isNotEmpty()){

            val campos = popularDadosComoTabela(logins)
            mostarDadosComoTabela(campos)

        }else{
            CoroutineScope(Dispatchers.Main).launch {
              eventosExibicaoComponentesEsconder()
                toastMensagem("Nenhum Login encontrado !")
            }
        }
    }
    fun popularDadosComoTabela( logins: List<Login>) : Array<String> {

        var word = ""
        val campos = arrayOf("","","")

        logins.forEach {
            word = word + "ID: ${it.id} \nlogin: ${it.login} \nsenha: ${it.senha}"+ "\n\n"
            campos[0] += "${it.id}\n"
            campos[1] += "${it.login}\n"
            campos[2] += "${it.senha}\n"
        }

        return campos
    }

    fun mostarDadosComoTabela( campos: Array<String> ) {

        CoroutineScope(Dispatchers.Main).launch {
            eventosExibicaoComponentesMostar()
            binding.tvIdResultado.text    = campos[0]
            binding.tvLoginResultado.text = campos[1]
            binding.tvSenhaResultado.text = campos[2]
            binding.btnListar.text = "Atualizar"
        }

    }

    // eventos botoes
    fun initBotoes(){
        binding.btnInsercaoDados.setOnClickListener {
            salvar()
        }

        binding.btnListar.setOnClickListener {
            listar()
        }
        binding.btnDeletar.setOnClickListener {
            deletar()
        }

        binding.btnUpdateProcurar.setOnClickListener {
            update()
        }
    }

    //teste
    fun mostarCard(card: Int){
        when (card) {
            1 -> {
                binding.cardDbSalvar.mostrar()
                binding.cardDbListar.esconder()
                binding.cardDbDeletar.esconder()
                binding.cardDbAlterar.esconder()
            }
            2 -> {
                binding.cardDbSalvar.esconder()
                binding.cardDbListar.mostrar()
                binding.cardDbDeletar.esconder()
                binding.cardDbAlterar.esconder()
            }
            3 -> {
                binding.cardDbSalvar.esconder()
                binding.cardDbListar.esconder()
                binding.cardDbDeletar.mostrar()
                binding.cardDbAlterar.esconder()
            }
            4 -> {
                binding.cardDbSalvar.esconder()
                binding.cardDbListar.esconder()
                binding.cardDbDeletar.esconder()
                binding.cardDbAlterar.mostrar()
            }
        }
    }
}