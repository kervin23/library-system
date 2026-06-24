<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-6" elevation="4" rounded="lg">
          <v-card-title class="text-center text-h5 mb-4">
            Create Account
          </v-card-title>

          <v-alert v-if="error" type="error" class="mb-3">{{ error }}</v-alert>
          <v-alert v-if="success" type="success" class="mb-3">{{ success }}</v-alert>

          <v-text-field
            v-model="username"
            label="Username"
            prepend-inner-icon="mdi-account"
            variant="outlined"
            class="mb-3"
          />

          <v-text-field
            v-model="email"
            label="Email"
            prepend-inner-icon="mdi-email"
            variant="outlined"
            class="mb-3"
          />

          <v-text-field
            v-model="password"
            label="Password"
            prepend-inner-icon="mdi-lock"
            type="password"
            variant="outlined"
            class="mb-3"
          />

          <v-text-field
            v-model="confirmPassword"
            label="Confirm Password"
            prepend-inner-icon="mdi-lock-check"
            type="password"
            variant="outlined"
            class="mb-3"
          />

          <v-btn color="primary" block size="large" class="mb-3" @click="register">
            Sign Up
          </v-btn>

          <div class="text-center">
            <span>Already have an account? </span>
            <RouterLink to="/">Login</RouterLink>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
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

const register = async () => {
  error.value = ''
  success.value = ''

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match!'
    return
  }

  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username.value,
        email: email.value,
        password: password.value,
        role: 'user'
      })
    })

    if (response.ok) {
      success.value = 'Account created! Redirecting to login...'
      setTimeout(() => router.push('/'), 2000)
    } else {
      const msg = await response.text()
      error.value = msg
    }
  } catch (e) {
    error.value = 'Cannot connect to server'
  }
}
</script>