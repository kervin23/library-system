<template>
  <v-app>
    <v-app-bar color="primary" elevation="2">
      <v-app-bar-title>Library System</v-app-bar-title>
      <v-btn icon>
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
import { ref } from 'vue'

const books = ref([
  { id: 1, title: 'book1', author: 'author 1' },
  { id: 2, title: 'book2', author: 'author 2' },
  { id: 3, title: 'book3', author: 'author 3' },
])
</script>