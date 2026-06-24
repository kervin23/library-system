<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-6" elevation="4" rounded="lg">
          <v-card-title class="text-center text-h5 mb-4">
            Library System
          </v-card-title>

          <v-alert v-if="error" type="error" class="mb-3">{{ error }}</v-alert>

          <v-text-field
            v-model="username"
            label="Username"
            prepend-inner-icon="mdi-account"
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

          <v-btn color="primary" block size="large" class="mb-3" @click="login">
            Login
          </v-btn>

          <div class="text-center">
            <span>Don't have an account? </span>
            <RouterLink to="/register">Sign Up</RouterLink>
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
const password = ref('')
const error = ref('')

const login = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    })

    if (response.ok) {
      const user = await response.json()
      localStorage.setItem('user', JSON.stringify(user))
      
      if (user.role === 'admin') {
        router.push('/admin')
      } else if (user.role === 'manager') {
        router.push('/manager')
      } else {
        router.push('/home')
      }
    } else {
      error.value = 'Invalid username or password'
    }
  } catch (e) {
    error.value = 'Cannot connect to server'
  }
}
</script>