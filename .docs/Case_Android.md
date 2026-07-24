# 📱 Desafio Técnico — App de Venda de Ingressos com Cielo Lio

> **Cargo:** Desenvolvedor(a) Mobile / Backend  
> **Plataforma:** Android (Kotlin)  
> **Integração:** Ecossistema Cielo Smart / Cielo Lio

---

## 📌 Visão Geral do Projeto

| Pilar             | Descrição                                                                                     |
|:------------------|:----------------------------------------------------------------------------------------------|
| 🎯 **Escopo**     | Venda de ingressos para eventos locais com um fluxo de compra simples e intuitivo.            |
| 💳 **Integração** | Processamento de pagamento por meio do ecossistema **Cielo Smart / Cielo Lio**.               |
| 🧪 **Qualidade**  | Tratamento robusto de erros, prevenção contra duplicidade de cobrança e testes automatizados. |

---

## 🎯 Contexto e Objetivo

### Contexto

Você foi contratado(a) para desenvolver um aplicativo mobile de venda de ingressos para eventos locais, com foco em simplicidade na experiência de compra e
integração de pagamentos com a **Cielo Smart**.

Após a entrega, será realizada uma sessão de **code review** e apresentação para discussão sobre:

- Decisões técnicas e de arquitetura
- Trade-offs considerados
- Possíveis evoluções do projeto

### Objetivo

Construir uma solução funcional para a venda de ingressos, cobrindo os fluxos essenciais de seleção, pagamento e confirmação, utilizando o ecossistema Cielo
Smart.

---

## ⚙️ Funcionalidades e Requisitos

### Funcionalidades Esperadas

- 📅 Listagem de eventos disponíveis
- 🎟️ Seleção da quantidade de ingressos por evento
- 💳 Processamento de pagamento via Cielo Smart
- 📱 Geração de QR Code do ingresso *(funcionalidade opcional)*

### Requisitos Funcionais

A aplicação deve implementar, no mínimo, o seguinte fluxo do usuário:

1. **Visualização de Eventos:** Exibir os eventos disponíveis para compra.
2. **Seleção de Ingressos:** Permitir que o usuário selecione a quantidade desejada.
3. **Fluxo de Pagamento:** Iniciar e concluir a transação via integração com a Cielo.
4. **Registro de Resultado:** Registrar o status final da compra (*Aprovada*, *Negada* ou *Cancelada*).
5. **Comprovante / Resumo:** Exibir o resumo/comprovante detalhado da transação concluída.

> 💡 **Nota:** Se optar por implementar a geração de QR Code, o ingresso gerado deve estar estritamente vinculado à compra concluída com sucesso.

---

## 🛠️ Requisitos Não-Funcionais & Restrições Técnicas

### Requisitos Não-Funcionais

- **Tratamento de Erros:** Tratamento explícito e amigável de falhas de integração e erros de pagamento.
- **Prevenção de Duplicidade:** O fluxo de pagamento deve garantir resiliência contra cobranças duplicadas (ex: reenvio acidental de ação pelo usuário).
- **Manutenibilidade:** Código limpo, bem estruturado, modular e de fácil manutenção.
- **Testes Automatizados:** Cobertura de testes automatizados para os cenários mais críticos do sistema.
- **Uso de IA:** O uso de ferramentas de Inteligência Artificial como suporte ao desenvolvimento é esperado e encorajado. Será avaliado *como* a IA foi
  utilizada na construção da solução.
- **Backend:** A construção de um backend de apoio é opcional e não será objeto direto de avaliação.

### Restrições Técnicas

- **Linguagem & Framework:** Kotlin (Android nativo).
- **Banco de Dados:** Livre escolha (caso seja aplicável à solução).
- **Execução:** O projeto deve ser facilmente executável a partir de instruções claras no `README.md`.
- **Harness / Documentação de IA:** Disponibilizar a documentação referente ao harness do agente, incluindo Specs, decisões arquiteturais, prompts utilizados,
  restrições e resultados orientadores.
- **Bibliotecas Externas:** É permitido (e recomendado) o uso de bibliotecas de terceiros devidamente justificadas.

---

## 📚 Materiais e Documentação de Apoio

- 📥 [Emulador Cielo Smart — Download e Configuração](https://docs.cielo.com.br/cielo-smart/docs/baixando-o-emulador-cielo)
- 📖 [Documentação Oficial de Integração Cielo Smart](https://docs.cielo.com.br/cielo-smart/docs/conheca-a-cielo-smart)

---

## 🚀 Orientações para a Entrega

### Repositório

- Crie um repositório público no **GitHub** contendo todo o código-fonte da solução.
- Envie o link do repositório dentro do prazo combinado.

### Estrutura do `README.md`

O `README.md` principal do repositório deve conter, obrigatoriamente:

1. 🚀 **Instruções de Execução:** Passo a passo detalhado para rodar o projeto e o emulador.
2. 📐 **Decisões Arquiteturais:** Padrões adotados (ex: MVVM, Clean Architecture) e estrutura do projeto.
3. 📦 **Bibliotecas Externas:** Lista de dependências utilizadas e justificativa da escolha.
4. 💳 **Integração Cielo Smart:** Explicação técnica de como a integração de pagamento foi implementada.
5. ⚖️ **Trade-offs:** Decisões e escolhas feitas durante o desenvolvimento.
6. 🔮 **Evoluções Futuras:** O que seria melhorado ou adicionado caso houvesse mais tempo.

---