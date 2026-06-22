<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-6" elevation="4" rounded="lg">
          <v-card-title class="text-center text-h5 mb-4">
            Library System
          </v-card-title>

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

          <v-btn color="primary" block size="large" class="mb-3">
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

const username = ref('')
const password = ref('')
</script>