import axiosInstance from './axiosInstance';

export const eventService = {
  getAllEvents: async (search = '', category = '') => {
    let url = '/events';
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (category) params.append('category', category.toUpperCase());
    if (params.toString()) url += `?${params}`;
    
    const response = await axiosInstance.get(url);
    return response.data;
  },

  getEventById: async (id) => {
    const response = await axiosInstance.get(`/events/${id}`);
    return response.data;
  },

  createEvent: async (eventData) => {
    const payload = {
      ...eventData,
      category: eventData.category.toUpperCase()
    };
    const response = await axiosInstance.post('/events', payload);
    return response.data;
  },

  updateEvent: async (eventId, eventData) => {
    const payload = {
      ...eventData,
      category: eventData.category.toUpperCase()
    };
    const response = await axiosInstance.put(`/events/${eventId}`, payload);
    return response.data;
  },

  registerForEvent: async (eventId) => {
    const response = await axiosInstance.post(`/events/${eventId}/register`);
    return response.data;
  },

  unregisterFromEvent: async (eventId) => {
    await axiosInstance.delete(`/events/${eventId}/unregister`);
  },

  markInterested: async (eventId) => {
    const response = await axiosInstance.post(`/events/${eventId}/interested`);
    return response.data;
  },

  deleteEvent: async (eventId) => {
    await axiosInstance.delete(`/events/${eventId}`);
  },

  getMyCreatedEvents: async () => {
    const response = await axiosInstance.get('/events/my-events');
    return response.data;
  }
};