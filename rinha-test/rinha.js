import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Test configuration
const MAX_REQUESTS = __ENV.MAX_REQUESTS || 500;

export const options = {
  stages: [
    { duration: '10s', target: 10 },    // Warm up
    { duration: '30s', target: 50 },    // Ramp up
    { duration: '60s', target: MAX_REQUESTS }, // Peak load
    { duration: '30s', target: 50 },    // Ramp down
    { duration: '10s', target: 0 },     // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(99)<11000'], // 99% of requests must complete below 11s
    http_req_failed: ['rate<0.1'],      // Error rate must be below 10%
    errors: ['rate<0.1'],               // Custom error rate
  },
};

// Base URL for the backend
const BASE_URL = 'http://localhost:9999';

// Generate unique correlation ID
function generateCorrelationId() {
  return `test-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
}

// Generate payment amount (fixed value as per specification)
function generatePaymentAmount() {
  return 100.00; // Fixed amount as specified in the rules
}

export default function () {
  // Test POST /payments endpoint
  const paymentPayload = JSON.stringify({
    correlationId: generateCorrelationId(),
    amount: generatePaymentAmount()
  });

  const paymentParams = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const paymentResponse = http.post(`${BASE_URL}/payments`, paymentPayload, paymentParams);
  
  const paymentSuccess = check(paymentResponse, {
    'payment status is 201': (r) => r.status === 201,
    'payment response has correlationId': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.correlationId !== undefined;
      } catch (e) {
        return false;
      }
    },
    'payment response has amount': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.amount !== undefined;
      } catch (e) {
        return false;
      }
    },
    'payment response has fee': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.fee !== undefined;
      } catch (e) {
        return false;
      }
    },
    'payment response has netAmount': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.netAmount !== undefined;
      } catch (e) {
        return false;
      }
    },
  });

  if (!paymentSuccess) {
    errorRate.add(1);
    console.log(`Payment failed: ${paymentResponse.status} - ${paymentResponse.body}`);
  } else {
    errorRate.add(0);
  }

  // Occasionally test GET /payments-summary endpoint (10% of requests)
  if (Math.random() < 0.1) {
    const summaryResponse = http.get(`${BASE_URL}/payments-summary`);
    
    const summarySuccess = check(summaryResponse, {
      'summary status is 200': (r) => r.status === 200,
      'summary response has processedPayments': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.processedPayments !== undefined;
        } catch (e) {
          return false;
        }
      },
      'summary response has processedAmount': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.processedAmount !== undefined;
        } catch (e) {
          return false;
        }
      },
      'summary response has processedFees': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.processedFees !== undefined;
        } catch (e) {
          return false;
        }
      },
      'summary response has processedNetAmount': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.processedNetAmount !== undefined;
        } catch (e) {
          return false;
        }
      },
      'summary response has processors': (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.processors !== undefined && 
                 body.processors.defaultProcessor !== undefined &&
                 body.processors.fallbackProcessor !== undefined;
        } catch (e) {
          return false;
        }
      },
    });

    if (!summarySuccess) {
      errorRate.add(1);
      console.log(`Summary failed: ${summaryResponse.status} - ${summaryResponse.body}`);
    } else {
      errorRate.add(0);
    }
  }

  // Small delay between requests to avoid overwhelming the system
  sleep(0.1);
}

export function handleSummary(data) {
  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    'summary.json': JSON.stringify(data),
  };
}

function textSummary(data, options = {}) {
  const indent = options.indent || '';
  const enableColors = options.enableColors || false;
  
  let summary = '';
  
  if (enableColors) {
    summary += '\n' + indent + '✓ Test completed successfully\n';
  } else {
    summary += '\n' + indent + 'Test completed successfully\n';
  }
  
  summary += indent + `Total requests: ${data.metrics.http_reqs.count}\n`;
  summary += indent + `Failed requests: ${data.metrics.http_req_failed.count}\n`;
  summary += indent + `Request rate: ${data.metrics.http_reqs.rate.toFixed(2)}/s\n`;
  summary += indent + `Average response time: ${data.metrics.http_req_duration.avg.toFixed(2)}ms\n`;
  summary += indent + `95th percentile: ${data.metrics.http_req_duration['p(95)'].toFixed(2)}ms\n`;
  summary += indent + `99th percentile: ${data.metrics.http_req_duration['p(99)'].toFixed(2)}ms\n`;
  
  return summary;
}