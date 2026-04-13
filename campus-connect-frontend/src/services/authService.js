import axios from './axiosInstance';

export const authService = {
  login: async (loginData) => {
    const response = await axios.post('/auth/login', loginData);
    return response.data;
  },

  register: async (registerData) => {
    const response = await axios.post('/auth/register', registerData);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getToken: () => localStorage.getItem('token'),
  getUser: () => JSON.parse(localStorage.getItem('user') || '{}'),
};