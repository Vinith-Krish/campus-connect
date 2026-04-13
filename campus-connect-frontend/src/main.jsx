import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { GoogleReCaptchaProvider } from '@react-recaptcha-v3/react'

const recaptchaKey = import.meta.env.VITE_RECAPTCHA_SITE_KEY

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <GoogleReCaptchaProvider reCaptchaKey={recaptchaKey}>
      <App />
    </GoogleReCaptchaProvider>
  </React.StrictMode>,
)