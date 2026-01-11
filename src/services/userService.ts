import axiosInstance from './axiosInstance';
import { Event } from './eventService';

export interface UserEvents {
  registered: Event[];
  interested: Event[];
}

export const userService = {
  getMyEvents: async (): Promise<UserEvents> => {
    const response = await axiosInstance.get<UserEvents>('/users/me/events');
    return response.data;
  },
};
