import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Clock, Heart, Loader2, MapPin } from 'lucide-react';
import { Button } from './ui/button';
import { useToast } from '../hooks/use-toast';
import { eventService } from '../services/eventService';
import { getCategoryClassName, normalizeCategory } from '../lib/eventUtils';
import { getUserFriendlyErrorMessage } from '../lib/errorUtils';

const InterestedEventCard = ({ event, onUnmarkInterested }) => {
  const { toast } = useToast();
  const [isRemoving, setIsRemoving] = useState(false);
  const formattedDate = new Date(event.date).toLocaleDateString('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });

  const handleUnmarkInterested = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsRemoving(true);

    try {
      await eventService.removeInterest(event.id);
      onUnmarkInterested?.(event.id);
      toast({
        title: 'Interest Removed',
        description: 'This event has been removed from your interested events.',
      });
    } catch (error) {
      toast({
        title: 'Unable to Remove Interest',
        description: getUserFriendlyErrorMessage(error),
        variant: 'destructive',
      });
    } finally {
      setIsRemoving(false);
    }
  };

  return (
    <article className="group bg-card rounded-xl overflow-hidden shadow-card border border-border">
      <Link to={`/events/${event.id}`}>
        <div className="relative h-40 overflow-hidden bg-muted">
          {event.imageUrl ? (
            <img src={event.imageUrl} alt={event.title} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full gradient-primary opacity-80 flex items-center justify-center">
              <Calendar className="h-12 w-12 text-primary-foreground/50" />
            </div>
          )}
          <span className={`absolute top-3 right-3 px-3 py-1 rounded-full text-xs font-semibold ${getCategoryClassName(event.category)}`}>
            {normalizeCategory(event.category)}
          </span>
        </div>

        <div className="p-5">
          <h3 className="font-display text-lg font-bold text-foreground line-clamp-1">{event.title}</h3>
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
        </div>
      </Link>

      <div className="px-5 pb-5">
        <Button
          variant="outline"
          size="sm"
          className="w-full text-destructive hover:text-destructive hover:bg-destructive/10 border-destructive/30"
          onClick={handleUnmarkInterested}
          disabled={isRemoving}
        >
          {isRemoving ? (
            <Loader2 className="h-4 w-4 mr-2 animate-spin" />
          ) : (
            <Heart className="h-4 w-4 mr-2 fill-current" />
          )}
          {isRemoving ? 'Removing...' : 'Unmark Interested'}
        </Button>
      </div>
    </article>
  );
};

export default InterestedEventCard;