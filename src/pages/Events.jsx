import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import EventCard from '../components/EventCard';
import SearchBar from '../components/SearchBar';
import CategoryFilter from '../components/CategoryFilter';
import { Calendar, Loader2 } from 'lucide-react';

// Mock data for demo
const mockEvents = [
  {
    id: '1',
    title: 'Hackathon 2024: Build the Future',
    description: 'Join us for a 24-hour coding marathon',
    date: '2024-02-15',
    time: '09:00 AM',
    venue: 'Engineering Block, Hall A',
    category: 'Tech',
    collegeName: 'MIT University',
    organizerName: 'Tech Club',
    organizerEmail: 'tech@mit.edu',
    registeredCount: 156,
  },
  {
    id: '2',
    title: 'Spring Cultural Festival',
    description: 'Annual cultural extravaganza featuring music, dance, and art',
    date: '2024-02-20',
    time: '06:00 PM',
    venue: 'Open Air Theatre',
    category: 'Cultural',
    collegeName: 'Stanford Arts College',
    organizerName: 'Cultural Committee',
    organizerEmail: 'culture@stanford.edu',
    registeredCount: 320,
  },
  {
    id: '3',
    title: 'Inter-College Basketball Championship',
    description: 'Annual basketball tournament featuring top college teams',
    date: '2024-02-18',
    time: '10:00 AM',
    venue: 'Sports Complex',
    category: 'Sports',
    collegeName: 'UCLA',
    organizerName: 'Sports Council',
    organizerEmail: 'sports@ucla.edu',
    registeredCount: 85,
  },
  {
    id: '4',
    title: 'AI/ML Workshop: From Basics to Production',
    description: 'Hands-on workshop covering machine learning fundamentals',
    date: '2024-02-22',
    time: '02:00 PM',
    venue: 'Computer Science Lab',
    category: 'Workshop',
    collegeName: 'Carnegie Mellon',
    organizerName: 'AI Society',
    organizerEmail: 'ai@cmu.edu',
    registeredCount: 64,
  },
  {
    id: '5',
    title: 'Startup Pitch Competition',
    description: 'Present your startup ideas to investors and mentors',
    date: '2024-02-25',
    time: '11:00 AM',
    venue: 'Business School Auditorium',
    category: 'Tech',
    collegeName: 'Harvard Business School',
    organizerName: 'Entrepreneurship Cell',
    organizerEmail: 'ecell@hbs.edu',
    registeredCount: 42,
  },
  {
    id: '6',
    title: 'Dance Workshop: Contemporary Fusion',
    description: 'Learn contemporary dance techniques with professional choreographers',
    date: '2024-02-28',
    time: '04:00 PM',
    venue: 'Dance Studio, Building C',
    category: 'Cultural',
    collegeName: 'Juilliard',
    organizerName: 'Dance Club',
    organizerEmail: 'dance@juilliard.edu',
    registeredCount: 28,
  },
];

const Events = () => {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Simulate API call - replace with actual API call
    const fetchEvents = async () => {
      setIsLoading(true);
      try {
        // const data = await eventService.getAllEvents();
        // setEvents(data);
        
        // Using mock data for demo
        await new Promise(resolve => setTimeout(resolve, 500));
        setEvents(mockEvents);
      } catch (error) {
        console.error('Failed to fetch events:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchEvents();
  }, []);

  useEffect(() => {
    let result = events;

    // Filter by search query
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(
        (event) =>
          event.title.toLowerCase().includes(query) ||
          event.collegeName.toLowerCase().includes(query) ||
          event.venue.toLowerCase().includes(query)
      );
    }

    // Filter by category
    if (selectedCategory !== 'All') {
      result = result.filter((event) => event.category === selectedCategory);
    }

    setFilteredEvents(result);
  }, [events, searchQuery, selectedCategory]);

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
              No events found
            </h3>
            <p className="text-muted-foreground">
              Try adjusting your search or filter criteria
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Events;
