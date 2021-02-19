package tests.refact;

import core.BaseTest;
import io.restassured.RestAssured;

import org.junit.Test;
import tests.Movimentacao;
import utils.BarrigaUtils;
import utils.DataUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;

public class MovimentacaoTest extends BaseTest {

    @Test
    public void deveInserirMovimentacaoComSucesso(){
        Movimentacao mov = getMovimentacaoValida();
        given()
                .body(mov)
                .when()
                .post("/transacoes")
                .then()
                .log().all()
                .statusCode(201)
        ;
    }

    @Test
    public void deveValidarCamposObrigatoriosDaMovimentacao(){
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
    public void naoDeveInserirMovimentacaoComDataFutura(){
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
    public void naoDeveRemoverContaComMovimentacao() {
        given()
                //.header("Authorization", "JWT " + token)
                .pathParam("id", BarrigaUtils.getIdContaPeloNome("Conta com movimentacao"))
                .when()
                .delete("/contas/{id}")
                .then()
                .statusCode(500)//ta 500, mas porque o dev nao tratou o erro
                .body("constraint", is("transacoes_conta_id_foreign"))//vaidação feia, mas o erro é devido aplica'ão nao ter tratado
        ;
    }

    @Test
    public void deveRemoverMovimentacao() {
        Integer MOV_ID = BarrigaUtils.getIdMovimentacaoPeloNome("Movimentacao para exclusao");
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

    private Movimentacao getMovimentacaoValida() {
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
