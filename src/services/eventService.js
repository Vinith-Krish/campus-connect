import axiosInstance from './axiosInstance';

export const eventService = {
  getAllEvents: async (search, category) => {
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (category && category !== 'All') params.append('category', category);
    
    const response = await axiosInstance.get(`/events?${params.toString()}`);
    return response.data;
  },

  getEventById: async (id) => {
    const response = await axiosInstance.get(`/events/${id}`);
    return response.data;
  },

  createEvent: async (data) => {
    const response = await axiosInstance.post('/events', data);
    return response.data;
  },

  registerForEvent: async (eventId) => {
    await axiosInstance.post(`/events/${eventId}/register`);
  },

  markInterested: async (eventId) => {
    await axiosInstance.post(`/events/${eventId}/interested`);
  },
};
