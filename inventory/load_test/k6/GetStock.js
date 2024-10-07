import http from 'k6/http';
import { sleep, check} from 'k6';

const maxVus = 1000;

// maxVus * 5 / 2 + maxVus * 10 + maxVus * 5 / 2 = 15 * maxVus
// 15 * maxVus 초 동안 실행
// 250ms delay이기 때문에, 4rps
// 총 15 * maxVus * 4 = 60 * maxVus 번 실행
export const options = {
  scenarios: {
    getStock: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        {duration: '5s', target: maxVus},
        {duration: '10s', target: maxVus},
        {duration: '5s', target: 0},
      ],
    }
  }
};

export default function() {
  const resp = http.get('http://localhost:8080/api/v1/inventory/1');

  check(resp, {
    'response status should be 200' : (r) => r.status === 200,
    'response body should have itemId' : (r) => r.json('data.itemId') === '1',
    'response body should have stock gte 0' : (r) => r.json('data.stock') >= 0,
  });

  sleep(0.25); // 250ms 간격으로 요청
}
