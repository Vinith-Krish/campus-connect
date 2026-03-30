import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, MapPin, Clock, Users, X, Loader2 } from 'lucide-react';
import { Button } from './ui/button';
import { useToast } from '../hooks/use-toast';
import { eventService } from '../services/eventService';

const categoryColors = {
  Tech: 'bg-blue-500 text-white',
  Cultural: 'bg-rose-500 text-white',
  Sports: 'bg-green-500 text-white',
  Workshop: 'bg-amber-500 text-white',
};

const RegisteredEventCard = ({ event, onUnregister }) => {
  const { toast } = useToast();
  const [isUnregistering, setIsUnregistering] = useState(false);

  const formattedDate = new Date(event.date).toLocaleDateString('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });

  const handleUnregister = async (e) => {
    e.preventDefault(); // Prevent navigation to event details
    e.stopPropagation();

    setIsUnregistering(true);
    try {
      await eventService.unregisterFromEvent(event.id);
      toast({
        title: 'Unregistered Successfully',
        description: 'You have been unregistered from this event.',
      });
      // Call the callback to refresh the events list
      if (onUnregister) {
        onUnregister(event.id);
      }
    } catch (error) {
      toast({
        title: 'Unregister Failed',
        description: error.response?.data?.message || 'Something went wrong. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setIsUnregistering(false);
    }
  };

  return (
    <article className="group bg-card rounded-xl overflow-hidden shadow-card hover:shadow-card-hover transition-all duration-300 hover:-translate-y-1 border border-border relative">
      <Link to={`/events/${event.id}`}>
        {/* Event Image */}
        <div className="relative h-40 overflow-hidden bg-muted">
          {event.imageUrl ? (
            <img
              src={event.imageUrl}
              alt={event.title}
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
            />
          ) : (
            <div className="w-full h-full gradient-primary opacity-80 flex items-center justify-center">
              <Calendar className="h-12 w-12 text-primary-foreground/50" />
            </div>
          )}
          <span className={`absolute top-3 right-3 px-3 py-1 rounded-full text-xs font-semibold ${categoryColors[event.category] || 'bg-muted text-muted-foreground'}`}>
            {event.category}
          </span>
        </div>

        {/* Event Content */}
        <div className="p-5">
          <h3 className="font-display text-lg font-bold text-foreground group-hover:text-primary transition-colors line-clamp-1">
            {event.title}
          </h3>
          
          <p className="mt-1 text-sm text-muted-foreground">{event.collegename}</p>

          <div className="mt-4 space-y-2">
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Calendar className="h-4 w-4 text-primary" />
              <span>{formattedDate}</span>
              <Clock className="h-4 w-4 text-primary ml-2" />
              <span>{event.time}</span>
            </div>
            
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <MapPin className="h-4 w-4 text-accent" />
              <span className="line-clamp-1">{event.venue}</span>
            </div>
          </div>

          {event.registeredCount !== undefined && (
            <div className="mt-4 pt-4 border-t border-border flex items-center gap-2 text-sm text-muted-foreground">
              <Users className="h-4 w-4" />
              <span>{event.registeredCount} registered</span>
            </div>
          )}
        </div>
      </Link>

      {/* Unregister Button */}
      <div className="px-5 pb-5">
        <Button
          variant="outline"
          size="sm"
          className="w-full text-destructive hover:text-destructive hover:bg-destructive/10 border-destructive/30"
          onClick={handleUnregister}
          disabled={isUnregistering}
        >
          {isUnregistering ? (
            <>
              <Loader2 className="h-4 w-4 mr-2 animate-spin" />
              Unregistering...
            </>
          ) : (
            <>
              <X className="h-4 w-4 mr-2" />
              Unregister
            </>
          )}
        </Button>
      </div>
    </article>
  );
};

export default RegisteredEventCard;
