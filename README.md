# SIMPLE ENV

Esse **script** foi criado para facilitar a configuração inicial das pastas dos fontes do **sim3g/master sales**.

Esqueleto:

- sim3g
- maven
- workspace

A ideia é, com uma pasta de referência(fontes), o *script* irá varrer cada pasta e se não tiver o esqueleto acima será montado.

Para utilizar o executável, basta baixar o **simpleEnv.jar** e tambem o arquivo ***config.properties***(ambos devem estar na mesma pasta), uma dica seria adicionar o *script* para executar a cada 5 segundos em seu ambiente *windows/linux*. Desta forma, toda vez que uma nova pasta for criada dentro dos fontes, o ambiente será configurado.

>java -jar simpleEnv.jar A

Passando como parametro a letra A, para identificar que é a configuração de ambiente que será executada.

No arquivo de configuração(properties) será possível definir as variáveis abaixo:

CAMINHO_FONTES=/home/user/TesteSript

> Esse parâmetro representa onde o script irá trabalhar para compor o ambiente.

CAMINHO_MAVEN_SETTINGS=./settings.xml

> Esse parâmetro representa o arquivo settings.xml utilizado em todos os ambientes do SVN. Será feito uma copia do mesmo apenas alterando o *localrepository*.

CAMINHO_WORKSPACE_STANDARD=./workspace
> Esse parâmetro representa seu *workspace* na versão mais standard, onde o mesmo será copiado à pasta dos fontes.

CAMINHO_LIB_MAVEN_CONFIG= C:/ProgramData/chocolatey/lib/maven/apache-maven-3.5.0/conf
> Esse parâmetro representa o caminho da configuração dos settings do maven.

**LINUX**

Caso executar a cada 5 segundo seja complicado, baixar o arquivo *env.sh*, e adicionar no gedit ~/.profile, conforme abaixo:

>export SIMPLE_ENV=/home/seu.usuario/caminho/pastadoscript
>
>export PATH=$PATH:$SIMPLE_ENV

Quando necessitar executar o *script* para atualizar o novo ambiente, basta abrir o console e digitar ***env.sh*** passando o parametro A.

**WINDOWS**

Criado o arquivo **env.bat** é possível executá-lo manualmente, ou até mesmo agendar no **Agendador de Tarefas** passando o parametro A. 

Adicionar no **PATH** o caminho do script para poder executá-lo quando abrir o **cmd** é uma boa dica. 

**Parametros**

Adicionado outra função que é alterar o settings do maven para utilização do maven no GIT ou SVN. Ou seja, além do parametro A que indica a criação do ambiente, pode mandar executar com parametro GIT ou SVN.

Os arquivos devem ficar na mesma pasta com nomenclatura abaixo:

settings_git

settings_svn

Desta forma, será subistituido quando executar.

Caso nenhum parametro for passado, será perguntado no prompt de comando.