import axiosInstance from './axiosInstance';

export const userService = {
  getMyEvents: async () => {
    const response = await axiosInstance.get('/users/me/events');
    return response.data;
  },
};
