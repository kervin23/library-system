<template>
  <v-app class="lib-app">

    <!-- SIDEBAR -->
    <v-navigation-drawer v-model="drawer" :rail="rail" permanent color="#1A1A2E" width="240">
      <div class="sidebar-brand" :class="{ 'rail-brand': rail }">
        <v-icon color="#C8933A" :size="rail ? 28 : 32">mdi-shield-crown-outline</v-icon>
        <Transition name="fade">
          <div v-if="!rail" class="brand-text">
            <span class="brand-name">Library</span>
            <span class="brand-role">Admin Panel</span>
          </div>
        </Transition>
      </div>

      <v-divider color="rgba(200,147,58,0.2)" class="mx-3"/>

      <div class="user-chip" v-if="!rail">
        <v-avatar size="36" color="#C8933A" class="mr-3">
          <v-icon size="18" color="#FAF6EF">mdi-shield-account</v-icon>
        </v-avatar>
        <div>
          <div class="user-name">{{ user?.username }}</div>
          <div class="user-role">Administrator</div>
        </div>
      </div>

      <v-list nav density="compact" class="mt-2">
        <v-list-item
          v-for="item in navItems" :key="item.value"
          :prepend-icon="item.icon"
          :title="rail ? '' : item.label"
          rounded="lg" class="nav-item"
          :class="{ 'nav-active': currentPage === item.value }"
          @click="currentPage = item.value"
        />
      </v-list>

      <template #append>
        <v-divider color="rgba(200,147,58,0.2)" class="mx-3 mb-2"/>
        <v-list nav density="compact">
          <v-list-item
            :prepend-icon="rail ? 'mdi-chevron-right-circle-outline' : 'mdi-chevron-left-circle-outline'"
            :title="rail ? '' : 'Collapse'" rounded="lg" class="nav-item" @click="rail = !rail"
          />
          <v-list-item prepend-icon="mdi-logout" :title="rail ? '' : 'Sign Out'" rounded="lg" class="nav-item nav-danger" @click="logout"/>
        </v-list>
      </template>
    </v-navigation-drawer>

    <v-main class="main-content">
      <div class="topbar">
        <div>
          <h2 class="page-title">{{ navItems.find(n => n.value === currentPage)?.label ?? 'Dashboard' }}</h2>
          <p class="page-sub">{{ navItems.find(n => n.value === currentPage)?.sub ?? '' }}</p>
        </div>
        <div class="topbar-right">
          <v-btn icon variant="text" class="icon-btn" @click="drawer = !drawer; rail = false"><v-icon>mdi-menu</v-icon></v-btn>
          <v-avatar size="36" color="#C8933A"><v-icon size="18" color="#FAF6EF">mdi-shield-account</v-icon></v-avatar>
        </div>
      </div>

      <div class="page-body">

        <!-- ═══════════════ DASHBOARD ═══════════════ -->
        <div v-if="currentPage === 'dashboard'">
          <!-- STAT CARDS -->
          <v-row class="mb-6">
            <v-col cols="12" sm="6" md="6" v-for="s in statCards" :key="s.label">
              <div class="stat-card" :style="{ '--accent': s.color }" @click="s.action && s.action()">
                <div class="stat-icon-wrap"><v-icon :icon="s.icon" size="28" :color="s.color"/></div>
                <div class="stat-body">
                  <span class="stat-value">{{ s.value }}</span>
                  <span class="stat-label">{{ s.label }}</span>
                </div>
                <v-icon v-if="s.action" size="16" color="rgba(255,255,255,0.3)" class="stat-arrow">mdi-arrow-top-right</v-icon>
              </div>
            </v-col>
          </v-row>

          <!-- TWO-COL BELOW CARDS -->
          <v-row>
            <!-- RECENT BORROWINGS TABLE -->
            <v-col cols="12" md="7">
              <div class="section-card">
                <div class="section-card-header">
                  <span class="section-card-title">Recent Borrowing Activity</span>
                  <v-btn size="x-small" variant="text" class="view-all-btn" @click="currentPage = 'borrowings'">View all</v-btn>
                </div>
                <table class="lib-table">
                  <thead>
                    <tr>
                      <th>User</th>
                      <th>Book</th>
                      <th>Status</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="recentActivity.length === 0">
                      <td colspan="4" class="td-empty">No activity yet</td>
                    </tr>
                    <tr v-for="b in recentActivity" :key="b.id">
                      <td>{{ b.user?.username }}</td>
                      <td class="td-book">{{ b.book?.title }}</td>
                      <td>
                        <v-chip size="x-small" :color="statusColor(b.status)">{{ b.status }}</v-chip>
                      </td>
                      <td>{{ formatDate(b.borrowDate) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </v-col>

            <!-- OVERDUE ALERT LIST -->
            <v-col cols="12" md="5">
              <div class="section-card">
                <div class="section-card-header">
                  <span class="section-card-title">Overdue Books</span>
                  <v-chip size="x-small" color="#C62828" v-if="overdueList.length > 0">{{ overdueList.length }}</v-chip>
                </div>
                <div v-if="overdueList.length === 0" class="mini-empty">
                  <v-icon color="#52A77A">mdi-check-circle-outline</v-icon>
                  <span>No overdue books</span>
                </div>
                <div v-else class="overdue-list">
                  <div v-for="b in overdueList" :key="b.id" class="overdue-item">
                    <div>
                      <p class="overdue-user">{{ b.user?.username }}</p>
                      <p class="overdue-book">{{ b.book?.title }}</p>
                    </div>
                    <div class="overdue-days">
                      {{ daysOverdue(b.dueDate) }}d overdue
                    </div>
                  </div>
                </div>
              </div>
            </v-col>
          </v-row>
        </div>

        <!-- ═══════════════ USERS ═══════════════ -->
        <div v-if="currentPage === 'users'">
          <div class="table-toolbar mb-4">
            <v-text-field v-model="userSearch" placeholder="Search users..." prepend-inner-icon="mdi-magnify" variant="outlined" rounded="lg" hide-details density="compact" style="max-width:280px" class="dark-field"/>
          </div>
          <div class="section-card">
            <table class="lib-table">
              <thead>
                <tr>
                  <th>User</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Borrowings</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="u in filteredUsers" :key="u.id" class="clickable-row" @click="openUserDetail(u)">
                  <td>
                    <div class="td-user">
                      <v-avatar size="30" :color="roleColor(u.role)">
                        <span style="font-size:0.7rem;font-weight:700;color:#FAF6EF">{{ u.username[0].toUpperCase() }}</span>
                      </v-avatar>
                      {{ u.username }}
                    </div>
                  </td>
                  <td>{{ u.email }}</td>
                  <td><v-chip size="x-small" :color="roleColor(u.role)">{{ u.role }}</v-chip></td>
                  <td>{{ userBorrowCount(u.id) }}</td>
                  <td><v-icon size="16" color="rgba(200,147,58,0.6)">mdi-chevron-right</v-icon></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- ═══════════════ BOOKS ═══════════════ -->
        <div v-if="currentPage === 'books'">
          <div class="table-toolbar mb-4">
            <v-text-field v-model="bookSearch" placeholder="Search books..." prepend-inner-icon="mdi-magnify" variant="outlined" rounded="lg" hide-details density="compact" style="max-width:280px" class="dark-field"/>
            <v-btn class="add-btn" prepend-icon="mdi-plus" @click="bookDialog = true; searchBooks()">Add Book</v-btn>
          </div>
          <div class="section-card">
            <table class="lib-table">
              <thead>
                <tr>
                  <th>Book</th>
                  <th>Author</th>
                  <th>Category</th>
                  <th>Qty</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="book in filteredBooks" :key="book.id">
                  <td>
                    <div class="td-book-row">
                      <img
                        :src="book.coverUrl ?? 'https://placehold.co/36x48/5C3D1E/FAF6EF?text=📚'"
                        class="table-thumb"
                        @error="(e: any) => e.target.src = 'https://placehold.co/36x48/5C3D1E/FAF6EF?text=📚'"
                      />
                      <span>{{ book.title }}</span>
                    </div>
                  </td>
                  <td>{{ book.author }}</td>
                  <td>{{ book.category ?? '—' }}</td>
                  <td>{{ book.quantity }}</td>
                  <td><v-chip size="x-small" :color="book.status === 'available' ? '#2D6A4F' : '#C62828'">{{ book.status }}</v-chip></td>
                  <td>
                    <v-menu>
                      <template #activator="{ props }">
                        <v-btn icon v-bind="props" variant="text" size="small"><v-icon>mdi-dots-vertical</v-icon></v-btn>
                      </template>
                      <v-list density="compact" rounded="lg" class="action-menu">
                        <v-list-item @click="addQuantity(book)"><v-list-item-title>+ Add copy</v-list-item-title></v-list-item>
                        <v-list-item @click="removeQuantity(book)"><v-list-item-title>– Remove copy</v-list-item-title></v-list-item>
                        <v-divider/>
                        <v-list-item @click="deleteBook(book.id)"><v-list-item-title class="text-red">Delete</v-list-item-title></v-list-item>
                      </v-list>
                    </v-menu>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- ═══════════════ STAFF ═══════════════ -->
        <div v-if="currentPage === 'staff'">
          <div class="table-toolbar mb-4">
            <span class="table-hint">Managers who process borrow requests</span>
            <v-btn class="add-btn" prepend-icon="mdi-account-plus-outline" @click="promoteDialog = true">Promote User</v-btn>
          </div>
          <div class="section-card">
            <table class="lib-table">
              <thead>
                <tr><th>Manager</th><th>Email</th><th>Role</th><th></th></tr>
              </thead>
              <tbody>
                <tr v-if="staffList.length === 0">
                  <td colspan="4" class="td-empty">No managers yet. Promote a user to get started.</td>
                </tr>
                <tr v-for="s in staffList" :key="s.id">
                  <td>
                    <div class="td-user">
                      <v-avatar size="30" color="#52A77A">
                        <span style="font-size:0.7rem;font-weight:700;color:#fff">{{ s.username[0].toUpperCase() }}</span>
                      </v-avatar>
                      {{ s.username }}
                    </div>
                  </td>
                  <td>{{ s.email }}</td>
                  <td><v-chip size="x-small" color="#52A77A">Manager</v-chip></td>
                  <td>
                    <v-btn size="small" variant="outlined" color="#C62828" @click="demoteToUser(s.id)">Remove</v-btn>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- ═══════════════ ALL BORROWINGS ═══════════════ -->
        <div v-if="currentPage === 'borrowings'">
          <div class="section-card">
            <table class="lib-table">
              <thead>
                <tr><th>User</th><th>Book</th><th>Status</th><th>Borrowed</th><th>Due / Returned</th></tr>
              </thead>
              <tbody>
                <tr v-if="allBorrowings.length === 0"><td colspan="5" class="td-empty">No borrowing records</td></tr>
                <tr v-for="b in allBorrowings" :key="b.id" :class="{ 'row-overdue': isOverdue(b.dueDate) && b.status === 'approved' }">
                  <td>{{ b.user?.username }}</td>
                  <td class="td-book">{{ b.book?.title }}</td>
                  <td><v-chip size="x-small" :color="statusColor(b.status)">{{ b.status }}</v-chip></td>
                  <td>{{ formatDate(b.borrowDate) }}</td>
                  <td>
                    <span v-if="b.status === 'returned'">{{ formatDate(b.returnDate) }}</span>
                    <span v-else :class="{ 'text-danger': isOverdue(b.dueDate) }">{{ formatDate(b.dueDate) }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </v-main>

    <!-- ═══ USER DETAIL DRAWER ═══ -->
    <v-navigation-drawer v-model="userDetailOpen" location="right" width="420" color="#1A1A2E" temporary>
      <div v-if="selectedUser" class="user-detail-panel">
        <div class="detail-header">
          <v-btn icon variant="text" class="nav-item" @click="userDetailOpen = false">
            <v-icon color="rgba(245,236,215,0.6)">mdi-close</v-icon>
          </v-btn>
          <v-avatar size="64" :color="roleColor(selectedUser.role)" class="mb-3">
            <span class="detail-avatar-letter">{{ selectedUser.username[0].toUpperCase() }}</span>
          </v-avatar>
          <h3 class="detail-username">{{ selectedUser.username }}</h3>
          <p class="detail-email">{{ selectedUser.email }}</p>
          <v-chip size="small" :color="roleColor(selectedUser.role)" class="mt-1">{{ selectedUser.role }}</v-chip>
        </div>

        <div class="detail-section">
          <p class="detail-section-title">Current Borrowings</p>
          <div v-if="userActiveBorrowings.length === 0" class="detail-empty">
            <v-icon size="32" color="rgba(200,147,58,0.4)">mdi-book-outline</v-icon>
            <span>No active borrowings</span>
          </div>
          <div v-else class="detail-borrow-list">
            <div v-for="b in userActiveBorrowings" :key="b.id" class="detail-borrow-item">
              <img
                :src="b.book?.coverUrl ?? 'https://placehold.co/40x54/5C3D1E/FAF6EF?text=📚'"
                class="detail-thumb"
                @error="(e: any) => e.target.src = 'https://placehold.co/40x54/5C3D1E/FAF6EF?text=📚'"
              />
              <div>
                <p class="detail-book-title">{{ b.book.title }}</p>
                <p class="detail-book-meta">Due: {{ formatDate(b.dueDate) }}
                  <span v-if="isOverdue(b.dueDate)" class="overdue-pill">Overdue</span>
                </p>
              </div>
              <v-chip size="x-small" :color="statusColor(b.status)" class="ml-auto mt-1">{{ b.status }}</v-chip>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <p class="detail-section-title">Borrowing History</p>
          <div v-if="userHistoryBorrowings.length === 0" class="detail-empty">
            <v-icon size="32" color="rgba(200,147,58,0.4)">mdi-history</v-icon>
            <span>No history yet</span>
          </div>
          <div v-else class="detail-borrow-list">
            <div v-for="b in userHistoryBorrowings" :key="b.id" class="detail-borrow-item">
              <img
                :src="b.book?.coverUrl ?? 'https://placehold.co/40x54/5C3D1E/FAF6EF?text=📚'"
                class="detail-thumb grayscale"
                @error="(e: any) => e.target.src = 'https://placehold.co/40x54/5C3D1E/FAF6EF?text=📚'"
              />
              <div>
                <p class="detail-book-title">{{ b.book.title }}</p>
                <p class="detail-book-meta">{{ formatDate(b.borrowDate) }} → {{ formatDate(b.returnDate) }}</p>
              </div>
              <v-chip size="x-small" color="#6B7280" class="ml-auto mt-1">Returned</v-chip>
            </div>
          </div>
        </div>

        <div class="detail-actions" v-if="selectedUser.role === 'user'">
          <v-btn block class="promote-btn" @click="promoteToManager(selectedUser.id); userDetailOpen = false">
            <v-icon start>mdi-account-arrow-up-outline</v-icon>
            Promote to Manager
          </v-btn>
        </div>
        <div class="detail-actions" v-if="selectedUser.role === 'manager'">
          <v-btn block variant="outlined" color="#C62828" @click="demoteToUser(selectedUser.id); userDetailOpen = false">
            Remove Manager Role
          </v-btn>
        </div>
      </div>
    </v-navigation-drawer>

    <!-- ═══ ADD BOOK DIALOG ═══ -->
    <v-dialog v-model="bookDialog" max-width="920" scrollable>
      <v-card class="add-book-card">
        <v-card-title class="dialog-title-bar">
          <span>Add Books to Library</span>
          <v-btn icon variant="text" @click="bookDialog = false"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-card-text>
          <h4 class="form-section-title mb-3">Enter Book Manually</h4>
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field v-model="customBook.title" label="Title" variant="outlined" density="compact" class="dark-field"/>
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="customBook.author" label="Author" variant="outlined" density="compact" class="dark-field"/>
            </v-col>
            <v-col cols="12" sm="4">
              <v-text-field v-model="customBook.isbn" label="ISBN (optional)" variant="outlined" density="compact" class="dark-field"/>
            </v-col>
            <v-col cols="12" sm="4">
              <v-select v-model="customBook.category" :items="categories" label="Category" variant="outlined" density="compact" class="dark-field"/>
            </v-col>
            <v-col cols="12" sm="4">
              <v-text-field v-model="customBook.quantity" label="Quantity" type="number" variant="outlined" density="compact" class="dark-field"/>
            </v-col>
            <v-col cols="12">
              <v-btn class="add-btn" prepend-icon="mdi-plus" @click="addCustomBook">Add to Library</v-btn>
            </v-col>
          </v-row>

          <v-divider class="my-5" color="rgba(200,147,58,0.2)"/>

          <h4 class="form-section-title mb-3">Search Open Library</h4>
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field v-model="searchQuery" label="Search by title or author" prepend-inner-icon="mdi-magnify" variant="outlined" density="compact" class="dark-field" @keyup.enter="searchBooks"/>
            </v-col>
            <v-col cols="12" sm="4">
              <v-select v-model="selectedCategory" :items="categories" label="Category" variant="outlined" density="compact" class="dark-field"/>
            </v-col>
            <v-col cols="12" sm="2">
              <v-btn block class="add-btn" @click="searchBooks">Search</v-btn>
            </v-col>
          </v-row>

          <v-row class="mt-3">
            <v-col cols="12" sm="6" md="4" v-for="book in searchResults" :key="book.key">
              <v-card class="search-result-card" elevation="0">
                <v-img
                  :src="book.cover_i ? `https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg` : 'https://placehold.co/300x180/2C1A0E/FAF6EF?text=No+Cover'"
                  height="140" cover
                  @error="(e: any) => e.target.src = 'https://placehold.co/300x180/2C1A0E/FAF6EF?text=No+Cover'"
                />
                <div class="search-result-body">
                  <p class="search-title">{{ book.title }}</p>
                  <p class="search-author">{{ book.author_name?.[0] ?? 'Unknown' }}</p>
                  <v-btn size="x-small" class="add-btn mt-2" @click="addToLibrary(book)">+ Add</v-btn>
                </div>
              </v-card>
            </v-col>
          </v-row>

          <div class="text-center mt-4" v-if="searchResults.length > 0">
            <v-btn variant="outlined" class="load-more-btn" @click="loadMore">Load more results</v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- ═══ PROMOTE DIALOG ═══ -->
    <v-dialog v-model="promoteDialog" max-width="560">
      <v-card class="add-book-card">
        <v-card-title class="dialog-title-bar">
          <span>Promote User to Manager</span>
          <v-btn icon variant="text" @click="promoteDialog = false"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-card-text>
          <table class="lib-table">
            <thead>
              <tr><th>User</th><th>Email</th><th></th></tr>
            </thead>
            <tbody>
              <tr v-for="u in userList.filter(u => u.role === 'user')" :key="u.id">
                <td>{{ u.username }}</td>
                <td>{{ u.email }}</td>
                <td>
                  <v-btn size="small" class="add-btn" @click="promoteToManager(u.id); promoteDialog = false">Promote</v-btn>
                </td>
              </tr>
            </tbody>
          </table>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snack.show" :color="snack.color" rounded="lg" location="bottom right">{{ snack.text }}</v-snackbar>

  </v-app>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const currentPage = ref('dashboard')
const drawer = ref(true)
const rail = ref(false)
const snack = ref({ show: false, text: '', color: 'success' })

// Data
const libraryBooks = ref<any[]>([])
const allUsers = ref<any[]>([])
const staffList = ref<any[]>([])
const userList = ref<any[]>([])
const allBorrowings = ref<any[]>([])

// Dialogs
const bookDialog = ref(false)
const promoteDialog = ref(false)
const userDetailOpen = ref(false)
const selectedUser = ref<any>(null)

// Filters
const userSearch = ref('')
const bookSearch = ref('')

// Book search
const searchQuery = ref('')
const selectedCategory = ref('all')
const searchResults = ref<any[]>([])
const currentSearchPage = ref(1)
const categories = ['all','fiction','history','education','science','technology','biography']
const customBook = ref({ title: '', author: '', isbn: '', category: 'all', quantity: 1 })

const navItems = [
  { value: 'dashboard', icon: 'mdi-view-dashboard-outline', label: 'Dashboard', sub: 'Overview of your library system' },
  { value: 'users', icon: 'mdi-account-group-outline', label: 'Users', sub: 'Browse and inspect user accounts' },
  { value: 'books', icon: 'mdi-book-multiple-outline', label: 'Books', sub: 'Manage the library collection' },
  { value: 'staff', icon: 'mdi-account-tie-outline', label: 'Staff', sub: 'Manage managers and staff roles' },
  { value: 'borrowings', icon: 'mdi-clipboard-list-outline', label: 'All Borrowings', sub: 'Full borrowing record history' },
]

const recentActivity = computed(() => [...allBorrowings.value].sort((a, b) => new Date(b.borrowDate).getTime() - new Date(a.borrowDate).getTime()).slice(0, 6))
const overdueList = computed(() => allBorrowings.value.filter(b => b.status === 'approved' && isOverdue(b.dueDate)))
const filteredUsers = computed(() => allUsers.value.filter(u => u.username.toLowerCase().includes(userSearch.value.toLowerCase()) || u.email?.toLowerCase().includes(userSearch.value.toLowerCase())))
const filteredBooks = computed(() => libraryBooks.value.filter(b => b.title?.toLowerCase().includes(bookSearch.value.toLowerCase()) || b.author?.toLowerCase().includes(bookSearch.value.toLowerCase())))

const userBorrowCount = (userId: number) => allBorrowings.value.filter(b => b.user?.id === userId).length
const userActiveBorrowings = computed(() => selectedUser.value ? allBorrowings.value.filter(b => b.user?.id === selectedUser.value.id && b.status !== 'returned') : [])
const userHistoryBorrowings = computed(() => selectedUser.value ? allBorrowings.value.filter(b => b.user?.id === selectedUser.value.id && b.status === 'returned') : [])

const statCards = computed(() => [
  { label: 'Total Books', value: libraryBooks.value.length, icon: 'mdi-book-multiple-outline', color: '#C8933A', action: () => currentPage.value = 'books' },
  { label: 'Registered Users', value: allUsers.value.filter(u => u.role === 'user').length, icon: 'mdi-account-group-outline', color: '#5B8DEF', action: () => currentPage.value = 'users' },
  { label: 'Successful Transactions', value: allBorrowings.value.filter(b => b.status === 'approved').length, icon: 'mdi-book-clock-outline', color: '#52A77A', action: () => currentPage.value = 'borrowings' },
  { label: 'Overdue', value: overdueList.value.length, icon: 'mdi-alert-circle-outline', color: '#C62828', action: null },
])

// Fetches
const fetchAll = async () => {
  try {
    const [booksRes, usersRes, borrowRes] = await Promise.all([
      fetch('http://localhost:8080/api/books'),
      fetch('http://localhost:8080/api/users'),
      fetch('http://localhost:8080/api/borrowings'),
    ])
    libraryBooks.value = booksRes.ok ? await booksRes.json() : []
    const allU = usersRes.ok ? await usersRes.json() : []
    allUsers.value = allU
    staffList.value = allU.filter((u: any) => u.role === 'manager')
    userList.value = allU
    allBorrowings.value = borrowRes.ok ? await borrowRes.json() : []
  } catch {}
}

const openUserDetail = (u: any) => { selectedUser.value = u; userDetailOpen.value = true }

const deleteBook = async (id: number) => { await fetch(`http://localhost:8080/api/books/${id}`, { method: 'DELETE' }); fetchAll() }
const addQuantity = async (book: any) => {
  await fetch(`http://localhost:8080/api/books/${book.id}/quantity`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ quantity: book.quantity + 1 }) })
  fetchAll()
}
const removeQuantity = async (book: any) => {
  if (book.quantity <= 0) return
  await fetch(`http://localhost:8080/api/books/${book.id}/quantity`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ quantity: book.quantity - 1 }) })
  fetchAll()
}

const searchBooks = async () => {
  currentSearchPage.value = 1
  const sub = selectedCategory.value === 'all' ? '' : `&subject=${selectedCategory.value}`
  const q = searchQuery.value || 'library'
  const r = await fetch(`https://openlibrary.org/search.json?q=${q}${sub}&limit=12&page=1`)
  const data = await r.json()
  searchResults.value = data.docs
}
const loadMore = async () => {
  currentSearchPage.value++
  const sub = selectedCategory.value === 'all' ? '' : `&subject=${selectedCategory.value}`
  const q = searchQuery.value || 'library'
  const r = await fetch(`https://openlibrary.org/search.json?q=${q}${sub}&limit=12&page=${currentSearchPage.value}`)
  const data = await r.json()
  searchResults.value = [...searchResults.value, ...data.docs]
}
const addToLibrary = async (book: any) => {
  await fetch('http://localhost:8080/api/books', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ title: book.title, author: book.author_name?.[0] ?? 'Unknown', isbn: book.isbn?.[0] ?? null, category: selectedCategory.value === 'all' ? null : selectedCategory.value, coverUrl: book.cover_i ? `https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg` : null, openLibraryId: book.key, quantity: 1 }) })
  snack.value = { show: true, text: 'Book added to library!', color: 'success' }
  fetchAll()
}
const addCustomBook = async () => {
  if (!customBook.value.title || !customBook.value.author) { snack.value = { show: true, text: 'Title and Author are required', color: 'error' }; return }
  await fetch('http://localhost:8080/api/books', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ ...customBook.value, category: customBook.value.category === 'all' ? null : customBook.value.category, status: 'available' }) })
  snack.value = { show: true, text: 'Book added!', color: 'success' }
  customBook.value = { title: '', author: '', isbn: '', category: 'all', quantity: 1 }
  fetchAll()
}
const promoteToManager = async (id: number) => { await fetch(`http://localhost:8080/api/users/${id}/promote`, { method: 'PUT' }); snack.value = { show: true, text: 'User promoted to Manager', color: 'success' }; fetchAll() }
const demoteToUser = async (id: number) => { await fetch(`http://localhost:8080/api/users/${id}/demote`, { method: 'PUT' }); snack.value = { show: true, text: 'Manager role removed', color: 'warning' }; fetchAll() }

const formatDate = (d: string) => { if (!d) return '—'; return new Date(d).toLocaleDateString('en-PH', { year: 'numeric', month: 'short', day: 'numeric' }) }
const isOverdue = (d: string) => d && new Date(d) < new Date()
const daysOverdue = (d: string) => Math.floor((Date.now() - new Date(d).getTime()) / 86400000)
const statusColor = (s: string) => ({ approved: '#2D6A4F', pending: '#B45309', rejected: '#C62828', returned: '#6B7280' })[s] ?? '#6B7280'
const roleColor = (r: string) => ({ admin: '#C8933A', manager: '#52A77A', user: '#5B8DEF' })[r] ?? '#6B7280'

const logout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); router.push('/') }

onMounted(fetchAll)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=Inter:wght@300;400;500;600&display=swap');

.lib-app { background: #0F0F1C !important; }

/* SIDEBAR */
:deep(.v-navigation-drawer) { border-right: 1px solid rgba(200,147,58,0.12) !important; }
.sidebar-brand { display: flex; align-items: center; gap: 0.75rem; padding: 1.4rem 1.2rem 1rem; }
.rail-brand { justify-content: center; padding: 1.4rem 0 1rem; }
.brand-name { font-family: 'Playfair Display', serif; font-size: 1.1rem; font-weight: 700; color: #F5ECD7; line-height: 1.1; }
.brand-role { font-family: 'Inter', sans-serif; font-size: 0.65rem; color: #C8933A; text-transform: uppercase; letter-spacing: 0.12em; }
.user-chip { display: flex; align-items: center; padding: 0.75rem 1.2rem; margin: 0.5rem 0.75rem; background: rgba(200,147,58,0.06); border-radius: 10px; }
.user-name { font-family: 'Inter', sans-serif; font-size: 0.85rem; font-weight: 600; color: #F5ECD7; }
.user-role { font-family: 'Inter', sans-serif; font-size: 0.7rem; color: #C8933A; }
.nav-item :deep(.v-list-item__prepend .v-icon) { color: rgba(245,236,215,0.35) !important; }
.nav-item :deep(.v-list-item-title) { font-family: 'Inter', sans-serif; font-size: 0.88rem; color: rgba(245,236,215,0.55); }
.nav-item:hover :deep(.v-list-item__prepend .v-icon), .nav-item:hover :deep(.v-list-item-title) { color: #C8933A !important; }
.nav-active { background: rgba(200,147,58,0.1) !important; }
.nav-active :deep(.v-list-item__prepend .v-icon), .nav-active :deep(.v-list-item-title) { color: #C8933A !important; }
.nav-danger :deep(.v-list-item__prepend .v-icon), .nav-danger :deep(.v-list-item-title) { color: rgba(239,100,100,0.55) !important; }
.nav-danger:hover :deep(.v-list-item__prepend .v-icon), .nav-danger:hover :deep(.v-list-item-title) { color: #EF6464 !important; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* MAIN */
.main-content { background: #13131F; }
.topbar { display: flex; align-items: center; justify-content: space-between; padding: 1.5rem 2rem 0.5rem; border-bottom: 1px solid rgba(200,147,58,0.1); background: #13131F; }
.page-title { font-family: 'Playfair Display', serif; font-size: 1.6rem; font-weight: 700; color: #F5ECD7; }
.page-sub { font-family: 'Inter', sans-serif; font-size: 0.85rem; color: rgba(200,147,58,0.7); }
.topbar-right { display: flex; align-items: center; gap: 0.75rem; }
.icon-btn { color: rgba(245,236,215,0.6) !important; }
.page-body { padding: 1.5rem 2rem 2rem; }

/* STAT CARDS */
.stat-card {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(200,147,58,0.12);
  border-radius: 14px; padding: 1.2rem 1.4rem;
  display: flex; align-items: center; gap: 1rem;
  cursor: pointer; transition: background 0.2s, transform 0.15s;
  position: relative; overflow: hidden;
}
.stat-card::before {
  content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 3px;
  background: var(--accent);
}
.stat-card:hover { background: rgba(255,255,255,0.07); transform: translateY(-2px); }
.stat-icon-wrap { flex-shrink: 0; }
.stat-body { flex: 1; }
.stat-value { display: block; font-family: 'Playfair Display', serif; font-size: 1.7rem; font-weight: 700; color: #F5ECD7; line-height: 1.1; }
.stat-label { font-family: 'Inter', sans-serif; font-size: 0.78rem; color: rgba(245,236,215,0.5); text-transform: uppercase; letter-spacing: 0.08em; }
.stat-arrow { flex-shrink: 0; }

/* SECTION CARDS */
.section-card { background: rgba(255,255,255,0.04); border: 1px solid rgba(200,147,58,0.1); border-radius: 14px; overflow: hidden; }
.section-card-header { display: flex; align-items: center; justify-content: space-between; padding: 1rem 1.2rem 0.75rem; border-bottom: 1px solid rgba(200,147,58,0.1); }
.section-card-title { font-family: 'Inter', sans-serif; font-size: 0.88rem; font-weight: 600; color: rgba(245,236,215,0.7); text-transform: uppercase; letter-spacing: 0.08em; }
.view-all-btn { color: #C8933A !important; font-size: 0.78rem !important; }

/* TABLE */
.lib-table { width: 100%; border-collapse: collapse; font-family: 'Inter', sans-serif; font-size: 0.875rem; }
.lib-table thead { background: rgba(200,147,58,0.06); }
.lib-table th { padding: 0.8rem 1.1rem; text-align: left; font-size: 0.72rem; font-weight: 600; color: rgba(200,147,58,0.7); text-transform: uppercase; letter-spacing: 0.08em; border-bottom: 1px solid rgba(200,147,58,0.1); }
.lib-table td { padding: 0.85rem 1.1rem; color: rgba(245,236,215,0.8); border-bottom: 1px solid rgba(255,255,255,0.04); vertical-align: middle; }
.lib-table tbody tr:last-child td { border-bottom: none; }
.lib-table tbody tr:hover { background: rgba(200,147,58,0.04); }
.clickable-row { cursor: pointer; }
.row-overdue { background: rgba(198,40,40,0.06) !important; }
.td-user { display: flex; align-items: center; gap: 0.5rem; }
.td-book { font-weight: 500; color: #F5ECD7; }
.td-empty { text-align: center; padding: 2rem !important; color: rgba(245,236,215,0.3) !important; font-style: italic; }
.text-danger { color: #EF6464 !important; }
.table-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 1rem; flex-wrap: wrap; }
.table-hint { font-family: 'Inter', sans-serif; font-size: 0.85rem; color: rgba(245,236,215,0.4); }
.td-book-row { display: flex; align-items: center; gap: 0.6rem; }
.table-thumb { width: 32px; height: 44px; object-fit: cover; border-radius: 3px; flex-shrink: 0; }

/* OVERDUE PANEL */
.mini-empty { display: flex; align-items: center; gap: 0.5rem; padding: 1.2rem; font-family: 'Inter', sans-serif; font-size: 0.85rem; color: rgba(245,236,215,0.35); }
.overdue-list { max-height: 280px; overflow-y: auto; }
.overdue-item { display: flex; justify-content: space-between; align-items: center; padding: 0.75rem 1.1rem; border-bottom: 1px solid rgba(255,255,255,0.04); }
.overdue-item:last-child { border-bottom: none; }
.overdue-user { font-family: 'Inter', sans-serif; font-size: 0.82rem; font-weight: 600; color: rgba(245,236,215,0.85); }
.overdue-book { font-family: 'Inter', sans-serif; font-size: 0.75rem; color: rgba(245,236,215,0.4); }
.overdue-days { font-family: 'Inter', sans-serif; font-size: 0.75rem; font-weight: 700; color: #EF6464; white-space: nowrap; }

/* BUTTONS */
.add-btn { background: linear-gradient(135deg, #C8933A, #A0702A) !important; color: #FAF6EF !important; font-weight: 600 !important; font-family: 'Inter', sans-serif !important; box-shadow: 0 3px 10px rgba(160,112,42,0.3) !important; }
.load-more-btn { color: #C8933A !important; border-color: rgba(200,147,58,0.3) !important; }
.promote-btn { background: rgba(200,147,58,0.12) !important; color: #C8933A !important; font-weight: 600 !important; border: 1px solid rgba(200,147,58,0.25) !important; }
.action-menu { background: #1A1A2E !important; }

/* FIELDS */
.dark-field :deep(.v-field) { background: rgba(255,255,255,0.05) !important; border-color: rgba(200,147,58,0.2) !important; }
.dark-field :deep(input) { color: #F5ECD7 !important; }
.dark-field :deep(label) { color: rgba(245,236,215,0.5) !important; }
.dark-field :deep(.v-field--focused) { border-color: #C8933A !important; }

/* USER DETAIL DRAWER */
.user-detail-panel { height: 100%; display: flex; flex-direction: column; overflow-y: auto; }
.detail-header { display: flex; flex-direction: column; align-items: center; padding: 1rem 1.5rem 1.5rem; background: rgba(200,147,58,0.05); border-bottom: 1px solid rgba(200,147,58,0.12); }
.detail-header .v-btn { align-self: flex-end; margin-bottom: 0.5rem; }
.detail-avatar-letter { font-family: 'Playfair Display', serif; font-weight: 700; font-size: 1.6rem; color: #FAF6EF; }
.detail-username { font-family: 'Playfair Display', serif; font-size: 1.3rem; font-weight: 700; color: #F5ECD7; }
.detail-email { font-family: 'Inter', sans-serif; font-size: 0.82rem; color: rgba(245,236,215,0.5); }
.detail-section { padding: 1.2rem 1.5rem; border-bottom: 1px solid rgba(200,147,58,0.1); }
.detail-section-title { font-family: 'Inter', sans-serif; font-size: 0.72rem; font-weight: 600; color: rgba(200,147,58,0.7); text-transform: uppercase; letter-spacing: 0.1em; margin-bottom: 0.8rem; }
.detail-empty { display: flex; flex-direction: column; align-items: center; gap: 0.4rem; padding: 1rem 0; font-family: 'Inter', sans-serif; font-size: 0.82rem; color: rgba(245,236,215,0.3); }
.detail-borrow-list { display: flex; flex-direction: column; gap: 0.5rem; }
.detail-borrow-item { display: flex; align-items: flex-start; gap: 0.75rem; background: rgba(255,255,255,0.03); border-radius: 8px; padding: 0.6rem 0.75rem; }
.detail-thumb { width: 36px; height: 50px; object-fit: cover; border-radius: 4px; flex-shrink: 0; }
.grayscale { filter: grayscale(0.5); }
.detail-book-title { font-family: 'Inter', sans-serif; font-size: 0.82rem; font-weight: 600; color: rgba(245,236,215,0.85); }
.detail-book-meta { font-family: 'Inter', sans-serif; font-size: 0.73rem; color: rgba(245,236,215,0.4); margin-top: 0.15rem; }
.overdue-pill { background: #C62828; color: #fff; font-size: 0.65rem; font-weight: 700; padding: 1px 5px; border-radius: 3px; margin-left: 4px; }
.detail-actions { padding: 1.2rem 1.5rem; margin-top: auto; }

/* ADD BOOK DIALOG */
.add-book-card { background: #1A1A2E !important; color: #F5ECD7 !important; }
.dialog-title-bar { display: flex; align-items: center; justify-content: space-between; padding: 1.2rem 1.5rem; border-bottom: 1px solid rgba(200,147,58,0.12); font-family: 'Playfair Display', serif; font-size: 1.2rem; color: #F5ECD7; }
.form-section-title { font-family: 'Inter', sans-serif; font-size: 0.8rem; font-weight: 600; color: rgba(200,147,58,0.8); text-transform: uppercase; letter-spacing: 0.08em; }
.search-result-card { background: rgba(255,255,255,0.05) !important; border: 1px solid rgba(200,147,58,0.15); border-radius: 10px; overflow: hidden; }
.search-result-body { padding: 0.6rem 0.8rem 0.8rem; }
.search-title { font-family: 'Playfair Display', serif; font-size: 0.85rem; font-weight: 600; color: #F5ECD7; }
.search-author { font-family: 'Inter', sans-serif; font-size: 0.75rem; color: rgba(245,236,215,0.4); }
</style>
