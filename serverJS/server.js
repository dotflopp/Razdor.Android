const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });

// Хранилище комнат: { sessionId: { clients: [] } }
const rooms = {};

wss.on('connection', (socket) => {
    console.log('Клиент подключен');

    // Обработчик входящих сообщений
    socket.on('message', (message) => {
        try {
            const data = JSON.parse(message);
            console.log('Получено сообщение:', data);

            const { type, sessionId, payload } = data;

            // Валидация обязательных полей
            if (!type || !sessionId) {
                socket.send(JSON.stringify({ 
                    type: 'error', 
                    message: 'Отсутствует type или sessionId' 
                }));
                return;
            }

            // Создание комнаты, если её нет
            if (!rooms[sessionId]) {
                rooms[sessionId] = {
                    clients: [],
                };
                console.log(`Создана комната: ${sessionId}`);
            }

            const room = rooms[sessionId];

            // Проверка на заполнение комнаты (максимум 2 участника)
            if (room.clients.length >= 2 && type === 'join') {
                socket.send(JSON.stringify({ 
                    type: 'error', 
                    message: 'Room is full' 
                }));
                socket.close();
                console.log(`Комната ${sessionId} переполнена, соединение закрыто`);
                return;
            }

            // Добавление нового клиента в комнату
            if (type === 'join' && !room.clients.includes(socket)) {
                room.clients.push(socket);
                console.log(`Клиент добавлен в комнату ${sessionId}. Всего клиентов: ${room.clients.length}`);
                
                // Уведомление о готовности комнаты
                if (room.clients.length === 2) {
                    room.clients.forEach((client, index) => {
                        if (client.readyState === WebSocket.OPEN) {
                            client.send(JSON.stringify({
                                type: 'room-ready',
                                clientIndex: index
                            }));
                        }
                    });
                    console.log(`Комната ${sessionId} готова к звонку`);
                }
                return;
            }

            // Пересылка WebRTC сообщений другим участникам
            if (type === 'offer' || type === 'answer' || type === 'ice-candidate') {
                // Проверка готовности комнаты
                if (room.clients.length < 2) {
                    socket.send(JSON.stringify({
                        type: 'error',
                        message: 'Второй участник не подключен'
                    }));
                    return;
                }

                // Пересылка сообщения всем другим клиентам в комнате
                room.clients.forEach(client => {
                    if (client !== socket && client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify({
                            type: type,
                            payload: payload
                        }));
                        console.log(`Сообщение ${type} переслано участнику в комнате ${sessionId}`);
                    }
                });
            }
        } catch (error) {
            console.error('Ошибка обработки сообщения:', error);
            socket.send(JSON.stringify({ 
                type: 'error', 
                message: 'Ошибка обработки сообщения' 
            }));
        }
    });

    // Обработчик закрытия соединения
    socket.on('close', () => {
        console.log('Клиент отключен');
        
        // Удаление клиента из всех комнат
        Object.entries(rooms).forEach(([sessionId, room]) => {
            const initialCount = room.clients.length;
            
            room.clients = room.clients.filter(client => client !== socket);
            
            // Удаление пустых комнат
            if (room.clients.length === 0) {
                delete rooms[sessionId];
                console.log(`Комната ${sessionId} удалена (пустая)`);
            } 
            // Уведомление оставшегося участника
            else if (initialCount === 2 && room.clients.length === 1) {
                room.clients[0].send(JSON.stringify({
                    type: 'peer-disconnected'
                }));
                console.log(`Участник в комнате ${sessionId} уведомлен об отключении`);
            }
        });
    });

    // Обработчик ошибок соединения
    socket.on('error', (error) => {
        console.error('Ошибка WebSocket:', error);
    });
});

console.log('WebSocket-сервер запущен на ws://localhost:8080');