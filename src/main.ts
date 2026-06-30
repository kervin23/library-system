import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'libraryLight',
        themes: {
            libraryLight: {
                dark: false,
                colors: {
                    primary: '#C8933A',
                    secondary: '#5C3D1E',
                    background: '#FAF6EF',
                    surface: '#FFF8EE',
                    error: '#C62828',
                    success: '#2D6A4F',
                    warning: '#B45309',
                    info: '#5B8DEF',
                }
            },
            libraryDark: {
                dark: true,
                colors: {
                    primary: '#C8933A',
                    secondary: '#F5ECD7',
                    background: '#13131F',
                    surface: '#1A1A2E',
                    error: '#EF6464',
                    success: '#52A77A',
                    warning: '#D97706',
                    info: '#5B8DEF',
                }
            }
        }
    }
})

createApp(App).use(router).use(vuetify).mount('#app')
