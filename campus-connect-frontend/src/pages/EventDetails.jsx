import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { Button } from '../components/ui/button';
import { useAuth } from '../context/AuthContext';
import { eventService } from '../services/eventService';
import { useToast } from '../hooks/use-toast';
import {
  Calendar,
  Clock,
  MapPin,
  Users,
  Mail,
  ArrowLeft,
  Heart,
  CheckCircle,
  Loader2,
  Tag,
} from 'lucide-react';

const categoryColors = {
  Tech: 'bg-blue-500 text-white',
  Cultural: 'bg-rose-500 text-white',
  Sports: 'bg-green-500 text-white',
  Workshop: 'bg-amber-500 text-white',
};

const EventDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const { toast } = useToast();
  const [event, setEvent] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isRegistered, setIsRegistered] = useState(false);
  const [isInterested, setIsInterested] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    const fetchEvent = async () => {
      setIsLoading(true);
      try {
        const data = await eventService.getEventById(id);
        setEvent(data);
      } catch (error) {
        console.error('Failed to fetch event:', error);
        toast({
          title: 'Error',
          description: 'Failed to load event details',
          variant: 'destructive',
        });
        navigate('/events');
      } finally {
        setIsLoading(false);
      }
    };

    if (id) {
      fetchEvent();
    }
  }, [id, navigate, toast]);

  const handleRegister = async () => {
    if (!isAuthenticated) {
      toast({
        title: 'Login Required',
        description: 'Please login to register for events',
        variant: 'destructive',
      });
      navigate('/login', { state: { from: { pathname: `/events/${id}` } } });
      return;
    }

    setActionLoading(true);
    try {
      await eventService.registerForEvent(id);
      setIsRegistered(true);
      // Refresh event data to update registered count
      const data = await eventService.getEventById(id);
      setEvent(data);
      toast({
        title: 'Registration Successful!',
        description: 'You have been registered for this event.',
      });
    } catch (error) {
      toast({
        title: 'Registration Failed',
        description: error.response?.data?.message || 'Something went wrong. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setActionLoading(false);
    }
  };

  const handleInterested = async () => {
    if (!isAuthenticated) {
      toast({
        title: 'Login Required',
        description: 'Please login to mark interest in events',
        variant: 'destructive',
      });
      return;
    }

    setActionLoading(true);
    try {
      await eventService.markInterested(id);
      setIsInterested(!isInterested);
      toast({
        title: isInterested ? 'Removed from interests' : 'Marked as interested!',
        description: isInterested 
          ? 'This event has been removed from your interests.' 
          : 'This event has been added to your interests.',
      });
    } catch (error) {
      toast({
        title: 'Action Failed',
        description: 'Something went wrong. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setActionLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="flex items-center justify-center py-40">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="container mx-auto px-4 py-20 text-center">
          <h2 className="font-display text-2xl font-bold text-foreground">Event not found</h2>
          <Link to="/events" className="text-primary hover:underline mt-4 inline-block">
            Back to events
          </Link>
        </div>
      </div>
    );
  }

  const formattedDate = new Date(event.date).toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <div className="container mx-auto px-4 py-8">
        {/* Back Button */}
        <Link
          to="/events"
          className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground transition-colors mb-8"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to events
        </Link>

        <div className="grid lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6 animate-slide-up">
            {/* Hero */}
            <div className="relative h-64 md:h-80 rounded-2xl overflow-hidden bg-muted">
              <div className="absolute inset-0 gradient-primary opacity-80 flex items-center justify-center">
                <Calendar className="h-20 w-20 text-primary-foreground/30" />
              </div>
              <span className={`absolute top-4 right-4 px-4 py-1.5 rounded-full text-sm font-semibold ${categoryColors[event.category]}`}>
                {event.category}
              </span>
            </div>

            {/* Title */}
            <div>
              <h1 className="font-display text-3xl md:text-4xl font-bold text-foreground">
                {event.title}
              </h1>
              <p className="mt-2 text-lg text-muted-foreground">{event.collegename}</p>
            </div>

            {/* Description */}
            <div className="bg-card rounded-xl p-6 border border-border">
              <h2 className="font-display text-xl font-bold text-foreground mb-4">About this event</h2>
              <div className="prose prose-muted max-w-none">
                {event.description.split('\n').map((paragraph, index) => (
                  <p key={index} className="text-muted-foreground mb-3">
                    {paragraph}
                  </p>
                ))}
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6 animate-slide-up" style={{ animationDelay: '0.1s' }}>
            {/* Event Info Card */}
            <div className="bg-card rounded-xl p-6 border border-border sticky top-24">
              <div className="space-y-4">
                <div className="flex items-start gap-4">
                  <div className="p-3 rounded-xl bg-primary/10">
                    <Calendar className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Date</p>
                    <p className="font-medium text-foreground">{formattedDate}</p>
                  </div>
                </div>

                <div className="flex items-start gap-4">
                  <div className="p-3 rounded-xl bg-accent/10">
                    <Clock className="h-5 w-5 text-accent" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Time</p>
                    <p className="font-medium text-foreground">{event.time}</p>
                  </div>
                </div>

                <div className="flex items-start gap-4">
                  <div className="p-3 rounded-xl bg-success/10">
                    <MapPin className="h-5 w-5 text-success" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Venue</p>
                    <p className="font-medium text-foreground">{event.venue}</p>
                  </div>
                </div>

                <div className="flex items-start gap-4">
                  <div className="p-3 rounded-xl bg-muted">
                    <Users className="h-5 w-5 text-muted-foreground" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Registered</p>
                    <p className="font-medium text-foreground">{event.registeredCount} attendees</p>
                  </div>
                </div>
              </div>

              <div className="mt-6 pt-6 border-t border-border space-y-3">
                {isRegistered ? (
                  <Button variant="secondary" className="w-full" size="lg" disabled>
                    <CheckCircle className="h-5 w-5 mr-2" />
                    Registered
                  </Button>
                ) : (
                  <Button
                    variant="hero"
                    className="w-full"
                    size="lg"
                    onClick={handleRegister}
                    disabled={actionLoading}
                  >
                    {actionLoading ? (
                      <Loader2 className="h-5 w-5 animate-spin" />
                    ) : (
                      'Register Now'
                    )}
                  </Button>
                )}
                
                <Button
                  variant={isInterested ? 'default' : 'outline'}
                  className="w-full"
                  size="lg"
                  onClick={handleInterested}
                  disabled={actionLoading}
                >
                  <Heart className={`h-5 w-5 mr-2 ${isInterested ? 'fill-current' : ''}`} />
                  {isInterested ? 'Interested' : 'Mark Interested'}
                </Button>
              </div>
            </div>

            {/* Organizer Card */}
            <div className="bg-card rounded-xl p-6 border border-border">
              <h3 className="font-display text-lg font-bold text-foreground mb-4">Organizer</h3>
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-full gradient-primary flex items-center justify-center">
                  <Users className="h-6 w-6 text-primary-foreground" />
                </div>
                <div>
                  <p className="font-medium text-foreground">{event.organizerName}</p>
                  <a
                    href={`mailto:${event.organizerEmail}`}
                    className="text-sm text-primary hover:underline flex items-center gap-1"
                  >
                    <Mail className="h-3 w-3" />
                    Contact
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EventDetails;
