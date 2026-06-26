<template>
  <v-app>
    <v-app-bar color="grey-darken-4" elevation="2" style="z-index: 100;">
      <v-btn icon @click="drawer = !drawer">
        <v-icon>mdi-menu</v-icon>
      </v-btn>
      <v-app-bar-title>📚 Library System</v-app-bar-title>
      <v-spacer/>
      <span class="mr-4">Welcome, {{ user?.username }}!</span>
      <v-btn icon @click="logout">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <!-- SIDEBAR -->
    <v-navigation-drawer v-model="drawer" temporary color="grey-darken-4">
      <v-list-item
        prepend-icon="mdi-account-circle"
        :title="user?.username"
        :subtitle="user?.role"
        class="py-4 text-white"
      />
      <v-divider/>
      <v-list density="compact" nav>
        <v-list-item
          prepend-icon="mdi-home"
          title="Home"
          value="home"
          rounded="lg"
          class="sidebar-item"
          @click="currentPage = 'home'"
        />
        <v-list-item
          prepend-icon="mdi-book-open"
          title="My Borrowings"
          value="borrowings"
          rounded="lg"
          class="sidebar-item"
          @click="currentPage = 'borrowings'"
        />
        <v-list-item
          prepend-icon="mdi-history"
          title="History"
          value="history"
          rounded="lg"
          class="sidebar-item"
          @click="currentPage = 'history'"
        />
      </v-list>
      <template #append>
        <v-divider/>
        <v-list density="compact" nav>
          <v-list-item
            prepend-icon="mdi-logout"
            title="Logout"
            rounded="lg"
            class="sidebar-item text-red"
            @click="logout"
          />
        </v-list>
      </template>
    </v-navigation-drawer>

    <v-main>
      <v-container fluid class="pa-6">

        <!-- HOME PAGE -->
        <div v-if="currentPage === 'home'">
          <h2 class="mb-6">Available Books</h2>

          <div class="scroll-container mb-6" ref="scrollEl">
            <v-slide-group show-arrows>
              <v-slide-group-item v-for="book in books" :key="book.id">
                <v-card
                  elevation="2"
                  rounded="lg"
                  width="160"
                  class="mr-4 book-card"
                  @click="viewBook(book)"
                >
                  <v-img
                    :src="book.coverUrl ?? 'https://placehold.co/160x220?text=No+Cover'"
                    height="220"
                    cover
                    @error="(e: any) => e.target.src = 'https://placehold.co/160x220?text=No+Cover'"
                  />
                  <v-card-text class="pa-2">
                    <div style="font-size: 0.8rem; font-weight: 600; line-height: 1.2" class="mb-1">
                      {{ book.title }}
                    </div>
                    <div style="font-size: 0.75rem; opacity: 0.6">{{ book.author }}</div>
                  </v-card-text>
                  <v-card-actions class="pa-2 pt-0">
                    <v-btn
                      color="primary"
                      variant="tonal"
                      size="x-small"
                      @click.stop="viewBook(book)"
                    >
                      View
                    </v-btn>
                    <v-btn
                      color="green"
                      variant="tonal"
                      size="x-small"
                      @click.stop="borrowBook(book.id)"
                    >
                      Borrow
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-slide-group-item>
            </v-slide-group>
          </div>
        </div>

        <!-- MY BORROWINGS PAGE -->
        <div v-if="currentPage === 'borrowings'">
          <h2 class="mb-6">My Borrowings</h2>
          <v-table>
            <thead>
              <tr>
                <th>Book</th>
                <th>Borrowed</th>
                <th>Due Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="myBorrowings.length === 0">
                <td colspan="4" class="text-center pa-4">No borrowings yet</td>
              </tr>
              <tr v-for="b in myBorrowings.filter((b: any) => b.status !== 'returned')" :key="b.id">
                <td>{{ b.book.title }}</td>
                <td>{{ formatDate(b.borrowDate) }}</td>
                <td>{{ formatDate(b.dueDate) }}</td>
                <td>
                  <v-chip
                    :color="b.status === 'approved' ? 'green' : b.status === 'pending' ? 'orange' : 'red'"
                    size="small"
                  >
                    {{ b.status }}
                  </v-chip>
                </td>
              </tr>
            </tbody>
          </v-table>
        </div>

        <!-- HISTORY PAGE -->
        <div v-if="currentPage === 'history'">
          <h2 class="mb-6">Borrowing History</h2>
          <v-table>
            <thead>
              <tr>
                <th>Book</th>
                <th>Borrowed</th>
                <th>Returned</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="myBorrowings.filter((b: any) => b.status === 'returned').length === 0">
                <td colspan="4" class="text-center pa-4">No history yet</td>
              </tr>
              <tr v-for="b in myBorrowings.filter((b: any) => b.status === 'returned')" :key="b.id">
                <td>{{ b.book.title }}</td>
                <td>{{ formatDate(b.borrowDate) }}</td>
                <td>{{ formatDate(b.returnDate) }}</td>
                <td>
                  <v-chip color="grey" size="small">returned</v-chip>
                </td>
              </tr>
            </tbody>
          </v-table>
        </div>

      </v-container>
    </v-main>

    <!-- BOOK DETAIL DIALOG -->
    <v-dialog v-model="bookDialog" max-width="500">
      <v-card rounded="lg" v-if="selectedBook">
        <v-img
          :src="selectedBook.coverUrl ?? 'https://placehold.co/500x300?text=No+Cover'"
          height="250"
          cover
          @error="(e: any) => e.target.src = 'https://placehold.co/500x300?text=No+Cover'"
        />
        <v-card-title class="mt-2">{{ selectedBook.title }}</v-card-title>
        <v-card-subtitle>by {{ selectedBook.author }}</v-card-subtitle>
        <v-card-text>
          <v-row dense>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">Category</div>
              <div>{{ selectedBook.category ?? 'N/A' }}</div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">ISBN</div>
              <div>{{ selectedBook.isbn ?? 'N/A' }}</div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">Available</div>
              <div>{{ selectedBook.quantity }} copies</div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">Status</div>
              <v-chip
                :color="selectedBook.status === 'available' ? 'green' : 'red'"
                size="small"
              >
                {{ selectedBook.status }}
              </v-chip>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-btn variant="text" @click="bookDialog = false">Close</v-btn>
          <v-spacer/>
          <v-btn
            color="green"
            variant="tonal"
            :disabled="selectedBook.status !== 'available'"
            @click="borrowBook(selectedBook.id); bookDialog = false"
          >
            Borrow
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const books = ref<any[]>([])
const myBorrowings = ref<any[]>([])
const drawer = ref(false)
const currentPage = ref('home')
const bookDialog = ref(false)
const selectedBook = ref<any>(null)
const scrollEl = ref<HTMLElement | null>(null)

const fetchBooks = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/books')
    books.value = await response.json()
  } catch (e) {
    console.error('Cannot fetch books')
  }
}

const fetchMyBorrowings = async () => {
  try {
    const response = await fetch(`http://localhost:8080/api/borrowings/user/${user.id}`)
    myBorrowings.value = await response.json()
  } catch (e) {
    console.error('Cannot fetch borrowings')
  }
}

const viewBook = (book: any) => {
  selectedBook.value = book
  bookDialog.value = true
}

const borrowBook = async (bookId: number) => {
  try {
    const response = await fetch('http://localhost:8080/api/borrowings/request', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: user.id, bookId: bookId })
    })
    if (response.ok) {
      alert('Borrow request submitted! Wait for manager approval.')
      fetchMyBorrowings()
    } else {
      const msg = await response.text()
      alert(msg)
    }
  } catch (e) {
    alert('Cannot connect to server')
  }
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('en-PH', {
    year: 'numeric', month: 'short', day: 'numeric'
  })
}

onMounted(() => {
  fetchBooks()
  fetchMyBorrowings()

  setTimeout(() => {
    const slider = document.querySelector('.v-slide-group__content')
    if (slider) {
      slider.addEventListener('wheel', (e: any) => {
        e.preventDefault()
        slider.scrollLeft += e.deltaY
      })
    }
  }, 500)
})

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/')
}
</script>

<style scoped>
.scroll-container :deep(.v-slide-group__content) {
  overflow-x: auto !important;
  scrollbar-width: thin;
  padding-bottom: 8px;
}

.scroll-container :deep(.v-slide-group__content)::-webkit-scrollbar {
  height: 4px;
}

.scroll-container :deep(.v-slide-group__content)::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.2);
  border-radius: 4px;
}

.book-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.2) !important;
}

.sidebar-item {
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.sidebar-item:hover {
  background: rgba(255,255,255,0.08) !important;
  box-shadow: 0 0 12px rgba(255,255,255,0.1) !important;
}
</style>