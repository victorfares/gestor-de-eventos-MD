import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import { createVuetify } from 'vuetify'
import { pt } from 'vuetify/locale'

export default createVuetify({
  locale: {
    locale: 'pt',
    messages: { pt },
  },
  theme: {
    defaultTheme: 'meninasDigitais',
    themes: {
      meninasDigitais: {
        dark: false,
        colors: {
          primary: '#6B2D91',
          secondary: '#E8892D',
          accent: '#C4458C',
          background: '#FBF6F0',
          surface: '#FFFFFF',
          error: '#B3261E',
          info: '#6B2D91',
          success: '#2E7D4F',
          warning: '#E8892D',
        },
      },
    },
  },
  defaults: {
    VBtn: { rounded: 'lg' },
    VCard: { rounded: 'lg' },
    VTextField: { variant: 'outlined', density: 'comfortable' },
    VTextarea: { variant: 'outlined', density: 'comfortable' },
    VSelect: { variant: 'outlined', density: 'comfortable' },
  },
})
