<template>
  <v-app>
    <v-app-bar color="red-darken-2" elevation="2">
      <v-app-bar-title>Library System - Admin Panel</v-app-bar-title>
      <span class="mr-4">Welcome, {{ user?.username }}!</span>
      <v-btn icon @click="logout">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <v-container>
        <v-tabs v-model="tab" class="mb-4">
          <v-tab value="dashboard">Dashboard</v-tab>
          <v-tab value="addbook">Add Book</v-tab>
          <v-tab value="managebooks">Manage Books</v-tab>
        </v-tabs>

        <v-window v-model="tab">

          <!-- DASHBOARD TAB -->
          <v-window-item value="dashboard">
            <v-row>
              <v-col cols="12" sm="4">
                <v-card color="blue" theme="dark" class="pa-4 text-center">
                  <v-icon size="40">mdi-account-group</v-icon>
                  <h3 class="mt-2">Manage Users</h3>
                </v-card>
              </v-col>
              <v-col cols="12" sm="4">
                <v-card color="green" theme="dark" class="pa-4 text-center">
                  <v-icon size="40">mdi-book-multiple</v-icon>
                  <h3 class="mt-2">Manage Books</h3>
                </v-card>
              </v-col>
              <v-col cols="12" sm="4">
                <v-card color="orange" theme="dark" class="pa-4 text-center">
                  <v-icon size="40">mdi-clipboard-list</v-icon>
                  <h3 class="mt-2">Borrowing Records</h3>
                </v-card>
              </v-col>
            </v-row>
          </v-window-item>

          <!-- ADD BOOK TAB -->
          <v-window-item value="addbook">
            <v-row>
              <v-col cols="12">
                <h3 class="mb-4">Search Books from Open Library</h3>
                <v-text-field
                  v-model="searchQuery"
                  label="Search by title or author"
                  prepend-inner-icon="mdi-magnify"
                  variant="outlined"
                  @keyup.enter="searchBooks"
                />
                <v-select
                  v-model="selectedCategory"
                  :items="categories"
                  label="Category"
                  variant="outlined"
                  class="mb-3"
                />
                <v-btn color="primary" @click="searchBooks">Search</v-btn>
              </v-col>
            </v-row>

            <v-row class="mt-4">
              <v-col cols="12" sm="6" md="4" v-for="book in searchResults" :key="book.key">
                <v-card elevation="2" rounded="lg">
                  <v-img
                    v-if="book.cover_i"
                    :src="`https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg`"
                    height="200"
                    cover
                  />
                  <v-card-title>{{ book.title }}</v-card-title>
                  <v-card-subtitle>{{ book.author_name?.[0] ?? 'Unknown Author' }}</v-card-subtitle>
                  <v-card-actions>
                    <v-btn color="green" variant="tonal" @click="addToLibrary(book)">
                      Add to Library
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-col>
            </v-row>

            <v-row justify="center" class="mt-4" v-if="searchResults.length > 0">
              <v-btn color="primary" @click="loadMore">Load More</v-btn>
            </v-row>

          </v-window-item>

          <!-- MANAGE BOOKS TAB -->
          <v-window-item value="managebooks">
            <h3 class="mb-4">Books in Library</h3>
            <v-table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Author</th>
                  <th>Category</th>
                  <th>Quantity</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="book in libraryBooks" :key="book.id">
                  <td>{{ book.title }}</td>
                  <td>{{ book.author }}</td>
                  <td>{{ book.category }}</td>
                  <td>{{ book.quantity }}</td>
                  <td>{{ book.status }}</td>
                  <td>
                    <v-btn color="red" size="small" @click="deleteBook(book.id)">Delete</v-btn>
                  </td>
                </tr>
              </tbody>
            </v-table>
          </v-window-item>

        </v-window>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const tab = ref('dashboard')
const searchQuery = ref('')
const selectedCategory = ref('fiction')
const searchResults = ref([])
const libraryBooks = ref([])
const currentPage = ref(1)

const categories = ['fiction', 'history', 'education', 'science', 'technology', 'biography']

const searchBooks = async () => {
  currentPage.value = 1
  const response = await fetch(
    `https://openlibrary.org/search.json?q=${searchQuery.value}&subject=${selectedCategory.value}&limit=12&page=1`
  )
  const data = await response.json()
  searchResults.value = data.docs
}

const loadMore = async () => {
  currentPage.value++
  const response = await fetch(
    `https://openlibrary.org/search.json?q=${searchQuery.value}&subject=${selectedCategory.value}&limit=12&page=${currentPage.value}`
  )
  const data = await response.json()
  searchResults.value = [...searchResults.value, ...data.docs]
}

const addToLibrary = async (book: any) => {
  const response = await fetch('http://localhost:8080/api/books', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      title: book.title,
      author: book.author_name?.[0] ?? 'Unknown',
      isbn: book.isbn?.[0] ?? null,
      category: selectedCategory.value,
      coverUrl: book.cover_i ? `https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg` : null,
      openLibraryId: book.key,
      quantity: 1
    })
  })
  if (response.ok) {
    alert('Book added to library!')
    fetchLibraryBooks()
  }
}

const fetchLibraryBooks = async () => {
  const response = await fetch('http://localhost:8080/api/books')
  libraryBooks.value = await response.json()
}

const deleteBook = async (id: number) => {
  await fetch(`http://localhost:8080/api/books/${id}`, { method: 'DELETE' })
  fetchLibraryBooks()
}

onMounted(() => {
  fetchLibraryBooks()
})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/')
}
</script>