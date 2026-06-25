<template>
  <v-app>
    <v-app-bar color="green-darken-2" elevation="2">
      <v-app-bar-title>Library System - Manager Panel</v-app-bar-title>
      <span class="mr-4">Welcome, {{ user?.username }}!</span>
      <v-btn icon @click="logout">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <v-container>
        <v-row>
          <v-col cols="12">
            <h2 class="mb-4">Manager Dashboard</h2>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" sm="6">
            <v-card color="blue" theme="dark" class="pa-4 text-center">
              <v-icon size="40">mdi-book-multiple</v-icon>
              <h3 class="mt-2">Manage Books</h3>
            </v-card>
          </v-col>
          <v-col cols="12" sm="6">
            <v-card color="orange" theme="dark" class="pa-4 text-center">
              <v-icon size="40">mdi-clipboard-list</v-icon>
              <h3 class="mt-2">Borrowing Records</h3>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/')
}
</script>