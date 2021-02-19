package utils;

import io.restassured.RestAssured;

public class BarrigaUtils {

    public static Integer getIdContaPeloNome(String nome){  //static para todos poderem usar sem precisas instanciar
        return RestAssured.get("/contas?nome=" +nome).then().extract().path("id[0]"); //usando restassured para fazer uma query em contas e pegar o primeiro id da lista dentro do array[0]
    }

    public static Integer getIdMovimentacaoPeloNome(String desc){  //usado na movimentacao
        return RestAssured.get("/transacoes?descricao=" +desc).then().extract().path("id[0]"); //usando restassured para fazer uma query em contas e pegar o primeiro id da lista dentro do array[0]
    }
}
