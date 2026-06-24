<template>
  <v-app>
    <v-app-bar color="primary" elevation="2">
      <v-app-bar-title>Library System</v-app-bar-title>
      <span class="mr-4">Welcome, {{ user?.username }}!</span>
      <v-btn icon @click="logout">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <v-container>
        <v-row>
          <v-col cols="12">
            <h2 class="mb-4">Available Books</h2>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" sm="6" md="4"
            v-for="book in books" :key="book.id">
            <v-card elevation="2" rounded="lg">
              <v-card-title>{{ book.title }}</v-card-title>
              <v-card-subtitle>{{ book.author }}</v-card-subtitle>
              <v-card-actions>
                <v-btn color="primary" variant="tonal">
                  Borrow
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const books = ref([])

const fetchBooks = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/books')
    books.value = await response.json()
  } catch (e) {
    console.error('Cannot fetch books')
  }
}

onMounted(() => {
  fetchBooks()
})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/')
}
</script>