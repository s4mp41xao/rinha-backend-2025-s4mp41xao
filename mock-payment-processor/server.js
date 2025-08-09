const express = require('express');
const app = express();
const port = 8080;

app.use(express.json());

// Get processor type and instability from environment variables
const processorType = process.env.PROCESSOR_TYPE || 'default';
const instability = parseFloat(process.env.PROCESSOR_INSTABILITY || '0.1');

// Calculate fee based on processor type
const getFee = (amount) => {
  const feeRate = processorType === 'default' ? 0.05 : 0.03; // 5% for default, 3% for fallback
  return amount * feeRate;
};

// Simulate instability - randomly fail requests
const shouldFail = () => {
  return Math.random() < instability;
};

app.post('/payments', (req, res) => {
  console.log(`[${processorType.toUpperCase()}] Received payment request:`, req.body);
  
  // Simulate instability
  if (shouldFail()) {
    console.log(`[${processorType.toUpperCase()}] Simulating failure due to instability`);
    return res.status(500).json({
      error: 'Payment processor temporarily unavailable',
      processor: processorType
    });
  }

  const { correlationId, amount } = req.body;
  
  if (!correlationId || !amount) {
    return res.status(400).json({
      error: 'Missing required fields: correlationId and amount'
    });
  }

  const fee = getFee(amount);
  const netAmount = amount - fee;

  const response = {
    correlationId,
    amount,
    fee,
    netAmount,
    processor: processorType,
    status: 'PROCESSED',
    timestamp: new Date().toISOString()
  };

  console.log(`[${processorType.toUpperCase()}] Payment processed successfully:`, response);
  
  // Simulate processing delay
  setTimeout(() => {
    res.status(200).json(response);
  }, Math.random() * 100 + 50); // 50-150ms delay
});

app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'healthy',
    processor: processorType,
    timestamp: new Date().toISOString()
  });
});

app.listen(port, () => {
  console.log(`Mock Payment Processor (${processorType}) running on port ${port}`);
  console.log(`Instability rate: ${(instability * 100).toFixed(1)}%`);
});