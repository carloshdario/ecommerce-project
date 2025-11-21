# E-commerce Full Stack com Spring Boot e Nuxt.js

Este projeto é uma aplicação de e-commerce. Ele utiliza:

- **Backend:** Java 11, Spring Boot (API REST)  
- **Frontend:** Nuxt.js (Vue.js) e Tailwind CSS  
- **Banco de Dados:** Oracle 19c XE  
- **Orquestração:** Docker Compose

---

## 💻 1. Arquitetura e Visão Geral

A aplicação é dividida em três serviços isolados, comunicando-se através de uma rede Docker:

| Serviço       | Tecnologia         | Porta Host | URL de Acesso (Host)                    |
|---------------|------------------|------------|----------------------------------------|
| Frontend      | Nuxt.js (Vue)    | 3000       | [http://localhost:3000](http://localhost:3000) |
| Backend       | Spring Boot (Java)| 8080       | [http://localhost:8080/api](http://localhost:8080/api) |
| Banco de Dados| Oracle 19c XE    | 1521       | db:1521/XEPDB1 (Acesso Interno)        |

---

## 🛠️ 2. Instruções de Inicialização (Docker Compose)

O projeto é configurado para ser iniciado com um único comando, garantindo que todos os serviços sejam construídos, configurados e conectados automaticamente.

### Pré-requisitos

- Docker e Docker Compose instalados  
- Memória RAM: O Oracle 19c XE exige **mínimo de 4GB de RAM** dedicados ao Docker para inicializar corretamente

### Passos para Subir o Projeto

1. Navegue até o diretório raiz do projeto:

```bash
cd ecommerce-project/

Execute o comando para construir as imagens e iniciar os containers:

sudo docker compose up --build

⚠️ Observação: A inicialização do container Oracle é o passo mais demorado (3 a 5 minutos na primeira vez), pois ele descompacta a imagem e executa o init.sql. O Backend aguardará o DB estar pronto.

🔗 URLs e Credenciais
Descrição	Endereço / Valor
Aplicação Frontend	http://localhost:3000
API Backend (Base)	http://localhost:8080/api
Credencial Padrão (Exemplo)	username: admin
password: 123456
Porem para realizar uma compra sera necessario criar uma conta de usuario comum.

