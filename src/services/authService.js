import axiosInstance from './axiosInstance';

export const authService = {
  register: async (userData) => {
    const response = await axiosInstance.post('/auth/register', userData);
    return response.data; // { token, user }
  },

  login: async (credentials) => {
    const response = await axiosInstance.post('/auth/login', credentials);
    return response.data; // { token, user }
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  forgotPassword: async (email) => {
    const response = await axiosInstance.post('/auth/forgot-password', { email });
    return response.data; // { message }
  },

  resetPassword: async (token, newPassword) => {
    const response = await axiosInstance.post('/auth/reset-password', { token, newPassword });
    return response.data; // { message }
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  isAuthenticated: () => !!localStorage.getItem('token')
};