import React from 'react';
import { Link } from 'react-router-dom';
import { Calendar, MapPin, Clock, Users, Tag } from 'lucide-react';

const categoryColors = {
  Tech: 'bg-blue-500 text-white',
  Cultural: 'bg-rose-500 text-white',
  Sports: 'bg-green-500 text-white',
  Workshop: 'bg-amber-500 text-white',
};

const EventCard = ({ event }) => {
  const formattedDate = new Date(event.date).toLocaleDateString('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });

  return (
    <Link to={`/events/${event.id}`}>
      <article className="group bg-card rounded-xl overflow-hidden shadow-card hover:shadow-card-hover transition-all duration-300 hover:-translate-y-1 border border-border">
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
      </article>
    </Link>
  );
};

export default EventCard;
