import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import EventCard from '../components/EventCard';
import { useAuth } from '../context/AuthContext';
import { User, Calendar, Heart, Loader2, GraduationCap, Users } from 'lucide-react';
import { cn } from '@/lib/utils';

// Mock user events
const mockUserEvents = {
  registered: [
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
  ],
  interested: [
    {
      id: '2',
      title: 'Spring Cultural Festival',
      description: 'Annual cultural extravaganza',
      date: '2024-02-20',
      time: '06:00 PM',
      venue: 'Open Air Theatre',
      category: 'Cultural',
      collegeName: 'Stanford Arts College',
      organizerName: 'Cultural Committee',
      organizerEmail: 'culture@stanford.edu',
      registeredCount: 320,
    },
  ],
};

const Profile = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('registered');
  const [registeredEvents, setRegisteredEvents] = useState([]);
  const [interestedEvents, setInterestedEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchUserEvents = async () => {
      setIsLoading(true);
      try {
        // const data = await userService.getMyEvents();
        // setRegisteredEvents(data.registered);
        // setInterestedEvents(data.interested);
        
        await new Promise(resolve => setTimeout(resolve, 500));
        setRegisteredEvents(mockUserEvents.registered);
        setInterestedEvents(mockUserEvents.interested);
      } catch (error) {
        console.error('Failed to fetch user events:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserEvents();
  }, []);

  const currentEvents = activeTab === 'registered' ? registeredEvents : interestedEvents;

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <div className="container mx-auto px-4 py-12">
        {/* Profile Header */}
        <div className="bg-card rounded-2xl border border-border p-8 mb-8 animate-slide-up">
          <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
            <div className="w-24 h-24 rounded-2xl gradient-primary flex items-center justify-center">
              <User className="h-12 w-12 text-primary-foreground" />
            </div>
            <div className="text-center md:text-left">
              <h1 className="font-display text-2xl md:text-3xl font-bold text-foreground">
                {user?.name || 'User'}
              </h1>
              <p className="text-muted-foreground mt-1">{user?.email}</p>
              <div className="flex items-center justify-center md:justify-start gap-2 mt-3">
                <span className={cn(
                  'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-medium',
                  user?.role === 'CLUB_ADMIN' 
                    ? 'bg-accent/10 text-accent' 
                    : 'bg-primary/10 text-primary'
                )}>
                  {user?.role === 'CLUB_ADMIN' ? (
                    <>
                      <Users className="h-3.5 w-3.5" />
                      Club Admin
                    </>
                  ) : (
                    <>
                      <GraduationCap className="h-3.5 w-3.5" />
                      Student
                    </>
                  )}
                </span>
              </div>
            </div>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 gap-4 mt-8 pt-8 border-t border-border">
            <div className="text-center">
              <div className="font-display text-3xl font-bold text-foreground">
                {registeredEvents.length}
              </div>
              <div className="text-sm text-muted-foreground">Registered Events</div>
            </div>
            <div className="text-center">
              <div className="font-display text-3xl font-bold text-foreground">
                {interestedEvents.length}
              </div>
              <div className="text-sm text-muted-foreground">Interested Events</div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-2 mb-8 animate-slide-up" style={{ animationDelay: '0.1s' }}>
          <button
            onClick={() => setActiveTab('registered')}
            className={cn(
              'flex items-center gap-2 px-5 py-2.5 rounded-xl font-medium transition-all duration-200',
              activeTab === 'registered'
                ? 'gradient-primary text-primary-foreground shadow-md'
                : 'bg-card text-muted-foreground hover:text-foreground border border-border'
            )}
          >
            <Calendar className="h-4 w-4" />
            Registered
          </button>
          <button
            onClick={() => setActiveTab('interested')}
            className={cn(
              'flex items-center gap-2 px-5 py-2.5 rounded-xl font-medium transition-all duration-200',
              activeTab === 'interested'
                ? 'gradient-primary text-primary-foreground shadow-md'
                : 'bg-card text-muted-foreground hover:text-foreground border border-border'
            )}
          >
            <Heart className="h-4 w-4" />
            Interested
          </button>
        </div>

        {/* Events Grid */}
        {isLoading ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
          </div>
        ) : currentEvents.length > 0 ? (
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {currentEvents.map((event, index) => (
              <div
                key={event.id}
                className="animate-slide-up"
                style={{ animationDelay: `${(index + 2) * 0.1}s` }}
              >
                <EventCard event={event} />
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-20 bg-card rounded-2xl border border-border">
            {activeTab === 'registered' ? (
              <>
                <Calendar className="h-16 w-16 text-muted-foreground/50 mx-auto mb-4" />
                <h3 className="font-display text-xl font-semibold text-foreground mb-2">
                  No registered events
                </h3>
                <p className="text-muted-foreground">
                  Events you register for will appear here
                </p>
              </>
            ) : (
              <>
                <Heart className="h-16 w-16 text-muted-foreground/50 mx-auto mb-4" />
                <h3 className="font-display text-xl font-semibold text-foreground mb-2">
                  No interested events
                </h3>
                <p className="text-muted-foreground">
                  Events you mark as interested will appear here
                </p>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Profile;
