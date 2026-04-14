import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import EventCard from '../components/EventCard';
import SearchBar from '../components/SearchBar';
import CategoryFilter from '../components/CategoryFilter';
import { eventService } from '../services/eventService';
import { Button } from '../components/ui/button';
import {
  Calendar,
  ChevronLeft,
  ChevronRight,
  Loader2,
  RotateCcw,
  ArrowUpDown,
} from 'lucide-react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../components/ui/select';
import { useQuery } from "@tanstack/react-query";

const PAGE_SIZE = 12;
const SORT_OPTIONS = [
  { value: 'date', label: 'Date' },
  { value: 'createdAt', label: 'Created At' },
  { value: 'title', label: 'Title' },
  { value: 'category', label: 'Category' },
  { value: 'id', label: 'ID' },
];

const Events = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [page, setPage] = useState(0);
  const [sortBy, setSortBy] = useState('date');
  const [direction, setDirection] = useState('asc');

  useEffect(() => {
    setPage(0);
  }, [searchQuery, selectedCategory, sortBy, direction]);

  const {
    data,
    error,
    isLoading,
    isError,
    refetch,
    isFetching,
  } = useQuery({
    queryKey: ["events", searchQuery, selectedCategory, page, sortBy, direction],
    queryFn: () =>
      eventService.getAllEvents(
        {
          search: searchQuery,
          category: selectedCategory !== "All" ? selectedCategory : "",
          page,
          size: PAGE_SIZE,
          sortBy,
          direction,
        }
      ),
    staleTime: 30_000,
    retry: false,
    refetchOnWindowFocus: false,
  });

  const eventsPage = data || { content: [], totalElements: 0, totalPages: 0, number: 0, size: PAGE_SIZE };
  const filteredEvents = eventsPage.content || [];
  const totalPages = eventsPage.totalPages || 0;
  const currentPage = eventsPage.number || 0;
  const totalElements = eventsPage.totalElements || 0;
  const errorMessage = error?.response?.data?.message || error?.message || 'Failed to load events. Please try again.';

  const handlePageChange = (nextPage) => {
    if (nextPage < 0) return;
    if (totalPages > 0 && nextPage >= totalPages) return;
    setPage(nextPage);
  };

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
            <div className="flex flex-col sm:flex-row gap-3 sm:items-center sm:justify-between">
              <div className="flex items-center gap-3">
                <div className="min-w-40">
                  <Select value={sortBy} onValueChange={setSortBy}>
                    <SelectTrigger className="h-11 bg-card">
                      <SelectValue placeholder="Sort by" />
                    </SelectTrigger>
                    <SelectContent>
                      {SORT_OPTIONS.map((option) => (
                        <SelectItem key={option.value} value={option.value}>
                          {option.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <Button
                  type="button"
                  variant="outline"
                  className="h-11 gap-2"
                  onClick={() => setDirection((current) => (current === 'asc' ? 'desc' : 'asc'))}
                >
                  <ArrowUpDown className="h-4 w-4" />
                  {direction === 'asc' ? 'Ascending' : 'Descending'}
                </Button>
              </div>
              <p className="text-sm text-muted-foreground">
                Page {currentPage + 1}{totalPages > 0 ? ` of ${totalPages}` : ''}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Events Grid */}
      <div className="container mx-auto px-4 py-12">
        {isError ? (
          <div className="text-center py-20 max-w-lg mx-auto">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-destructive/10 text-destructive mb-4">
              <Calendar className="h-8 w-8" />
            </div>
            <h3 className="font-display text-2xl font-bold text-foreground mb-2">
              Could not load events
            </h3>
            <p className="text-muted-foreground mb-6">
              {errorMessage}
            </p>
            <Button onClick={() => refetch()} disabled={isFetching}>
              <RotateCcw className={`h-4 w-4 mr-2 ${isFetching ? 'animate-spin' : ''}`} />
              Retry
            </Button>
          </div>
        ) : isLoading ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
          </div>
        ) : filteredEvents.length > 0 ? (
          <>
            <p className="text-sm text-muted-foreground mb-6">
              Showing {filteredEvents.length} of {totalElements} event{totalElements !== 1 ? 's' : ''}
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
            {totalPages > 1 && (
              <div className="mt-10 flex items-center justify-center gap-3">
                <Button
                  variant="outline"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 0 || isFetching}
                >
                  <ChevronLeft className="h-4 w-4 mr-2" />
                  Previous
                </Button>
                <div className="px-4 py-2 rounded-lg border border-border bg-card text-sm text-muted-foreground">
                  {currentPage + 1} / {totalPages}
                </div>
                <Button
                  variant="outline"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage >= totalPages - 1 || isFetching}
                >
                  Next
                  <ChevronRight className="h-4 w-4 ml-2" />
                </Button>
              </div>
            )}
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
