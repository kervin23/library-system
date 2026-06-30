<template>
  <v-app>

    <!-- NAVBAR -->
    <v-app-bar flat elevation="1" color="white">
      <template #prepend>
        <v-icon size="32" color="#2d5a3d" class="ml-2">mdi-bookshelf</v-icon>
      </template>
      <v-app-bar-title>
        <span style="font-weight: 700">Library System</span>
        <div style="font-size: 0.7rem; color: #888; line-height: 1">Digital Library</div>
      </v-app-bar-title>

      <div class="d-none d-md-flex ga-6 mx-6">
        <a href="#" class="nav-link">Home</a>
        <a href="#books" class="nav-link">Browse Books</a>
        <a href="#categories" class="nav-link">Categories</a>
        <a href="#about" class="nav-link">About Us</a>
      </div>

      <template #append>
        <RouterLink to="/login">
          <v-btn color="#2d5a3d" rounded="lg" prepend-icon="mdi-account" class="mr-2">
            Login
          </v-btn>
        </RouterLink>
      </template>
    </v-app-bar>

    <v-main>

      <!-- HERO SECTION -->
      <v-sheet class="hero-section" color="transparent">
        <div class="hero-bg"></div>
        <div class="hero-overlay"></div>

        <v-container class="hero-content" style="position: relative; z-index: 2;">
          <v-row align="center" style="min-height: 100vh">
            <v-col cols="12" md="7">
              <h1 class="hero-title">Discover. Learn.<br/>Grow.</h1>
              <p class="hero-sub mb-6">
                Explore thousands of books, expand your knowledge,
                and empower your future.
              </p>

              <v-sheet rounded="lg" class="d-flex align-center pa-2 mb-8" max-width="520" elevation="0">
                <v-icon color="#888" class="ml-2">mdi-magnify</v-icon>
                <v-text-field
                  v-model="searchQuery"
                  placeholder="Search books, authors, categories..."
                  variant="plain"
                  hide-details
                  density="compact"
                  class="mx-2"
                />
                <v-btn color="#2d5a3d" rounded="lg" @click="search">Search</v-btn>
              </v-sheet>
              
              <v-row>
                <v-col cols="auto" v-for="stat in stats" :key="stat.label">
                  <div class="d-flex align-center ga-3">
                    <v-icon color="white" size="28">{{ stat.icon }}</v-icon>
                    <div>
                      <div class="stat-num">{{ stat.value }}+</div>
                      <div class="stat-label">{{ stat.label }}</div>
                    </div>
                  </div>
                </v-col>
              <RouterLink to="/register" class="ml-4">
              <v-btn class="ml-auto align-self-center" color="#2d5a3d" rounded="lg" size="large">
                Get Started
              </v-btn>
            </RouterLink>
              </v-row>
              
            </v-col>
            
          </v-row>
          
        </v-container>
        
      </v-sheet>

      <!-- FEATURED BOOKS -->
      <v-container class="py-12" id="books">
        <v-row align="center" class="mb-6">
          <v-col><h2 class="section-title">Featured Books</h2></v-col>
          <v-col cols="auto">
            <RouterLink to="/login" class="view-all">View all →</RouterLink>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" md="12">

            <h2 class="mb-6">Available Books</h2>

            <div class="scroll-container mb-6" ref="scrollEl">
              <v-slide-group show-arrows>
                <v-slide-group-item v-for="book in featuredBooks" :key="book.id">
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
                        @click.stop="search"
                      >
                        Borrow
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-slide-group-item>
              </v-slide-group>
            </div>

          <!-- JOIN CARD -->
          <v-col cols="12" md="3">
            <v-card color="#f0f5f1" rounded="xl" elevation="0" class="pa-4 text-center">
              <v-icon size="50" color="#2d5a3d" class="mb-3">mdi-book-open-page-variant</v-icon>
              <v-card-title class="text-h6 pa-0 mb-2">Join Our Library</v-card-title>
              <v-card-text class="pa-0 mb-4 text-body-2 text-medium-emphasis">
                Create an account to borrow books, track your reading, and more.
              </v-card-text>
              <RouterLink to="/register">
                <v-btn color="#2d5a3d" rounded="lg"  prepend-icon="mdi-account-plus">
                  Login / Register
                </v-btn>
              </RouterLink>
            </v-card>
          </v-col>
          </v-col>

        </v-row>
      </v-container>

      <!-- BROWSE BY CATEGORY -->
      <v-sheet color="#f8f8f5" class="py-12" id="categories">
        <v-container>
          <v-row align="center" class="mb-6">
            <v-col><h2 class="section-title">Browse by Category</h2></v-col>
            <v-col cols="auto">
              <RouterLink to="/login" class="view-all">View all categories →</RouterLink>
            </v-col>
          </v-row>

          <v-row>
            <v-col cols="6" sm="4" md="3" lg="2" v-for="cat in categories" :key="cat.name">
              <v-card
                rounded="xl"
                elevation="0"
                border
                class="pa-4 category-card"
                @click="search"
              >
                <v-icon size="28" color="#2d5a3d" class="mb-2">{{ cat.icon }}</v-icon>
                <div class="cat-name">{{ cat.name }}</div>
                <div class="cat-count">{{ cat.count }} books</div>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-sheet>

      <!-- ABOUT SECTION -->
      <v-container class="py-12" id="about">
        <v-row justify="center">
          <v-col cols="12" md="6" class="text-center">
            <v-icon size="60" color="#2d5a3d" class="mb-4">mdi-library</v-icon>
            <h2 class="section-title mb-4">About Our Library</h2>
            <p class="text-body-1 text-medium-emphasis mb-6" style="line-height: 1.8">
              We are a digital library system dedicated to making books accessible to everyone.
              Browse our collection, borrow books, and track your reading journey all in one place.
            </p>
            <RouterLink to="/register">
              <v-btn color="#2d5a3d" rounded="lg" size="large">
                Get Started
              </v-btn>
            </RouterLink>
          </v-col>
        </v-row>
      </v-container>

    </v-main>

    <!-- BOOK DETAILS DIALOG -->
    <v-dialog v-model="bookDialog" max-width="420">
      <v-card v-if="selectedBook" rounded="lg">
        <v-img
          :src="selectedBook.coverUrl ?? 'https://placehold.co/400x250?text=No+Cover'"
          height="250"
          cover
        />
        <v-card-title>{{ selectedBook.title }}</v-card-title>
        <v-card-subtitle>{{ selectedBook.author }}</v-card-subtitle>
        <v-card-text>
          {{ selectedBook.description ?? 'No description available.' }}
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="bookDialog = false">Close</v-btn>
          <v-btn color="#2d5a3d" @click="search">Login to Borrow</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchQuery = ref('')
const featuredBooks = ref<any[]>([])
const totalBooks = ref(0)
const totalUsers = ref(0)
const bookDialog = ref(false)
const selectedBook = ref<any>(null)

const categories = ref([
  { name: 'Fiction', icon: 'mdi-feather', count: 0 },
  { name: 'Science', icon: 'mdi-flask', count: 0 },
  { name: 'Technology', icon: 'mdi-laptop', count: 0 },
  { name: 'History', icon: 'mdi-bank', count: 0 },
  { name: 'Education', icon: 'mdi-school', count: 0 },
  { name: 'Biography', icon: 'mdi-account-details', count: 0 },
])

const stats = computed(() => [
  { icon: 'mdi-book-open-variant', value: totalBooks.value, label: 'Books' },
  { icon: 'mdi-account-group', value: totalUsers.value, label: 'Members' },
  { icon: 'mdi-bookmark-multiple', value: categories.value.length, label: 'Categories' },
])

const viewBook = (book: any) => {
  selectedBook.value = book
  bookDialog.value = true
}

const search = () => router.push('/login')

const fetchBooks = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/books')
    const data = await response.json()
    featuredBooks.value = data.slice(0, 6)
    totalBooks.value = data.length
    categories.value = categories.value.map(cat => ({
      ...cat,
      count: data.filter((b: any) =>
        b.category?.toLowerCase() === cat.name.toLowerCase()
      ).length
    }))
  } catch (e) {
    console.error('Cannot fetch books', e)
  }
}

const fetchUsers = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/users')
    const users = await response.json()
    totalUsers.value = users.length
  } catch (e) {
    console.error('Cannot fetch users', e)
  }
}

onMounted(() => {
  fetchBooks()
  fetchUsers()

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
</script>

<style scoped>
.hero-section {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-image: url('../components/image/landing page photo1.png');
  background-size: cover;
  background-position: center;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to right,
    rgba(0,0,0,0.85) 0%,
    rgba(0,0,0,0.6) 50%,
    rgba(0,0,0,0.1) 100%
  );
}

.hero-title {
  font-size: clamp(2.5rem, 5vw, 4rem);
  font-weight: 900;
  color: white;
  line-height: 1.1;
  margin-bottom: 1rem;
}

.hero-sub {
  font-size: 1.1rem;
  color: rgba(255,255,255,0.85);
  line-height: 1.6;
}

.stat-num {
  font-size: 1.4rem;
  font-weight: 800;
  color: white;
}

.stat-label {
  font-size: 0.8rem;
  color: rgba(255,255,255,0.7);
}

.nav-link {
  text-decoration: none;
  color: #444;
  font-size: 0.9rem;
  font-weight: 500;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #2d5a3d;
}

.section-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a1a1a;
}

.view-all {
  color: #2d5a3d;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 600;
}

.book-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.3;
}

.book-author {
  font-size: 0.75rem;
  color: #888;
}

.book-card {
  transition: transform 0.2s;
}

.book-card:hover {
  transform: translateY(-4px);
}

.category-card {
  cursor: pointer;
  transition: all 0.2s;
}

.category-card:hover {
  border-color: #2d5a3d !important;
  transform: translateY(-2px);
}

.cat-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1a1a1a;
}

.cat-count {
  font-size: 0.75rem;
  color: #888;
}
</style>