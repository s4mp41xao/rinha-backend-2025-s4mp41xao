# Rinha de Backend 2025 - Testes K6

Este diretório contém os scripts de teste para a Rinha de Backend 2025 usando k6.

## Pré-requisitos

1. **k6 instalado**: Siga as instruções em https://k6.io/docs/get-started/installation/
2. **Backend rodando**: Certifique-se de que seu backend está rodando na porta 9999
3. **Payment Processors**: Os serviços de processamento de pagamento devem estar ativos

## Execução dos Testes

### Teste Básico
```bash
k6 run rinha.js
```

### Teste com Dashboard e Relatório HTML
Configure as variáveis de ambiente para acompanhar os testes via dashboard:

```bash
export K6_WEB_DASHBOARD=true
export K6_WEB_DASHBOARD_PORT=5665
export K6_WEB_DASHBOARD_PERIOD=2s
export K6_WEB_DASHBOARD_OPEN=true
export K6_WEB_DASHBOARD_EXPORT='report.html'
k6 run rinha.js
```

### Configurar Número Máximo de Requisições Simultâneas
```bash
k6 run -e MAX_REQUESTS=550 rinha.js
```

## Estrutura do Teste

O script `rinha.js` executa os seguintes cenários:

### Cenários de Carga
1. **Warm up**: 10s com 10 usuários virtuais
2. **Ramp up**: 30s aumentando para 50 usuários
3. **Peak load**: 60s com carga máxima (configurável via MAX_REQUESTS)
4. **Ramp down**: 30s diminuindo para 50 usuários
5. **Cool down**: 10s finalizando com 0 usuários

### Endpoints Testados

#### POST /payments
- **Frequência**: 100% das requisições
- **Payload**: 
  ```json
  {
    "correlationId": "test-{timestamp}-{random}",
    "amount": 100.00
  }
  ```
- **Validações**:
  - Status 201
  - Resposta contém `correlationId`
  - Resposta contém `amount`
  - Resposta contém `fee`
  - Resposta contém `netAmount`

#### GET /payments-summary
- **Frequência**: 10% das requisições (auditoria)
- **Validações**:
  - Status 200
  - Resposta contém `processedPayments`
  - Resposta contém `processedAmount`
  - Resposta contém `processedFees`
  - Resposta contém `processedNetAmount`
  - Resposta contém `processors` com `defaultProcessor` e `fallbackProcessor`

## Métricas e Thresholds

### Thresholds Configurados
- **p99 < 11s**: 99% das requisições devem completar em menos de 11 segundos
- **Taxa de erro < 10%**: Menos de 10% das requisições podem falhar
- **Taxa de erro customizada < 10%**: Métrica adicional para monitoramento

### Métricas Coletadas
- Total de requisições
- Requisições falhadas
- Taxa de requisições por segundo
- Tempo médio de resposta
- Percentil 95 e 99

## Critérios da Rinha de Backend 2025

### Pontuação
- **Lucro**: Baseado no número de pagamentos processados com menor taxa
- **Performance**: Bônus de 2% para cada 1ms abaixo de 11ms no p99
- **Consistência**: Multa de 35% sobre o lucro total por inconsistências

### Fórmula do Bônus de Performance
```
Bônus = (11 - p99) * 0.02
```

Exemplos:
- p99 de 10ms = 2% de bônus
- p99 de 9ms = 4% de bônus  
- p99 de 5ms = 12% de bônus
- p99 de 1ms = 20% de bônus

## Arquivos Gerados

- `summary.json`: Relatório detalhado em JSON
- `report.html`: Relatório visual (se configurado)

## Troubleshooting

### Backend não responde
Verifique se:
1. Docker containers estão rodando: `docker-compose ps`
2. Backend está na porta 9999: `curl http://localhost:9999/payments-summary`
3. Payment processors estão ativos

### Muitos erros 500
- Verifique logs dos containers: `docker-compose logs`
- Reduza a carga: `k6 run -e MAX_REQUESTS=100 rinha.js`

### Performance baixa
- Monitore recursos: CPU e memória
- Verifique configurações do banco de dados
- Analise logs de aplicação