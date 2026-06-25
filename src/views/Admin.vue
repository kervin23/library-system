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
          <v-tab value="managebooks">Manage Books</v-tab>
          <v-tab value="staffmanagement">Manage Staff</v-tab>
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

          <!-- MANAGE BOOKS TAB -->
          <v-window-item value="managebooks">
            <v-row class="mb-4" align="center">
              <v-col><h3>Books in Library</h3></v-col>
              <v-col cols="auto">
                <v-btn color="green" prepend-icon="mdi-plus" @click="openBookDialog">
                  Add Book
                </v-btn>
              </v-col>
            </v-row>
            <v-table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Author</th>
                  <th>Category</th>
                  <th>Quantity</th>
                  <th>Status</th>
                  <th></th>
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
                    <v-menu>
                      <template #activator="{ props }">
                        <v-btn icon v-bind="props" variant="text">
                          <v-icon>mdi-dots-vertical</v-icon>
                        </v-btn>
                      </template>
                      <v-list>
                        <v-list-item @click="addQuantity(book)">
                          <v-list-item-title>Add Quantity</v-list-item-title>
                        </v-list-item>
                        <v-list-item @click="removeQuantity(book)">
                          <v-list-item-title>Remove Quantity</v-list-item-title>
                        </v-list-item>
                        <v-list-item @click="deleteBook(book.id)">
                          <v-list-item-title class="text-red">Delete</v-list-item-title>
                        </v-list-item>
                      </v-list>
                    </v-menu>
                  </td>
                </tr>
              </tbody>
            </v-table>
          </v-window-item>

          <!-- STAFF MANAGEMENT TAB -->
          <v-window-item value="staffmanagement">
            <v-row class="mb-4" align="center">
              <v-col><h3>Staff Members</h3></v-col>
              <v-col cols="auto">
                <v-btn color="green" prepend-icon="mdi-plus" @click="staffDialog = true">
                  Add Staff
                </v-btn>
              </v-col>
            </v-row>
            <v-table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="staff in staffList" :key="staff.id">
                  <td>{{ staff.id }}</td>
                  <td>{{ staff.username }}</td>
                  <td>{{ staff.email }}</td>
                  <td>{{ staff.role }}</td>
                  <td>
                    <v-menu>
                      <template #activator="{ props }">
                        <v-btn icon v-bind="props" variant="text">
                          <v-icon>mdi-dots-vertical</v-icon>
                        </v-btn>
                      </template>
                      <v-list>
                        <v-list-item @click="demoteToUser(staff.id)">
                          <v-list-item-title class="text-red">Remove Manager</v-list-item-title>
                        </v-list-item>
                      </v-list>
                    </v-menu>
                  </td>
                </tr>
              </tbody>
            </v-table>
          </v-window-item>

        </v-window>
      </v-container>
    </v-main>

    <!-- ADD BOOK DIALOG -->
    <v-dialog v-model="bookDialog" max-width="900">
      <v-card>
        <v-card-title class="pa-4 d-flex justify-space-between align-center">
          <span>Add Book</span>
          <v-btn icon variant="text" @click="bookDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>

          <!-- CUSTOM BOOK FORM -->
          <h4 class="mb-3">Add Custom Book</h4>
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field v-model="customBook.title" label="Title" variant="outlined"/>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="customBook.author" label="Author" variant="outlined"/>
            </v-col>
            <v-col cols="12" sm="4">
              <v-text-field v-model="customBook.isbn" label="ISBN (optional)" variant="outlined"/>
            </v-col>
            <v-col cols="12" sm="4">
              <v-select v-model="customBook.category" :items="categories" label="Category" variant="outlined"/>
            </v-col>
            <v-col cols="12" sm="4">
              <v-text-field v-model="customBook.quantity" label="Quantity" type="number" variant="outlined"/>
            </v-col>
            <v-col cols="12">
              <v-btn color="primary" prepend-icon="mdi-plus" @click="addCustomBook">
                Add Custom Book
              </v-btn>
            </v-col>
          </v-row>

          <v-divider class="my-4"/>

          <!-- OPEN LIBRARY SEARCH -->
          <h4 class="mb-3">Search from Open Library</h4>
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="searchQuery"
                label="Search by title or author"
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                @keyup.enter="searchBooks"
              />
            </v-col>
            <v-col cols="12" sm="4">
              <v-select
                v-model="selectedCategory"
                :items="categories"
                label="Category"
                variant="outlined"
              />
            </v-col>
            <v-col cols="12" sm="2" class="d-flex align-center">
              <v-btn color="primary" block @click="searchBooks">Search</v-btn>
            </v-col>
          </v-row>

          <v-row class="mt-2">
            <v-col cols="12" sm="6" md="4" v-for="book in searchResults" :key="book.key">
              <v-card elevation="2" rounded="lg">
                <v-img
                  :src="book.cover_i ? `https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg` : 'https://placehold.co/300x200?text=No+Cover'"
                  height="150"
                  cover
                  @error="(e: any) => e.target.src = 'https://placehold.co/300x200?text=No+Cover'"
                />
                <v-card-title style="font-size: 0.9rem">{{ book.title }}</v-card-title>
                <v-card-subtitle>{{ book.author_name?.[0] ?? 'Unknown Author' }}</v-card-subtitle>
                <v-card-actions>
                  <v-btn color="green" variant="tonal" size="small" @click="addToLibrary(book)">
                    Add to Library
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>

          <v-row justify="center" class="mt-4" v-if="searchResults.length > 0">
            <v-btn color="primary" @click="loadMore">Load More</v-btn>
          </v-row>

        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- ADD STAFF DIALOG -->
    <v-dialog v-model="staffDialog" max-width="600">
      <v-card>
        <v-card-title class="pa-4 d-flex justify-space-between align-center">
          <span>Promote User to Manager</span>
          <v-btn icon variant="text" @click="staffDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="u in userList" :key="u.id">
                <td>{{ u.username }}</td>
                <td>{{ u.email }}</td>
                <td>{{ u.role }}</td>
                <td>
                  <v-btn
                    color="orange"
                    size="small"
                    @click="promoteToManager(u.id)"
                    :disabled="u.role === 'manager' || u.role === 'admin'"
                  >
                    Promote
                  </v-btn>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
      </v-card>
    </v-dialog>

  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const tab = ref('dashboard')
const bookDialog = ref(false)
const staffDialog = ref(false)
const searchQuery = ref('')
const selectedCategory = ref('all')
const searchResults = ref([])
const libraryBooks = ref([])
const staffList = ref([])
const userList = ref([])
const currentPage = ref(1)

const categories = ['all', 'fiction', 'history', 'education', 'science', 'technology', 'biography']

const customBook = ref({
  title: '',
  author: '',
  isbn: '',
  category: 'all',
  quantity: 1
})

const openBookDialog = () => {
  bookDialog.value = true
  searchBooks()
}

const searchBooks = async () => {
  currentPage.value = 1
  const subjectQuery = selectedCategory.value === 'all' ? '' : `&subject=${selectedCategory.value}`
  const q = searchQuery.value || 'library'
  const response = await fetch(
    `https://openlibrary.org/search.json?q=${q}${subjectQuery}&limit=12&page=1`
  )
  const data = await response.json()
  searchResults.value = data.docs
}

const loadMore = async () => {
  currentPage.value++
  const subjectQuery = selectedCategory.value === 'all' ? '' : `&subject=${selectedCategory.value}`
  const q = searchQuery.value || 'library'
  const response = await fetch(
    `https://openlibrary.org/search.json?q=${q}${subjectQuery}&limit=12&page=${currentPage.value}`
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
      category: selectedCategory.value === 'all' ? null : selectedCategory.value,
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

const addCustomBook = async () => {
  if (!customBook.value.title || !customBook.value.author) {
    alert('Title and Author are required!')
    return
  }
  const response = await fetch('http://localhost:8080/api/books', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      title: customBook.value.title,
      author: customBook.value.author,
      isbn: customBook.value.isbn || null,
      category: customBook.value.category === 'all' ? null : customBook.value.category,
      quantity: customBook.value.quantity,
      status: 'available'
    })
  })
  if (response.ok) {
    alert('Custom book added!')
    customBook.value = { title: '', author: '', isbn: '', category: 'all', quantity: 1 }
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

const addQuantity = async (book: any) => {
  await fetch(`http://localhost:8080/api/books/${book.id}/quantity`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ quantity: book.quantity + 1 })
  })
  fetchLibraryBooks()
}

const removeQuantity = async (book: any) => {
  if (book.quantity <= 0) return
  await fetch(`http://localhost:8080/api/books/${book.id}/quantity`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ quantity: book.quantity - 1 })
  })
  fetchLibraryBooks()
}

const fetchStaff = async () => {
  const response = await fetch('http://localhost:8080/api/users')
  const all = await response.json()
  staffList.value = all.filter((u: any) => u.role === 'manager')
  userList.value = all
}

const promoteToManager = async (id: number) => {
  await fetch(`http://localhost:8080/api/users/${id}/promote`, { method: 'PUT' })
  fetchStaff()
}

const demoteToUser = async (id: number) => {
  await fetch(`http://localhost:8080/api/users/${id}/demote`, { method: 'PUT' })
  fetchStaff()
}

onMounted(() => {
  fetchLibraryBooks()
  fetchStaff()
})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/')
}
</script>