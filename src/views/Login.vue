<template>
  <v-app :theme="isDark ? 'dark' : 'light'">
    <v-main>
      <v-container fluid class="fill-height pa-0">
        <v-row no-gutters class="fill-height">
<!-- LEFT SIDE -->
<v-col cols="12" md="6" class="d-none d-md-flex left-panel" :class="isDark ? 'left-dark' : 'left-light'">
  <div class="left-bg-circles">
    <div class="circle circle-1"></div>
    <div class="circle circle-2"></div>
    <div class="circle circle-3"></div>
  </div>
  <div class="left-content">
    <div class="icon-grid">
      <v-icon size="28" opacity="0.3">mdi-book-open-variant</v-icon>
      <v-icon size="20" opacity="0.2">mdi-bookmark</v-icon>
      <v-icon size="24" opacity="0.25">mdi-library</v-icon>
      <v-icon size="18" opacity="0.2">mdi-book</v-icon>
      <v-icon size="32" opacity="0.15">mdi-bookshelf</v-icon>
      <v-icon size="22" opacity="0.3">mdi-feather</v-icon>
    </div>

    <svg viewBox="0 0 200 200" width="220" height="220" xmlns="http://www.w3.org/2000/svg">
      <rect x="20" y="155" width="160" height="8" rx="3" fill="currentColor" opacity="0.4"/>
      <rect x="30" y="100" width="22" height="55" rx="3" fill="currentColor" opacity="0.9"/>
      <rect x="31" y="101" width="4" height="53" rx="1" fill="white" opacity="0.2"/>
      <rect x="56" y="85" width="22" height="70" rx="3" fill="currentColor" opacity="0.7"/>
      <rect x="57" y="86" width="4" height="68" rx="1" fill="white" opacity="0.2"/>
      <rect x="82" y="95" width="18" height="60" rx="3" fill="currentColor" opacity="0.85"/>
      <rect x="104" y="78" width="24" height="77" rx="3" fill="currentColor" opacity="0.6"/>
      <rect x="105" y="79" width="4" height="75" rx="1" fill="white" opacity="0.2"/>
      <rect x="132" y="90" width="20" height="65" rx="3" fill="currentColor" opacity="0.8"/>
      <g transform="rotate(-15, 155, 130)">
        <rect x="148" y="105" width="16" height="50" rx="3" fill="currentColor" opacity="0.5"/>
      </g>
      <circle cx="60" cy="40" r="2" fill="currentColor" opacity="0.4"/>
      <circle cx="100" cy="25" r="3" fill="currentColor" opacity="0.3"/>
      <circle cx="150" cy="45" r="2" fill="currentColor" opacity="0.5"/>
      <circle cx="40" cy="60" r="1.5" fill="currentColor" opacity="0.3"/>
      <circle cx="170" cy="65" r="2" fill="currentColor" opacity="0.4"/>
    </svg>

    <h1 class="left-title">Library System</h1>

    <div class="quote-box">
      <v-icon size="28" class="quote-icon">mdi-format-quote-open</v-icon>
      <p class="left-quote">A reader lives a thousand lives before he dies. The man who never reads lives only one.</p>
      <p class="left-author">— George R.R. Martin</p>
    </div>

    <div class="stats-row">
      <div class="stat">
        <span class="stat-number">1000+</span>
        <span class="stat-label">Books</span>
        <span class="stat-number">  |  </span>
        <span class="stat-number">500+</span>
        <span class="stat-label">Readers</span>
        <span class="stat-number">  |  </span>
        <span class="stat-number">50+</span>
        <span class="stat-label">Categories</span>
      </div>
    </div>
  </div>
</v-col>

          <!-- RIGHT SIDE -->
          <v-col cols="12" md="6" class="d-flex align-center justify-center right-panel" :class="isDark ? 'right-dark' : 'right-light'">
            <div class="form-wrapper">

              <div class="d-flex justify-space-between align-center mb-6">
                <h2 class="form-title">Welcome Back</h2>
                  <v-btn icon variant="text" @click="toggleDark">
                  <v-icon>{{ isDark ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
                </v-btn>
              </div>

              <p class="form-subtitle mb-6">Sign in to your account</p>

              <v-alert v-if="error" type="error" class="mb-4" rounded="lg">{{ error }}</v-alert>

              <v-text-field
                v-model="username"
                label="Username"
                prepend-inner-icon="mdi-account-outline"
                variant="outlined"
                rounded="lg"
                class="mb-3"
              />

              <v-text-field
                v-model="password"
                label="Password"
                prepend-inner-icon="mdi-lock-outline"
                :type="showPassword ? 'text' : 'password'"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append-inner="showPassword = !showPassword"
                variant="outlined"
                rounded="lg"
                class="mb-4"
              />

              <v-btn
                color="primary"
                block
                size="large"
                rounded="lg"
                class="mb-4"
                @click="login"
              >
                Sign In
              </v-btn>

              <div class="text-center">
                <span class="text-medium-emphasis">Don't have an account? </span>
                <RouterLink to="/register" class="text-primary">Create one</RouterLink>
              </div>

            </div>
          </v-col>

        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const error = ref('')
const isDark = ref(localStorage.getItem('darkMode') === 'true')
const showPassword = ref(false)

const toggleDark = () => {
  isDark.value = !isDark.value
  localStorage.setItem('darkMode', String(isDark.value))
}

const login = async () => {
  error.value = ''
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    })

    if (response.ok) {
      const user = await response.json()
      localStorage.setItem('user', JSON.stringify(user))
      if (user.role === 'admin') router.push('/admin')
      else if (user.role === 'manager') router.push('/manager')
      else router.push('/home')
    } else {
      error.value = 'Invalid username or password'
    }
  } catch (e) {
    error.value = 'Cannot connect to server'
  }
}
</script>

<style scoped>
.left-panel {
  align-items: center;
  justify-content: center;
}
.left-dark {
  background: #1a1a2e;
  color: #e0e0e0;
}
.left-light {
  background: #f0f4ff;
  color: #3a3a5c;
}
.left-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 2rem;
}
.left-title {
  font-size: 2rem;
  font-weight: 700;
  margin-top: 1.5rem;
  margin-bottom: 1rem;
}
.left-quote {
  font-size: 1rem;
  font-style: italic;
  opacity: 0.8;
  max-width: 320px;
  line-height: 1.6;
}
.left-author {
  font-size: 0.85rem;
  opacity: 0.6;
  margin-top: 0.5rem;
}
.right-panel {
  min-height: 100vh;
}
.right-dark {
  background: #0f0f1a;
}
.right-light {
  background: #ffffff;
}
.form-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
}
.form-title {
  font-size: 1.8rem;
  font-weight: 700;
}
.form-subtitle {
  opacity: 0.6;
  font-size: 0.95rem;
}
</style>