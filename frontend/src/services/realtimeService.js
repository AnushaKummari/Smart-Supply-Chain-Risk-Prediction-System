import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let client = null;

export function connectAlerts(onAlert) {
  const token = localStorage.getItem('ssrp_jwt');

  client = new Client({
    // Use SockJS for compatibility
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    reconnectDelay: 3000,
    connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
    onConnect: () => {
      client.subscribe('/topic/alerts', (msg) => {
        try {
          const payload = JSON.parse(msg.body);
          onAlert(payload);
        } catch (e) {
          // ignore malformed messages
        }
      });
    }
  });

  client.activate();

  return () => {
    try {
      client.deactivate();
    } catch (e) {
      // ignore
    } finally {
      client = null;
    }
  };
}

