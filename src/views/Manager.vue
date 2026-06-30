<template>
  <v-app class="lib-app">

    <!-- SIDEBAR -->
    <v-navigation-drawer v-model="drawer" :rail="rail" permanent color="#1B3A2D" width="240">
      <div class="sidebar-brand" :class="{ 'rail-brand': rail }">
        <v-icon color="#52A77A" :size="rail ? 28 : 32">mdi-bookshelf</v-icon>
        <Transition name="fade">
          <div v-if="!rail" class="brand-text">
            <span class="brand-name">Library</span>
            <span class="brand-role">Manager</span>
          </div>
        </Transition>
      </div>

      <v-divider color="rgba(82,167,122,0.2)" class="mx-3"/>

      <div class="user-chip" v-if="!rail">
        <v-avatar size="36" color="#52A77A" class="mr-3">
          <span class="avatar-letter">{{ user?.username?.[0]?.toUpperCase() }}</span>
        </v-avatar>
        <div>
          <div class="user-name">{{ user?.username }}</div>
          <div class="user-role">Manager</div>
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
        >
          <template v-if="!rail && item.value === 'pending' && pendingList.length > 0" #append>
            <v-chip size="x-small" color="#52A77A" class="badge-chip">{{ pendingList.length }}</v-chip>
          </template>
        </v-list-item>
      </v-list>

      <template #append>
        <v-divider color="rgba(82,167,122,0.2)" class="mx-3 mb-2"/>
        <v-list nav density="compact">
          <v-list-item
            :prepend-icon="rail ? 'mdi-chevron-right-circle-outline' : 'mdi-chevron-left-circle-outline'"
            :title="rail ? '' : 'Collapse'"
            rounded="lg" class="nav-item"
            @click="rail = !rail"
          />
          <v-list-item prepend-icon="mdi-logout" :title="rail ? '' : 'Sign Out'" rounded="lg" class="nav-item nav-danger" @click="logout"/>
        </v-list>
      </template>
    </v-navigation-drawer>

    <v-main class="main-content">
      <div class="topbar">
        <div>
          <h2 class="page-title">{{ currentPage === 'pending' ? 'Pending Requests' : currentPage === 'active' ? 'Active Borrowings' : 'Return History' }}</h2>
          <p class="page-sub">{{ currentPage === 'pending' ? 'Review and approve borrow requests' : currentPage === 'active' ? 'Monitor outstanding loans' : 'Past returns and records' }}</p>
        </div>
        <div class="topbar-right">
          <v-btn icon variant="text" class="icon-btn" @click="drawer = !drawer; rail = false">
            <v-icon>mdi-menu</v-icon>
          </v-btn>
          <v-avatar size="36" color="#52A77A">
            <span class="avatar-letter-sm">{{ user?.username?.[0]?.toUpperCase() }}</span>
          </v-avatar>
        </div>
      </div>

      <div class="page-body">

        <!-- PENDING REQUESTS -->
        <div v-if="currentPage === 'pending'">
          <div v-if="pendingList.length === 0" class="empty-state">
            <v-icon size="64" color="#52A77A" class="mb-4">mdi-check-circle-outline</v-icon>
            <p>All caught up! No pending requests.</p>
          </div>
          <div v-else class="request-grid">
            <div v-for="b in pendingList" :key="b.id" class="request-card">
              <div class="request-header">
                <v-avatar size="40" color="rgba(82,167,122,0.15)" class="mr-3">
                  <v-icon color="#52A77A">mdi-account-outline</v-icon>
                </v-avatar>
                <div>
                  <p class="requester-name">{{ b.user.username }}</p>
                  <p class="request-date">Requested {{ formatDate(b.borrowDate) }}</p>
                </div>
                <v-chip size="x-small" color="#B45309" class="ml-auto">Pending</v-chip>
              </div>
              <div class="request-book">
                <img
                  :src="b.book?.coverUrl ?? 'https://placehold.co/48x64/1B3A2D/FAF6EF?text=📚'"
                  class="request-thumb"
                  @error="(e: any) => e.target.src = 'https://placehold.co/48x64/1B3A2D/FAF6EF?text=📚'"
                />
                <div>
                  <p class="request-book-title">{{ b.book.title }}</p>
                  <p class="request-book-author">by {{ b.book.author }}</p>
                </div>
              </div>
              <div class="request-actions">
                <v-btn variant="outlined" size="small" class="reject-btn" @click="reject(b.id)">
                  <v-icon start size="14">mdi-close</v-icon>Reject
                </v-btn>
                <v-btn size="small" class="approve-btn" @click="approve(b.id)">
                  <v-icon start size="14">mdi-check</v-icon>Approve
                </v-btn>
              </div>
            </div>
          </div>
        </div>

        <!-- ACTIVE BORROWINGS -->
        <div v-if="currentPage === 'active'">
          <div v-if="activeList.length === 0" class="empty-state">
            <v-icon size="64" color="#52A77A" class="mb-4">mdi-book-check-outline</v-icon>
            <p>No active borrowings at the moment.</p>
          </div>
          <div v-else>
            <!-- Overdue alert -->
            <v-alert
              v-if="overdueCount > 0"
              type="warning" variant="tonal" rounded="lg" class="mb-4"
              icon="mdi-alert-outline"
            >
              <strong>{{ overdueCount }} overdue {{ overdueCount === 1 ? 'borrowing' : 'borrowings' }}</strong> — please follow up with the borrowers.
            </v-alert>
            <div class="borrowing-table-wrap">
              <table class="lib-table">
                <thead>
                  <tr>
                    <th>Borrower</th>
                    <th>Book</th>
                    <th>Borrowed</th>
                    <th>Due Date</th>
                    <th>Status</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="b in activeList" :key="b.id" :class="{ 'row-overdue': isOverdue(b.dueDate) }">
                    <td>
                      <div class="td-user">
                        <v-avatar size="28" color="rgba(82,167,122,0.15)">
                          <span style="font-size:0.7rem;color:#52A77A;font-weight:700">{{ b.user.username[0].toUpperCase() }}</span>
                        </v-avatar>
                        <span>{{ b.user.username }}</span>
                      </div>
                    </td>
                    <td class="td-book">{{ b.book.title }}</td>
                    <td>{{ formatDate(b.borrowDate) }}</td>
                    <td>
                      <span :class="{ 'text-danger': isOverdue(b.dueDate) }">{{ formatDate(b.dueDate) }}</span>
                      <v-chip v-if="isOverdue(b.dueDate)" size="x-small" color="#C62828" class="ml-2">Overdue</v-chip>
                    </td>
                    <td>
                      <v-chip size="small" color="#2D6A4F">Active</v-chip>
                    </td>
                    <td>
                      <v-btn size="small" class="return-btn" @click="returnBook(b.id)">Mark Returned</v-btn>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- HISTORY -->
        <div v-if="currentPage === 'history'">
          <div v-if="historyList.length === 0" class="empty-state">
            <v-icon size="64" color="#52A77A" class="mb-4">mdi-history</v-icon>
            <p>No return history yet.</p>
          </div>
          <div v-else class="borrowing-table-wrap">
            <table class="lib-table">
              <thead>
                <tr>
                  <th>Borrower</th>
                  <th>Book</th>
                  <th>Borrowed</th>
                  <th>Returned</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="b in historyList" :key="b.id">
                  <td>{{ b.user.username }}</td>
                  <td class="td-book">{{ b.book.title }}</td>
                  <td>{{ formatDate(b.borrowDate) }}</td>
                  <td>{{ formatDate(b.returnDate) }}</td>
                  <td><v-chip size="small" color="#6B7280">Returned</v-chip></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </v-main>

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
const currentPage = ref('pending')
const drawer = ref(true)
const rail = ref(false)
const pendingList = ref<any[]>([])
const activeList = ref<any[]>([])
const historyList = ref<any[]>([])
const snack = ref({ show: false, text: '', color: 'success' })

const navItems = [
  { value: 'pending', icon: 'mdi-inbox-outline', label: 'Pending Requests' },
  { value: 'active', icon: 'mdi-book-clock-outline', label: 'Active Borrowings' },
  { value: 'history', icon: 'mdi-history', label: 'Return History' },
]

const overdueCount = computed(() => activeList.value.filter(b => isOverdue(b.dueDate)).length)

const fetchPending = async () => {
  try { const r = await fetch('http://localhost:8080/api/borrowings/pending'); pendingList.value = await r.json() } catch {}
}
const fetchActive = async () => {
  try { const r = await fetch('http://localhost:8080/api/borrowings/active'); activeList.value = await r.json() } catch {}
}
const fetchHistory = async () => {
  try { const r = await fetch('http://localhost:8080/api/borrowings/returned'); historyList.value = await r.json() } catch {}
}

const approve = async (id: number) => {
  await fetch(`http://localhost:8080/api/borrowings/${id}/approve?managerId=${user.id}`, { method: 'PUT' })
  snack.value = { show: true, text: 'Request approved!', color: 'success' }
  fetchPending(); fetchActive()
}
const reject = async (id: number) => {
  await fetch(`http://localhost:8080/api/borrowings/${id}/reject`, { method: 'PUT' })
  snack.value = { show: true, text: 'Request rejected.', color: 'warning' }
  fetchPending()
}
const returnBook = async (id: number) => {
  await fetch(`http://localhost:8080/api/borrowings/${id}/return`, { method: 'PUT' })
  snack.value = { show: true, text: 'Book marked as returned.', color: 'success' }
  fetchActive(); fetchHistory()
}

const formatDate = (d: string) => {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('en-PH', { year: 'numeric', month: 'short', day: 'numeric' })
}
const isOverdue = (dueDate: string) => dueDate && new Date(dueDate) < new Date()

const logout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); router.push('/') }

onMounted(() => { fetchPending(); fetchActive(); fetchHistory() })
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=Inter:wght@300;400;500;600&display=swap');

.lib-app { background: #F0F7F3 !important; }
:deep(.v-navigation-drawer) { border-right: 1px solid rgba(82,167,122,0.15) !important; }
.sidebar-brand { display: flex; align-items: center; gap: 0.75rem; padding: 1.4rem 1.2rem 1rem; }
.rail-brand { justify-content: center; padding: 1.4rem 0 1rem; }
.brand-text { display: flex; flex-direction: column; }
.brand-name { font-family: 'Playfair Display', serif; font-size: 1.1rem; font-weight: 700; color: #E8F5EE; line-height: 1.1; }
.brand-role { font-family: 'Inter', sans-serif; font-size: 0.65rem; color: #52A77A; text-transform: uppercase; letter-spacing: 0.12em; }
.user-chip { display: flex; align-items: center; padding: 0.75rem 1.2rem; margin: 0.5rem 0.75rem; background: rgba(82,167,122,0.08); border-radius: 10px; }
.avatar-letter, .avatar-letter-sm { font-family: 'Playfair Display', serif; font-weight: 700; color: #FAF6EF; }
.avatar-letter { font-size: 1rem; } .avatar-letter-sm { font-size: 0.9rem; }
.user-name { font-family: 'Inter', sans-serif; font-size: 0.85rem; font-weight: 600; color: #E8F5EE; }
.user-role { font-family: 'Inter', sans-serif; font-size: 0.7rem; color: #52A77A; }
.nav-item :deep(.v-list-item__prepend .v-icon) { color: rgba(232,245,238,0.45) !important; }
.nav-item :deep(.v-list-item-title) { font-family: 'Inter', sans-serif; font-size: 0.88rem; color: rgba(232,245,238,0.65); }
.nav-item:hover :deep(.v-list-item__prepend .v-icon), .nav-item:hover :deep(.v-list-item-title) { color: #52A77A !important; }
.nav-active { background: rgba(82,167,122,0.12) !important; }
.nav-active :deep(.v-list-item__prepend .v-icon), .nav-active :deep(.v-list-item-title) { color: #52A77A !important; }
.nav-danger :deep(.v-list-item__prepend .v-icon), .nav-danger :deep(.v-list-item-title) { color: rgba(239,100,100,0.65) !important; }
.badge-chip { font-weight: 700 !important; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.main-content { background: #F0F7F3; }
.topbar { display: flex; align-items: center; justify-content: space-between; padding: 1.5rem 2rem 0.5rem; border-bottom: 1px solid rgba(82,167,122,0.12); background: #F0F7F3; }
.page-title { font-family: 'Playfair Display', serif; font-size: 1.6rem; font-weight: 700; color: #133528; }
.page-sub { font-family: 'Inter', sans-serif; font-size: 0.85rem; color: #4A7A60; }
.topbar-right { display: flex; align-items: center; gap: 0.75rem; }
.icon-btn { color: #1B3A2D !important; }
.page-body { padding: 1.5rem 2rem 2rem; }

/* CARDS */
.request-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1rem; }
.request-card { background: #fff; border: 1px solid rgba(82,167,122,0.2); border-radius: 12px; padding: 1.2rem; box-shadow: 0 2px 8px rgba(27,58,45,0.05); }
.request-header { display: flex; align-items: center; margin-bottom: 1rem; }
.requester-name { font-family: 'Inter', sans-serif; font-weight: 600; font-size: 0.9rem; color: #133528; }
.request-date { font-family: 'Inter', sans-serif; font-size: 0.75rem; color: #4A7A60; }
.request-book { display: flex; align-items: center; gap: 0.75rem; background: #F0F7F3; border-radius: 8px; padding: 0.75rem; margin-bottom: 1rem; }
.request-thumb { width: 40px; height: 54px; object-fit: cover; border-radius: 4px; flex-shrink: 0; }
.request-book-title { font-family: 'Playfair Display', serif; font-size: 0.88rem; font-weight: 600; color: #133528; }
.request-book-author { font-family: 'Inter', sans-serif; font-size: 0.75rem; color: #4A7A60; }
.request-actions { display: flex; gap: 0.6rem; }
.reject-btn { color: #C62828 !important; border-color: rgba(198,40,40,0.3) !important; flex: 1; }
.approve-btn { background: linear-gradient(135deg, #52A77A, #2D6A4F) !important; color: #fff !important; font-weight: 600 !important; flex: 1; box-shadow: 0 3px 10px rgba(45,106,79,0.25) !important; }

/* TABLE */
.borrowing-table-wrap { background: #fff; border: 1px solid rgba(82,167,122,0.18); border-radius: 12px; overflow: hidden; }
.lib-table { width: 100%; border-collapse: collapse; font-family: 'Inter', sans-serif; font-size: 0.875rem; }
.lib-table thead { background: #F0F7F3; }
.lib-table th { padding: 0.85rem 1.1rem; text-align: left; font-size: 0.75rem; font-weight: 600; color: #4A7A60; text-transform: uppercase; letter-spacing: 0.07em; border-bottom: 1px solid rgba(82,167,122,0.15); }
.lib-table td { padding: 0.9rem 1.1rem; color: #133528; border-bottom: 1px solid rgba(82,167,122,0.08); vertical-align: middle; }
.lib-table tbody tr:last-child td { border-bottom: none; }
.lib-table tbody tr:hover { background: rgba(82,167,122,0.04); }
.row-overdue { background: rgba(198,40,40,0.04) !important; }
.td-user { display: flex; align-items: center; gap: 0.5rem; }
.td-book { font-weight: 500; }
.text-danger { color: #C62828; font-weight: 600; }
.return-btn { background: rgba(82,167,122,0.12) !important; color: #2D6A4F !important; font-weight: 600 !important; }

.empty-state { display: flex; flex-direction: column; align-items: center; padding: 5rem 2rem; font-family: 'Inter', sans-serif; color: #4A7A60; text-align: center; }
</style>
