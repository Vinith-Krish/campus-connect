import axios from './axiosInstance';

const ensureAuthSuccess = (data) => {
  if (data?.success === false) {
    throw new Error(data.message || 'Authentication failed');
  }

  if (!data?.token || !data?.user) {
    throw new Error(data?.message || 'Authentication failed');
  }

  return data;
};

export const authService = {
  login: async (loginData) => {
    const response = await axios.post('/auth/login', loginData);
    return ensureAuthSuccess(response.data);
  },

  register: async (registerData) => {
    const response = await axios.post('/auth/register', registerData);
    return ensureAuthSuccess(response.data);
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  forgotPassword: async (email) => {
    const response = await axios.post('/auth/forgot-password', { email });
    return response.data;
  },

  resetPassword: async (token, newPassword) => {
    const response = await axios.post('/auth/reset-password', {
      token,
      newPassword,
    });
    return response.data;
  },

  resetPasswordDirect: async (email, newPassword) => {
    const response = await axios.post('/auth/reset-password-direct', {
      email,
      newPassword,
    });
    return response.data;
  },

  getToken: () => localStorage.getItem('token'),

  getUser: () => {
    const rawUser = localStorage.getItem('user');
    if (!rawUser) return null;

    try {
      return JSON.parse(rawUser);
    } catch {
      return null;
    }
  },

  getCurrentUser: () => {
    return authService.getUser();
  },

  isAuthenticated: () => {
    return Boolean(authService.getToken());
  },
};
