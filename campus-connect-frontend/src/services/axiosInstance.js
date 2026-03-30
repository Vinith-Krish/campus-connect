import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
});

// Auto-attach token to every request
axiosInstance.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 errors globally (auto-logout) - but only for authenticated requests
axiosInstance.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      const requestUrl = error.config?.url || '';
      
      // Only redirect to login if the request had an auth token
      // This allows public endpoints to fail gracefully without redirecting
      const hadAuthToken = error.config?.headers?.Authorization;
      
      // Public endpoints that shouldn't trigger redirect
      const publicEndpoints = ['/events', '/auth/login', '/auth/register'];
      const isPublicEndpoint = publicEndpoints.some(endpoint => 
        requestUrl.includes(endpoint) || requestUrl.startsWith(endpoint)
      );
      
      // Only auto-redirect if user was authenticated and endpoint is not public
      if (hadAuthToken && !isPublicEndpoint) {
        localStorage.clear();
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;