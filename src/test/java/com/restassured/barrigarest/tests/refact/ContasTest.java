package com.restassured.barrigarest.tests.refact;

import com.restassured.barrigarest.core.BaseTest;
import com.restassured.barrigarest.utils.BarrigaUtils;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ContasTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso() {
        System.out.println("Incluir");
        given()
                .body("{ \"nome\": \"Conta inserida\" }")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso(){
        System.out.println("Alterar");
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar"); //usando método da query para selecionar a Conta para alterar

        given()
                .body("{ \"nome\": \"Conta alterada\" }")
                .pathParam("id", CONTA_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    @Test
    public void naoDeveInserirContaComMesmoNome(){

        given()
                //.header("Authorization", "JWT " + token.json)
                .body("{ \"nome\": \"Conta mesmo nome\" }")
                .when()
                .post("/contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

}
