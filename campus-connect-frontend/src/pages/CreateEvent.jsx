import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Textarea } from '../components/ui/textarea';
import { eventService } from '../services/eventService';
import { useToast } from '../hooks/use-toast';
import { normalizeCategory } from '../lib/eventUtils';
import {
  Calendar,
  Clock,
  MapPin,
  FileText,
  Tag,
  Loader2,
  ArrowRight,
  GraduationCap,
  Image as ImageIcon,
} from 'lucide-react';
import { cn } from '@/lib/utils';

const categories = ['Tech', 'Cultural', 'Sports', 'Workshop'];

const CreateEvent = () => {
  const navigate = useNavigate();
  const { id: eventId } = useParams();
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(false);
  const [imageUploading, setImageUploading] = useState(false);
  const [loadingEvent, setLoadingEvent] = useState(!!eventId);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    date: '',
    time: '',
    venue: '',
    category: '',
    collegename: '',
    imageUrl: '',
  });
  const isEditMode = !!eventId;

  // Load event data if in edit mode
  useEffect(() => {
    if (eventId) {
      const loadEvent = async () => {
        setLoadingEvent(true);
        try {
          const event = await eventService.getEventById(eventId);
          setFormData({
            title: event.title || '',
            description: event.description || '',
            date: event.date || '',
            time: event.time || '',
            venue: event.venue || '',
            category: normalizeCategory(event.category),
            collegename: event.collegename || '',
            imageUrl: event.imageUrl || '',
          });
        } catch (error) {
          toast({
            title: 'Error',
            description: 'Failed to load event details',
            variant: 'destructive',
          });
          navigate('/profile');
        } finally {
          setLoadingEvent(false);
        }
      };
      loadEvent();
    }
  }, [eventId, navigate, toast]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCategoryChange = (category) => {
    setFormData((prev) => ({ ...prev, category }));
  };

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      toast({
        title: 'Invalid File',
        description: 'Please upload an image file',
        variant: 'destructive',
      });
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      toast({
        title: 'File Too Large',
        description: 'Please upload an image smaller than 5MB',
        variant: 'destructive',
      });
      return;
    }

    setImageUploading(true);

    try {
      // Upload to Cloudinary
      const uploadData = new FormData();
      uploadData.append('file', file);
      uploadData.append('upload_preset', 'campus-connect'); // You'll need to create this in Cloudinary
      
      const response = await fetch(
        'https://api.cloudinary.com/v1_1/dh4roff76/image/upload', // Replace with your cloud name
        {
          method: 'POST',
          body: uploadData,
        }
      );

      const data = await response.json();
      
      if (data.secure_url) {
        setFormData((prev) => ({ ...prev, imageUrl: data.secure_url }));
        toast({
          title: 'Image Uploaded',
          description: 'Your image has been uploaded successfully',
        });
      }
    } catch (error) {
      toast({
        title: 'Upload Failed',
        description: 'Failed to upload image. Please try again or use a URL instead.',
        variant: 'destructive',
      });
    } finally {
      setImageUploading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setIsLoading(true);
    try {
      let event;
      if (isEditMode) {
        event = await eventService.updateEvent(eventId, formData);
        toast({
          title: 'Event Updated!',
          description: 'Your event has been successfully updated.',
        });
      } else {
        event = await eventService.createEvent(formData);
        toast({
          title: 'Event Created!',
          description: 'Your event has been successfully created.',
        });
      }
      
      navigate(`/events/${event.id || eventId}`);
    } catch (error) {
      toast({
        title: isEditMode ? 'Update Failed' : 'Creation Failed',
        description: error.response?.data?.message || 'Something went wrong. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      {loadingEvent ? (
        <div className="container mx-auto px-4 py-20 flex items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      ) : (
        <div className="container mx-auto px-4 py-12">
          <div className="max-w-2xl mx-auto">
            <div className="text-center mb-10 animate-slide-up">
              <h1 className="font-display text-3xl md:text-4xl font-bold text-foreground">
                {isEditMode ? 'Edit Event' : 'Create New Event'}
              </h1>
              <p className="mt-2 text-muted-foreground">
                {isEditMode 
                  ? 'Update your event details' 
                  : 'Share an exciting event with the campus community'
                }
              </p>
            </div>

          <form onSubmit={handleSubmit} className="space-y-6 animate-slide-up" style={{ animationDelay: '0.1s' }}>
            {/* Title */}
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-foreground mb-2">
                Event Title
              </label>
              <div className="relative">
                <FileText className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  id="title"
                  name="title"
                  type="text"
                  placeholder="Enter event title"
                  value={formData.title}
                  onChange={handleInputChange}
                  className="pl-12"
                  required
                />
              </div>
            </div>

            {/* Description */}
            <div>
              <label htmlFor="description" className="block text-sm font-medium text-foreground mb-2">
                Description
              </label>
              <Textarea
                id="description"
                name="description"
                placeholder="Describe your event in detail..."
                value={formData.description}
                onChange={handleInputChange}
                rows={5}
                className="resize-none"
                required
              />
            </div>

            {/* Date & Time */}
            <div className="grid md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="date" className="block text-sm font-medium text-foreground mb-2">
                  Date
                </label>
                <div className="relative">
                  <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                  <Input
                    id="date"
                    name="date"
                    type="date"
                    value={formData.date}
                    onChange={handleInputChange}
                    className="pl-12"
                    required
                  />
                </div>
              </div>
              <div>
                <label htmlFor="time" className="block text-sm font-medium text-foreground mb-2">
                  Time
                </label>
                <div className="relative">
                  <Clock className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                  <Input
                    id="time"
                    name="time"
                    type="time"
                    value={formData.time}
                    onChange={handleInputChange}
                    className="pl-12"
                    required
                  />
                </div>
              </div>
            </div>

            {/* Venue */}
            <div>
              <label htmlFor="venue" className="block text-sm font-medium text-foreground mb-2">
                Venue
              </label>
              <div className="relative">
                <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  id="venue"
                  name="venue"
                  type="text"
                  placeholder="Enter event venue"
                  value={formData.venue}
                  onChange={handleInputChange}
                  className="pl-12"
                  required
                />
              </div>
            </div>

            {/* College Name */}
            <div>
              <label htmlFor="collegename" className="block text-sm font-medium text-foreground mb-2">
                College Name
              </label>
              <div className="relative">
                <GraduationCap className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  id="collegename"
                  name="collegename"
                  type="text"
                  placeholder="Enter your college name"
                  value={formData.collegename}
                  onChange={handleInputChange}
                  className="pl-12"
                  required
                />
              </div>
            </div>

            {/* Image URL */}
            <div>
              <label className="block text-sm font-medium text-foreground mb-2">
                Event Image <span className="text-muted-foreground text-xs">(Optional)</span>
              </label>
              
              {/* File Upload Button */}
              <div className="flex gap-3 mb-3">
                <label
                  htmlFor="imageFile"
                  className={cn(
                    "flex-1 flex items-center justify-center gap-2 px-4 py-3 rounded-lg border-2 border-dashed transition-all cursor-pointer",
                    imageUploading 
                      ? "border-muted-foreground/50 bg-muted/50 cursor-not-allowed" 
                      : "border-muted-foreground hover:border-primary hover:bg-primary/5"
                  )}
                >
                  {imageUploading ? (
                    <>
                      <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                      <span className="text-sm text-muted-foreground">Uploading...</span>
                    </>
                  ) : (
                    <>
                      <ImageIcon className="h-5 w-5 text-muted-foreground" />
                      <span className="text-sm font-medium text-muted-foreground">Upload from Computer</span>
                    </>
                  )}
                  <input
                    id="imageFile"
                    type="file"
                    accept="image/*"
                    onChange={handleImageUpload}
                    className="hidden"
                    disabled={imageUploading}
                  />
                </label>
              </div>

              {/* URL Input */}
              <div className="relative">
                <ImageIcon className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  id="imageUrl"
                  name="imageUrl"
                  type="url"
                  placeholder="Or paste image URL here..."
                  value={formData.imageUrl}
                  onChange={handleInputChange}
                  className="pl-12"
                />
              </div>
              
              {/* Image Preview */}
              {formData.imageUrl && (
                <div className="mt-3 rounded-lg overflow-hidden border border-border">
                  <img
                    src={formData.imageUrl}
                    alt="Event preview"
                    className="w-full h-48 object-cover"
                    onError={(e) => {
                      e.target.style.display = 'none';
                    }}
                  />
                </div>
              )}
            </div>

            {/* Category */}
            <div>
              <label className="block text-sm font-medium text-foreground mb-3">
                Category
              </label>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {categories.map((category) => (
                  <button
                    key={category}
                    type="button"
                    onClick={() => handleCategoryChange(category)}
                    className={cn(
                      'p-3 rounded-xl border-2 transition-all duration-200 flex items-center justify-center gap-2',
                      formData.category === category
                        ? 'border-primary bg-primary/5'
                        : 'border-border hover:border-muted-foreground'
                    )}
                  >
                    <Tag className={cn(
                      'h-4 w-4',
                      formData.category === category ? 'text-primary' : 'text-muted-foreground'
                    )} />
                    <span className={cn(
                      'font-medium text-sm',
                      formData.category === category ? 'text-foreground' : 'text-muted-foreground'
                    )}>
                      {category}
                    </span>
                  </button>
                ))}
              </div>
            </div>

            {/* Submit Button */}
            <div className="pt-4">
              <Button
                type="submit"
                variant="hero"
                size="xl"
                className="w-full"
                disabled={isLoading}
              >
                {isLoading ? (
                  <Loader2 className="h-5 w-5 animate-spin" />
                ) : (
                  <>
                    {isEditMode ? 'Update Event' : 'Create Event'}
                    <ArrowRight className="h-5 w-5" />
                  </>
                )}
              </Button>
            </div>
          </form>
        </div>
      </div>
      )}
    </div>
  );
};

export default CreateEvent;
