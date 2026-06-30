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

              <div class="quote-card mt-10 mb-10">
                <span class="quote-open">"</span>
                <p class="quote-text">A reader lives a thousand lives before he dies.<br>The man who never reads lives only one.</p>
                <p class="quote-author">— George R.R. Martin</p>
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
              <div class="form-header mb-8">
                <div class="mobile-brand d-flex d-md-none align-center mb-6 gap-2">
                  <v-icon color="#C8933A" size="28">mdi-bookshelf</v-icon>
                  <span class="brand-title-sm">Library</span>
                </div>
                <h2 class="form-title">Welcome back</h2>
                <p class="form-sub">Sign in to continue your reading journey</p>
              </div>

              <v-alert v-if="error" type="error" variant="tonal" rounded="lg" class="mb-5">{{ error }}</v-alert>

              <div class="field-group mb-4">
                <label class="field-label">Username</label>
                <v-text-field
                  v-model="username"
                  placeholder="Enter your username"
                  prepend-inner-icon="mdi-account-outline"
                  variant="outlined"
                  rounded="lg"
                  hide-details
                  class="warm-field"
                  @keyup.enter="login"
                />
              </div>

              <div class="field-group mb-6">
                <label class="field-label">Password</label>
                <v-text-field
                  v-model="password"
                  placeholder="Enter your password"
                  prepend-inner-icon="mdi-lock-outline"
                  :type="showPassword ? 'text' : 'password'"
                  :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                  @click:append-inner="showPassword = !showPassword"
                  variant="outlined"
                  rounded="lg"
                  hide-details
                  class="warm-field"
                  @keyup.enter="login"
                />
              </div>

              <v-btn
                block
                size="large"
                rounded="lg"
                class="sign-in-btn mb-5"
                :loading="loading"
                @click="login"
              >
                <v-icon start>mdi-login</v-icon>
                Sign In
              </v-btn>

              <p class="text-center register-link">
                Don't have an account?
                <RouterLink to="/register" class="link-accent">Create one here</RouterLink>
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
const password = ref('')
const error = ref('')
const loading = ref(false)
const showPassword = ref(false)

const login = async () => {
  error.value = ''
  loading.value = true
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    })
    if (response.ok) {
      const data = await response.json()
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify({ id: data.id, username: data.username, role: data.role }))
      if (data.role === 'admin') router.push('/admin')
      else if (data.role === 'manager') router.push('/manager')
      else router.push('/home')
    } else {
      error.value = 'Invalid username or password'
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

/* LEFT */
.left-panel {
  position: relative;
  background: url("../components/image/bg.png") center/cover no-repeat;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.left-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(168, 122, 76, 0.9) 0%, rgba(50,28,10,0.82) 100%);
}
.left-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 3rem 2.5rem;
  color: #FAF6EF;
}
.brand-mark { opacity: 0.9; }
.brand-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 2.6rem;
  font-weight: 700;
  color: #F5ECD7;
  letter-spacing: 0.02em;
  line-height: 1.1;
}
.brand-sub {
  font-family: 'Inter', sans-serif;
  font-size: 0.85rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: #C8933A;
  margin-top: 0.3rem;
}
.quote-card {
  background: rgba(255,255,255,0.07);
  border-left: 3px solid #C8933A;
  border-radius: 0 12px 12px 0;
  padding: 1.5rem 1.8rem;
  max-width: 360px;
  text-align: left;
}
.quote-open {
  font-family: 'Playfair Display', serif;
  font-size: 3rem;
  color: #C8933A;
  line-height: 0.5;
  display: block;
  margin-bottom: 0.5rem;
}
.quote-text {
  font-family: 'Playfair Display', serif;
  font-size: 1rem;
  font-style: italic;
  color: #F5ECD7;
  line-height: 1.7;
  margin: 0 0 0.75rem;
}
.quote-author {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: #C8933A;
  letter-spacing: 0.05em;
}
.stat-strip {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(200,147,58,0.25);
  border-radius: 50px;
  padding: 0.9rem 2rem;
}
.stat-item { display: flex; flex-direction: column; align-items: center; }
.stat-num {
  font-family: 'Playfair Display', serif;
  font-size: 1.15rem;
  font-weight: 700;
  color: #C8933A;
}
.stat-lbl {
  font-family: 'Inter', sans-serif;
  font-size: 0.7rem;
  color: rgba(245,236,215,0.6);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}
.stat-divider { width: 1px; height: 28px; background: rgba(200,147,58,0.3); }

/* RIGHT */
.right-panel { background: #FAF6EF; min-height: 100vh; }
.form-card {
  width: 100%;
  max-width: 420px;
  padding: 2.5rem 2rem;
}
.mobile-brand {}
.brand-title-sm {
  font-family: 'Playfair Display', serif;
  font-size: 1.4rem;
  font-weight: 700;
  color: #5C3D1E;
}
.form-title {
  font-family: 'Playfair Display', serif;
  font-size: 2rem;
  font-weight: 700;
  color: #3B2209;
  line-height: 1.2;
}
.form-sub {
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  color: #8A7060;
  margin-top: 0.4rem;
}
.field-label {
  display: block;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 600;
  color: #5C3D1E;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 0.4rem;
}
.warm-field :deep(.v-field) {
  background: #FFF8EE !important;
  border-color: #D9C5A0 !important;
}
.warm-field :deep(.v-field--focused) { border-color: #C8933A !important; }
.warm-field :deep(input::placeholder) { color: #B0957A; }
.sign-in-btn {
  background: linear-gradient(135deg, #C8933A 0%, #A0702A 100%) !important;
  color: #FAF6EF !important;
  font-family: 'Inter', sans-serif;
  font-weight: 600;
  letter-spacing: 0.05em;
  box-shadow: 0 4px 16px rgba(160,112,42,0.35) !important;
}
.sign-in-btn:hover {
  box-shadow: 0 6px 24px rgba(160,112,42,0.5) !important;
  transform: translateY(-1px);
}
.register-link {
  font-family: 'Inter', sans-serif;
  font-size: 0.88rem;
  color: #8A7060;
}
.link-accent { color: #C8933A; font-weight: 600; text-decoration: none; }
.link-accent:hover { text-decoration: underline; }
</style>
