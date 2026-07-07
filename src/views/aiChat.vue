<template>
    <div class="ai-chat">
        <h1>AI Chat</h1>
        <div class="chat-window">
        <div v-for="(message, index) in messages" :key="index" class="message">
            <span class="user">{{ message.user }}:</span>
            <span class="text">{{ message.text }}</span>
        </div>
        </div>
        <input v-model="newMessage" @keyup.enter="sendMessage" placeholder="Type a message..." />
        <button @click="sendMessage">Send</button>
    </div>
</template>
