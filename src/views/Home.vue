  <template>
    <v-app class="lib-app">

      <!-- SIDEBAR -->
      <v-navigation-drawer v-model="drawer" :rail="rail" permanent color="#2C1A0E" width="240">
        <div class="sidebar-brand" :class="{ 'rail-brand': rail }">
          <v-icon color="#C8933A" :size="rail ? 28 : 32">mdi-bookshelf</v-icon>
          <Transition name="fade">
            <div v-if="!rail" class="brand-text">
              <span class="brand-name">Library</span>
              <span class="brand-role">Reader</span>
            </div>
          </Transition>
        </div>

        <v-divider color="rgba(200,147,58,0.2)" class="mx-3"/>

        <div class="user-chip" v-if="!rail">
          <v-avatar size="36" color="#C8933A" class="mr-3">
            <span class="avatar-letter">{{ user?.username?.[0]?.toUpperCase() }}</span>
          </v-avatar>
          <div>
            <div class="user-name">{{ user?.username }}</div>
            <div class="user-role">Member</div>
          </div>
        </div>

        <v-list nav density="compact" class="mt-2">
          <v-list-item
            v-for="item in navItems"
            :key="item.value"
            :prepend-icon="item.icon"
            :title="rail ? '' : item.label"
            rounded="lg"
            class="nav-item"
            :class="{ 'nav-active': currentPage === item.value }"
            @click="currentPage = item.value"
          />
        </v-list>

        <template #append>
          <v-divider color="rgba(200,147,58,0.2)" class="mx-3 mb-2"/>
          <v-list nav density="compact">
            <v-list-item
              prepend-icon="mdi-chevron-left-circle-outline"
              :title="rail ? '' : 'Collapse'"
              rounded="lg"
              class="nav-item"
              @click="rail = !rail"
            />
            <v-list-item
              prepend-icon="mdi-logout"
              :title="rail ? '' : 'Sign Out'"
              rounded="lg"
              class="nav-item nav-danger"
              @click="logout"
            />
          </v-list>
        </template>
      </v-navigation-drawer>

      <v-main class="main-content">
        <!-- TOP BAR -->
        <div class="topbar">
          <div class="page-heading">
            <h2 class="page-title">{{ pageTitle }}</h2>
            <p class="page-sub">{{ pageSub }}</p>
          </div>
          <div class="topbar-right">
            <v-btn icon variant="text" class="icon-btn" @click="drawer = !drawer; rail = false">
              <v-icon>mdi-menu</v-icon>
            </v-btn>
            <v-avatar size="36" color="#C8933A">
              <span class="avatar-letter">{{ user?.username?.[0]?.toUpperCase() }}</span>
            </v-avatar>
          </div>
        </div>

        <div class="page-body">

          <!-- HOME: AVAILABLE BOOKS -->
          <div v-if="currentPage === 'home'">
            <div class="section-header mb-4">
              <v-text-field
                v-model="search"
                placeholder="Search books..."
                prepend-inner-icon="mdi-magnify"
                variant="outlined" rounded="lg" hide-details
                density="compact"
                style="max-width: 320px;"
                class="warm-field"
              />
            </div>

            <div v-if="filteredBooks.length === 0" class="empty-state">
              <v-icon size="64" color="#C8933A" class="mb-4">mdi-book-off-outline</v-icon>
              <p>No books available right now</p>
            </div>

            <div class="books-grid">
              <div
                v-for="book in filteredBooks"
                :key="book.id"
                class="book-card"
                @click="viewBook(book)"
              >
                <div class="book-cover-wrap">
                  <img
                    :src="book.coverUrl ?? 'https://placehold.co/180x240/5C3D1E/FAF6EF?text=No+Cover'"
                    class="book-cover"
                    @error="(e: any) => e.target.src = 'https://placehold.co/180x240/5C3D1E/FAF6EF?text=No+Cover'"
                  />
                  <div class="book-overlay">
                    <v-btn size="small" class="borrow-quick-btn" @click.stop="borrowBook(book.id)">
                      Borrow
                    </v-btn>
                  </div>
                  <v-chip
                    size="x-small"
                    :color="book.status === 'available' ? '#2D6A4F' : '#C62828'"
                    class="status-chip"
                  >{{ book.status }}</v-chip>
                </div>
                <div class="book-info">
                  <p class="book-title">{{ book.title }}</p>
                  <p class="book-author">{{ book.author }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- MY BORROWINGS -->
          <div v-if="currentPage === 'borrowings'">
            <div v-if="activeBorrowings.length === 0" class="empty-state">
              <v-icon size="64" color="#C8933A" class="mb-4">mdi-book-clock-outline</v-icon>
              <p>You haven't borrowed any books yet</p>
              <v-btn variant="tonal" class="mt-3 warm-btn" @click="currentPage = 'home'">Browse Books</v-btn>
            </div>
            <div v-else class="records-list">
              <div v-for="b in activeBorrowings" :key="b.id" class="record-card">
                <img
                  :src="b.book?.coverUrl ?? 'https://placehold.co/60x80/5C3D1E/FAF6EF?text=📚'"
                  class="record-thumb"
                  @error="(e: any) => e.target.src = 'https://placehold.co/60x80/5C3D1E/FAF6EF?text=📚'"
                />
                <div class="record-info">
                  <p class="record-title">{{ b.book.title }}</p>
                  <p class="record-author">by {{ b.book.author }}</p>
                  <div class="record-meta">
                    <span><v-icon size="13" class="mr-1">mdi-calendar-start</v-icon>{{ formatDate(b.borrowDate) }}</span>
                    <span :class="isOverdue(b.dueDate) ? 'overdue' : ''">
                      <v-icon size="13" class="mr-1">mdi-calendar-end</v-icon>{{ formatDate(b.dueDate) }}
                      <span v-if="isOverdue(b.dueDate)" class="overdue-tag">Overdue</span>
                    </span>
                    


                  </div>

                </div>
                <div class="ml-auto align-self-center">
                  <v-chip
                    size="small"
                    :color="b.status === 'approved' ? '#2D6A4F' : b.status === 'pending' ? '#B45309' : '#C62828'"
                    class="ml-auto align-self-center"
                  >{{ b.status }}</v-chip>

                  <v-btn
                    v-if="b.status === 'pending'"
                    size="small"
                    variant="outlined"
                    class="cancel-request-btn ml-2 align-self-center"
                    @click.stop="cancelBorrow(b.id)"
                  >
                    Cancel
                  </v-btn>
                </div>
              </div>
              
            </div>
            
          </div>

          <!-- HISTORY -->
          <div v-if="currentPage === 'history'">
                      <h2 class="mb-4">Borrowing History</h2>

            <v-chip-group v-model="historyFilter" mandatory class="mb-4">
              <v-chip value="all" filter>All</v-chip>
              <v-chip value="requested" filter color="orange">Requested</v-chip>
              <v-chip value="approved" filter color="green">Approved</v-chip>
              <v-chip value="rejected" filter color="red">Rejected</v-chip>
              <v-chip value="cancelled" filter color="grey-darken-1">Cancelled</v-chip>
              <v-chip value="returned" filter color="grey">Returned</v-chip>
            </v-chip-group>

            <div v-if="filteredHistory.length === 0" class="empty-state">
              <v-icon size="64" color="#C8933A" class="mb-4">mdi-history</v-icon>
              <p>No borrowing history yet</p>
            </div>
            <div v-else class="records-list">
              <div v-for="b in filteredHistory" :key="b.id" class="record-card returned">
                <img
                  :src="b.book?.coverUrl ?? 'https://placehold.co/60x80/5C3D1E/FAF6EF?text=📚'"
                  class="record-thumb grayscale"
                  @error="(e: any) => e.target.src = 'https://placehold.co/60x80/5C3D1E/FAF6EF?text=📚'"
                />
                <div class="record-info">
                  <p class="record-title">{{ b.book.title }}</p>
                  <p class="record-author">by {{ b.book.author }}</p>
                  <div class="record-meta">
                    <span><v-icon size="13" class="mr-1">mdi-calendar-start</v-icon>{{ formatDate(b.borrowDate) }}</span>
                    <span><v-icon size="13" class="mr-1">mdi-calendar-check</v-icon>{{ formatDate(b.returnDate) }}</span>
                  </div>
                </div>
                  <v-chip
                    size="small"
                    :color="
                      b.status === 'approved' ? '#2D6A4F' :
                      b.status === 'requested' ? '#B45309' :
                      b.status === 'rejected' ? '#C62828' :
                      b.status === 'cancelled' ? '#9CA3AF' :
                      '#6B7280'
                    "
                    class="ml-auto align-self-center"
                  >{{ b.status }}</v-chip>         
                </div>            
              </div>
          </div>

        </div>
      </v-main>

      <!-- BOOK DETAIL DIALOG -->
      <v-dialog v-model="bookDialog" max-width="520" scrollable>
        <v-card class="book-dialog" v-if="selectedBook">
          <div class="dialog-cover-wrap">
            <img
              :src="selectedBook.coverUrl ?? 'https://placehold.co/520x280/5C3D1E/FAF6EF?text=No+Cover'"
              class="dialog-cover"
              @error="(e: any) => e.target.src = 'https://placehold.co/520x280/5C3D1E/FAF6EF?text=No+Cover'"
            />
            <div class="dialog-cover-gradient"/>
            <v-btn icon variant="text" class="dialog-close-btn" @click="bookDialog = false">
              <v-icon color="white">mdi-close</v-icon>
            </v-btn>
          </div>
          <v-card-text class="dialog-body">
            <h3 class="dialog-title">{{ selectedBook.title }}</h3>
            <p class="dialog-author">by {{ selectedBook.author }}</p>
            <v-row density="comfortable" class="mt-4 mb-2">
              <v-col cols="6">
                <div class="meta-label">Category</div>
                <div class="meta-value">{{ selectedBook.category ?? 'General' }}</div>
              </v-col>
              <v-col cols="6">
                <div class="meta-label">ISBN</div>
                <div class="meta-value">{{ selectedBook.isbn ?? '—' }}</div>
              </v-col>
              <v-col cols="6">
                <div class="meta-label">Copies Available</div>
                <div class="meta-value">{{ selectedBook.quantity }}</div>
              </v-col>
              <v-col cols="6">
                <div class="meta-label">Status</div>
                <v-chip
                  :color="selectedBook.status === 'available' ? '#2D6A4F' : '#C62828'"
                  size="small" class="mt-1"
                >{{ selectedBook.status }}</v-chip>
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions class="dialog-actions">
            <v-btn variant="outlined" class="cancel-btn" @click="bookDialog = false">Cancel</v-btn>
            <v-btn
              class="borrow-btn"
              :disabled="selectedBook.status !== 'available'"
              @click="borrowBook(selectedBook.id); bookDialog = false"
            >
              <v-icon start>mdi-book-arrow-right-outline</v-icon>
              Request to Borrow
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- SNACKBAR -->
      <v-snackbar v-model="snack.show" :color="snack.color" rounded="lg" location="bottom right">
        {{ snack.text }}
      </v-snackbar>

    </v-app>
  </template>

  <script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'

  const router = useRouter()
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const books = ref<any[]>([])
  const myBorrowings = ref<any[]>([])
  const drawer = ref(true)
  const rail = ref(false)
  const currentPage = ref('home')
  const bookDialog = ref(false)
  const selectedBook = ref<any>(null)
  const search = ref('')
  const snack = ref({ show: false, text: '', color: 'success' })
  const historyFilter = ref('all')
  const myHistory = ref<any[]>([])

  const filteredHistory = computed(() => {
    if (historyFilter.value === 'all') return myHistory.value
    return myHistory.value.filter((h: any) => h.status === historyFilter.value)
  })

onMounted(() => { fetchBooks(); fetchMyBorrowings(); fetchMyHistory() })

const fetchMyHistory = async () => {
  try {
    const r = await fetch(`http://localhost:8080/api/borrowings/history/${user.id}`)
    myHistory.value = await r.json()
  } catch {}
}




  const navItems = [
    { value: 'home', icon: 'mdi-home-outline', label: 'Browse Books' },
    { value: 'borrowings', icon: 'mdi-book-clock-outline', label: 'My Borrowings' },
    { value: 'history', icon: 'mdi-history', label: 'History' },
  ]

  const pageTitles: Record<string, { title: string; sub: string }> = {
    home: { title: 'Browse Books', sub: 'Discover your next great read' },
    borrowings: { title: 'My Borrowings', sub: 'Track your active loans' },
    history: { title: 'Borrowing History', sub: 'Everything you\'ve read' },
  }
  const pageTitle = computed(() => pageTitles[currentPage.value]?.title ?? '')
  const pageSub = computed(() => pageTitles[currentPage.value]?.sub ?? '')

  const filteredBooks = computed(() =>
    books.value.filter(b =>
      b.title?.toLowerCase().includes(search.value.toLowerCase()) ||
      b.author?.toLowerCase().includes(search.value.toLowerCase())
    )
  )
  const activeBorrowings = computed(() => myBorrowings.value.filter((b: any) => b.status === 'pending' || b.status === 'approved'))
  const returnedBorrowings = computed(() => myBorrowings.value.filter((b: any) => b.status === 'returned'))

  const fetchBooks = async () => {
    try {
      const r = await fetch('http://localhost:8080/api/books')
      books.value = await r.json()
    } catch {}
  }

  const fetchMyBorrowings = async () => {
    try {
      const r = await fetch(`http://localhost:8080/api/borrowings/user/${user.id}`)
      myBorrowings.value = await r.json()
    } catch {}
  }

  const viewBook = (book: any) => {
    selectedBook.value = book
    bookDialog.value = true
  }

  const borrowBook = async (bookId: number) => {
    try {
      const r = await fetch('http://localhost:8080/api/borrowings/request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: user.id, bookId })
      })
      if (r.ok) {
        snack.value = { show: true, text: 'Borrow request submitted! Awaiting approval.', color: 'success' }
        fetchMyBorrowings()
      } else {
        snack.value = { show: true, text: await r.text(), color: 'error' }
      }
    } catch {
      snack.value = { show: true, text: 'Cannot connect to server', color: 'error' }
    }
  }

  const cancelBorrow = async (borrowId: number) => {
  try {
    const r = await fetch(`http://localhost:8080/api/borrowings/cancel/${borrowId}`, {
      method: 'PUT' // or PATCH/DELETE — match whatever your backend expects
    })
    if (r.ok) {
      snack.value = { show: true, text: 'Request cancelled.', color: 'success' }
      fetchMyBorrowings()
      fetchMyHistory()
    } else {
      snack.value = { show: true, text: await r.text(), color: 'error' }
    }
  } catch {
    snack.value = { show: true, text: 'Cannot connect to server', color: 'error' }
  }
}

  const formatDate = (d: string) => {
    if (!d) return '—'
    return new Date(d).toLocaleDateString('en-PH', { year: 'numeric', month: 'short', day: 'numeric' })
  }
  const isOverdue = (dueDate: string) => dueDate && new Date(dueDate) < new Date()

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/')
  }

  </script>

  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=Inter:wght@300;400;500;600&display=swap');

  .lib-app { background: #FAF6EF !important; }

  /* SIDEBAR */
  :deep(.v-navigation-drawer) { border-right: 1px solid rgba(200,147,58,0.15) !important; }
  .sidebar-brand {
    display: flex; align-items: center; gap: 0.75rem;
    padding: 1.4rem 1.2rem 1rem;
  }
  .rail-brand { justify-content: center; padding: 1.4rem 0 1rem; }
  .brand-text { display: flex; flex-direction: column; }
  .brand-name {
    font-family: 'Playfair Display', serif; font-size: 1.1rem;
    font-weight: 700; color: #F5ECD7; line-height: 1.1;
  }
  .brand-role {
    font-family: 'Inter', sans-serif; font-size: 0.65rem;
    color: #C8933A; text-transform: uppercase; letter-spacing: 0.12em;
  }
  .user-chip {
    display: flex; align-items: center;
    padding: 0.75rem 1.2rem; margin: 0.5rem 0.75rem;
    background: rgba(200,147,58,0.08);
    border-radius: 10px;
  }
  .avatar-letter { font-family: 'Playfair Display', serif; font-weight: 700; font-size: 1rem; color: #FAF6EF; }
  .user-name { font-family: 'Inter', sans-serif; font-size: 0.85rem; font-weight: 600; color: #F5ECD7; }
  .user-role { font-family: 'Inter', sans-serif; font-size: 0.7rem; color: #C8933A; }
  .nav-item :deep(.v-list-item__prepend .v-icon) { color: rgba(245,236,215,0.5) !important; }
  .nav-item :deep(.v-list-item-title) { font-family: 'Inter', sans-serif; font-size: 0.88rem; color: rgba(245,236,215,0.7); }
  .nav-item:hover :deep(.v-list-item__prepend .v-icon),
  .nav-item:hover :deep(.v-list-item-title) { color: #C8933A !important; }
  .nav-active { background: rgba(200,147,58,0.12) !important; }
  .nav-active :deep(.v-list-item__prepend .v-icon),
  .nav-active :deep(.v-list-item-title) { color: #C8933A !important; }
  .nav-danger :deep(.v-list-item__prepend .v-icon),
  .nav-danger :deep(.v-list-item-title) { color: rgba(239,100,100,0.7) !important; }
  .nav-danger:hover :deep(.v-list-item__prepend .v-icon),
  .nav-danger:hover :deep(.v-list-item-title) { color: #EF6464 !important; }
  .fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
  .fade-enter-from, .fade-leave-to { opacity: 0; }

  /* MAIN */
  .main-content { background: #FAF6EF; }
  .topbar {
    display: flex; align-items: center; justify-content: space-between;
    padding: 1.5rem 2rem 0.5rem;
    border-bottom: 1px solid rgba(200,147,58,0.12);
    background: #FAF6EF;
  }
  .page-title {
    font-family: 'Playfair Display', serif; font-size: 1.6rem;
    font-weight: 700; color: #3B2209;
  }
  .page-sub { font-family: 'Inter', sans-serif; font-size: 0.85rem; color: #8A7060; }
  .topbar-right { display: flex; align-items: center; gap: 0.75rem; }
  .icon-btn { color: #5C3D1E !important; }
  .page-body { padding: 1.5rem 2rem 2rem; }
  .section-header { display: flex; align-items: center; justify-content: flex-end; }
  .warm-field :deep(.v-field) { background: #FFF8EE !important; border-color: #D9C5A0 !important; }

  /* BOOKS GRID */
  .books-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 1.25rem;
  }
  .book-card {
    cursor: pointer;
    border-radius: 10px;
    overflow: hidden;
    background: #FFF8EE;
    border: 1px solid rgba(200,147,58,0.15);
    transition: transform 0.2s, box-shadow 0.2s;
  }
  .book-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 30px rgba(92,61,30,0.18);
  }
  .book-cover-wrap {
    position: relative;
    aspect-ratio: 3/4;
    overflow: hidden;
    background: #E8D9C0;
  }
  .book-cover { width: 100%; height: 100%; object-fit: cover; display: block; }
  .book-overlay {
    position: absolute; inset: 0;
    background: rgba(44,26,14,0.65);
    display: flex; align-items: center; justify-content: center;
    opacity: 0; transition: opacity 0.2s;
  }
  .book-card:hover .book-overlay { opacity: 1; }
  .borrow-quick-btn {
    background: #C8933A !important; color: #FAF6EF !important;
    font-family: 'Inter', sans-serif; font-size: 0.78rem; font-weight: 600;
  }
  .status-chip { position: absolute; top: 6px; right: 6px; }
  .book-info { padding: 0.6rem 0.75rem 0.75rem; }
  .book-title {
    font-family: 'Inter', sans-serif; font-size: 0.8rem; font-weight: 600;
    color: #3B2209; line-height: 1.3; margin-bottom: 0.2rem;
    display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
  }
  .book-author {
    font-family: 'Inter', sans-serif; font-size: 0.72rem;
    color: #8A7060;
    display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden;
  }

  /* RECORDS */
  .records-list { display: flex; flex-direction: column; gap: 0.85rem; }
  .record-card {
    display: flex; align-items: flex-start; gap: 1rem;
    background: #FFF8EE; border: 1px solid rgba(200,147,58,0.18);
    border-radius: 12px; padding: 1rem 1.2rem;
    transition: box-shadow 0.2s;
  }
  .record-card:hover { box-shadow: 0 4px 16px rgba(92,61,30,0.1); }
  .record-thumb { width: 50px; height: 68px; object-fit: cover; border-radius: 5px; flex-shrink: 0; }
  .grayscale { filter: grayscale(0.6); }
  .record-title { font-family: 'Playfair Display', serif; font-size: 0.95rem; font-weight: 600; color: #3B2209; }
  .record-author { font-family: 'Inter', sans-serif; font-size: 0.8rem; color: #8A7060; margin-bottom: 0.4rem; }
  .record-meta {
    display: flex; flex-wrap: wrap; gap: 0.85rem;
    font-family: 'Inter', sans-serif; font-size: 0.75rem; color: #8A7060;
  }
  .overdue { color: #C62828 !important; font-weight: 600; }
  .overdue-tag {
    background: #FEE2E2; color: #C62828;
    border-radius: 4px; padding: 1px 5px;
    font-size: 0.7rem; font-weight: 700; margin-left: 4px;
  }

  /* EMPTY */
  .empty-state {
    display: flex; flex-direction: column; align-items: center;
    padding: 5rem 2rem;
    font-family: 'Inter', sans-serif; color: #8A7060; text-align: center;
  }
  .warm-btn {
    background: rgba(200,147,58,0.12) !important;
    color: #C8933A !important; font-weight: 600;
  }

  /* DIALOG */
  .book-dialog { background: #FAF6EF !important; overflow: hidden; }
  .dialog-cover-wrap { position: relative; height: 220px; overflow: hidden; }
  .dialog-cover { width: 100%; height: 100%; object-fit: cover; }
  .dialog-cover-gradient {
    position: absolute; bottom: 0; left: 0; right: 0; height: 80px;
    background: linear-gradient(transparent, #FAF6EF);
  }
  .dialog-close-btn { position: absolute !important; top: 10px; right: 10px; background: rgba(0,0,0,0.4) !important; }
  .dialog-body { padding: 0 1.5rem 0.5rem !important; }
  .dialog-title { font-family: 'Playfair Display', serif; font-size: 1.4rem; font-weight: 700; color: #3B2209; }
  .dialog-author { font-family: 'Inter', sans-serif; font-size: 0.88rem; color: #8A7060; margin-top: 0.2rem; }
  .meta-label { font-family: 'Inter', sans-serif; font-size: 0.72rem; font-weight: 600; color: #8A7060; text-transform: uppercase; letter-spacing: 0.07em; }
  .meta-value { font-family: 'Inter', sans-serif; font-size: 0.9rem; color: #3B2209; font-weight: 500; }
  .dialog-actions { padding: 0.75rem 1.5rem 1.5rem !important; gap: 0.75rem; }
  .cancel-btn { color: #8A7060 !important; border-color: #D9C5A0 !important; }
  .borrow-btn {
    background: linear-gradient(135deg, #C8933A, #A0702A) !important;
    color: #FAF6EF !important; font-weight: 600;
    box-shadow: 0 4px 12px rgba(160,112,42,0.3) !important;
    flex: 1;
  }

  .cancel-request-btn {
  color: #C62828 !important;
  border-color: #C62828 !important;
  font-size: 0.75rem;
}
  </style>
