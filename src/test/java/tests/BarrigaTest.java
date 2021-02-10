package tests;

import core.BaseTest;
import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import utils.DataUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)//executa os métodos de teste em ordem alfabética, modo feio
public class BarrigaTest extends BaseTest {

    //private  String TOKEN;
    private static String CONTA_NAME = "Conta " + System.nanoTime(); // mantive static para o Junit não ser zerar a cada teste
    private static Integer CONTA_ID;
    private static Integer MOV_ID;


    //@Before //o Before é feito apenas uma vez, diferente do Beforeclass que é feito antes de todos testes
    @BeforeClass  //tive que alterar para BeforeClass para fazer executar apenas uma vez, o Before estava acumulado tokens, pois executa uma vez antes de cada teste
    public static void login(){  //precisa ser static devido ao BeforeClass
        Map<String, String> login = new HashMap<>();
        login.put("email", "ricardoveiga.ti@gmail.com");
        login.put("senha", "123456");

        Response response
             //String TOKEN
                     = given()
                .body(login)
                .when()
                .post("/signin");
        response
                .then()
                    .statusCode(200)
                    .body("token", is(notNullValue()))
                    //.extract().path("token"); //tem ficar após as validações
        ;
        String TOKEN = response.jsonPath().getString("token");
        System.out.print("TOKEN => " + TOKEN); //apenas didático

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN); //faz com que o token vá em todas requisicoes
    }



    @Test
    public void t02_deveCadastrarContaComSucesso(){
        Response response = given()
              //.header("Authorization", "JWT " + token)
              .body("{\"nome\": \" "+CONTA_NAME+" \"}")
        .when()
              .post("/contas");
        response.then()
              .log().all()
              .statusCode(201)
        ;

        CONTA_ID = response.jsonPath().getInt("id");
    }

    @Test
    public void t03_deveAlterarContaComSucesso(){

        given()
             //.header("Authorization", "JWT " + token)
             .body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
             .pathParam("id", CONTA_ID)
        .when()
             .put("/contas/{id}")
        .then()
             .statusCode(200)
             .body("nome", containsString(CONTA_NAME+" alterada"))
        ;
    }

    @Test
    public void t04_naoDeveInserirContaComMesmoNome(){

        given()
             //.header("Authorization", "JWT " + token)
             .body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
        .when()
             .post("/contas")
        .then()
             .statusCode(400)
             .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void t05_deveInserirMovimentacaoComSucesso(){
        Movimentacao mov = getMovimentacaoValida();
        //mov.setConta_id(CONTA_ID);
        //mov.setUsuario_id(usuario_id);
        //mov.setDescricao("Descricao da movimentacao");
        //mov.setEnvolvido("Envolvido na mov");
        //mov.setTipo("REC");
        //mov.setData_transacao("01/01/2021");
        //mov.setData_pagamento("01/02/2021");
        //mov.setValor(100f);
        //mov.setStatus(true);

        MOV_ID = given()
             //.header("Authorization", "JWT " + token)
             .body(mov)
        .when()
             .post("/transacoes")
        .then()
             .log().all()
             .statusCode(201)
             .extract().path("id") // outra forma de pegar um campo com extração
                //.body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void t06_deveValidarCamposObrigatoriosDaMovimentacao(){
            given()
                //.header("Authorization", "JWT " + token)
                .body("{}")
            .when()
                .post("/transacoes")
            .then()
                .log().all()
                .statusCode(400)
                .body("$", hasSize(8)) // $ raiz do array
                .body("msg", hasItems(
                "Data da Movimentação é obrigatório",
                "Data do pagamento é obrigatório",
                "Descrição é obrigatório",
                "Interessado é obrigatório",
                "Valor é obrigatório",
                "Valor deve ser um número",
                "Conta é obrigatório",
                "Situação é obrigatório"
                ))
            ;
    }

    @Test
    public void t07_naoDeveInserirMovimentacaoComDataFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2)); //setando somente a data diferente do objeto mov

            given()
                //.header("Authorization", "JWT " + token)
                .body(mov)
            .when()
                .post("/transacoes")
            .then()
                .log().all()
                .statusCode(400)
                .body("$", hasSize(1)) //amarrando a validação da mensagem para ter apenas uma
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void t08_naoDeveRemoverContaComMovimentacao() {
            given()
                //.header("Authorization", "JWT " + token)
                .pathParam("id", CONTA_ID)
            .when()
                .delete("/contas/{id}")
            .then()
                .statusCode(500)//ta 500, mas porque o dev nao tratou o erro
                .body("constraint", is("transacoes_conta_id_foreign"))//vaidação feia, mas o erro é devido aplica'ão nao ter tratado
        ;
    }

    @Test
    public void t09_deveCalcularSaldoContas() {
            given()
                 //.header("Authorization", "JWT " + token)
            .when()
                .get("/saldo")
            .then()
                    .log().all()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00")) //buscando o id dentro do array
        ;
    }

    @Test
    public void t10_deveRemoverMovimentacao() {
        given()
             //.header("Authorization", "JWT " + token)
             .pathParam("id", MOV_ID)
        .when()
             .delete("/transacoes/{id}")
        .then()
             .log().all()
             .statusCode(204)
        ;
    }

    @Test
    public void t11_naoDeveAcessarApiSemToken(){  // tive que mudar para 11, pois o teste remove o TOKEN

        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization"); //removendo header para não pegar o header statico do metodo login

        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }


    private Movimentacao getMovimentacaoValida() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(CONTA_ID);
        //mov.setUsuario_id(usuario_id);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1)); //ontem
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5)); // 5 dias a frente
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }

}
