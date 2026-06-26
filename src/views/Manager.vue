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
        <v-tabs v-model="tab" class="mb-4">
          <v-tab value="pending">Pending Requests</v-tab>
          <v-tab value="active">Active Borrowings</v-tab>
        </v-tabs>

        <v-window v-model="tab">

          <!-- PENDING REQUESTS -->
          <v-window-item value="pending">
            <v-row class="mb-4" align="center">
              <v-col><h3>Pending Borrow Requests</h3></v-col>
            </v-row>

            <v-table>
              <thead>
                <tr>
                  <th>User</th>
                  <th>Book</th>
                  <th>Requested</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="pendingList.length === 0">
                  <td colspan="4" class="text-center pa-4">No pending requests</td>
                </tr>
                <tr v-for="b in pendingList" :key="b.id">
                  <td>{{ b.user.username }}</td>
                  <td>{{ b.book.title }}</td>
                  <td>{{ formatDate(b.borrowDate) }}</td>
                  <td>
                    <v-btn color="green" size="small" class="mr-2" @click="approve(b.id)">
                      Approve
                    </v-btn>
                    <v-btn color="red" size="small" @click="reject(b.id)">
                      Reject
                    </v-btn>
                  </td>
                </tr>
              </tbody>
            </v-table>
          </v-window-item>

          <!-- ACTIVE BORROWINGS -->
          <v-window-item value="active">
            <v-row class="mb-4" align="center">
              <v-col><h3>Active Borrowings</h3></v-col>
            </v-row>

            <v-table>
              <thead>
                <tr>
                  <th>User</th>
                  <th>Book</th>
                  <th>Borrowed</th>
                  <th>Due Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="activeList.length === 0">
                  <td colspan="5" class="text-center pa-4">No active borrowings</td>
                </tr>
                <tr v-for="b in activeList" :key="b.id">
                  <td>{{ b.user.username }}</td>
                  <td>{{ b.book.title }}</td>
                  <td>{{ formatDate(b.borrowDate) }}</td>
                  <td :class="isOverdue(b.dueDate) ? 'text-red' : ''">
                    {{ formatDate(b.dueDate) }}
                    <v-chip v-if="isOverdue(b.dueDate)" color="red" size="x-small" class="ml-1">
                      Overdue
                    </v-chip>
                  </td>
                  <td>
                    <v-btn color="primary" size="small" @click="returnBook(b.id)">
                      Mark Returned
                    </v-btn>
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
const tab = ref('pending')
const pendingList = ref([])
const activeList = ref([])

const fetchPending = async () => {
  const response = await fetch('http://localhost:8080/api/borrowings/pending')
  pendingList.value = await response.json()
}

const fetchActive = async () => {
  const response = await fetch('http://localhost:8080/api/borrowings/active')
  activeList.value = await response.json()
}

const approve = async (id: number) => {
  await fetch(`http://localhost:8080/api/borrowings/${id}/approve?managerId=${user.id}`, {
    method: 'PUT'
  })
  fetchPending()
  fetchActive()
}

const reject = async (id: number) => {
  await fetch(`http://localhost:8080/api/borrowings/${id}/reject`, {
    method: 'PUT'
  })
  fetchPending()
}

const returnBook = async (id: number) => {
  await fetch(`http://localhost:8080/api/borrowings/${id}/return`, {
    method: 'PUT'
  })
  fetchActive()
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('en-PH', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const isOverdue = (dueDate: string) => {
  if (!dueDate) return false
  return new Date(dueDate) < new Date()
}

onMounted(() => {
  fetchPending()
  fetchActive()
})

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/')
}
</script>