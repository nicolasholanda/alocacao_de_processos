# Algotitmos de Alocação de Processos
Trabalho da disciplina de Sistemas Operacionais. IFCE - Campus Fortaleza.<br>
Alunos: Nicolas Holanda e Raphael Otaviano
# Descrição
Este trabalho tem como objetivo implementar de maneira simples e intuitiva os algoritmos de alocação de processos na memória, os quais são:
<ul>
<li> First Fit: Primeiro espaço que couber o processo.</li>
<li> Best Fit: O menor espaço que couber o processo.</li>
<li> Worst Fit: O maior espaço que couber o processo.</li>
</ul>
Na tabela ao lado direito, os processos vão aparecendo na ordem em que vão sendo criados, sincronizadamente com os desenhos dos
procesos.
Nesta tabela, você poderá ver informações sobre o processo.<br>
Cada desenho possui um número que identifica o processo correspondente pelo seu ID (primeira coluna da tabela).<br>
No fim da execução, o programa mostra uma média que corresponde à média de espera dos processos.

# Parâmetros
É essencial o preenchimento correto dos parâmetros do programa. <b>Todos são obrigatórios</b>.<br>
* Método: Define a estratégia de alocação que será utilizada durante a execução.
* Memória: Tamanho da memória em MB.
* SO: Tamanho do sistema operacional em MB.
* TC1 e TC2: Intervalo no qual servirá para gerar números aleatórios que definirão o tempo de criação de um processo em relação ao anterior
* TD1 e TD2: Intervalo no qual será gerado um número aleatório que definirá o tempo de duração do processo.
* M1 e M2: Intervalo no qual será gerado um número aleatório que definirá o tamanho do processo em MB.
