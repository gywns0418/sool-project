import { Client } from '@stomp/stompjs'

let stompClient = null

export const connectChatSocket = (onConnect) => {

    stompClient = new Client({

        brokerURL: 'ws://localhost:8081/ws-chat',

        reconnectDelay: 3000,

        onConnect: () => {

            console.log('채팅 소켓 연결 완료')

            if (onConnect) {
                onConnect()
            }
        },

        onStompError: (frame) => {
            console.error(frame)
        }
    })

    stompClient.activate()
}

export const subscribeRoom = (roomId, callback) => {

    if (!stompClient || !stompClient.connected) {
        return
    }

    stompClient.subscribe(
        `/topic/chat/rooms/${roomId}`,
        (message) => {
            callback(JSON.parse(message.body))
        }
    )
}

export const sendChatMessage = (payload) => {

    if (!stompClient || !stompClient.connected) {
        return
    }

    stompClient.publish({
        destination: '/app/chat/message',
        body: JSON.stringify(payload)
    })
}

export const disconnectChatSocket = () => {

    if (stompClient) {
        stompClient.deactivate()
    }
}