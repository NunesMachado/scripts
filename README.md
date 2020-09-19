# SIMPLE ENV

Esse **script** foi criado para facilitar a configuração inicial das pastas dos fontes do **sim3g/master sales**.

Esqueleto:

- sim3g
- maven
- workspace

A ideia é, com uma pasta de referência(fontes), o *script* irá varrer cada pasta e se não tiver o esqueleto acima será montado.

Para utilizar o executável, basta baixar o **simpleEnv.jar** e como dica, adicionar o *script* para executar a cada 5 segundos em seu ambiente *windows/linux*. Desta forma, toda vez que uma nova pasta for criada dentro dos fontes, o ambiente será configurado.

>java -jar simpleEnv.jar

Dentro do jar, há um arquivo de configuração(properties), onde será possível definir as variáveis abaixo:

CAMINHO_FONTES=/home/user/TesteSript
> Esse parâmetro representa onde o script irá trabalhar para compor o ambiente.

CAMINHO_MAVEN_SETTINGS=./settings.xml
> Esse parâmetro representa o arquivo settings.xml utilizado em todos os ambientes do SVN. Será feito uma copia do mesmo apenas alterando o *localrepository*.

CAMINHO_WORKSPACE_STANDARD=./workspace
> Esse parâmetro representa seu *workspace* na versão mais standard, onde o mesmo será copiado à pasta dos fontes.