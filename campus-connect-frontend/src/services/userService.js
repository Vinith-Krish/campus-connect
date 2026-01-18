import axiosInstance from './axiosInstance';

export const userService = {
  getMyProfile: async () => {
    const response = await axiosInstance.get('/users/me');
    return response.data;
  },

  getMyEvents: async () => {
    const response = await axiosInstance.get('/users/me/events');
    return response.data; // { registered: [], interested: [] }
  },

  updateProfile: async (userData) => {
    const response = await axiosInstance.put('/users/me', userData);
    return response.data;
  }
};