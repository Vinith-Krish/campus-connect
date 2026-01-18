import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import EventCard from '../components/EventCard';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { User, Calendar, Heart, Loader2, GraduationCap, Users, Edit2, X } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { useToast } from '../hooks/use-toast';

const Profile = () => {
  const { user } = useAuth();
  const { toast } = useToast();
  const [activeTab, setActiveTab] = useState('registered');
  const [registeredEvents, setRegisteredEvents] = useState([]);
  const [interestedEvents, setInterestedEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [editFormData, setEditFormData] = useState({
    name: user?.name || '',
    email: user?.email || '',
    collegename: user?.collegename || '',
  });

  useEffect(() => {
    const fetchUserEvents = async () => {
      setIsLoading(true);
      try {
        const data = await userService.getMyEvents();
        setRegisteredEvents(data.registered || []);
        setInterestedEvents(data.interested || []);
      } catch (error) {
        console.error('Failed to fetch user events:', error);
        setRegisteredEvents([]);
        setInterestedEvents([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserEvents();
  }, []);

  useEffect(() => {
    if (user) {
      setEditFormData({
        name: user.name || '',
        email: user.email || '',
        collegename: user.collegename || '',
      });
    }
  }, [user]);

  const handleEditProfile = () => {
    setIsEditModalOpen(true);
  };

  const handleSaveProfile = async () => {
    if (!editFormData.name.trim()) {
      toast({
        title: 'Validation Error',
        description: 'Name is required',
        variant: 'destructive',
      });
      return;
    }

    setIsUpdating(true);
    try {
      await userService.updateProfile(editFormData);
      toast({
        title: 'Profile Updated!',
        description: 'Your profile has been updated successfully.',
      });
      setIsEditModalOpen(false);
      // Refresh the page to show updated data
      window.location.reload();
    } catch (error) {
      toast({
        title: 'Update Failed',
        description: error.response?.data?.message || 'Something went wrong. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setIsUpdating(false);
    }
  };

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
            <div className="flex-1 text-center md:text-left">
              <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
                <div>
                  <h1 className="font-display text-2xl md:text-3xl font-bold text-foreground">
                    {user?.name || 'User'}
                  </h1>
                  <p className="text-muted-foreground mt-1">{user?.email}</p>
                  {user?.collegename && (
                    <p className="text-muted-foreground text-sm mt-1 flex items-center justify-center md:justify-start gap-1">
                      <GraduationCap className="h-4 w-4" />
                      {user.collegename}
                    </p>
                  )}
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
                <Button
                  onClick={handleEditProfile}
                  variant="outline"
                  className="flex items-center gap-2"
                >
                  <Edit2 className="h-4 w-4" />
                  Edit Profile
                </Button>
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

      {/* Edit Profile Modal */}
      {isEditModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card rounded-2xl border border-border max-w-md w-full p-6 animate-slide-up">
            <div className="flex items-center justify-between mb-6">
              <h2 className="font-display text-2xl font-bold text-foreground">Edit Profile</h2>
              <button
                onClick={() => setIsEditModalOpen(false)}
                className="text-muted-foreground hover:text-foreground transition-colors"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label htmlFor="edit-name" className="block text-sm font-medium text-foreground mb-2">
                  Full Name
                </label>
                <Input
                  id="edit-name"
                  type="text"
                  value={editFormData.name}
                  onChange={(e) => setEditFormData({ ...editFormData, name: e.target.value })}
                  placeholder="Your name"
                />
              </div>

              <div>
                <label htmlFor="edit-email" className="block text-sm font-medium text-foreground mb-2">
                  Email
                </label>
                <Input
                  id="edit-email"
                  type="email"
                  value={editFormData.email}
                  onChange={(e) => setEditFormData({ ...editFormData, email: e.target.value })}
                  placeholder="your@email.com"
                  disabled
                  className="opacity-60"
                />
                <p className="text-xs text-muted-foreground mt-1">Email cannot be changed</p>
              </div>

              <div>
                <label htmlFor="edit-college" className="block text-sm font-medium text-foreground mb-2">
                  College Name
                </label>
                <Input
                  id="edit-college"
                  type="text"
                  value={editFormData.collegename}
                  onChange={(e) => setEditFormData({ ...editFormData, collegename: e.target.value })}
                  placeholder="Your college name"
                  disabled
                  className="opacity-60"
                />
                <p className="text-xs text-muted-foreground mt-1">College name cannot be changed</p>
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <Button
                variant="outline"
                className="flex-1"
                onClick={() => setIsEditModalOpen(false)}
                disabled={isUpdating}
              >
                Cancel
              </Button>
              <Button
                className="flex-1"
                onClick={handleSaveProfile}
                disabled={isUpdating}
              >
                {isUpdating ? (
                  <>
                    <Loader2 className="h-4 w-4 animate-spin mr-2" />
                    Saving...
                  </>
                ) : (
                  'Save Changes'
                )}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Profile;
