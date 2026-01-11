import axiosInstance from './axiosInstance';

export interface Event {
  id: string;
  title: string;
  description: string;
  date: string;
  time: string;
  venue: string;
  category: 'Tech' | 'Cultural' | 'Sports' | 'Workshop';
  collegeName: string;
  organizerName: string;
  organizerEmail: string;
  imageUrl?: string;
  registeredCount?: number;
}

export interface CreateEventData {
  title: string;
  description: string;
  date: string;
  time: string;
  venue: string;
  category: 'Tech' | 'Cultural' | 'Sports' | 'Workshop';
}

export const eventService = {
  getAllEvents: async (search?: string, category?: string): Promise<Event[]> => {
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (category && category !== 'All') params.append('category', category);
    
    const response = await axiosInstance.get<Event[]>(`/events?${params.toString()}`);
    return response.data;
  },

  getEventById: async (id: string): Promise<Event> => {
    const response = await axiosInstance.get<Event>(`/events/${id}`);
    return response.data;
  },

  createEvent: async (data: CreateEventData): Promise<Event> => {
    const response = await axiosInstance.post<Event>('/events', data);
    return response.data;
  },

  registerForEvent: async (eventId: string): Promise<void> => {
    await axiosInstance.post(`/events/${eventId}/register`);
  },

  markInterested: async (eventId: string): Promise<void> => {
    await axiosInstance.post(`/events/${eventId}/interested`);
  },
};
