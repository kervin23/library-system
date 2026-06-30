<template>
  <v-app>
    <v-main class="auth-bg">
      <v-container fluid class="fill-height pa-0">
        <v-row no-gutters class="fill-height">

          <!-- LEFT PANEL -->
          <v-col cols="12" md="6" class="d-none d-md-flex left-panel">
            <div class="left-overlay"/>
            <div class="left-content">
              <div class="brand-mark mb-6">
                <v-icon size="48" color="#C8933A">mdi-bookshelf</v-icon>
              </div>
              <h1 class="brand-title">Library</h1>
              <p class="brand-sub">Library Management System</p>

              <div class="features-list mt-10 mb-10">
                <div class="feature-item" v-for="f in features" :key="f.text">
                  <v-icon :icon="f.icon" color="#C8933A" size="20"/>
                  <span>{{ f.text }}</span>
                </div>
              </div>

              <div class="stat-strip">
                <div class="stat-item">
                  <span class="stat-num">1,000+</span>
                  <span class="stat-lbl">Books</span>
                </div>
                <div class="stat-divider"/>
                <div class="stat-item">
                  <span class="stat-num">500+</span>
                  <span class="stat-lbl">Readers</span>
                </div>
                <div class="stat-divider"/>
                <div class="stat-item">
                  <span class="stat-num">50+</span>
                  <span class="stat-lbl">Categories</span>
                </div>
              </div>
            </div>
          </v-col>

          <!-- RIGHT PANEL -->
          <v-col cols="12" md="6" class="d-flex align-center justify-center right-panel">
            <div class="form-card">
              <div class="form-header mb-7">
                <div class="mobile-brand d-flex d-md-none align-center mb-6 gap-2">
                  <v-icon color="#C8933A" size="28">mdi-bookshelf</v-icon>
                  <span class="brand-title-sm">Library</span>
                </div>
                <h2 class="form-title">Create your account</h2>
                <p class="form-sub">Join the community of readers today</p>
              </div>

              <v-alert v-if="error" type="error" variant="tonal" rounded="lg" class="mb-4">{{ error }}</v-alert>
              <v-alert v-if="success" type="success" variant="tonal" rounded="lg" class="mb-4">{{ success }}</v-alert>

              <div class="field-group mb-4">
                <label class="field-label">Username</label>
                <v-text-field
                  v-model="username"
                  placeholder="Choose a username"
                  prepend-inner-icon="mdi-account-outline"
                  variant="outlined" rounded="lg" hide-details
                  class="warm-field"
                />
              </div>

              <div class="field-group mb-4">
                <label class="field-label">Email Address</label>
                <v-text-field
                  v-model="email"
                  placeholder="your@email.com"
                  prepend-inner-icon="mdi-email-outline"
                  variant="outlined" rounded="lg" hide-details
                  class="warm-field"
                />
              </div>

              <div class="field-group mb-4">
                <label class="field-label">Password</label>
                <v-text-field
                  v-model="password"
                  placeholder="Create a strong password"
                  prepend-inner-icon="mdi-lock-outline"
                  :type="showPw ? 'text' : 'password'"
                  :append-inner-icon="showPw ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                  @click:append-inner="showPw = !showPw"
                  variant="outlined" rounded="lg" hide-details
                  class="warm-field"
                />
              </div>

              <div class="field-group mb-6">
                <label class="field-label">Confirm Password</label>
                <v-text-field
                  v-model="confirmPassword"
                  placeholder="Repeat your password"
                  prepend-inner-icon="mdi-lock-check-outline"
                  type="password"
                  variant="outlined" rounded="lg" hide-details
                  class="warm-field"
                />
              </div>

              <v-btn block size="large" rounded="lg" class="sign-up-btn mb-5" :loading="loading" @click="register">
                <v-icon start>mdi-account-plus-outline</v-icon>
                Create Account
              </v-btn>

              <p class="text-center login-link">
                Already have an account?
                <RouterLink to="/login" class="link-accent">Sign in here</RouterLink>
              </p>
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
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)
const showPw = ref(false)

const features = [
  { icon: 'mdi-book-search-outline', text: 'Browse thousands of books across 50+ categories' },
  { icon: 'mdi-clock-check-outline', text: 'Request and track borrowings in real time' },
  { icon: 'mdi-history', text: 'Keep a full history of everything you\'ve read' },
  { icon: 'mdi-bell-outline', text: 'Get notified when your requests are approved' },
]

const register = async () => {
  error.value = ''
  success.value = ''
  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }
  loading.value = true
  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, email: email.value, password: password.value, role: 'user' })
    })
    if (response.ok) {
      success.value = 'Account created! Redirecting to sign in...'
      setTimeout(() => router.push('/'), 2000)
    } else {
      error.value = await response.text()
    }
  } catch {
    error.value = 'Cannot connect to server'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;0,700;1,600&family=Inter:wght@300;400;500;600&display=swap');

.auth-bg { background: #FAF6EF; }
.left-panel {
  position: relative;
  background: url('https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=900&q=80') center/cover no-repeat;
  align-items: center; justify-content: center; overflow: hidden;
}
.left-overlay {
  position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(202, 161, 120, 0.9) 0%, rgba(50,28,10,0.82) 100%);
}
.left-content {
  position: relative; z-index: 2;
  display: flex; flex-direction: column; align-items: center; text-align: center;
  padding: 3rem 2.5rem; color: #FAF6EF;
}
.brand-mark { opacity: 0.9; }
.brand-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 2.6rem; font-weight: 700;
  color: #F5ECD7; letter-spacing: 0.02em; line-height: 1.1;
}
.brand-sub {
  font-family: 'Inter', sans-serif;
  font-size: 0.85rem; letter-spacing: 0.18em;
  text-transform: uppercase; color: #C8933A; margin-top: 0.3rem;
}
.features-list { display: flex; flex-direction: column; gap: 1rem; max-width: 340px; text-align: left; }
.feature-item {
  display: flex; align-items: center; gap: 0.85rem;
  font-family: 'Inter', sans-serif; font-size: 0.88rem;
  color: rgba(245,236,215,0.85); line-height: 1.4;
}
.stat-strip {
  display: flex; align-items: center; gap: 1.5rem;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(200,147,58,0.25);
  border-radius: 50px; padding: 0.9rem 2rem;
}
.stat-item { display: flex; flex-direction: column; align-items: center; }
.stat-num {
  font-family: 'Playfair Display', serif;
  font-size: 1.15rem; font-weight: 700; color: #C8933A;
}
.stat-lbl {
  font-family: 'Inter', sans-serif;
  font-size: 0.7rem; color: rgba(245,236,215,0.6);
  text-transform: uppercase; letter-spacing: 0.1em;
}
.stat-divider { width: 1px; height: 28px; background: rgba(200,147,58,0.3); }
.right-panel { background: #FAF6EF; min-height: 100vh; }
.form-card { width: 100%; max-width: 420px; padding: 2.5rem 2rem; }
.brand-title-sm {
  font-family: 'Playfair Display', serif; font-size: 1.4rem;
  font-weight: 700; color: #5C3D1E;
}
.form-title {
  font-family: 'Playfair Display', serif; font-size: 2rem;
  font-weight: 700; color: #3B2209; line-height: 1.2;
}
.form-sub {
  font-family: 'Inter', sans-serif; font-size: 0.9rem;
  color: #8A7060; margin-top: 0.4rem;
}
.field-label {
  display: block; font-family: 'Inter', sans-serif;
  font-size: 0.8rem; font-weight: 600; color: #5C3D1E;
  letter-spacing: 0.05em; text-transform: uppercase; margin-bottom: 0.4rem;
}
.warm-field :deep(.v-field) { background: #FFF8EE !important; border-color: #D9C5A0 !important; }
.warm-field :deep(.v-field--focused) { border-color: #C8933A !important; }
.warm-field :deep(input::placeholder) { color: #B0957A; }
.sign-up-btn {
  background: linear-gradient(135deg, #C8933A 0%, #A0702A 100%) !important;
  color: #FAF6EF !important; font-family: 'Inter', sans-serif;
  font-weight: 600; letter-spacing: 0.05em;
  box-shadow: 0 4px 16px rgba(160,112,42,0.35) !important;
}
.login-link { font-family: 'Inter', sans-serif; font-size: 0.88rem; color: #8A7060; }
.link-accent { color: #C8933A; font-weight: 600; text-decoration: none; }
.link-accent:hover { text-decoration: underline; }
</style>
