package tests.refact;

import core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldoContas() {
        Integer CONTA_ID = getIdContaIdPeloNome("Conta para saldo");
        given()
                //.header("Authorization", "JWT " + token)
                .when()
                .get("/saldo")
                .then()
                .log().all()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00")) //buscando o id dentro do array
        ;
    }

    public Integer getIdContaIdPeloNome(String nome){
        return RestAssured.get("/contas?nome=" +nome).then().extract().path("id[0]"); //usando restassured para fazer uma query em contas e pegar o primeiro id da lista dentro do array[0]
    }
}