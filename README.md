# 📱 CieloPass — App de Venda de Ingressos para Cielo Smart / LIO

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-compileSdk%2037%20%7C%20targetSdk%2029-3DDC84.svg?style=flat&logo=android)](https://developer.android.com/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.06.01-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI%20%2B%20UDF-FF6F00.svg?style=flat)]()
[![DI](https://img.shields.io/badge/Koin-4.2.2-blue.svg?style=flat)](https://insert-koin.io/)
[![Security](https://img.shields.io/badge/Security-Google%20Tink%20AES--256-green.svg?style=flat)](https://github.com/tink-crypto/tink-mcp)

> **Plataforma:** Android Nativo (Kotlin)  
> **Arquitetura:** Clean Architecture + MVI (Model-View-Intent) + UDF  
> **Integração:** Ecossistema POS Cielo Smart / Cielo LIO (Deeplink `order://`)

---

## 📌 Visão Geral do Projeto

O **CieloPass** é uma aplicação Android nativa em Kotlin projetada e otimizada para execução em terminais de ponto de venda (POS) **Cielo Smart / Cielo LIO**. O
aplicativo gerencia todo o ciclo de vida da venda de ingressos para eventos locais, abrangendo:

- 📅 **Discovery & Eventos:** Listagem de eventos locais com controle dinâmico de estoque, categorias e preços.
- 🎟️ **Seleção de Ingressos:** Seleção interativa de quantidade com cálculo de total em tempo real.
- 💳 **Checkout & Pagamento:** Integração por Deeplink com a Cielo LIO (`order://`), pré-registro idempotente da transação e execução sustentada via
  `ForegroundService` no hardware POS.
- 📋 **Processamento de Resposta:** Leitura e decodificação Base64 de retornos de pagamento (Aprovado, Cancelado, Falhou) via `MainActivity`.
- 🎫 **Comprovante & QR Code:** Confirmação detalhada da transação com geração offline de QR Code para validação e controle de entrada no evento.

---

## 1. 🚀 Instruções de Execução

### Pré-requisitos de Ambiente

- **JDK 17** instalado e configurado na variável de ambiente `JAVA_HOME`.
- **Android Studio** (versão Ladybug / Jellyfish ou mais recente).
- **Android SDK** configurado com:
    - `compileSdk = 37`
    - `targetSdk = 29` *(Requisito técnico estrito da plataforma Cielo LIO)*
    - `minSdk = 24`

### Configuração Obrigatória de Credenciais da Cielo

A integração via Deep Link exige **obrigatoriamente** o envio das credenciais de autenticação (**Client-ID** e **Access Token**) cadastradas na plataforma Cielo
Smart, conforme a [Documentação Oficial de Autenticação e Credenciais da Cielo](https://docs.cielo.com.br/cielo-smart/docs/autenticacao-e-credenciais).

#### Passo a Passo para Obtenção das Credenciais no Portal Cielo:

1. Acesse o [Portal de Desenvolvedores Cielo](https://desenvolvedores.cielo.com.br/api-portal/) e vá para a página de **Cadastro de Aplicativo**.
2. Preencha os campos obrigatórios (*Ícone do aplicativo, Nome, Descrição*).
3. No campo **API Disponível**, selecione obrigatoriamente a opção **Cielo Smart - Order Manager**.
4. Clique em **Registrar** para concluir a criação.
5. Acesse **Perfil > Client-IDs Cadastrados** para visualizar e copiar o **Client-ID** (`clientID`) e o **Access Token** (`accessToken`).

#### Configuração das Credenciais no Projeto (`local.properties`):

No arquivo `local.properties` localizado na raiz do projeto, adicione as credenciais obtidas:

```properties
CIELO_CLIENT_ID=seu_client_id_obtido_no_portal
CIELO_ACCESS_TOKEN=seu_access_token_obtido_no_portal
```

> 🔒 **Segurança das Credenciais:**  
> Durante a inicialização da aplicação (Tela de Splash), o caso de uso `InitializeAppUseCase` lê o `BuildConfig.CIELO_CLIENT_ID` e
`BuildConfig.CIELO_ACCESS_TOKEN` e os armazena de forma persistente e encriptada usando **Google Tink (AES-256-GCM)** no **Proto DataStore** por meio do
`CieloCredentialsRepository`. Em cada requisição de pagamento, o `LaunchCieloPaymentUseCase` injeta essas credenciais no objeto DTO (`CieloPaymentRequestDTO`),
> garantindo a autenticação na Cielo LIO.

### Configuração do Emulador Cielo Smart / Dispositivo Físico

Para testar o fluxo completo de pagamento durante o desenvolvimento:

1. **Download do APK do Emulador Cielo LIO:**
    - Faça o download da aplicação simuladora de pagamentos da Cielo (`lio-emulator.apk`) através
      da [Documentação Oficial da Cielo Smart](https://docs.cielo.com.br/cielo-smart/docs/baixando-o-emulador-cielo).
2. **Instalação no Emulador (AVD) ou Dispositivo Físico:**
    - Inicie um AVD Android no Android Studio ou conecte a POS LIO via cabo USB com depuração ADB ativada.
    - Instale o APK do simulador Cielo via linha de comando:
      ```powershell
      adb install -r lio-emulator.apk
      ```

### Compilação e Execução via CLI / Gradle

1. **Clonar o Repositório:**
   ```powershell
   git clone https://github.com/gomes-eric/cielo-pass.git
   cd cielo-pass
   ```
2. **Análise Estática e Formatação (ktlint):**
   ```powershell
   ./gradlew ktlintFormat
   ```
3. **Execução da Suíte de Testes Unitários:**
   ```powershell
   ./gradlew test
   ```
4. **Build e Instalação da Aplicação:**
   ```powershell
   ./gradlew installDebug
   ```
   *(Ou abra o projeto no Android Studio e selecione **Run 'app'**).*

---

## 2. 📐 Decisões Arquiteturais

A arquitetura do **CieloPass** foi desenhada com foco em testabilidade, facilidade de manutenção, resiliência contra falhas de rede/hardware e desacoplamento de
responsabilidades, combinando **Clean Architecture** com o padrão **MVI (Model-View-Intent)** e **Fluxo Unidirecional de Dados (UDF)**.

### Estrutura de Diretórios do Projeto

O projeto utiliza modularização por features e componentes centrais compartilhados (`core`):

```
com.cielo.cielopass/
├── core/
│   ├── cielo/                 # Intent Builder, Parser Base64, Repositório Deeplink e Foreground Service
│   ├── constants/             # Constantes globais do sistema e integrações
│   ├── credentials/           # Gerenciamento de credenciais encriptadas no DataStore via Google Tink
│   ├── database/              # Room Database, Converters de tipo, DAOs e Entidades
│   ├── datastore/             # Instância segura de Proto DataStore
│   ├── navigation/            # Roteamento centralizado com Jetpack Navigation 3 e NavKeys
│   ├── security/              # Gerenciador de Criptografia AES-256 via Google Tink
│   ├── theme/                 # Design System, Tema, Cores do Cielo Brandbook, Montserrat Typography
│   ├── transaction/           # Mapeamento e modelos do domínio de transações
│   └── utils/                 # Gerador de QR Code (ZXing) e utilitários
└── features/
    ├── splash/                # Inicialização de banco e credenciais em background
    ├── events/                # Listagem e Detalhes de Eventos com gerenciamento de estoque
    ├── checkout/              # Resumo do pedido, cálculo do valor e lançamento do pagamento
    └── payment/               # Resultado do pagamento, comprovante e exibição de QR Code
```

### Padrão MVI e UDF na Camada de Apresentação

Cada tela possui seu próprio contrato imutável (`Presentation Contract`):

- **State (`StateFlow<UiState>`):** Estado reativo imutável lido pela UI Compose.
- **Event (`onEvent(UiEvent)`):** Intenções do usuário enviadas da UI para o ViewModel.
- **Effect (`SharedFlow<UiEffect>`):** Disparos pontuais de efeitos colaterais (ex: navegação entre telas, Toasts, lançamento de Intents externos de pagamento).

### Organização em Camadas (Clean Architecture)

1. **Presentation:** Composables e ViewModels sem dependência direta com DTOs ou banco de dados.
2. **Domain:** Use Cases puros, entidades imutáveis e interfaces de repositório sem dependência do ecossistema Android.
3. **Data:** Implementação de Repositórios, Mappers, DAOs do Room e serialização/parser de respostas da Cielo.

---

## 3. 📦 Bibliotecas Externas e Justificativas

| Biblioteca                       | Versão               | Justificativa Técnica                                                                                                                               |
|:---------------------------------|:---------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------|
| **Kotlin & AGP**                 | `2.4.0` / `9.3.1`    | Sintaxe moderna, corrotinas de alta performance e integração nativa com o plugin do Compose Compiler.                                               |
| **Jetpack Compose BOM**          | `2026.06.01`         | Construção de UI declarativa, responsiva e performática, implementando rigorosamente o **Cielo Brandbook**.                                         |
| **Jetpack Navigation 3**         | `1.1.4`              | Navegação reativa no Compose orientada a estado (`NavKey`), oferecendo desacoplamento em relação ao grafo de telas legado.                          |
| **Koin**                         | `4.2.2`              | Injeção de dependência pragmática e leve. Elimina o tempo extenso de compilação do Dagger/Hilt e simplifica a injeção em ViewModels Compose.        |
| **Room Database**                | `2.8.4`              | Persistência relacional local reativa (`Flow`) com suporte a transações atômicas (`@Transaction`), garantindo consistência no histórico de compras. |
| **Jetpack DataStore + Protobuf** | `1.2.1` / `4.35.1`   | Armazenamento assíncrono e tipado de preferências da aplicação, substituindo com segurança o SharedPreferences.                                     |
| **Google Tink Android**          | `1.23.0`             | Criptografia simétrica (AES-256-GCM) para proteção e encriptação local de chaves e credenciais no DataStore.                                        |
| **ZXing Core**                   | `3.5.4`              | Geração de matrizes de QR Code em tempo real e offline para exibição nos ingressos aprovados.                                                       |
| **Kotlinx Serialization**        | `1.11.0`             | Parser JSON rápido e seguro para codificação e decodificação dos objetos DTO enviados/recebidos da Cielo LIO.                                       |
| **Core Library Desugaring**      | `2.1.5`              | Habilita suporte a APIs Java 8+ em dispositivos Android mais antigos (minSdk 24) sem alterar o `targetSdk = 29`.                                    |
| **Mockk & Coroutines Test**      | `1.14.11` / `1.10.1` | Suíte robusta de testes unitários com suporte completo a suspensão de corrotinas e espelhamento de comportamentos.                                  |
| **ktlint**                       | `14.2.0`             | Padronização de estilo de código e checagem estática automatizada via Gradle.                                                                       |

---

## 4. 💳 Integração Cielo Smart & Garantia de Idempotência

### Comunicação por Deeplink URI (`order://`)

A comunicação entre o **CieloPass** e a aplicação de pagamentos do terminal LIO ocorre por meio do protocolo de Deeplink da Cielo. A visibilidade do pacote e o
tipo de integração estão declarados no `AndroidManifest.xml`:

```xml

<queries>
    <package android:name="com.ads.lio.uriappclient" />
</queries>

<meta-data android:name="cs_integration_type" android:value="uri" />
```

### Fluxo de Pagamento e Estratégia Anti-Duplicidade

```
┌──────────────────────────┐
│  Usuário confirma Compra │
└─────────────┬────────────┘
              │
              ▼
┌──────────────────────────────────────────────────────────┐
│  1. Pre-Registration no Room DB                          │
│  - Cria ID único UUID (reference)                        │
│  - Define status inicial: PENDING                        │
│  - Invocação idempotente via insertIfNoPending()         │
└─────────────┬────────────────────────────────────────────┘
              │
              ├── (Se existir transação PENDING ativa) ──> Bloqueia nova tentativa
              │
              ▼
┌──────────────────────────────────────────────────────────┐
│  2. Inicia Foreground Service                            │
│  - CieloForegroundService (dataSync)                     │
│  - Impede a destruição do app pelo Android durante o POS │
└─────────────┬────────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────────────────────┐
│  3. Dispara Intent de Deeplink para a Cielo LIO          │
│  - Encaminha DTO de itens, valores e credenciais         │
└─────────────┬────────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────────────────────┐
│  4. Retorno de Callback via Deeplink no MainActivity     │
│  - order://response?response=<BASE64>&responsecode=0     │
└─────────────┬────────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────────────────────┐
│  5. Parse do Payload & Atualização no Room DB            │
│  - Decodificação Base64 via CieloResponseParser          │
│  - Atualiza status para APPROVED, CANCELLED ou FAILED    │
│  - Encerra o CieloForegroundService                      │
└─────────────┬────────────────────────────────────────────┘
              │
              ▼
┌──────────────────────────────────────────────────────────┐
│  6. Exibição do Resultado & Geração de QR Code (se ok)   │
└──────────────────────────────────────────────────────────┘
```

1. **Pré-Registro Obrigatório:** O `LaunchCieloPaymentUseCase` gera um UUID exclusivo para a transação e efetua a inserção no Room com status `PENDING` antes de
   abrir a tela de pagamento da Cielo.
2. **Garantia de Idempotência:** O método `insertIfNoPending` assegura que, caso o usuário dê toques múltiplos acidentais ou ocorra re-tentativa da UI, a
   aplicação recuse a criação de nova cobrança enquanto houver uma pendente em andamento.
3. **Execução em Foreground Service:** Devido à troca de contexto no hardware POS, o `CieloForegroundService` mantém o processo ativo no sistema operacional
   Android, prevenindo encerramentos por falta de memória.
4. **Processamento do Retorno:** O callback da Cielo envia os dados serializados em Base64 para a `MainActivity`. O `ProcessCieloResponseUseCase` decodifica a
   string JSON e mapeia o código retornado:
    - **Código `0` (Aprovado):** Salva o NSU, código de autorização e marca a transação como `APPROVED`, gerando o QR Code offline.
    - **Código `1` (Cancelado):** Atualiza a transação para `CANCELLED`.
    - **Códigos `2`, `3` ou `4` (Erro):** Atualiza a transação para `FAILED` com mensagem descritiva.

---

## 5. ⚖️ Trade-offs

Durante o desenvolvimento do projeto, foram avaliados e adotados os seguintes trade-offs técnicos:

1. **Manutenção do `targetSdk = 29` vs Atualização para Target SDK Recente:**
    - *Decisão:* Manter `targetSdk = 29` no manifesto do aplicativo.
    - *Trade-off:* Atende à exigência estrita de compatibilidade dos terminais POS Cielo LIO e do app `lio-emulator`, aceitando as limitações de permissão do
      Android 10 e utilizando `dataSync` no Foreground Service para sustentação em sistemas mais recentes.

2. **Adoção do Koin em Detrimento do Hilt:**
    - *Decisão:* Utilizar Koin 4.2.2 para Injeção de Dependências.
    - *Trade-off:* O Koin não faz checagem de erros de injeção em tempo de compilação como o Hilt, contudo reduz drasticamente os tempos de compilação e elimina
      código gerado excessivo, proporcionando uma sintaxe 100% idiomática em Kotlin.

3. **MVI com StateFlow/SharedFlow vs MVVM Clássico com LiveData:**
    - *Decisão:* Implementar MVI com fluxo unidirecional.
    - *Trade-off:* Aumenta a quantidade de código boilerplate (criação explicita de `State`, `Event` e `Effect`), mas elimina estados inconsistentes na
      interface gráfica e evita execuções duplicadas de efeitos colaterais.

4. **Uso de Jetpack Navigation 3 vs Navigation 2 (Grafos XML/SafeArgs):**
    - *Decisão:* Utilização da versão mais recente do Jetpack Navigation 3.
    - *Trade-off:* Como a biblioteca é recente (`1.1.4`), exige adaptação na estrutura de rotas baseada em listas e chaves (`NavKey`), mas oferece suporte total
      ao paradigma puramente reativo do Compose.

5. **Armazenamento Seguro Tink + DataStore vs SharedPreferences Simples:**
    - *Decisão:* Uso do Google Tink para encriptação AES-256 de dados sensíveis.
    - *Trade-off:* Aumenta o tamanho do APK com a biblioteca de criptografia, mas garante conformidade com requisitos de segurança de meios de pagamento e PCI.

---

## 6. 🔮 Evoluções Futuras

Caso houvesse um ciclo adicional de desenvolvimento, as seguintes melhorias seriam implementadas:

1. **Sincronização com Backend Server & Reconciliação:**
    - Implementação de um serviço remoto em nuvem para sincronização de estoque, consulta de transações em tempo real e conciliação via Webhooks oficiais da
      Cielo.
2. **Validador de Ingressos via Câmera/Scanner:**
    - Módulo de leitura do QR Code do ingresso utilizando a câmera integrada da POS LIO para controle de acesso na portaria do evento.
3. **Impressão Térmica Direta do Comprovante:**
    - Suporte à impressão do comprovante e ingresso em papel térmico utilizando a Intent de impressão da Cielo LIO (`order://print`).
4. **Módulo de Estorno / Cancelamento:**
    - Interface para busca e estorno de transações diretamente pelo terminal utilizando a Intent de reversão (`order://reversal`).
5. **Controle de Sessão do Operador (Autenticação JWT):**
    - Autenticação de operadores de terminal via token JWT com integração a um servidor backend. O backend autentica o operador e fornece dinamicamente o
      `clientId` e `accessToken` da Cielo para a sessão ativa, eliminando a necessidade de armazenar credenciais estáticas no `local.properties`.
6. **QR Codes Assinados (JWT / ECDSA):**
    - Evoluir a geração de QR Code de um UUID simples para um token JWT assinado criptograficamente. Isso permite que catracas ou leitores de portaria validem a
      autenticidade do ingresso offline sem risco de fraude ou duplicidade.
7. **Envio de Ingressos por E-mail:**
    - Disparo automático do comprovante e ingresso digital com QR Code para o e-mail do comprador após a confirmação do pagamento.
8. **Validação de CPF por Algoritmo:**
    - Validação matemática de CPF via algoritmo de Dígitos Verificadores (Módulo 11) nos dados do comprador durante a etapa de checkout.
9. **Reconciliação Automática com WorkManager:**
    - Implementação de tarefas em segundo plano para consultar a API da Cielo e ajustar automaticamente transações que permaneceram no status UNKNOWN devido a
      quedas de conexão durante o fluxo.
10. **Aumento da Cobertura de Testes de UI:**
    - Adição de testes de interface instrumentados com `Compose Test Rule` simulando os fluxos completos do usuário de ponta a ponta.