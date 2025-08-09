# Mini Guia - Testes K6 Rinha Backend 2025

## Setup Rápido

### 1. Instalar k6 (macOS)
```bash
brew install k6
```

### 2. Subir o Backend
```bash
docker-compose up -d
```

### 3. Executar Teste
```bash
cd rinha-test
k6 run rinha.js
```

## Comandos Essenciais

### Teste com Dashboard
```bash
export K6_WEB_DASHBOARD=true
export K6_WEB_DASHBOARD_PORT=5665
k6 run rinha.js
```
Acesse: http://localhost:5665

### Teste com Carga Personalizada
```bash
k6 run -e MAX_REQUESTS=300 rinha.js
```

### Verificar Status do Backend
```bash
curl http://localhost:9999/payments-summary
```

## Interpretação Rápida dos Resultados

### ✅ Sucesso
- `http_req_failed` < 10%
- `p(99)` < 11000ms
- Sem erros de conexão

### ❌ Problemas Comuns
- **Muitos 500**: Backend sobrecarregado
- **Timeouts**: Processadores lentos
- **Conexão recusada**: Backend não está rodando

## Otimização de Performance

### Para melhorar p99:
1. Otimizar queries do banco
2. Implementar cache
3. Ajustar pool de conexões
4. Configurar timeout dos processadores

### Para reduzir taxa de erro:
1. Implementar circuit breaker
2. Adicionar retry logic
3. Melhorar tratamento de exceções
4. Monitorar recursos (CPU/Memória)

## Fórmula de Pontuação

```
Lucro = Pagamentos Processados × (100 - Taxa do Processador)
Bônus Performance = (11 - p99_ms) × 0.02 × Lucro
Multa Consistência = 0.35 × Lucro (se houver inconsistências)

Pontuação Final = Lucro + Bônus - Multa
```

## Dicas de Última Hora

1. **Monitore em tempo real**: Use o dashboard do k6
2. **Teste localmente primeiro**: Valide antes de submeter
3. **Verifique consistência**: Compare dados entre endpoints
4. **Otimize para p99**: Foque na latência do percentil 99
5. **Balance carga vs estabilidade**: Não sacrifique confiabilidade por throughput