const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });

const rooms = {};

wss.on('connection', ws => {
  ws.on('message', message => {
    const data = JSON.parse(message);
    
    // Простая логика комнат: соединяем первых двух пользователей
    if (data.type === 'join') {
      const roomId = data.room || 'default';
      if (!rooms[roomId]) rooms[roomId] = []; // Создаем комнату
      
      rooms[roomId].push(ws); // Добавляем первого пользователя
      // На этом этапе ничего не отправляется - пользователь просто ждет
      ws.room = roomId;
      
      if (rooms[roomId].length === 2) {
        // Уведомляем обоих пользователей о готовности
        rooms[roomId].forEach(client => {
          client.send(JSON.stringify({ type: 'ready' }));
        });
      }
      return;
    }
    
    // Пересылка сообщений другим участникам комнаты
    if (ws.room) {
      rooms[ws.room].forEach(client => {
        if (client !== ws && client.readyState === WebSocket.OPEN) {
          client.send(JSON.stringify(data));
        }
      });
    }
  });

  ws.on('close', () => {
    if (ws.room && rooms[ws.room]) {
      rooms[ws.room] = rooms[ws.room].filter(client => client !== ws);
    }
  });
});