# BarrigaRest
Projeto de testes em Rest Assured bem didádico


Cenários da aplicação seu Barriga

Nao deve acessar API sem token - rota de contas
Deve incluir uma conta com sucesso - POST/signin  - devemos logar e pegar o token e depois incluir a conta - POST nas contas
Deve alterar uma conta com suceso - PUT/contas:id
Não deve incluir uma conta com nove repetido - POST/contas
Deve inserir movimentacoes com sucesso - POST/transacoes - são vários campos, ver no vídeo
Deve validar campos obrigatórios na movimentacao
Não deve cadastrar movimentacão futura - POST/transacoes
Não deve remover conta com movimentacao - DELETE/contas/:id
Deve calcular saldo das contas - GET/saldo
Deve remover movimentacao - DELETE/transacao/:id


