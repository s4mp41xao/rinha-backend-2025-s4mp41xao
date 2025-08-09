# Especificação do Ambiente de Teste - Rinha de Backend 2025

## Recursos Computacionais

### Limites Totais
- **CPU**: 1.5 vCPUs
- **Memória**: 350MB
- **Rede**: Sem limitação específica
- **Armazenamento**: Sem limitação específica

### Distribuição de Recursos (Configuração Atual)

| Serviço | CPU | Memória | Descrição |
|---------|-----|---------|-----------|
| nginx | 0.1 | 50MB | Load balancer |
| app1 | 0.6 | 120MB | Instância 1 da aplicação |
| app2 | 0.6 | 120MB | Instância 2 da aplicação |
| db | 0.15 | 60MB | PostgreSQL |
| payment-processor-default | 0.025 | 5MB | Mock processador padrão |
| payment-processor-fallback | 0.025 | 5MB | Mock processador fallback |
| **TOTAL** | **1.5** | **360MB** | **10MB acima do limite** |

⚠️ **Atenção**: A configuração atual excede o limite de memória em 10MB. Ajustes necessários:
- Reduzir memória das aplicações para 115MB cada
- Ou reduzir memória do banco para 50MB

## Arquitetura do Sistema

### Load Balancer (nginx)
- **Porta**: 9999 (externa)
- **Algoritmo**: Round-robin
- **Health Check**: Implícito via proxy_pass
- **Timeout**: 5s conexão, 10s leitura/escrita

### Aplicações Backend (2 instâncias)
- **Framework**: Spring Boot
- **Porta interna**: 9999
- **Banco**: PostgreSQL compartilhado
- **Processadores**: 2 mocks externos

### Banco de Dados
- **SGBD**: PostgreSQL 15 Alpine
- **Porta**: 5432
- **Persistência**: Volume Docker

### Processadores de Pagamento
- **Tecnologia**: Node.js + Express
- **Tipos**: Default (taxa 5%) e Fallback (taxa 10%)
- **Instabilidade**: Configurável via variável de ambiente

## Configuração de Rede

### Comunicação Interna
```
Cliente → nginx:9999 → {app1:9999, app2:9999} → db:5432
                                              → payment-processor-default:8080
                                              → payment-processor-fallback:8080
```

### Portas Expostas
- **9999**: nginx (entrada principal)
- **5432**: PostgreSQL (para debug/admin)

## Variáveis de Ambiente

### Aplicação Backend
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/rinha
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
PAYMENT_PROCESSOR_DEFAULT_URL=http://payment-processor-default:8080
PAYMENT_PROCESSOR_FALLBACK_URL=http://payment-processor-fallback:8080
```

### Processadores de Pagamento
```bash
# Default Processor
PROCESSOR_TYPE=default
PROCESSOR_INSTABILITY=0.1

# Fallback Processor  
PROCESSOR_TYPE=fallback
PROCESSOR_INSTABILITY=0.2
```

## Comandos de Gerenciamento

### Iniciar Ambiente
```bash
docker-compose up -d
```

### Verificar Status
```bash
docker-compose ps
docker-compose logs
```

### Monitorar Recursos
```bash
docker stats
```

### Parar Ambiente
```bash
docker-compose down
```

### Rebuild Completo
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## Testes de Carga

### Ferramenta
- **k6**: Ferramenta de teste de carga moderna
- **Versão**: >= 0.40.0

### Configuração de Teste
- **Duração**: ~2 minutos (warm-up + carga + cool-down)
- **Usuários simultâneos**: Até 550 (configurável)
- **Endpoints**: POST /payments (90%) + GET /payments-summary (10%)

### Métricas Importantes
- **p99 < 11s**: Requisito para bônus de performance
- **Taxa de erro < 10%**: Limite aceitável
- **Throughput**: Pagamentos processados por segundo

## Critérios de Avaliação

### Pontuação Base
```
Lucro = Σ(Pagamentos × (100 - Taxa_Processador))
```

### Bônus de Performance
```
Bônus = (11 - p99_segundos) × 0.02 × Lucro
```

### Multa por Inconsistência
```
Multa = 0.35 × Lucro (se dados inconsistentes)
```

### Pontuação Final
```
Pontuação = Lucro + Bônus - Multa
```

## Troubleshooting

### Problemas Comuns

#### 1. Containers não sobem
```bash
# Verificar logs
docker-compose logs

# Verificar recursos
docker system df
docker system prune
```

#### 2. Aplicação não responde
```bash
# Testar conectividade
curl http://localhost:9999/payments-summary

# Verificar logs das aplicações
docker-compose logs app1 app2
```

#### 3. Performance baixa
```bash
# Monitorar recursos em tempo real
docker stats

# Verificar configuração do banco
docker-compose exec db psql -U postgres -d rinha -c "SHOW all;"
```

#### 4. Inconsistências nos dados
```bash
# Verificar dados no banco
docker-compose exec db psql -U postgres -d rinha -c "SELECT COUNT(*) FROM payments;"

# Comparar com endpoint
curl http://localhost:9999/payments-summary
```

## Otimizações Recomendadas

### Aplicação
1. **Pool de conexões**: Configurar adequadamente
2. **Cache**: Implementar para consultas frequentes
3. **Índices**: Otimizar queries do banco
4. **Timeout**: Ajustar timeouts dos processadores

### Infraestrutura
1. **JVM**: Configurar heap size adequado
2. **PostgreSQL**: Ajustar shared_buffers e work_mem
3. **nginx**: Configurar worker_processes
4. **Docker**: Usar multi-stage builds

### Monitoramento
1. **Métricas**: Implementar Micrometer/Prometheus
2. **Logs**: Estruturar logs para análise
3. **Health checks**: Implementar endpoints de saúde
4. **Alertas**: Configurar alertas para recursos críticos