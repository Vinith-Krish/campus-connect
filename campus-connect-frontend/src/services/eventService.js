import axiosInstance from './axiosInstance';
import { normalizeEvent } from '../lib/eventUtils';

const normalizeEventList = (events) => events.map((event) => normalizeEvent(event));
const normalizeEventPage = (pageData) => {
  if (Array.isArray(pageData)) {
    return {
      content: normalizeEventList(pageData),
      totalElements: pageData.length,
      totalPages: pageData.length > 0 ? 1 : 0,
      size: pageData.length,
      number: 0,
    };
  }

  return {
    ...pageData,
    content: Array.isArray(pageData?.content) ? normalizeEventList(pageData.content) : [],
  };
};

export const eventService = {
  getAllEvents: async ({
    search = '',
    category = '',
    page = 0,
    size = 12,
    sortBy = 'date',
    direction = 'asc',
  } = {}) => {
    const params = new URLSearchParams();
    params.append('search', search ?? '');
    params.append('category', category ? String(category).toUpperCase() : '');
    params.append('page', String(page));
    params.append('size', String(size));
    params.append('sortBy', sortBy || 'date');
    params.append('direction', direction || 'asc');
    
    const response = await axiosInstance.get(`/events?${params.toString()}`);
    return normalizeEventPage(response.data);
  },

  getEventById: async (id) => {
    const response = await axiosInstance.get(`/events/${id}`);
    return normalizeEvent(response.data);
  },

  createEvent: async (eventData) => {
    const payload = {
      ...eventData,
      category: (eventData.category || '').toUpperCase()
    };
    const response = await axiosInstance.post('/events', payload);
    return normalizeEvent(response.data);
  },

  updateEvent: async (eventId, eventData) => {
    const payload = {
      ...eventData,
      category: (eventData.category || '').toUpperCase()
    };
    const response = await axiosInstance.put(`/events/${eventId}`, payload);
    return normalizeEvent(response.data);
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
    return Array.isArray(response.data) ? normalizeEventList(response.data) : [];
  },

  downloadRegistrationsExcel: async (eventId) => {
    const response = await axiosInstance.get(`/events/${eventId}/registrations/export`, {
      responseType: 'blob',
    });

    return {
      blob: response.data,
      contentType: response.headers['content-type'],
      contentDisposition: response.headers['content-disposition'],
    };
  }
};
