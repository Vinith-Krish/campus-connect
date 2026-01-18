import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import EventCard from '../components/EventCard';
import SearchBar from '../components/SearchBar';
import CategoryFilter from '../components/CategoryFilter';
import { eventService } from '../services/eventService';
import { Calendar, Loader2 } from 'lucide-react';

const Events = () => {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchEvents = async () => {
      setIsLoading(true);
      try {
        const data = await eventService.getAllEvents(searchQuery, selectedCategory !== 'All' ? selectedCategory : '');
        setEvents(data || []);
      } catch (error) {
        console.error('Failed to fetch events:', error);
        setEvents([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchEvents();
  }, [searchQuery, selectedCategory]);

  useEffect(() => {
    // Events are already filtered by the API, just set them
    setFilteredEvents(events);
  }, [events]);

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      {/* Header */}
      <div className="bg-card border-b border-border">
        <div className="container mx-auto px-4 py-12">
          <h1 className="font-display text-3xl md:text-4xl font-bold text-foreground">
            Discover Events
          </h1>
          <p className="mt-2 text-muted-foreground">
            Find and register for exciting campus events near you
          </p>

          {/* Search & Filters */}
          <div className="mt-8 space-y-4">
            <SearchBar
              value={searchQuery}
              onChange={setSearchQuery}
              placeholder="Search by event name, college, or venue..."
              isLoading={isLoading}
            />
            <CategoryFilter
              selectedCategory={selectedCategory}
              onCategoryChange={setSelectedCategory}
            />
          </div>
        </div>
      </div>

      {/* Events Grid */}
      <div className="container mx-auto px-4 py-12">
        {isLoading ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
          </div>
        ) : filteredEvents.length > 0 ? (
          <>
            <p className="text-sm text-muted-foreground mb-6">
              Showing {filteredEvents.length} event{filteredEvents.length !== 1 ? 's' : ''}
            </p>
            <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredEvents.map((event, index) => (
                <div
                  key={event.id}
                  className="animate-slide-up"
                  style={{ animationDelay: `${index * 0.1}s` }}
                >
                  <EventCard event={event} />
                </div>
              ))}
            </div>
          </>
        ) : (
          <div className="text-center py-20">
            <Calendar className="h-16 w-16 text-muted-foreground/50 mx-auto mb-4" />
            <h3 className="font-display text-xl font-semibold text-foreground mb-2">
              {searchQuery || selectedCategory !== 'All' ? 'No events found' : 'No events yet'}
            </h3>
            <p className="text-muted-foreground">
              {searchQuery || selectedCategory !== 'All' 
                ? 'Try adjusting your search or filter criteria'
                : 'Check back later for upcoming events'}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Events;
